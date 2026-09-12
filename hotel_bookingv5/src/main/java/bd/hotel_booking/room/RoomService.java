package bd.hotel_booking.room;

import bd.hotel_booking.room.dto.RoomActionResult;
import bd.hotel_booking.room.dto.RoomFormRequest;

import java.util.List;

public interface RoomService {

    List<Room> findAll();

    List<Room> findFeatured();

    Room findById(Long id);

    Room save(Room room);

    long count();

    long countByStatus(RoomStatus status);

    RoomActionResult createOrUpdate(RoomFormRequest request);

    RoomActionResult deleteRoom(Long id);

    RoomActionResult duplicateRoom(Long id);

    RoomActionResult toggleAvailability(Long id);
}
