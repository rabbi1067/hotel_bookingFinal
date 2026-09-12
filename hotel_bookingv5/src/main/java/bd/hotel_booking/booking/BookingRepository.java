package bd.hotel_booking.booking;

import bd.hotel_booking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    long countByPaymentStatus(PaymentStatus paymentStatus);

    /** Any booking whose stay is already over (checkout date has passed) but is still open. */
    @Query("select b from Booking b where b.checkOut < :today and b.status in :openStatuses")
    List<Booking> findPastCheckoutStillOpen(@Param("today") LocalDate today, @Param("openStatuses") List<BookingStatus> openStatuses);
}
