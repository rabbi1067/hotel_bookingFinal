package bd.hotel_booking.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Authenticates ONLY the 4 demo emails. Returns {@code null} for every real
 * user so {@code CustomAuthenticationProvider} handles them untouched.
 * <p>
 * No DB access here - that is the whole point of the public demo.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DemoAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        String email = authentication.getName().trim().toLowerCase();
        DemoAccount account = DemoAccount.fromEmail(email);
        if (account == null) {
            return null; // not a demo login -> let the real provider handle it
        }

        String presented = String.valueOf(authentication.getCredentials());
        if (!account.password().equals(presented)) {
            log.warn("Failed demo login attempt for email={}", email);
            throw new BadCredentialsException("Invalid email or password.");
        }

        log.info("Demo login: email={} role={}", email, account.role());
        List<SimpleGrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + account.role().name()));
        return new UsernamePasswordAuthenticationToken(account.email(), null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
