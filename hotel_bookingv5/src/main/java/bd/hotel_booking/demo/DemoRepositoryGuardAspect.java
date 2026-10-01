package bd.hotel_booking.demo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * THE isolation guarantee for public demo mode.
 * <p>
 * Intercepts EVERY Spring-Data repository call. For real users it is a pure
 * pass-through (zero behaviour change). For demo users:
 * <ul>
 *   <li><b>save and flush</b> - staged in {@link DemoSessionStore}, JPA detached so
 *       dirty-checking can NOT flush to Postgres.</li>
 *   <li><b>delete methods</b> - recorded in session, Postgres untouched.</li>
 *   <li><b>find, list, count and exists methods</b> - real Postgres rows first (so live
 *       admin changes stay visible), overlay applied on top.</li>
 * </ul>
 * Existing services / controllers / repositories are NOT modified.
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DemoRepositoryGuardAspect {

    private final ObjectProvider<DemoSessionStore> storeProvider;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public DemoRepositoryGuardAspect(ObjectProvider<DemoSessionStore> storeProvider,
                                     org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.storeProvider = storeProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /** Demo never sends real emails or uploads to Cloudinary - fake success instead. */
    @Around("execution(* bd.hotel_booking.email.EmailService.*(..))")
    public Object blockDemoEmail(ProceedingJoinPoint pjp) throws Throwable {
        if (DemoContext.isDemo()) {
            log.info("Demo blocked email: {}", pjp.getSignature().getName());
            return null;
        }
        return pjp.proceed();
    }

    @Around("execution(* bd.hotel_booking.food.cloudinary.CloudinaryService.*(..))")
    public Object fakeDemoUpload(ProceedingJoinPoint pjp) throws Throwable {
        if (DemoContext.isDemo()) {
            Object[] args = pjp.getArgs();
            if (args.length > 0 && args[0] instanceof String dataUrl
                    && dataUrl.startsWith("data:image")) {
                return dataUrl; // avatar preview keeps working without Cloudinary
            }
            log.info("Demo blocked Cloudinary upload: {}", pjp.getSignature().getName());
            return "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=800&q=60";
        }
        return pjp.proceed();
    }

    @Around("execution(* org.springframework.data.repository.Repository+.*(..))")
    public Object guard(ProceedingJoinPoint pjp) throws Throwable {
        if (!DemoContext.isDemo()) {
            return pjp.proceed(); // real user -> untouched fast path
        }

        String method = pjp.getSignature().getName();
        Object[] args = pjp.getArgs() == null ? new Object[0] : pjp.getArgs();
        DemoSessionStore store = storeProvider.getObject();

        // ---------------- writes: never touch Postgres ----------------
        if (method.equals("save") || method.equals("saveAndFlush")) {
            Object entity = args.length > 0 ? args[0] : null;
            Object staged = store.stageSave(entity);
            detachQuietly(entity);
            return staged;
        }
        if (method.equals("saveAll") || method.equals("saveAllAndFlush")) {
            Object arg = args.length > 0 ? args[0] : null;
            if (arg instanceof Iterable<?> iterable) {
                List<Object> staged = new ArrayList<>();
                for (Object entity : iterable) {
                    staged.add(store.stageSave(entity));
                    detachQuietly(entity);
                }
                return staged;
            }
            return pjp.proceed();
        }
        if (method.equals("flush")) {
            return null; // demo has nothing to flush to Postgres
        }
        if (method.startsWith("delete")) {
            handleDelete(pjp, store, method, args);
            return nullValueFor(pjp);
        }

        // ---------------- reads: Postgres first, overlay on top ----------------
        if (method.equals("findAll") && args.length == 0) {
            Object real = pjp.proceed();
            Class<?> entity = DemoEntityRegistry.resolve(pjp, real);
            if (entity == null || !(real instanceof List<?>)) {
                return real;
            }
            return store.mergeListWithAdded(entity, castList(real));
        }
        if (method.startsWith("find") || method.startsWith("list") || method.startsWith("get")
                || method.startsWith("query") || method.startsWith("search") || method.startsWith("fetch")) {
            Object real = pjp.proceed();
            return mergeReadResult(pjp, store, method, args, real);
        }
        if (method.startsWith("count")) {
            Object real = pjp.proceed();
            Class<?> entity = DemoEntityRegistry.fromRepositoryName(pjp);
            if (entity == null || !(real instanceof Number)) {
                return real;
            }
            if (args.length == 0) {
                return store.mergeCount(entity, ((Number) real).longValue());
            }
            return real; // filtered counts keep it simple: real value only
        }
        if (method.startsWith("exists")) {
            Object real = pjp.proceed();
            if (Boolean.TRUE.equals(real)) {
                return real;
            }
            Boolean overlayHit = existsInOverlay(pjp, store, method, args);
            return overlayHit == null ? real : overlayHit;
        }

        return pjp.proceed();
    }

    // ------------------------------------------------------------------
    // delete handling
    // ------------------------------------------------------------------

    private void handleDelete(ProceedingJoinPoint pjp, DemoSessionStore store, String method, Object[] args) {
        try {
            Class<?> entity = DemoEntityRegistry.fromRepositoryName(pjp);
            if (method.equals("deleteAll") && args.length == 0) {
                if (entity != null) {
                    store.stageWipe(entity);
                }
                return;
            }
            if ((method.equals("deleteById") || method.equals("deleteAllById"))
                    && args.length == 1) {
                if (entity == null) {
                    return;
                }
                if (args[0] instanceof Number id) {
                    store.stageDelete(entity, id.longValue());
                } else if (args[0] instanceof Iterable<?> ids) {
                    for (Object id : ids) {
                        if (id instanceof Number number) {
                            store.stageDelete(entity, number.longValue());
                        }
                    }
                }
                return;
            }
            if (method.equals("delete") && args.length == 1 && args[0] != null) {
                Object entityArg = args[0];
                Long id = DemoSessionStore.readId(entityArg);
                if (id != null) {
                    store.stageDelete(entityArg.getClass(), id);
                    detachQuietly(entityArg);
                }
                return;
            }
            if (method.equals("deleteAll") && args.length == 1 && args[0] instanceof Iterable<?> entities) {
                for (Object entityArg : entities) {
                    if (entityArg == null) {
                        continue;
                    }
                    Long id = DemoSessionStore.readId(entityArg);
                    if (id != null) {
                        store.stageDelete(entityArg.getClass(), id);
                        detachQuietly(entityArg);
                    }
                }
                return;
            }
            // deleteAllByUser / deleteByXxx custom deletes -> block silently.
            // Token/request rows are invisible in UI; blocking is enough.
            log.debug("Demo blocked repository delete: {}", method);
        } catch (Exception ex) {
            log.debug("Demo delete guard fallback: {}", ex.toString());
        }
    }

    // ------------------------------------------------------------------
    // read merging
    // ------------------------------------------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object mergeReadResult(ProceedingJoinPoint pjp, DemoSessionStore store,
                                   String method, Object[] args, Object real) {
        try {
            if (real instanceof List<?> list) {
                Class<?> entity = DemoEntityRegistry.resolve(pjp, real);
                if (entity == null) {
                    entity = DemoEntityRegistry.fromRepositoryName(pjp);
                    if (entity == null) {
                        return real;
                    }
                }
                if (method.equals("findAll") || method.equals("findAllByOrderByCreatedAtDesc")) {
                    return store.mergeListWithAdded(entity, (List) list);
                }
                // user-scoped finder (my bookings / my orders / my cart):
                // demo-created rows belonging to this user must show up.
                if (method.contains("ByUser")) {
                    List merged = store.mergeList(entity, (List) list);
                    mergeOwnAdded(store, entity, args, merged);
                    return merged;
                }
                // other filtered finders -> delete/update only
                return store.mergeList(entity, (List) list);
            }
            if (real instanceof Optional<?> optional) {
                // demo profile: synthesize the logged-in demo user (no DB row exists)
                if ((method.equals("findByEmailIgnoreCase") || method.equals("findByEmail"))
                        && args.length >= 1 && args[0] instanceof String emailArg) {
                    DemoAccount demo = DemoAccount.fromEmail(emailArg);
                    if (demo != null) {
                        return Optional.of(getOrCreateDemoUser(store, demo));
                    }
                }
                Class<?> entity = DemoEntityRegistry.resolve(pjp, real);
                if (entity == null) {
                    // findById with unknown entity: try id-based overlay lookup
                    if (method.equals("findById") && args.length == 1 && args[0] instanceof Number) {
                        return real; // nothing to merge without entity type
                    }
                    return real;
                }
                if (optional.isPresent()) {
                    Object value = optional.get();
                    Long id = DemoSessionStore.readId(value);
                    if (id != null) {
                        if (store.isDeleted(value.getClass(), id)) {
                            return Optional.empty();
                        }
                        Optional<?> overlay = store.overlayFor(value.getClass(), id);
                        if (overlay.isPresent()) {
                            return overlay;
                        }
                    }
                    return real;
                }
                // real empty -> demo-created row by id?
                if (method.equals("findById") && args.length == 1 && args[0] instanceof Number number) {
                    Optional<?> overlay = store.overlayFor(entity, number.longValue());
                    if (overlay.isPresent()) {
                        return overlay;
                    }
                    // demo profile by synthetic id (-101..-104)
                    Object synthetic = findSyntheticDemoUserById(store, number.longValue());
                    if (synthetic != null && entity.isInstance(synthetic)) {
                        return Optional.of(synthetic);
                    }
                }
                return real;
            }
            if (real instanceof Collection<?> collection) {
                Class<?> entity = DemoEntityRegistry.resolve(pjp, real);
                if (entity == null) {
                    return real;
                }
                return store.mergeList(entity, new ArrayList<>((Collection) collection));
            }
            // single entity return (e.g. findByNumber returning Room or null)
            if (real != null) {
                Long id = DemoSessionStore.readId(real);
                if (id != null && store.isDeleted(real.getClass(), id)) {
                    return null;
                }
                if (id != null) {
                    Optional<?> overlay = store.overlayFor(real.getClass(), id);
                    if (overlay.isPresent()) {
                        return overlay.get();
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("Demo read-merge fallback for {}: {}", method, ex.toString());
        }
        return real;
    }

    /**
     * Minimal overlay-aware exists check for single-field unique guards
     * (existsByNumber / existsByEmailIgnoreCase / existsByPhone). Anything
     * else falls back to the real DB value.
     */
    private Boolean existsInOverlay(ProceedingJoinPoint pjp, DemoSessionStore store,
                                    String method, Object[] args) {
        try {
            if (args.length != 1 || !(args[0] instanceof String wanted)) {
                return null;
            }
            String lower = method.toLowerCase();
            String field = null;
            boolean ignoreCase = false;
            if (lower.startsWith("existsbynumber")) {
                field = "number";
            } else if (lower.startsWith("existsbyemail")) {
                field = "email";
                ignoreCase = true;
            } else if (lower.startsWith("existsbyphone")) {
                field = "phone";
            } else {
                return null;
            }
            Class<?> entity = DemoEntityRegistry.fromRepositoryName(pjp);
            if (entity == null) {
                return null;
            }
            // mergeListWithAdded gives us real + session rows for this entity
            // only when called via findAll path; here we approximate by asking
            // the store directly is out of scope, so scan a merged snapshot.
            // Keep it cheap: true only on exact overlay match.
            List<?> snapshot = store.mergeListWithAdded(entity, List.of());
            for (Object row : snapshot) {
                String actual = readStringField(row, field);
                if (actual == null) {
                    continue;
                }
                if (ignoreCase ? actual.equalsIgnoreCase(wanted) : actual.equals(wanted)) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String readStringField(Object row, String field) {
        if (row == null) {
            return null;
        }
        try {
            String getter = "get" + Character.toUpperCase(field.charAt(0)) + field.substring(1);
            Method m = row.getClass().getMethod(getter);
            Object value = m.invoke(row);
            return value == null ? null : String.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    // ------------------------------------------------------------------
    // demo profile (synthetic users, never in Postgres)
    // ------------------------------------------------------------------

    private Object getOrCreateDemoUser(DemoSessionStore store, DemoAccount demo) {
        Optional<Object> existing = store.overlayFor(bd.hotel_booking.user.User.class, demo.demoId());
        if (existing.isPresent()) {
            return existing.get();
        }
        bd.hotel_booking.user.User fresh = bd.hotel_booking.user.User.builder()
                .id(demo.demoId())
                .name(demo.displayName())
                .email(demo.email())
                .phone("+8801000000000")
                .password(passwordEncoder.encode(demo.password()))
                .role(demo.role())
                .status(bd.hotel_booking.user.UserStatus.ACTIVE)
                .address("Demo preview account")
                .avatar("")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        store.stageSave(fresh);
        detachQuietly(fresh);
        return fresh;
    }

    private Object findSyntheticDemoUserById(DemoSessionStore store, long id) {
        for (DemoAccount demo : DemoAccount.values()) {
            if (demo.demoId() == id) {
                return getOrCreateDemoUser(store, demo);
            }
        }
        return null;
    }

    /** Adds session-created rows of the requesting demo user into ByUser lists. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mergeOwnAdded(DemoSessionStore store, Class<?> entity, Object[] args, List merged) {
        try {
            bd.hotel_booking.user.User owner = null;
            for (Object arg : args) {
                if (arg instanceof bd.hotel_booking.user.User user) {
                    owner = user;
                    break;
                }
            }
            if (owner == null || !DemoAccount.isDemoEmail(owner.getEmail())) {
                return;
            }
            Long ownerId = DemoSessionStore.readId(owner);
            String ownerEmail = owner.getEmail();
            for (Object added : store.addedValues(entity)) {
                if (containsById(merged, added)) {
                    continue;
                }
                if (belongsToUser(added, ownerId, ownerEmail)) {
                    merged.add(added);
                }
            }
        } catch (Exception ignored) {
            // best effort only
        }
    }

    private static boolean containsById(List merged, Object candidate) {
        Long wanted = DemoSessionStore.readId(candidate);
        if (wanted == null) {
            return false;
        }
        for (Object item : merged) {
            if (wanted.equals(DemoSessionStore.readId(item))) {
                return true;
            }
        }
        return false;
    }

    private static boolean belongsToUser(Object row, Long ownerId, String ownerEmail) {
        try {
            Method getter = row.getClass().getMethod("getUser");
            Object user = getter.invoke(row);
            if (user == null) {
                return false;
            }
            Long rowUserId = DemoSessionStore.readId(user);
            if (ownerId != null && ownerId.equals(rowUserId)) {
                return true;
            }
            try {
                Method emailGetter = user.getClass().getMethod("getEmail");
                Object email = emailGetter.invoke(user);
                return email != null && email.toString().equalsIgnoreCase(ownerEmail);
            } catch (Exception ignored) {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    private void detachQuietly(Object entity) {        try {
            if (entity != null && entityManager != null && entityManager.contains(entity)) {
                entityManager.detach(entity);
            }
        } catch (Exception ignored) {
            // detach is best-effort; overlay staging already succeeded
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List castList(Object real) {
        return (List) real;
    }

    /** @Around must return a compatible value for void repository deletes. */
    private static Object nullValueFor(ProceedingJoinPoint pjp) {
        try {
            Class<?> returnType = ((org.aspectj.lang.reflect.MethodSignature) pjp.getSignature()).getReturnType();
            if (returnType == void.class || returnType == Void.class) {
                return null;
            }
        } catch (Exception ignored) {
            // fall through
        }
        return null;
    }
}
