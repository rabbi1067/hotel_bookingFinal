package bd.hotel_booking.food;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    java.util.List<FoodItem> findByAvailableTrue();

    java.util.List<FoodItem> findByCategory(FoodCategory category);
}