package bd.hotel_booking.admin;

import bd.hotel_booking.booking.BookingJsonMapper;
import bd.hotel_booking.booking.BookingService;
import bd.hotel_booking.room.RoomJsonMapper;
import bd.hotel_booking.room.RoomService;
import bd.hotel_booking.user.UserJsonMapper;
import bd.hotel_booking.user.UserService;
import bd.hotel_booking.wifi.WifiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final UserService userService;
    private final WifiService wifiService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        model.addAttribute(
                "bookingsForJs",
                BookingJsonMapper.toFrontendList(
                        bookingService.findAll()
                )
        );

        model.addAttribute(
                "usersForJs",
                UserJsonMapper.toFrontendList(
                        userService.findAll()
                )
        );

        model.addAttribute(
                "wifiForJs",
                wifiService.toFrontendMap()
        );

        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        model.addAttribute(
                "bookingsForJs",
                BookingJsonMapper.toFrontendList(
                        bookingService.findAll()
                )
        );

        model.addAttribute(
                "usersForJs",
                UserJsonMapper.toFrontendList(
                        userService.findAll()
                )
        );

        return "admin/reports";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        model.addAttribute(
                "bookingsForJs",
                BookingJsonMapper.toFrontendList(
                        bookingService.findAll()
                )
        );

        return "admin/analytics";
    }

    @GetMapping("/promo-management")
    public String promoManagement() {
        return "admin/promo-management";
    }



    @GetMapping("/settings")
    public String settings() {
        return "admin/settings";
    }
}
