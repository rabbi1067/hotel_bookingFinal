package bd.hotel_booking.demo;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves a Spring-Data repository call to its domain entity class.
 * <p>
 * Strategy: generic return type first (works even for empty lists),
 * then repository interface-name heuristic as fallback.
 */
final class DemoEntityRegistry {

    private static final Map<String, Class<?>> BY_REPOSITORY = new HashMap<>();

    static {
        register("RoomRepository", "bd.hotel_booking.room.Room");
        register("UserRepository", "bd.hotel_booking.user.User");
        register("BookingRepository", "bd.hotel_booking.booking.Booking");
        register("FoodItemRepository", "bd.hotel_booking.food.FoodItem");
        register("FoodOrderRepository", "bd.hotel_booking.food.FoodOrder");
        register("CartItemRepository", "bd.hotel_booking.food.CartItem");
        register("GalleryRepository", "bd.hotel_booking.gallery.GalleryItem");
        register("PromotionRepository", "bd.hotel_booking.promo.Promotion");
        register("PromoCodeRepository", "bd.hotel_booking.promo.codes.PromoCode");
        register("WifiConfigRepository", "bd.hotel_booking.wifi.WifiConfig");
        register("SiteContentRepository", "bd.hotel_booking.content.SiteContent");
        register("PasswordResetTokenRepository", "bd.hotel_booking.user.PasswordResetToken");
    }

    private DemoEntityRegistry() {
    }

    private static void register(String repositorySimpleName, String entityClassName) {
        try {
            BY_REPOSITORY.put(repositorySimpleName, Class.forName(entityClassName));
        } catch (ClassNotFoundException ignored) {
            // entity does not exist in this build -> simply not resolvable
        }
    }

    /** Best-effort entity resolution for the current repository invocation. */
    static Class<?> resolve(ProceedingJoinPoint pjp, Object readResult) {
        // 1) non-empty payload tells the truth
        Class<?> fromPayload = fromPayload(readResult);
        if (fromPayload != null) {
            return fromPayload;
        }
        // 2) generic return type, e.g. List<Room> findAll()
        Class<?> fromGeneric = fromGenericReturnType(pjp);
        if (fromGeneric != null) {
            return fromGeneric;
        }
        // 3) repository interface name heuristic
        return fromRepositoryName(pjp);
    }

    static Class<?> fromRepositoryName(ProceedingJoinPoint pjp) {
        try {
            Object target = pjp.getTarget();
            if (target == null) {
                return null;
            }
            for (Class<?> iface : target.getClass().getInterfaces()) {
                Class<?> hit = BY_REPOSITORY.get(iface.getSimpleName());
                if (hit != null) {
                    return hit;
                }
            }
            // CGLIB / JDK proxy edge: inspect generic interfaces too
            String targetName = target.getClass().getName();
            for (Map.Entry<String, Class<?>> entry : BY_REPOSITORY.entrySet()) {
                if (targetName.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        } catch (Exception ignored) {
            // best effort only
        }
        return null;
    }

    private static Class<?> fromPayload(Object result) {
        try {
            if (result instanceof java.util.List<?> list && !list.isEmpty() && list.get(0) != null) {
                return list.get(0).getClass();
            }
            if (result instanceof Optional<?> optional && optional.isPresent()) {
                return optional.get().getClass();
            }
            if (result != null
                    && !(result instanceof Number)
                    && !(result instanceof Boolean)
                    && !(result instanceof String)
                    && !(result instanceof java.util.Collection)
                    && !(result instanceof Map)) {
                // single-entity finder (e.g. findByNumber returning Room)
                String pkg = result.getClass().getPackageName();
                if (pkg.startsWith("bd.hotel_booking.")) {
                    return result.getClass();
                }
            }
        } catch (Exception ignored) {
            // best effort only
        }
        return null;
    }

    private static Class<?> fromGenericReturnType(ProceedingJoinPoint pjp) {
        try {
            Method method = ((org.aspectj.lang.reflect.MethodSignature) pjp.getSignature()).getMethod();
            Type generic = method.getGenericReturnType();
            if (generic instanceof ParameterizedType parameterized) {
                for (Type arg : parameterized.getActualTypeArguments()) {
                    if (arg instanceof Class<?> clazz && clazz.getPackageName().startsWith("bd.hotel_booking.")) {
                        return clazz;
                    }
                }
            }
        } catch (Exception ignored) {
            // best effort only
        }
        return null;
    }
}
