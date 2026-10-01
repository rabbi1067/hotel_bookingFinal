package bd.hotel_booking.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Per-visitor in-memory overlay for demo mode.
 * <p>
 * <b>Lifecycle:</b> {@code @SessionScope} - created on first demo write/read,
 * destroyed on logout / session timeout. Nothing is ever written to Postgres.
 * <p>
 * <b>Semantics (Real DB -&gt; Demo YES, Demo -&gt; Real DB NO):</b>
 * <ul>
 *   <li>Reads = real Postgres rows + this overlay applied on top.</li>
 *   <li>Writes = only this overlay is mutated.</li>
 *   <li>Real admin changes are visible in demo because reads always go to Postgres first.</li>
 * </ul>
 */
@Component
@SessionScope
public class DemoSessionStore implements Serializable {

    private final Map<String, Map<Long, Object>> added = new ConcurrentHashMap<>();
    private final Map<String, Map<Long, Object>> updated = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> deleted = new ConcurrentHashMap<>();
    private final Map<String, Boolean> wiped = new ConcurrentHashMap<>();
    private final AtomicLong tempId = new AtomicLong(-1_000_000L);

    public long nextTempId() {
        return tempId.getAndDecrement();
    }

    public void clear() {
        added.clear();
        updated.clear();
        deleted.clear();
        wiped.clear();
    }

