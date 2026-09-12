package bd.hotel_booking.room;

import bd.hotel_booking.promo.PromotionPricingService;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class RoomJsonMapper {

    private RoomJsonMapper() {
    }

    public static Map<String, Object> toFrontendMap(Room room) {
        return toFrontendMap(room, null);
    }

    public static Map<String, Object> toFrontendMap(Room room, PromotionPricingService promotionPricingService) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(room.getId()));
        m.put("number", room.getNumber());
        m.put("name", room.getName());
        m.put("type", capitalize(room.getType().name()));
        m.put("desc", room.getDescription());
        m.put("price", room.getPrice());

        int discount = room.getDiscount() == null ? 0 : room.getDiscount();
        if (promotionPricingService != null) {
            discount = promotionPricingService.effectiveDiscount(room).percentOff();
        }
        m.put("discount", discount);

        m.put("capacity", room.getCapacity());
        m.put("beds", room.getBeds() + (room.getBeds() != null && room.getBeds() > 1 ? " beds" : " bed"));
        m.put("size", room.getSize());
        m.put("floor", room.getFloor());
        m.put("view", room.getView());
        m.put("rating", room.getRating());
        m.put("reviews", room.getReviews());
        m.put("featured", room.getFeatured());
        m.put("petFriendly", room.getPetFriendly());
        m.put("amenities", room.getAmenities());
        m.put("images", room.getImages());
        m.put("status", room.getStatus().name().toLowerCase());
        m.put("popularity", room.getReviews());
        m.put("createdAt", room.getCreatedAt() == null
                ? System.currentTimeMillis()
                : room.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return m;
    }

    public static List<Map<String, Object>> toFrontendList(List<Room> rooms) {
        return toFrontendList(rooms, null);
    }

    public static List<Map<String, Object>> toFrontendList(List<Room> rooms, PromotionPricingService promotionPricingService) {
        return rooms.stream().map(r -> toFrontendMap(r, promotionPricingService)).collect(Collectors.toList());
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}