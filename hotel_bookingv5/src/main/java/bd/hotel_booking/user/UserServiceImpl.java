package bd.hotel_booking.user;

import bd.hotel_booking.user.dto.AccountActionResult;
import bd.hotel_booking.user.dto.GuestCreateRequest;
import bd.hotel_booking.user.dto.GuestUpdateRequest;
import bd.hotel_booking.user.dto.RegisterRequest;
import bd.hotel_booking.user.dto.StaffAccountCreateRequest;
import bd.hotel_booking.user.dto.StaffAccountUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return email == null ? null : userRepository.findByEmailIgnoreCase(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findGuests() {
        return userRepository.findByRole(Role.GUEST);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailTaken(String email) {
        return email != null && !email.isBlank() && userRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneTaken(String phone) {
        return phone != null && !phone.isBlank() && userRepository.existsByPhone(phone);
    }

    @Override
    @Transactional
    public User registerGuest(RegisterRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.GUEST)
                .status(UserStatus.ACTIVE)
                .address(request.getAddress() == null ? "" : request.getAddress())
                .gender(request.getGender())
                .avatar("")
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        log.info("New guest registered: email={}", saved.getEmail());
        return saved;
    }

    @Override
    @Transactional
    public AccountActionResult createStaffAccount(StaffAccountCreateRequest request) {
        if (isEmailTaken(request.email())) {
            return AccountActionResult.failure("An account with this email already exists.");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone() == null ? "" : request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(parseStaffRole(request.role()))
                .status(parseStatus(request.status()))
                .address(request.address() == null ? "" : request.address())
                .gender(parseGender(request.gender()))
                .avatar("")
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        log.info("Staff/Admin account created: email={} role={}", saved.getEmail(), saved.getRole());
        return AccountActionResult.success("Account created.", saved);
    }

    @Override
    @Transactional
    public AccountActionResult updateStaffAccount(Long id, StaffAccountUpdateRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return AccountActionResult.failure("Account not found.");
        }
        if (user.getRole() == Role.SUPER_ADMIN) {
            return AccountActionResult.failure("The super admin account cannot be edited here.");
        }
        // name/email format & presence already enforced by @Valid on StaffAccountUpdateRequest.
        if (!user.getEmail().equalsIgnoreCase(request.email()) && isEmailTaken(request.email())) {
            return AccountActionResult.failure("An account with this email already exists.");
        }

        user.setName(request.name());
        user.setEmail(request.email().trim().toLowerCase());
        if (!isBlank(request.phone())) {
            user.setPhone(request.phone());
        }
        if (!isBlank(request.address())) {
            user.setAddress(request.address());
        }
        Gender gender = parseGender(request.gender());
        if (gender != null) {
            user.setGender(gender);
        }
        user.setRole(parseStaffRole(request.role()));
        user.setStatus(parseStatus(request.status()));

        User saved = userRepository.save(user);
        log.info("Staff/Admin account updated: id={} email={}", id, saved.getEmail());
        return AccountActionResult.success("Account updated.", saved);
    }

    @Override
    @Transactional
    public AccountActionResult setStatus(Long id, UserStatus status, Long actingUserId) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return AccountActionResult.failure("Account not found.");
        }
        if (user.getId().equals(actingUserId)) {
            return AccountActionResult.failure("You cannot change your own status.");
        }
        if (user.getRole() == Role.SUPER_ADMIN) {
            return AccountActionResult.failure("The super admin account cannot be changed here.");
        }

        user.setStatus(status);
        User saved = userRepository.save(user);
        log.info("Account status changed: id={} status={}", id, status);
        return AccountActionResult.success("Status updated.", saved);
    }

    @Override
    @Transactional
    public AccountActionResult deleteStaffAccount(Long id, Long actingUserId) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return AccountActionResult.failure("Account not found.");
        }
        if (user.getId().equals(actingUserId)) {
            return AccountActionResult.failure("You cannot delete your own account.");
        }
        if (user.getRole() == Role.SUPER_ADMIN) {
            return AccountActionResult.failure("The super admin account cannot be deleted.");
        }

        passwordResetTokenRepository.deleteAllByUser(user);
        userRepository.delete(user);
        log.info("Account deleted: id={}", id);
        return AccountActionResult.success("Account deleted.", null);
    }
    @Override
    @Transactional
    public AccountActionResult createGuestAccount(GuestCreateRequest request) {
        // name/email/password format & presence already enforced by @Valid on GuestCreateRequest.
        if (isEmailTaken(request.email())) {
            return AccountActionResult.failure("An account with this email already exists.");
        }
        if (isPhoneTaken(request.phone())) {
            return AccountActionResult.failure("An account with this phone number already exists.");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email().trim().toLowerCase())
                .phone(request.phone() == null ? "" : request.phone())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.GUEST)
                .status("inactive".equalsIgnoreCase(request.status()) ? UserStatus.INACTIVE : UserStatus.ACTIVE)
                .address(request.address() == null ? "" : request.address())
                .gender(parseGender(request.gender()))
                .avatar("")
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        log.info("Guest account created by admin: email={}", saved.getEmail());
        return AccountActionResult.success("Guest created.", saved);
    }

    @Override
    @Transactional
    public AccountActionResult updateGuestAccount(Long id, GuestUpdateRequest request) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return AccountActionResult.failure("Account not found.");
        }
        if (user.getRole() != Role.GUEST) {
            return AccountActionResult.failure("Please edit staff and admin accounts from Admin Management.");
        }
        // name/email format & presence already enforced by @Valid on GuestUpdateRequest.
        if (!user.getEmail().equalsIgnoreCase(request.email()) && isEmailTaken(request.email())) {
            return AccountActionResult.failure("An account with this email already exists.");
        }
        if (!isBlank(request.phone()) && !request.phone().equals(user.getPhone()) && isPhoneTaken(request.phone())) {
            return AccountActionResult.failure("An account with this phone number already exists.");
        }

        user.setName(request.name());
        user.setEmail(request.email().trim().toLowerCase());
        if (!isBlank(request.phone())) {
            user.setPhone(request.phone());
        }
        if (!isBlank(request.address())) {
            user.setAddress(request.address());
        }
        Gender gender = parseGender(request.gender());
        if (gender != null) {
            user.setGender(gender);
        }
        user.setStatus(parseStatus(request.status()));

        User saved = userRepository.save(user);
        log.info("Guest account updated by admin: id={} email={}", id, saved.getEmail());
        return AccountActionResult.success("Guest updated.", saved);
    }

    @Override
    @Transactional
    public AccountActionResult blockAccount(Long id, int days, Long actingUserId) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return AccountActionResult.failure("Account not found.");
        }
        if (user.getId().equals(actingUserId)) {
            return AccountActionResult.failure("You cannot block your own account.");
        }
        if (user.getRole() == Role.SUPER_ADMIN) {
            return AccountActionResult.failure("The super admin account cannot be blocked.");
        }
        if (days <= 0) {
            return unblockAccount(id, actingUserId);
        }

        user.setBlockedUntil(java.time.LocalDate.now().plusDays(days));
        User saved = userRepository.save(user);
        log.info("Account blocked: id={} until={}", id, saved.getBlockedUntil());
        return AccountActionResult.success("Account blocked until " + saved.getBlockedUntil() + ".", saved);
    }

    @Override
    @Transactional
    public AccountActionResult unblockAccount(Long id, Long actingUserId) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return AccountActionResult.failure("Account not found.");
        }
        user.setBlockedUntil(null);
        User saved = userRepository.save(user);
        log.info("Account block cleared: id={}", id);
        return AccountActionResult.success("Block removed.", saved);
    }

    /* ---------------- small, private parsing helpers ---------------- */
    /* Kept here (not in the controller) so every caller applies the same rules. */

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Role parseStaffRole(String role) {
        return "STAFF".equalsIgnoreCase(role) ? Role.STAFF : Role.ADMIN;
    }

    private UserStatus parseStatus(String status) {
        if ("blocked".equalsIgnoreCase(status)) return UserStatus.BLOCKED;
        if ("inactive".equalsIgnoreCase(status)) return UserStatus.INACTIVE;
        return UserStatus.ACTIVE;
    }

    private Gender parseGender(String gender) {
        if (isBlank(gender)) return null;
        try {
            return Gender.valueOf(gender.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
