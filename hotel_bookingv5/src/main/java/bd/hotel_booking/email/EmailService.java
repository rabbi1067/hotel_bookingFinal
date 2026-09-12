package bd.hotel_booking.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String name, String otp, int validMinutes) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your Grand Meridian Resort password reset code");
            message.setText(
                    "Hi " + (name == null || name.isBlank() ? "there" : name) + ",\n\n"
                            + "Your password reset code is: " + otp + "\n\n"
                            + "This code expires in " + validMinutes + " minutes. "
                            + "If you didn't request this, you can safely ignore this email.\n\n"
                            + "- Grand Meridian Resort"
            );
            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}: {}", toEmail, ex.getMessage());
        }
    }
}