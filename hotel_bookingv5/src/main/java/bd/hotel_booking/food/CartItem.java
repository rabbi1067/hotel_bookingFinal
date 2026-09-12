package bd.hotel_booking.food;

import bd.hotel_booking.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "food_item_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(nullable = false)
    private Integer qty;
}