package bd.hotel_booking.config;

import bd.hotel_booking.user.Role;
import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserRepository;
import bd.hotel_booking.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        createSuperAdmin();
    }

    private void createSuperAdmin() {
        String email = System.getenv().getOrDefault("SUPER_ADMIN_EMAIL", "superadmin@hotel.com");
        String password = System.getenv().getOrDefault("SUPER_ADMIN_PASSWORD", "Super123");

        if (userRepository.existsByEmailIgnoreCase(email)) {
            log.info("Super Admin already exists ({}) - skipping creation.", email);
            return;
        }

        User superAdmin = User.builder()
                .name("System Super Admin")
                .email(email.toLowerCase())
                .phone("+8801712665544")
                .password(passwordEncoder.encode(password))
                .role(Role.SUPER_ADMIN)
                .status(UserStatus.ACTIVE)
                .address("Level 6, Grand Meridian, Cox's Bazar")
                .avatar("")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(superAdmin);
        log.info("Super Admin created with email: {}", email);
    }
}
