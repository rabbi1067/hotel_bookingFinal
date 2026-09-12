package bd.hotel_booking.auth;

import bd.hotel_booking.user.UserService;
import bd.hotel_booking.user.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("activePage", "login");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterRequest registerForm,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {


        if (registerForm.getEmail() != null && userService.isEmailTaken(registerForm.getEmail())) {
            result.addError(new FieldError("registerForm", "email",
                    "An account with this email already exists."));
        }
        if (registerForm.getPhone() != null && userService.isPhoneTaken(registerForm.getPhone())) {
            result.addError(new FieldError("registerForm", "phone",
                    "An account with this phone number already exists."));
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.registerGuest(registerForm);

        redirectAttributes.addFlashAttribute("success", "Registration successful! You can now sign in.");
        return "redirect:/login?registered";
    }
}
