package bd.hotel_booking.security;

import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class StaleAuthCookieFilter extends OncePerRequestFilter {

    private static final String COOKIE_NAME = "hv_user";

    private final UserRepository userRepository;

    public StaleAuthCookieFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        boolean hasCookie = request.getCookies() != null
                && Arrays.stream(request.getCookies()).anyMatch(c -> COOKIE_NAME.equals(c.getName()));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isReallyAuthenticated = auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal());

        if (hasCookie && !isReallyAuthenticated) {
            Cookie stale = new Cookie(COOKIE_NAME, "");
            stale.setPath("/");
            stale.setMaxAge(0);
            stale.setHttpOnly(false);
            response.addCookie(stale);
        } else if (!hasCookie && isReallyAuthenticated) {
            Optional<User> user = userRepository.findByEmailIgnoreCase(auth.getName());
            user.ifPresent(u -> setUserCookie(response, u));
        }

        chain.doFilter(request, response);
    }

    private void setUserCookie(HttpServletResponse response, User user) {
        String payload = "{\"id\":" + user.getId()
                + ",\"name\":\"" + escape(user.getName())
                + "\",\"email\":\"" + escape(user.getEmail())
                + "\",\"role\":\"" + user.getRole().name()
                + "\",\"image\":\"" + escape(user.getAvatar() == null ? "" : user.getAvatar()) + "\"}";
        Cookie cookie = new Cookie(COOKIE_NAME, URLEncoder.encode(payload, StandardCharsets.UTF_8));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
