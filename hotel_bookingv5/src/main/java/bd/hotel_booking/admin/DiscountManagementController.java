package bd.hotel_booking.admin;

import bd.hotel_booking.room.RoomJsonMapper;
import bd.hotel_booking.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DiscountManagementController {

    private final RoomService roomService;

    @GetMapping("/discount-management")
    public String discountManagement(Model model) {
        model.addAttribute("roomsForJs", RoomJsonMapper.toFrontendList(roomService.findAll()));
        return "admin/discount-management";
    }
}