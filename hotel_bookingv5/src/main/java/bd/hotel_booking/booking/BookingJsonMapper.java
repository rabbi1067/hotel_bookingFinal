package bd.hotel_booking.booking;

import bd.hotel_booking.room.Room;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Same idea as {@code RoomJsonMapper}: the guest dashboard / confirmation /
 * admin booking-management pages were built against a specific demo JSON
 * shape. This is the single place that shape is produced from a real
 * {@link Booking} row, so it's not duplicated across every page.
 */
public final class BookingJsonMapper {

    private BookingJsonMapper() {
    }

    public static Map<String, Object> toFrontendMap(Booking b) {
        Room room = b.getRoom();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(b.getId()));
        m.put("bookingId", "HB-" + b.getCreatedAt().getYear() + "-" + String.format("%06d", b.getId()));
        m.put("guest", b.getGuestName());
        m.put("userId", b.getUser() != null ? b.getUser().getId() : null);
        m.put("roomId", String.valueOf(room.getId()));
        m.put("roomName", room.getName());
        m.put("roomType", capitalize(room.getType().name()));
        m.put("roomImage", (room.getImages() != null && !room.getImages().isEmpty()) ? room.getImages().get(0) : "");
        m.put("checkIn", b.getCheckIn().toString());
        m.put("checkOut", b.getCheckOut().toString());
        m.put("adults", b.getAdults());
        m.put("children", b.getChildren());
        m.put("pets", b.getWithPet());
        m.put("roomPrice", room.getPrice());
        m.put("nightlyDisc", room.getDiscount());
        m.put("nights", b.getNights());

        // Combined "how much was saved" for display: room-level per-night discount
        // AND any redeemed promo code both land in totalAmount already, so comparing
        // against the undiscounted full price captures both in one number - matching
        // what the templates show as a single "Discount (CODE)" row.
        BigDecimal fullPrice = room.getPrice().multiply(BigDecimal.valueOf(b.getNights()));
        BigDecimal discountAmount = fullPrice
                .subtract(Objects.requireNonNullElse(b.getTotalAmount(), BigDecimal.ZERO))
                .max(BigDecimal.ZERO);
        m.put("discountAmount", discountAmount);

        m.put("totalAmount", b.getTotalAmount());
        m.put("servicesTotal", BigDecimal.ZERO); // Extra services aren't wired to the DB yet
        m.put("taxAmount", BigDecimal.ZERO);      // Kept out so totals stay exactly consistent
        m.put("payment", b.getPaymentStatus().name().toLowerCase());
        m.put("paymentMethod", b.getPaymentMethod() == null ? null : b.getPaymentMethod().name());
        m.put("status", b.getStatus().name().toLowerCase());
        m.put("services", List.of());
        m.put("promoCode", b.getPromoCode());

        Map<String, Object> contact = new LinkedHashMap<>();
        contact.put("name", b.getGuestName());
        contact.put("email", b.getGuestEmail());
        contact.put("phone", b.getGuestPhone());
        contact.put("address", "");
        contact.put("request", b.getSpecialRequests());
        m.put("contact", contact);

        m.put("createdAt", b.getCreatedAt().toLocalDate().toString());
        return m;
    }

    public static List<Map<String, Object>> toFrontendList(List<Booking> bookings) {
        return bookings.stream().map(BookingJsonMapper::toFrontendMap).collect(Collectors.toList());
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}