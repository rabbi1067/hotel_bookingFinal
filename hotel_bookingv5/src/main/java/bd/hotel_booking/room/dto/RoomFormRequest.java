package bd.hotel_booking.room.dto;

import bd.hotel_booking.room.RoomStatus;
import bd.hotel_booking.room.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record RoomFormRequest(
        Long id,
        String number,
        String name,
        RoomType type,
        String description,
        BigDecimal price,
        Integer discount,
        Integer capacity,
        Integer beds,
        Integer size,
        Integer floor,
        String view,
        Double rating,
        Integer reviews,
        Boolean featured,
        Boolean petFriendly,
        List<String> amenities,
        List<String> images,
        RoomStatus status
) {
}
