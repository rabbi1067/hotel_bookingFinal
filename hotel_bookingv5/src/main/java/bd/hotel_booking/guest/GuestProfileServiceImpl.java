package bd.hotel_booking.guest;

import bd.hotel_booking.food.cloudinary.CloudinaryService;
import bd.hotel_booking.guest.dto.PasswordChangeRequest;
import bd.hotel_booking.guest.dto.ProfileUpdateRequest;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuestProfileServiceImpl implements GuestProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService ;

    @Override
    @Transactional(readOnly = true)
    public User getProfile(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: email=" + email));
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: id=" + userId));

        user.setName(request.name());
        user.setPhone(request.phone());
        user.setAddress(request.address() == null ? "" : request.address());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateAvatar(Long userId, String avatarDataUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: id=" + userId));
        String secureUrl = cloudinaryService.uploadBase64(avatarDataUrl);
        user.setAvatar(secureUrl);
        return userRepository.save(user);
    }
    @Override
    @Transactional
    public PasswordChangeResult changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: id=" + userId));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            log.info("Password change failed (wrong current password) userId={}", userId);
            return PasswordChangeResult.failure("Current password is incorrect.");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            return PasswordChangeResult.failure("New passwords do not match.");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            return PasswordChangeResult.failure("New password must be different from the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed successfully userId={}", userId);
        return PasswordChangeResult.success();
    }
}