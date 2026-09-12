package bd.hotel_booking.admin;

import bd.hotel_booking.booking.BookingJsonMapper;
import bd.hotel_booking.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping("/booking-management")
    public String bookingManagement(Model model) {
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findAll()));
        return "admin/booking-management";
    }

    @GetMapping("/check-ins")
    public String checkIns(Model model) {
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findAll()));
        return "admin/check-ins";
    }

    @GetMapping("/check-outs")
    public String checkOuts(Model model) {
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findAll()));
        return "admin/check-outs";
    }

    @GetMapping("/payment-management")
    public String paymentManagement(Model model) {
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findAll()));
        return "admin/payment-management";
    }
}
