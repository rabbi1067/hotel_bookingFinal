package bd.hotel_booking.booking.dto;

public record WalletPaymentRequest(
        Long bookingId,
        String provider,
        String phone,
        String pin
) {
}
