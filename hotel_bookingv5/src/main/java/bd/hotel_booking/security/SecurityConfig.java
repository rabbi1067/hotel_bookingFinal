package bd.hotel_booking.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role("SUPER_ADMIN").implies("ADMIN")
                .role("ADMIN").implies("STAFF")
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginSuccessHandler loginSuccessHandler,
                                                     CustomUserDetailsService customUserDetailsService,
                                                     bd.hotel_booking.user.UserRepository userRepository) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .addFilterAfter(new StaleAuthCookieFilter(userRepository),
                        org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class)

                .authorizeHttpRequests(request -> request

                        .requestMatchers(
                                "/", "/home", "/about", "/contact",
                                "/rooms", "/room-details", "/offers", "/booking", "/booking/create",
                                "/user/booking-confirmation",
                                "/login", "/register", "/forgot-password",
                                "/api/forgot-password", "/api/verify-otp", "/api/reset-password",
                                "/403", "/404",
                                "/css/**", "/js/**", "/img/**", "/images/**",
                                "/scss/**", "/libs/**",
                                "/login.css", "/register.css", "/booking.css", "/admin.css",
                                "/home_scene.js",
                                "/favicon.ico",
                                "/api/public/**",
                                "/api/promo-codes/**"
                        ).permitAll()

                        .requestMatchers("/admin/settings").authenticated()
                        .requestMatchers("/admin/admin-management", "/admin/admin-management/**")
                        .hasRole("SUPER_ADMIN")
                        .requestMatchers(
                                "/admin/analytics", "/admin/reports",
                                "/admin/promo-management", "/admin/discount-management",
                                "/admin/gallery-management", "/admin/gallery-management/**",
                                "/admin/content-management",
                                "/admin/room-form"
                        ).hasRole("ADMIN")

                        .requestMatchers("/admin/**").hasRole("STAFF")

                        .requestMatchers("/user/**", "/guest/**").authenticated()

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .rememberMe(rm -> rm
                        .key("grand-meridian-remember-me-key-change-in-production")
                        .tokenValiditySeconds(14 * 24 * 60 * 60) 
                        .userDetailsService(customUserDetailsService)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "hv_user", "remember-me")
                        .permitAll()
                )

                .exceptionHandling(handler -> handler
                        .accessDeniedPage("/403")
                );

        return http.build();
    }
}
