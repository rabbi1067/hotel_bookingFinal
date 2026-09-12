package bd.hotel_booking.security;

import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import bd.hotel_booking.user.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = String.valueOf(authentication.getName());
        String password = String.valueOf(authentication.getCredentials());

        User user = userService.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Failed login attempt for email={}", email);
            throw new BadCredentialsException("Invalid email or password.");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            log.warn("Login rejected - account blocked: email={}", email);
            throw new DisabledException("This account has been blocked by an administrator.");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            log.warn("Login rejected - account inactive: email={}", email);
            throw new DisabledException("This account has been deactivated. Please contact support.");
        }
        if (user.getBlockedUntil() != null && !user.getBlockedUntil().isBefore(java.time.LocalDate.now())) {
            log.warn("Login rejected - account temporarily blocked until {}: email={}", user.getBlockedUntil(), email);
            throw new DisabledException(
                    "This account is temporarily blocked until " + user.getBlockedUntil() + ". Please contact support.");
        }

        log.info("Successful login: email={} role={}", email, user.getRole());

        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}