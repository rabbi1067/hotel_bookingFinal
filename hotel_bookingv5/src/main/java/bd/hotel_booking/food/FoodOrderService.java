package bd.hotel_booking.food;

import bd.hotel_booking.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoodOrderService {

    private final FoodOrderRepository foodOrderRepository;
    private final FoodItemRepository foodItemRepository;
    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    public static class PlaceOrderResult {
        public final boolean ok;
        public final String message;
        public final FoodOrder order;

        private PlaceOrderResult(boolean ok, String message, FoodOrder order) {
            this.ok = ok;
            this.message = message;
            this.order = order;
        }
    }

    @Transactional
    public PlaceOrderResult placeOrder(User user, String cartJson, String deliverTo,
                                       PaymentMethod method, String cardNumber,
                                       String bkashNumber, String note) {
        List<Map<String, Object>> lines;
        try {
            lines = objectMapper.readValue(cartJson, List.class);
        } catch (Exception e) {
            return new PlaceOrderResult(false, "Could not read your cart. Please try again.", null);
        }

        if (lines == null || lines.isEmpty()) {
            return new PlaceOrderResult(false, "Your cart is empty.", null);
        }

        List<Map<String, Object>> snapshot = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        StringBuilder summary = new StringBuilder();

        for (Map<String, Object> line : lines) {
            Long itemId = Long.valueOf(String.valueOf(line.get("id")));
            int qty = Integer.parseInt(String.valueOf(line.getOrDefault("qty", 1)));
            if (qty < 1) qty = 1;

            FoodItem item = foodItemRepository.findById(itemId).orElse(null);
            if (item == null || !Boolean.TRUE.equals(item.getAvailable())) continue;

            BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(lineTotal);

            snapshot.add(Map.of(
                    "name", item.getName(),
                    "qty", qty,
                    "price", item.getPrice(),
                    "lineTotal", lineTotal
            ));
            if (summary.length() > 0) summary.append(", ");
            summary.append(item.getName()).append(" x").append(qty);
        }

        if (snapshot.isEmpty()) {
            return new PlaceOrderResult(false,
                    "None of the items in your cart are available right now.", null);
        }

        String itemsJson;
        try {
            itemsJson = objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            return new PlaceOrderResult(false,
                    "Something went wrong building your order. Please try again.", null);
        }

        if (method == null || !paymentService.process(method, cardNumber, bkashNumber)) {
            return new PlaceOrderResult(false, "Payment failed. Please try again.", null);
        }

        String transactionRef;
        if (method == PaymentMethod.CASH) {
            transactionRef = "CASH";
        } else if (method == PaymentMethod.CARD) {
            String last4 = cardNumber != null && cardNumber.length() >= 4
                    ? cardNumber.substring(cardNumber.length() - 4) : "0000";
            transactionRef = "CARD-" + last4;
        } else {
            transactionRef = "BKASH-" + (bkashNumber == null ? "UNKNOWN" : bkashNumber);
        }

        FoodOrder order = FoodOrder.builder()
                .user(user)
                .itemsJson(itemsJson)
                .summaryLabel(summary.toString())
                .totalAmount(total)
                .deliverTo(deliverTo == null || deliverTo.isBlank()
                        ? "Front desk pickup" : deliverTo)
                .status(FoodOrderStatus.PLACED)
                .paymentMethod(method)
                .paymentStatus(PaymentStatus.PAID)
                .transactionRef(transactionRef)
                .note(note)
                .createdAt(LocalDateTime.now())
                .build();

        return new PlaceOrderResult(true, "Order placed.", foodOrderRepository.save(order));
    }

    public List<FoodOrder> findAll() {
        return foodOrderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<FoodOrder> findForUser(User user) {
        return foodOrderRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public FoodOrder updateStatus(Long id, FoodOrderStatus status, String reason) {
        FoodOrder order = foodOrderRepository.findById(id).orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        order.setStatus(status);

        if (status == FoodOrderStatus.PREPARING && order.getPreparingAt() == null) {
            order.setPreparingAt(now);
        } else if (status == FoodOrderStatus.OUT_FOR_DELIVERY
                && order.getOutForDeliveryAt() == null) {
            order.setOutForDeliveryAt(now);
        } else if (status == FoodOrderStatus.DELIVERED
                && order.getDeliveredAt() == null) {
            order.setDeliveredAt(now);
        } else if (status == FoodOrderStatus.CANCELLED
                && reason != null && !reason.isBlank()) {
            order.setCancelReason(reason.trim());
        }

        return foodOrderRepository.save(order);
    }

    @Transactional
    public boolean cancelByGuest(User user, Long orderId) {
        FoodOrder order = foodOrderRepository.findById(orderId).orElse(null);
        if (order == null || order.getUser() == null
                || !order.getUser().getId().equals(user.getId())) return false;
        if (order.getStatus() == FoodOrderStatus.OUT_FOR_DELIVERY
                || order.getStatus() == FoodOrderStatus.DELIVERED
                || order.getStatus() == FoodOrderStatus.CANCELLED) return false;

        order.setStatus(FoodOrderStatus.CANCELLED);
        order.setCancelReason("Cancelled by guest");
        foodOrderRepository.save(order);
        return true;
    }
}
