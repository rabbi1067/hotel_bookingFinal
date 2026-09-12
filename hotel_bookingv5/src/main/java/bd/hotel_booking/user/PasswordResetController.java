package bd.hotel_booking.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "redirect:/login";
    }

    @PostMapping("/api/forgot-password")
    @ResponseBody
    public Map<String, Object> requestOtp(@RequestParam String email) {
        passwordResetService.requestReset(email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("sent", true);
        return body;
    }

    @PostMapping("/api/verify-otp")
    @ResponseBody
    public Map<String, Object> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean valid = passwordResetService.verifyOtp(email, otp);
        Map<String, Object> body = new HashMap<>();
        body.put("valid", valid);
        return body;
    }

    @PostMapping("/api/reset-password")
    @ResponseBody
    public Map<String, Object> resetPassword(@RequestParam String email,
                                             @RequestParam String otp,
                                             @RequestParam String newPassword) {
        PasswordResetService.ResetResult result = passwordResetService.resetPassword(email, otp, newPassword);
        Map<String, Object> body = new HashMap<>();
        body.put("ok", result.ok);
        body.put("message", result.message);
        return body;
    }
}