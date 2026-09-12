package bd.hotel_booking.guest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record PasswordChangeRequest(

        @NotBlank(message = "Enter your current password")
        String currentPassword,

        @NotBlank(message = "Enter a new password")
        @Size(min = 6, message = "New password must be at least 6 characters")
        String newPassword,

        @NotBlank(message = "Confirm your new password")
        String confirmPassword
) {
}