package bd.hotel_booking.food;

import bd.hotel_booking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndFoodItem(User user, FoodItem foodItem);
    void deleteByUserAndFoodItem(User user, FoodItem foodItem);
    @Modifying
    @Transactional
    void deleteByUser(User user);
}