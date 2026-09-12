package bd.hotel_booking.room.dto;

import bd.hotel_booking.room.Room;

public record RoomActionResult(boolean ok, String message, Room room) {

    public static RoomActionResult success(String message, Room room) {
        return new RoomActionResult(true, message, room);
    }

    public static RoomActionResult failure(String message) {
        return new RoomActionResult(false, message, null);
    }
}
