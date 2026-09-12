package bd.hotel_booking.booking;

/**
 * Lifecycle of a booking. Values match exactly what the existing
 * guest/admin templates already expect ('pending', 'confirmed', 'active',
 * 'completed', 'cancelled'), just upper-cased for the database column.
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    ACTIVE,
    COMPLETED,
    CANCELLED
}
