package bd.hotel_booking.admin;

import bd.hotel_booking.room.Room;
import bd.hotel_booking.room.RoomJsonMapper;
import bd.hotel_booking.room.RoomService;
import bd.hotel_booking.room.dto.RoomActionResult;
import bd.hotel_booking.room.dto.RoomFormRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRoomController {

    private final RoomService roomService;

    @GetMapping("/room-management")
    public String roomManagement(Model model) {
        model.addAttribute("roomsForJs", RoomJsonMapper.toFrontendList(roomService.findAll()));
        return "admin/room-management";
    }

    @GetMapping("/room-form")
    public String roomForm(@RequestParam(value = "id", required = false) Long id, Model model) {
        model.addAttribute("roomsForJs", RoomJsonMapper.toFrontendList(roomService.findAll()));
        model.addAttribute("room", id != null ? roomService.findById(id) : new Room());
        return "admin/room-form";
    }

    @PostMapping("/room-form")
    public String saveRoom(@ModelAttribute RoomFormRequest request, RedirectAttributes redirectAttributes) {
        RoomActionResult result = roomService.createOrUpdate(request);
        log.info("Admin {} room id={} -> {}", request.id() != null ? "updated" : "created", request.id(), outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/room-management";
    }

    @PostMapping("/rooms/{id}/delete")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RoomActionResult result = roomService.deleteRoom(id);
        log.info("Delete room id={} -> {}", id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/room-management";
    }

    @PostMapping("/rooms/{id}/duplicate")
    public String duplicateRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RoomActionResult result = roomService.duplicateRoom(id);
        log.info("Duplicate room id={} -> {}", id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/room-management";
    }

    @PostMapping("/rooms/{id}/toggle-availability")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RoomActionResult result = roomService.toggleAvailability(id);
        log.info("Toggle availability room id={} -> {}", id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/room-management";
    }

    @ExceptionHandler(Exception.class)
    public String handleRoomActionError(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("Unhandled error while processing a room action", ex);
        redirectAttributes.addFlashAttribute("roomActionOk", false);
        redirectAttributes.addFlashAttribute("roomActionMsg", "Something went wrong while processing this action. Please try again.");
        return "redirect:/admin/room-management";
    }

    private String outcome(RoomActionResult result) {
        return result.ok() ? "success" : "rejected: " + result.message();
    }

    private void flash(RedirectAttributes redirectAttributes, RoomActionResult result) {
        redirectAttributes.addFlashAttribute("roomActionOk", result.ok());
        redirectAttributes.addFlashAttribute("roomActionMsg", result.message());
    }
}