package bd.hotel_booking.booking;

import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/bookings")
public class GuestBookingActionsController {

    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping("/{id}/cancel")
    public String guestCancel(@PathVariable Long id, Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(authentication.getName());
        Booking booking = bookingService.findOwnedByUser(id, currentUser);

        // Self-service cancellation only makes sense before the stay starts.
        // Once checked in (ACTIVE), already finished (COMPLETED), or already
        // CANCELLED, only staff can make further changes (Admin Management
        // still has full override via the same BookingService.cancel()).
        if (booking.getStatus() == BookingStatus.ACTIVE
                || booking.getStatus() == BookingStatus.COMPLETED
                || booking.getStatus() == BookingStatus.CANCELLED) {
            redirectAttributes.addFlashAttribute("bookingActionOk", false);
            redirectAttributes.addFlashAttribute("bookingActionMsg",
                    booking.getStatus() == BookingStatus.CANCELLED
                            ? "This booking is already cancelled."
                            : "This booking can no longer be self-cancelled - please contact the front desk.");
            return "redirect:/user/booking-details?id=" + id;
        }

        bookingService.cancel(id);
        redirectAttributes.addFlashAttribute("bookingActionOk", true);
        redirectAttributes.addFlashAttribute("bookingActionMsg", "Booking cancelled.");
        return "redirect:/user/my-bookings";
    }

    /**
     * Real invoice download for "Download invoice" on the booking details
     * page - built from the actual booking row (see InvoiceHtmlBuilder),
     * not a placeholder. Ownership-checked the same way cancel is, so a
     * guest can only ever download their own invoice.
     */
    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> invoice(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByEmail(authentication.getName());
        Booking booking = bookingService.findOwnedByUser(id, currentUser);
        byte[] body = InvoiceHtmlBuilder.build(booking).getBytes(StandardCharsets.UTF_8);
        String filename = "invoice-" + booking.getId() + ".html";
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(body);
    }

    /**
     * Lightweight status feed the guest dashboard polls every ~20s to
     * auto-show the WiFi welcome popup the moment front desk checks a
     * guest's booking in (status flips to ACTIVE) - see
     * App.startBookingStatusWatch in core.js. Scoped to the logged-in
     * guest's own bookings only, so this naturally supports any number of
     * guests being checked in at the same time, each independently.
     */
    @GetMapping("/live-status")
    @ResponseBody
    public List<Map<String, Object>> liveStatus(Authentication authentication) {
        User currentUser = userService.findByEmail(authentication.getName());
        return bookingService.findForUser(currentUser).stream()
                .map(b -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", b.getId());
                    row.put("status", b.getStatus().name().toLowerCase());
                    row.put("roomId", b.getRoom() != null ? b.getRoom().getId() : null);
                    row.put("roomName", b.getRoom() != null ? b.getRoom().getName() : null);
                    return row;
                })
                .collect(Collectors.toList());
    }
}
