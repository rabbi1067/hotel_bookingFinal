package bd.hotel_booking.user;

import bd.hotel_booking.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int OTP_VALID_MINUTES = 10;
    private final SecureRandom secureRandom = new SecureRandom();

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /** Always behaves the same whether or not the email exists — no user enumeration. */
    @Transactional
    public void requestReset(String email) {
        if (email == null || email.isBlank()) return;

        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            log.info("OTP requested for unknown email={}", email);
            return;
        }

        User user = userOpt.get();
        tokenRepository.deleteAllByUser(user);

        String otp = generateOtp();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        tokenRepository.save(resetToken);

        emailService.sendOtpEmail(user.getEmail(), user.getName(), otp, OTP_VALID_MINUTES);
        log.info("OTP issued for email={} (valid {} min)", email, OTP_VALID_MINUTES);
    }

    private String generateOtp() {
        int number = 100000 + secureRandom.nextInt(900000); // always 6 digits
        return String.valueOf(number);
    }

    @Transactional(readOnly = true)
    public boolean verifyOtp(String email, String otp) {
        if (email == null || otp == null) return false;
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) return false;

        return tokenRepository.findByUserAndToken(userOpt.get(), otp.trim())
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public static class ResetResult {
        public final boolean ok;
        public final String message;
        private ResetResult(boolean ok, String message) { this.ok = ok; this.message = message; }
    }

    @Transactional
    public ResetResult resetPassword(String email, String otp, String newPassword) {
        Optional<User> userOpt = email == null ? Optional.empty() : userRepository.findByEmailIgnoreCase(email.trim());
        if (userOpt.isEmpty()) {
            return new ResetResult(false, "Invalid request.");
        }
        User user = userOpt.get();

        Optional<PasswordResetToken> tokenOpt = tokenRepository
                .findByUserAndToken(user, otp == null ? "" : otp.trim())
                .filter(t -> !t.isUsed())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()));

        if (tokenOpt.isEmpty()) {
            log.warn("Password reset attempted with invalid/expired OTP for email={}", email);
            return new ResetResult(false, "This code is invalid or has expired. Please request a new one.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            return new ResetResult(false, "Password must be at least 6 characters.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        PasswordResetToken resetToken = tokenOpt.get();
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        log.info("Password reset completed for email={}", user.getEmail());
        return new ResetResult(true, "Password updated. You can now sign in.");
    }
}