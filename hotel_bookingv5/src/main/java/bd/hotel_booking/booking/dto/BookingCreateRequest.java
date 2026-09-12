package bd.hotel_booking.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * What the public booking wizard (public/booking.html) submits.
 *
 * <p>Carries its own Bean Validation so bad input (blank name, malformed
 * email, missing dates) is rejected by {@code @Valid} in
 * {@code BookingController} with a friendly redirect - before it can ever
 * reach the {@code Booking} entity, which has the same rules enforced again
 * at the database layer. Skipping this step used to let a guest crash the
 * whole request with a raw 500 error simply by leaving the email blank.</p>
 */
public record BookingCreateRequest(

        @NotNull(message = "Please choose a room.")
        Long roomId,

        @NotBlank(message = "Full name is required.")
        @Size(max = 80, message = "Name must be under 80 characters.")
        String guestName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Please provide a valid email address.")
        @Size(max = 120, message = "Email must be under 120 characters.")
        String guestEmail,

        @NotBlank(message = "Phone number is required.")
        @Size(max = 20, message = "Phone number looks too long.")
        String guestPhone,

        @NotNull(message = "Check-in date is required.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,

        @NotNull(message = "Check-out date is required.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,

        @NotNull(message = "Number of adults is required.")
        @Min(value = 1, message = "At least 1 adult is required.")
        Integer adults,

        @Min(value = 0, message = "Number of children can't be negative.")
        Integer children,

        Boolean withPet,

        @Size(max = 1000, message = "Special requests must be under 1000 characters.")
        String specialRequests,

        /** Optional - blank/null means no promo code was applied. Re-validated server-side, never trusted as-is. */
        @Size(max = 30, message = "Promo code looks too long.")
        String promoCode,

        /**
         * "online" if the guest chose "Pay now" in the wizard, anything
         * else (including blank) means "Pay at desk". Only meaningful when
         * the guest is logged in - see BookingController for why.
         */
        String paymentIntent
) {
}