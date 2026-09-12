package bd.hotel_booking.guest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 80, message = "Name must be between 2 and 80 characters")
        String name,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^(?:\\+?88)?01[3-9]\\d{8}$",
                message = "Please provide a valid Bangladeshi phone number (e.g. 017XXXXXXXX)")
        String phone,

        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address
) {
}