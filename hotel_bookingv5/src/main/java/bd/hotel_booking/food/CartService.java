package bd.hotel_booking.food;

import bd.hotel_booking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;

    public List<CartItem> addOrIncrement(User user, Long foodItemId, int delta) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found: id=" + foodItemId));

        if (!Boolean.TRUE.equals(foodItem.getAvailable()) && delta > 0) {
            return getCart(user);
        }

        Optional<CartItem> existing = cartItemRepository.findByUserAndFoodItem(user, foodItem);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQty() + delta;
            if (newQty <= 0) {
                cartItemRepository.delete(item);
            } else {
                item.setQty(newQty);
                cartItemRepository.save(item);
            }
        } else if (delta > 0) {
            CartItem item = CartItem.builder()
                    .user(user)
                    .foodItem(foodItem)
                    .qty(delta)
                    .build();
            cartItemRepository.save(item);
        }

        return getCart(user);
    }

    public List<CartItem> getCart(User user) {
        return cartItemRepository.findByUser(user);
    }

    public void removeItem(User user, Long foodItemId) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId).orElse(null);
        if (foodItem == null) return;
        cartItemRepository.deleteByUserAndFoodItem(user, foodItem);
    }
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }

}