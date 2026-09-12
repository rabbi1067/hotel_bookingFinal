package bd.hotel_booking.food;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public List<FoodItem> findAll() {
        return foodItemRepository.findAll();
    }

    public FoodItem save(Long id, String name, String description, java.math.BigDecimal price,
                          FoodCategory category, String image, boolean available) {
        FoodItem item = id != null ? foodItemRepository.findById(id).orElse(null) : null;
        if (item == null) {
            item = FoodItem.builder().createdAt(LocalDateTime.now()).build();
        }
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setCategory(category);
        item.setImage(image);
        item.setAvailable(available);
        return foodItemRepository.save(item);
    }

    public void toggleAvailability(Long id) {
        FoodItem item = foodItemRepository.findById(id).orElseThrow();
        item.setAvailable(!Boolean.TRUE.equals(item.getAvailable()));
        foodItemRepository.save(item);
    }

    public void deleteById(Long id) {
        foodItemRepository.deleteById(id);
    }
}
