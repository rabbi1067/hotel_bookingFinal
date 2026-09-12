package bd.hotel_booking.wifi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WifiConfigRepository extends JpaRepository<WifiConfig, Long> {

    List<WifiConfig> findAllByOrderByUpdatedAtDesc();
}