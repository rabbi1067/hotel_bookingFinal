package bd.hotel_booking.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminNotificationController {

    @GetMapping("/admin/notifications")
    public String notifications() {
        return "admin/notifications"; // resolves to templates/admin/notifications.html
    }
}