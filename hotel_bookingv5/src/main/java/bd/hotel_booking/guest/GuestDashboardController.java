package bd.hotel_booking.guest;

import bd.hotel_booking.booking.BookingJsonMapper;
import bd.hotel_booking.booking.BookingService;
import bd.hotel_booking.promo.PromotionPricingService;
import bd.hotel_booking.room.Room;
import bd.hotel_booking.room.RoomJsonMapper;
import bd.hotel_booking.room.RoomService;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import bd.hotel_booking.wifi.WifiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class GuestDashboardController {

    private final BookingService bookingService;
    private final UserService userService;
    private final RoomService roomService;
    private final WifiService wifiService;
    private final PromotionPricingService promotionPricingService;

    private User currentUser(Authentication authentication) {
        return userService.findByEmail(authentication.getName());
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        model.addAttribute(
                "bookingsForJs",
                BookingJsonMapper.toFrontendList(bookingService.findForUser(user))
        );
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(roomService.findAll(), promotionPricingService)
        );
        model.addAttribute("wifiForJs", wifiService.toFrontendMap());
        return "guest/dashboard";
    }

    @GetMapping("/favorites")
    public String favorites(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(roomService.findAll(), promotionPricingService)
        );
        return "guest/favorites";
    }

    @GetMapping("/search-rooms")
    public String searchRooms(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(roomService.findAll(), promotionPricingService)
        );
        return "guest/search-rooms";
    }
    @GetMapping("/room-details")
    public String guestRoomDetails(
            @RequestParam(value = "id", required = false) Long id,
            Model model
    ) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Room ID is required"
            );
        }

        List<Room> all = roomService.findAll();
        boolean roomExists = all.stream()
                .anyMatch(room -> room.getId().equals(id));

        if (!roomExists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Room not found"
            );
        }

        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(all, promotionPricingService)
        );
        model.addAttribute("selectedRoomId", String.valueOf(id));
        model.addAttribute("detailsSource", "guest");

        return "public/room-details";
    }

    @GetMapping("/wifi")
    public String wifi(Authentication authentication, Model model) {
        User user = currentUser(authentication);
        List<bd.hotel_booking.booking.Booking> myBookings = bookingService.findForUser(user);
        myBookings.forEach(bookingService::autoCompleteIfExpired);
        bd.hotel_booking.booking.Booking mainWifiBooking = myBookings.stream()
                .filter(bookingService::isCurrentlyLive)
                .findFirst()
                .orElse(null);

        model.addAttribute(
                "bookingsForJs",
                BookingJsonMapper.toFrontendList(myBookings)
        );
        model.addAttribute(
                "mainWifiBookingForJs",
                mainWifiBooking == null
                        ? null
                        : BookingJsonMapper.toFrontendList(List.of(mainWifiBooking)).get(0)
        );
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(roomService.findAll(), promotionPricingService)
        );
        model.addAttribute("wifiForJs", wifiService.toFrontendMap());
        return "guest/wifi";
    }

    @GetMapping("/notifications")
    public String notifications() {
        return "guest/notifications";
    }
}