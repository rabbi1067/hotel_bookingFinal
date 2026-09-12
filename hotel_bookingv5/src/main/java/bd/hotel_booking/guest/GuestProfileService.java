package bd.hotel_booking.guest;

import bd.hotel_booking.guest.dto.PasswordChangeRequest;
import bd.hotel_booking.guest.dto.ProfileUpdateRequest;
import bd.hotel_booking.user.User;

public interface GuestProfileService {

    User getProfile(String email);

    User updateProfile(Long userId, ProfileUpdateRequest request);

    User updateAvatar(Long userId, String avatarDataUrl);

    PasswordChangeResult changePassword(Long userId, PasswordChangeRequest request);

    class PasswordChangeResult {
        public final boolean ok;
        public final String message;

        private PasswordChangeResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        public static PasswordChangeResult success() {
            return new PasswordChangeResult(true, "Password updated successfully.");
        }

        public static PasswordChangeResult failure(String message) {
            return new PasswordChangeResult(false, message);
        }
    }
}