package bd.hotel_booking.booking;

import bd.hotel_booking.promo.PromotionPricingService;
import bd.hotel_booking.room.Room;
import bd.hotel_booking.room.RoomService;
import bd.hotel_booking.room.RoomStatus;
import bd.hotel_booking.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final PromotionPricingService promotionPricingService;

    private static final int CHECKOUT_HOUR = 0;
    private static final int CHECKOUT_MINUTE = 1;

    public List<Booking> findAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Booking> findForUser(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: id=" + id));
    }

    public Booking findOwnedByUser(Long bookingId, bd.hotel_booking.user.User user) {
        Booking booking = findById(bookingId);
        boolean isOwner = booking.getUser() != null && booking.getUser().getId().equals(user.getId());
        if (!isOwner) {
            throw new org.springframework.security.access.AccessDeniedException("This booking does not belong to you.");
        }
        return booking;
    }

    public boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(b -> b.getRoom().getId().equals(room.getId()))
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED && b.getStatus() != BookingStatus.COMPLETED)
                .noneMatch(b -> !checkIn.isAfter(b.getCheckOut()) && !b.getCheckIn().isAfter(checkOut));
    }

    public BigDecimal calculateRoomTotal(Room room, LocalDate checkIn, LocalDate checkOut) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal nightly = room.getPrice();

        // Combines the room's own discount with any currently-active promotions
        // (room-specific or "all rooms") - see PromotionPricingService. Nothing
        // here is stored back on the Room, so this always reflects today's
        // live state, including promotions created/paused after the room was.
        PromotionPricingService.EffectiveDiscount discount = promotionPricingService.effectiveDiscount(room);
        if (discount.percentOff() > 0) {
            BigDecimal discountMultiplier = BigDecimal.valueOf(100 - discount.percentOff())
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            nightly = nightly.multiply(discountMultiplier);
        }

        BigDecimal total = nightly.multiply(BigDecimal.valueOf(nights)).setScale(2, RoundingMode.HALF_UP);
        if (discount.fixedOff() != null && discount.fixedOff().compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(discount.fixedOff()).max(BigDecimal.ZERO);
        }
        return total;
    }

    public Booking create(Booking booking) {
        booking.setCreatedAt(LocalDateTime.now());
        if (booking.getStatus() == null) booking.setStatus(BookingStatus.PENDING);
        if (booking.getPaymentStatus() == null) booking.setPaymentStatus(PaymentStatus.UNPAID);
        if (booking.getChildren() == null) booking.setChildren(0);
        if (booking.getWithPet() == null) booking.setWithPet(false);
        Booking saved = bookingRepository.save(booking);
        log.info("Booking created: id={} room={} guest={} checkIn={} checkOut={} total={}",
                saved.getId(), saved.getRoom().getId(), saved.getGuestEmail(),
                saved.getCheckIn(), saved.getCheckOut(), saved.getTotalAmount());
        Room room = saved.getRoom();
        if (room != null && room.getStatus() == RoomStatus.AVAILABLE) {
            room.setStatus(RoomStatus.RESERVED);
            roomService.save(room);
        }
        return saved;
    }

    public Booking save(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking updateStatus(Long id, BookingStatus status) {
        Booking booking = findById(id);
        log.info("Booking {} status changed: {} -> {}", id, booking.getStatus(), status);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    public Booking markPaid(Long id) {
        Booking booking = findById(id);
        log.info("Booking {} marked as paid (amount={})", id, booking.getTotalAmount());
        booking.setPaymentStatus(PaymentStatus.PAID);
        return bookingRepository.save(booking);
    }

    public Booking cancel(Long id) {
        Booking booking = findById(id);
        log.info("Booking {} cancelled (was status={})", id, booking.getStatus());
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus(PaymentStatus.UNPAID);
        Booking saved = bookingRepository.save(booking);
        Room room = saved.getRoom();
        if (room != null && room.getStatus() == RoomStatus.RESERVED) {
            room.setStatus(RoomStatus.AVAILABLE);
            roomService.save(room);
        }
        return saved;
    }


    public long count() {
        return bookingRepository.count();
    }

    public long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    public long countUnpaid() {
        return bookingRepository.countByPaymentStatus(PaymentStatus.UNPAID);
    }

    public BigDecimal totalRevenue() {
        return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.PAID)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int autoCompletePastCheckouts() {
        List<Booking> stale = bookingRepository.findPastCheckoutStillOpen(
                LocalDate.now(), List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.ACTIVE));

        for (Booking booking : stale) {
            log.info("Auto-completing booking {} (checkout {} has passed, was status={})",
                    booking.getId(), booking.getCheckOut(), booking.getStatus());
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            Room room = booking.getRoom();
            if (room != null && room.getStatus() == RoomStatus.OCCUPIED) {
                room.setStatus(RoomStatus.CLEANING);
                roomService.save(room);
            } else if (room != null && room.getStatus() == RoomStatus.RESERVED) {
                room.setStatus(RoomStatus.AVAILABLE);
                roomService.save(room);
            }
        }
        return stale.size();
    }

    public boolean isCurrentlyLive(Booking booking) {
        if (booking == null || booking.getStatus() != BookingStatus.ACTIVE) {
            return false;
        }
        LocalDateTime checkoutDeadline = booking.getCheckOut().atTime(CHECKOUT_HOUR, CHECKOUT_MINUTE);
        return LocalDateTime.now().isBefore(checkoutDeadline);
    }
    public void autoCompleteIfExpired(Booking booking) {
        if (booking == null || booking.getStatus() != BookingStatus.ACTIVE) {
            return;
        }
        LocalDateTime checkoutDeadline = booking.getCheckOut().atTime(CHECKOUT_HOUR, CHECKOUT_MINUTE);
        if (LocalDateTime.now().isAfter(checkoutDeadline)) {
            log.info("Auto-completing booking {} on read (checkout deadline {} has passed)",
                    booking.getId(), checkoutDeadline);
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            Room room = booking.getRoom();
            if (room != null && room.getStatus() == RoomStatus.OCCUPIED) {
                room.setStatus(RoomStatus.CLEANING);
                roomService.save(room);
            }
        }
    }
}