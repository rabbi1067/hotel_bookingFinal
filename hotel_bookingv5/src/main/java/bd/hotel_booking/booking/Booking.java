package bd.hotel_booking.booking;

import bd.hotel_booking.room.Room;
import bd.hotel_booking.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A reservation of a {@link Room} for a date range, made either by a
 * logged-in {@link User} (guest) or directly with contact details for
 * walk-in / not-yet-registered bookings.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** Null when the person booked without an account. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false, length = 80)
    private String guestName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, length = 120)
    private String guestEmail;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, length = 20)
    private String guestPhone;

    @NotNull(message = "Check-in date is required")
    @Column(nullable = false)
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Column(nullable = false)
    private LocalDate checkOut;

    @NotNull
    @Min(value = 1, message = "At least 1 adult is required")
    @Column(nullable = false)
    private Integer adults;

    @Min(value = 0)
    @Column(nullable = false)
    private Integer children;

    @Column(nullable = false)
    private Boolean withPet;

    @Size(max = 1000)
    @Column(length = 1000)
    private String specialRequests;

    /** Snapshot of the room price at booking time, so later price changes don't rewrite history. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal roomTotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /** Code the guest applied at booking time (uppercased), null if none was used. */
    @Column(length = 30)
    private String promoCode;

    /** Amount actually deducted by the promo, snapshotted so later edits to the promo don't rewrite history. */
    @Column(precision = 10, scale = 2)
    private BigDecimal promoDiscount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    public long getNights() {
        if (checkIn == null || checkOut == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }
}