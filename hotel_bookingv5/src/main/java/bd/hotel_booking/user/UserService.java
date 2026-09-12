package bd.hotel_booking.user;

import bd.hotel_booking.user.dto.AccountActionResult;
import bd.hotel_booking.user.dto.GuestCreateRequest;
import bd.hotel_booking.user.dto.GuestUpdateRequest;
import bd.hotel_booking.user.dto.RegisterRequest;
import bd.hotel_booking.user.dto.StaffAccountCreateRequest;
import bd.hotel_booking.user.dto.StaffAccountUpdateRequest;

import java.util.List;

public interface UserService {

    User findByEmail(String email);

    List<User> findAll();

    List<User> findGuests();

    long count();

    boolean isEmailTaken(String email);

    boolean isPhoneTaken(String phone);

    User registerGuest(RegisterRequest request);

    AccountActionResult createStaffAccount(StaffAccountCreateRequest request);

    AccountActionResult updateStaffAccount(Long id, StaffAccountUpdateRequest request);

    AccountActionResult setStatus(Long id, UserStatus status, Long actingUserId);


    AccountActionResult deleteStaffAccount(Long id, Long actingUserId);

    AccountActionResult createGuestAccount(GuestCreateRequest request);

    AccountActionResult updateGuestAccount(Long id, GuestUpdateRequest request);

    AccountActionResult blockAccount(Long id, int days, Long actingUserId);

    AccountActionResult unblockAccount(Long id, Long actingUserId);
}

