package bd.hotel_booking.food.dto;

public record PlaceFoodOrderRequest(
        String cart,
        String deliverTo,
        String paymentMethod,
        String cardNumber,
        String bkashNumber,
        String note
) {}