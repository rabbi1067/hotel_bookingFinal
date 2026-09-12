package bd.hotel_booking.food;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class FoodJsonMapper {

    private FoodJsonMapper() {
    }

    public static Map<String, Object> toFrontendMap(FoodItem f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(f.getId()));
        m.put("name", f.getName());
        m.put("desc", f.getDescription());
        m.put("price", f.getPrice());
        m.put("org", null); // no separate "original price"/discount tracked for food yet
        m.put("img", f.getImage());
        m.put("cat", capitalize(f.getCategory().name()));
        m.put("available", f.getAvailable());
        return m;
    }

    public static List<Map<String, Object>> toFrontendList(List<FoodItem> items) {
        return items.stream().map(FoodJsonMapper::toFrontendMap).collect(Collectors.toList());
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}
