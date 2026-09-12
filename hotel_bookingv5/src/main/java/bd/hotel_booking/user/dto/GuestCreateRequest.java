package bd.hotel_booking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GuestCreateRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Please provide a valid email address")
        @Size(max = 120, message = "Email must not exceed 120 characters")
        String email,

        @Pattern(regexp = "^$|^(?:\\+?88)?01[3-9]\\d{8}$",
                message = "Please provide a valid Bangladeshi phone number (e.g. 017XXXXXXXX), or leave it blank")
        String phone,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address,

        String gender,
        String status
) {
}
