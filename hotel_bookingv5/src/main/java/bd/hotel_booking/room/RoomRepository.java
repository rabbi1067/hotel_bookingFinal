package bd.hotel_booking.room;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByNumber(String number);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByFeaturedTrue();
}
