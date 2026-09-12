package bd.hotel_booking.booking;

import bd.hotel_booking.booking.dto.CardPaymentRequest;
import bd.hotel_booking.booking.dto.WalletPaymentRequest;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/checkout")
public class CheckoutController {

    private final BookingService bookingService;
    private final UserService userService;
    private final PaymentValidationService paymentValidationService;

    @GetMapping
    public String checkout(@RequestParam("bookingId") Long bookingId, Authentication authentication, Model model) {
        Booking booking = ownedBooking(bookingId, authentication);
        model.addAttribute("bookingsForJs", List.of(BookingJsonMapper.toFrontendMap(booking)));
        return "guest/checkout";
    }

    /**
     * Real format validation (Bangladeshi mobile number pattern, 4-5 digit PIN)
     * happens here before the booking is marked paid. No OTP step, and no real
     * money moves (there's no bKash/Nagad merchant account behind this) - but
     * this is a genuine server-side check, not a UI-only illusion.
     */
    @PostMapping("/wallet")
    public String payWithWallet(@ModelAttribute WalletPaymentRequest request, Authentication authentication, Model model) {
        Booking booking = ownedBooking(request.bookingId(), authentication);
        PaymentValidationService.ValidationResult result =
                paymentValidationService.validateMobileWallet(request.phone(), request.pin());

        if (!result.ok) {
            model.addAttribute("bookingsForJs", List.of(BookingJsonMapper.toFrontendMap(booking)));
            model.addAttribute("payError", result.message);
            return "guest/checkout";
        }

        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentMethod("nagad".equalsIgnoreCase(request.provider()) ? PaymentMethod.NAGAD : PaymentMethod.BKASH);
        bookingService.save(booking);
        return "redirect:/user/booking-confirmation?id=" + booking.getId() + "&paid=1";
    }

    @PostMapping("/card")
    public String payWithCard(@ModelAttribute CardPaymentRequest request, Authentication authentication, Model model) {
        Booking booking = ownedBooking(request.bookingId(), authentication);
        PaymentValidationService.ValidationResult result =
                paymentValidationService.validateCard(request.cardNumber(), request.expiry(), request.cvv(), request.cardHolder());

        if (!result.ok) {
            model.addAttribute("bookingsForJs", List.of(BookingJsonMapper.toFrontendMap(booking)));
            model.addAttribute("payError", result.message);
            return "guest/checkout";
        }

        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentMethod(PaymentMethod.CARD);
        bookingService.save(booking);
        return "redirect:/user/booking-confirmation?id=" + booking.getId() + "&paid=1";
    }

    private Booking ownedBooking(Long bookingId, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        return bookingService.findOwnedByUser(bookingId, user);
    }
}
