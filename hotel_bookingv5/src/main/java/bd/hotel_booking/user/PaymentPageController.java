package bd.hotel_booking.user;

import bd.hotel_booking.booking.Booking;
import bd.hotel_booking.booking.BookingService;
import bd.hotel_booking.booking.BookingStatus;
import bd.hotel_booking.booking.PaymentStatus;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PaymentPageController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/user/payment-status")
    @Transactional(readOnly = true)
    public String paymentStatus(Authentication authentication, Model model) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        User currentUser = userService.findByEmail(authentication.getName());

        List<BookingForJs> bookings = bookingService.findForUser(currentUser)
                .stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .map(this::toBookingForJs)
                .toList();

        model.addAttribute("bookingsForJs", bookings);
        return "guest/payment-status";
    }

    private BookingForJs toBookingForJs(Booking booking) {
        String roomImage = "";
        String roomName = "Room";
        Long roomId = null;

        if (booking.getRoom() != null) {
            roomId = booking.getRoom().getId();
            roomName = booking.getRoom().getName();

            if (booking.getRoom().getImages() != null
                    && !booking.getRoom().getImages().isEmpty()) {
                roomImage = booking.getRoom().getImages().get(0);
            }
        }

        String payment = booking.getPaymentStatus() == PaymentStatus.PAID
                ? "paid"
                : "unpaid";

        String paymentMethod = booking.getPaymentMethod() == null
                ? ""
                : booking.getPaymentMethod().name().toLowerCase();

        return new BookingForJs(
                booking.getId(),
                "BK-" + String.format("%06d", booking.getId()),
                booking.getUser() == null ? null : booking.getUser().getId(),
                booking.getGuestName(),
                roomId,
                roomName,
                roomImage,
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getTotalAmount() == null
                        ? BigDecimal.ZERO
                        : booking.getTotalAmount(),
                booking.getStatus().name().toLowerCase(),
                payment,
                paymentMethod
        );
    }


    public record BookingForJs(
            Long id,
            String bookingId,
            Long userId,
            String guest,
            Long roomId,
            String roomName,
            String roomImage,
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal totalAmount,
            String status,
            String payment,
            String paymentMethod
    ) {
    }
}