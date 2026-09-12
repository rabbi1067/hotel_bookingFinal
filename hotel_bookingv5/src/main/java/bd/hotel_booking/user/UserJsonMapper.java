package bd.hotel_booking.user;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class UserJsonMapper {

    private UserJsonMapper() {
    }

    public static Map<String, Object> toFrontendMap(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("phone", u.getPhone());
        m.put("role", u.getRole().name());
        m.put("status", u.getStatus().name().toLowerCase());
        m.put("address", u.getAddress());
        m.put("gender", u.getGender() == null ? null : u.getGender().name());
        m.put("image", u.getAvatar());
        m.put("blockedUntil", u.getBlockedUntil() == null ? null : u.getBlockedUntil().toString());
        m.put("memberSince", u.getCreatedAt() == null
                ? System.currentTimeMillis()
                : u.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        m.put("bookings", 0);
        return m;
    }

    public static List<Map<String, Object>> toFrontendList(List<User> users) {
        return users.stream().map(UserJsonMapper::toFrontendMap).collect(Collectors.toList());
    }
}