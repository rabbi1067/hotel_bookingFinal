package bd.hotel_booking.food;

import bd.hotel_booking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByUserOrderByCreatedAtDesc(User user);
    List<FoodOrder> findAllByOrderByCreatedAtDesc();
}
