package bd.hotel_booking.booking;

import bd.hotel_booking.room.Room;
import bd.hotel_booking.room.RoomService;
import bd.hotel_booking.room.RoomStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/bookings")
public class AdminBookingActionsController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id) {
        bookingService.updateStatus(id, BookingStatus.CONFIRMED);
        return "redirect:/admin/booking-management";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        bookingService.cancel(id);
        return "redirect:/admin/booking-management";
    }

    @PostMapping("/{id}/mark-paid")
    public String markPaid(@PathVariable Long id) {
        bookingService.markPaid(id);
        return "redirect:/admin/booking-management";
    }

    @PostMapping("/{id}/check-in")
    public String checkIn(@PathVariable Long id) {
        Booking booking = bookingService.updateStatus(id, BookingStatus.ACTIVE);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomService.save(room);
        return "redirect:/admin/check-ins";
    }

    @PostMapping("/{id}/check-out")
    public String checkOut(@PathVariable Long id) {
        Booking booking = bookingService.updateStatus(id, BookingStatus.COMPLETED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.CLEANING);
        roomService.save(room);
        return "redirect:/admin/check-outs";
    }

    @PostMapping("/{id}/toggle-payment")
    public String togglePayment(@PathVariable Long id) {
        Booking booking = bookingService.findById(id);
        booking.setPaymentStatus(booking.getPaymentStatus() == PaymentStatus.PAID ? PaymentStatus.UNPAID : PaymentStatus.PAID);
        bookingService.save(booking);
        return "redirect:/admin/payment-management";
    }
}