    // ------------------------------------------------------------------
    // writes
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <T> T stageSave(T entity) {
        if (entity == null) {
            return null;
        }
        Class<?> type = entity.getClass();
        String key = key(type);
        Long id = readId(entity);
        if (id == null) {
            long fresh = nextTempId();
            writeId(entity, fresh);
            added.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(fresh, entity);
            deleted.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).remove(fresh);
            return entity;
        }
        Map<Long, Object> addedBucket = added.get(key);
        if (addedBucket != null && addedBucket.containsKey(id)) {
            addedBucket.put(id, entity);
            return entity;
        }
        updated.computeIfAbsent(key, k -> new ConcurrentHashMap<>()).put(id, entity);
        Set<Long> deletedBucket = deleted.get(key);
        if (deletedBucket != null) {
            deletedBucket.remove(id);
        }
        return entity;
    }

    public void stageDelete(Class<?> type, Long id) {
        if (type == null || id == null) {
            return;
        }
        String key = key(type);
        Map<Long, Object> addedBucket = added.get(key);
        if (addedBucket != null && addedBucket.remove(id) != null) {
            Map<Long, Object> updatedBucket = updated.get(key);
            if (updatedBucket != null) {
                updatedBucket.remove(id);
            }
            return; // it only lived in this session -> just forget it
        }
        Map<Long, Object> updatedBucket = updated.get(key);
        if (updatedBucket != null) {
            updatedBucket.remove(id);
        }
        deleted.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(id);
    }

    public void stageWipe(Class<?> type) {
        if (type == null) {
            return;
        }
        String key = key(type);
        added.remove(key);
        updated.remove(key);
        deleted.remove(key);
        wiped.put(key, Boolean.TRUE);
    }

    // ------------------------------------------------------------------
    // reads (merge real rows + overlay)
    // ------------------------------------------------------------------

    public boolean isDeleted(Class<?> type, Long id) {
        if (type == null || id == null) {
            return false;
        }
        Set<Long> bucket = deleted.get(key(type));
        return bucket != null && bucket.contains(id);
    }

    public boolean isWiped(Class<?> type) {
        return type != null && Boolean.TRUE.equals(wiped.get(key(type)));
    }

    @SuppressWarnings("unchecked")
    public Optional<Object> overlayFor(Class<?> type, Long id) {
        if (type == null || id == null) {
            return Optional.empty();
        }
        String key = key(type);
        Map<Long, Object> addedBucket = added.get(key);
        if (addedBucket != null && addedBucket.containsKey(id)) {
            return Optional.of((T) addedBucket.get(id));
        }
        Map<Long, Object> updatedBucket = updated.get(key);
        if (updatedBucket != null && updatedBucket.containsKey(id)) {
            return Optional.of((T) updatedBucket.get(id));
        }
        return Optional.empty();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List mergeList(Class<?> type, List real) {
        if (type == null) {
            return real == null ? Collections.emptyList() : real;
        }
        String key = key(type);
        List base = real == null ? new ArrayList<>() : new ArrayList<>(real);
        if (Boolean.TRUE.equals(wiped.get(key))) {
            base.clear();
        } else {
            Set<Long> deletedBucket = deleted.get(key);
            if (deletedBucket != null && !deletedBucket.isEmpty()) {
                base.removeIf(item -> item != null && deletedBucket.contains(readId(item)));
            }
            Map<Long, Object> updatedBucket = updated.get(key);
            if (updatedBucket != null && !updatedBucket.isEmpty()) {
                for (int i = 0; i < base.size(); i++) {
                    Object item = base.get(i);
                    if (item == null) {
                        continue;
                    }
                    Object replacement = updatedBucket.get(readId(item));
                    if (replacement != null) {
                        base.set(i, replacement);
                    }
                }
            }
        }
        // New session rows only join the unfiltered "findAll" view. Filtered
        // finders (findByStatus, findByRole, ...) intentionally only get
        // delete/update treatment so we never leak a wrongly-filtered row.
        // The aspect decides when to call mergeListWithAdded vs mergeList.
        return base;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public List mergeListWithAdded(Class<?> type, List real) {
        List merged = mergeList(type, real);
        Map<Long, Object> addedBucket = added.get(key(type));
        if (addedBucket != null && !addedBucket.isEmpty()) {
            for (Object o : addedBucket.values()) {
                merged.add(o);
            }
        }
        return merged;
    }

    public long mergeCount(Class<?> type, long realCount) {
        if (type == null) {
            return realCount;
        }
        if (isWiped(type)) {
            return addedCount(type);
        }
        long merged = realCount + addedCount(type) - deletedCount(type);
        return Math.max(0, merged);
    }

    public long addedCount(Class<?> type) {
        Map<Long, Object> bucket = added.get(key(type));
        return bucket == null ? 0 : bucket.size();
    }

    public long deletedCount(Class<?> type) {
        Set<Long> bucket = deleted.get(key(type));
        return bucket == null ? 0 : bucket.size();
    }

    /** Snapshot of session-created rows for one entity (used for user-scoped merges). */
    public List<Object> addedValues(Class<?> type) {
        if (type == null) {
            return List.of();
        }
        Map<Long, Object> bucket = added.get(key(type));
        if (bucket == null || bucket.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(bucket.values());
    }

    // ------------------------------------------------------------------
    // id reflection (all entities use Long id with getId/setId or field id)
    // ------------------------------------------------------------------

    public static String key(Class<?> type) {
        return type.getName();
    }

    public static Long readId(Object entity) {
        if (entity == null) {
            return null;
        }
        try {
            Method getter = entity.getClass().getMethod("getId");
            Object value = getter.invoke(entity);
            return toLong(value);
        } catch (NoSuchMethodException ignored) {
            // fall through to field access
        } catch (Exception ignored) {
            return null;
        }
        try {
            Field field = findField(entity.getClass(), "id");
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            return toLong(field.get(entity));
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void writeId(Object entity, Long id) {
        if (entity == null || id == null) {
            return;
        }
        try {
            Method setter = entity.getClass().getMethod("setId", Long.class);
            setter.invoke(entity, id);
            return;
        } catch (NoSuchMethodException ignored) {
            // fall through
        } catch (Exception ignored) {
            return;
        }
        try {
            Method setter = entity.getClass().getMethod("setId", long.class);
            setter.invoke(entity, id.longValue());
            return;
        } catch (Exception ignored) {
            // fall through
        }
        try {
            Field field = findField(entity.getClass(), "id");
            if (field != null) {
                field.setAccessible(true);
                if (field.getType() == Long.class) {
                    field.set(entity, id);
                } else if (field.getType() == long.class) {
                    field.setLong(entity, id.longValue());
                }
            }
        } catch (Exception ignored) {
            // best effort only
        }
    }

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static Long toLong(Object value) {
        if (value instanceof Long l) {
            return l;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        return null;
    }
}
