package bd.hotel_booking.food.dto;

import java.math.BigDecimal;

public record FoodItemFormRequest(
        Long id,
        String name,
        String desc,
        BigDecimal price,
        String cat,
        String img,
        Boolean available
) {
}
