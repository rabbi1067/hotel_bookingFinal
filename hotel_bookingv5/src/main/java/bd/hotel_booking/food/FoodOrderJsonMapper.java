package bd.hotel_booking.food;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class FoodOrderJsonMapper {

    private static final DateTimeFormatter DISPLAY_DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, h:mm a");

    private FoodOrderJsonMapper() {
    }

    public static Map<String, Object> toFrontendMap(FoodOrder o) {
        Map<String, Object> m = new LinkedHashMap<>();

        m.put("id", String.valueOf(o.getId()));
        m.put("orderId", "FO-" + o.getCreatedAt().getYear() + "-"
                + String.format("%06d", o.getId()));

        m.put("userId", o.getUser().getId());
        m.put("guestName", o.getUser().getName());
        m.put("guestEmail", o.getUser().getEmail());
        m.put("guestPhone", o.getUser().getPhone());

        m.put("label", o.getSummaryLabel());
        m.put("itemsJson", o.getItemsJson());
        m.put("total", o.getTotalAmount());
        m.put("where", o.getDeliverTo());

        // Keep both formats: display date for the UI and ISO date for
        // reliable sorting and Today's orders / Revenue today calculations.
        m.put("createdAtIso", o.getCreatedAt().toString());
        m.put("createdAt", o.getCreatedAt().toString());
        m.put("orderDate", o.getCreatedAt().toString());
        m.put("date", o.getCreatedAt().format(DISPLAY_DATE_FMT));

        m.put("status", o.getStatus() != null ? o.getStatus().name() : null);
        m.put("paymentMethod", o.getPaymentMethod() != null
                ? o.getPaymentMethod().name() : null);
        m.put("paymentStatus", o.getPaymentStatus() != null
                ? o.getPaymentStatus().name() : null);
        m.put("transactionRef", o.getTransactionRef());
        m.put("note", o.getNote());
        m.put("cancelReason", o.getCancelReason());

        m.put("preparingAt", o.getPreparingAt() != null
                ? o.getPreparingAt().toString() : null);
        m.put("outForDeliveryAt", o.getOutForDeliveryAt() != null
                ? o.getOutForDeliveryAt().toString() : null);
        m.put("deliveredAt", o.getDeliveredAt() != null
                ? o.getDeliveredAt().toString() : null);

        return m;
    }

    public static List<Map<String, Object>> toFrontendList(List<FoodOrder> orders) {
        return orders.stream()
                .map(FoodOrderJsonMapper::toFrontendMap)
                .collect(Collectors.toList());
    }
}
