package bd.hotel_booking.security;

import bd.hotel_booking.user.User;
import bd.hotel_booking.user.UserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request,
                                        jakarta.servlet.http.HttpServletResponse response,
                                        Authentication authentication) throws java.io.IOException {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        String role = (user != null && user.getRole() != null) ? user.getRole().name() : "GUEST";
        String name = (user != null && user.getName() != null) ? user.getName() : email;
        String avatar = (user != null && user.getAvatar() != null) ? user.getAvatar() : "";

        String payload = "{\"id\":" + (user != null ? user.getId() : 0)
                + ",\"name\":\"" + escapeJson(name)
                + "\",\"email\":\"" + escapeJson(email)
                + "\",\"role\":\"" + role
                + "\",\"image\":\"" + escapeJson(avatar) + "\"}";;

        Cookie cookie = new Cookie("hv_user", URLEncoder.encode(payload, StandardCharsets.UTF_8));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);

        String target = switch (role) {
            case "SUPER_ADMIN", "ADMIN", "STAFF" -> "/admin/dashboard";
            default -> "/user/dashboard";
        };
        response.sendRedirect(target);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
