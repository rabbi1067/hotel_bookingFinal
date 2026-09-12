package bd.hotel_booking.booking.dto;

public record CardPaymentRequest(
        Long bookingId,
        String cardNumber,
        String expiry,
        String cvv,
        String cardHolder
) {
}
