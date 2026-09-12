package bd.hotel_booking.profile;

import bd.hotel_booking.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Component
public class ProfileControllerSupport {

    public void addCommonAttributes(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("memberSinceLabel", user.getCreatedAt() == null ? ""
                : user.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
    }

    public void refreshUserCookie(Long id, String name, String email, String role, String avatar, HttpServletResponse response) {
        String payload = "{\"id\":" + id
                + ",\"name\":\"" + name.replace("\\", "\\\\").replace("\"", "\\\"")
                + "\",\"email\":\"" + email.replace("\\", "\\\\").replace("\"", "\\\"")
                + "\",\"role\":\"" + role
                + "\",\"image\":\"" + (avatar == null ? "" : avatar.replace("\\", "\\\\").replace("\"", "\\\""))
                + "\"}";
        Cookie cookie = new Cookie("hv_user", URLEncoder.encode(payload, StandardCharsets.UTF_8));
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
    }
}