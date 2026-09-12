package bd.hotel_booking.booking;

import bd.hotel_booking.booking.dto.BookingCreateRequest;
import bd.hotel_booking.promo.codes.PromoCodeService;
import bd.hotel_booking.room.Room;
import bd.hotel_booking.room.RoomService;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * Public booking creation only (public/booking.html submits here). Admin
 * actions, guest self-service actions, and the checkout/payment flow each
 * have their own controller now - see AdminBookingActionsController,
 * GuestBookingActionsController, and CheckoutController.
 */
@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final UserService userService;
    private final PromoCodeService promoCodeService;

    @PostMapping("/booking/create")
    public String create(@Valid @ModelAttribute BookingCreateRequest request,
                         BindingResult bindingResult,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

        // public/booking.html is a fully client-rendered wizard - it never reads
        // Thymeleaf model attributes, so any error has to travel back as a flash
        // attribute + redirect (read by a small inline script on that page),
        // never as a re-rendered view. Returning "public/booking" directly here
        // would silently discard the error and reset the wizard to a random room.
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors().stream()
                    .findFirst()
                    .map(FieldError::getDefaultMessage)
                    .orElse("Please check your booking details and try again.");
            return backToBooking(redirectAttributes, request.roomId(), message);
        }

        Room room = roomService.findById(request.roomId());

        if (!request.checkIn().isBefore(request.checkOut())) {
            return backToBooking(redirectAttributes, request.roomId(), "Check-out date must be after check-in date.");
        }

        if (!bookingService.isRoomAvailable(room, request.checkIn(), request.checkOut())) {
            return backToBooking(redirectAttributes, request.roomId(),
                    "This room is already booked for part of those dates. Please choose different dates.");
        }

        BigDecimal roomTotal = bookingService.calculateRoomTotal(room, request.checkIn(), request.checkOut());

        // Promo code is optional. If the guest typed one, it is re-validated
        // here against the real promo_codes table (never trusting whatever
        // the client-side "Apply" preview showed) and only redeemed - i.e.
        // its `used` counter bumped - once we're sure the booking itself is
        // otherwise going through, so a failed booking never burns a use.
        String promoCode = null;
        BigDecimal promoDiscount = BigDecimal.ZERO;
        if (request.promoCode() != null && !request.promoCode().isBlank()) {
            PromoCodeService.Result promoResult = promoCodeService.redeem(request.promoCode(), roomTotal);
            if (!promoResult.ok()) {
                return backToBooking(redirectAttributes, request.roomId(), promoResult.message());
            }
            promoCode = promoResult.promo().getCode();
            promoDiscount = promoResult.discount();
        }

        BigDecimal totalAmount = roomTotal.subtract(promoDiscount).max(BigDecimal.ZERO);

        User currentUser = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            currentUser = userService.findByEmail(authentication.getName());
        }

        Booking booking = Booking.builder()
                .room(room)
                .user(currentUser)
                .guestName(request.guestName())
                .guestEmail(request.guestEmail())
                .guestPhone(request.guestPhone())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .adults(request.adults() == null ? 1 : request.adults())
                .children(request.children() == null ? 0 : request.children())
                .withPet(Boolean.TRUE.equals(request.withPet()))
                .specialRequests(request.specialRequests())
                .roomTotal(roomTotal)
                .promoCode(promoCode)
                .promoDiscount(promoDiscount)
                .totalAmount(totalAmount)
                .build();

        Booking saved = bookingService.create(booking);

        // "Pay now" in the wizard only leads straight to the real payment
        // form (bKash/Nagad/Card - see CheckoutController) when we know who
        // is paying, since /user/checkout requires a logged-in guest. An
        // anonymous "Pay now" still creates the booking as unpaid and lands
        // on the confirmation page, which already has its own "Pay now"
        // link once the guest logs in.
        if ("online".equals(request.paymentIntent()) && currentUser != null) {
            return "redirect:/user/checkout?bookingId=" + saved.getId();
        }

        return "redirect:/user/booking-confirmation?id=" + saved.getId();
    }

    private String backToBooking(RedirectAttributes redirectAttributes, Long roomId, String message) {
        redirectAttributes.addFlashAttribute("bookingError", message);
        return "redirect:/booking?room=" + roomId;
    }
}