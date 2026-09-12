package bd.hotel_booking.guest;

import bd.hotel_booking.booking.Booking;
import bd.hotel_booking.booking.BookingJsonMapper;
import bd.hotel_booking.booking.BookingService;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import bd.hotel_booking.wifi.WifiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class GuestBookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final WifiService wifiService;

    private User currentUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @GetMapping("/my-bookings")
    public String myBookings(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findForUser(user)));
        return "guest/my-bookings";
    }

    @GetMapping("/money-receipts")
    public String moneyReceipts(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findForUser(user)));
        return "guest/money-receipts";
    }

    @GetMapping("/booking-details")
    public String bookingDetails(@RequestParam("id") Long id, Authentication authentication, Model model) {
        Booking booking = bookingService.findById(id);
        User user = currentUser(authentication);

        boolean isOwner = booking.getUser() != null && booking.getUser().getId().equals(user.getId());
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")
                        || a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (!isOwner && !isStaff) {
            return "redirect:/403";
        }

        model.addAttribute("bookingsForJs", List.of(BookingJsonMapper.toFrontendMap(booking)));
        model.addAttribute("wifiForJs", wifiService.toFrontendMap());
        return "guest/booking-details";
    }

    @GetMapping("/booking-confirmation")
    public String bookingConfirmation(@RequestParam("id") Long id, Model model) {
        Booking booking = bookingService.findById(id);
        model.addAttribute("bookingsForJs", List.of(BookingJsonMapper.toFrontendMap(booking)));
        return "guest/booking-confirmation";
    }
}