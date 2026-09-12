package bd.hotel_booking.user.dto;

import bd.hotel_booking.user.User;

public record AccountActionResult(boolean ok, String message, User user) {

    public static AccountActionResult success(String message, User user) {
        return new AccountActionResult(true, message, user);
    }

    public static AccountActionResult failure(String message) {
        return new AccountActionResult(false, message, null);
    }
}
