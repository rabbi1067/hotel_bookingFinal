package bd.hotel_booking.security;

import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ---- public demo accounts: synthetic details, no DB row needed ----
        bd.hotel_booking.demo.DemoAccount demo = bd.hotel_booking.demo.DemoAccount.fromEmail(email);
        if (demo != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(demo.email())
                    .password(passwordEncoder.encode(demo.password()))
                    .disabled(false)
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + demo.role().name())))
                    .build();
        }
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account with email: " + email));

        boolean enabled = user.getStatus() == bd.hotel_booking.user.UserStatus.ACTIVE;

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .disabled(!enabled)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .build();
    }
}
