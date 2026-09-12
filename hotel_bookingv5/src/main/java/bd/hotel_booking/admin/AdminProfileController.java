package bd.hotel_booking.admin;

import bd.hotel_booking.guest.GuestProfileService;
import bd.hotel_booking.guest.dto.PasswordChangeRequest;
import bd.hotel_booking.guest.dto.ProfileUpdateRequest;
import bd.hotel_booking.profile.ProfileControllerSupport;
import bd.hotel_booking.user.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminProfileController {

    private final GuestProfileService profileService;
    private final ProfileControllerSupport support;

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = profileService.getProfile(authentication.getName());
        support.addCommonAttributes(model, user);
        model.addAttribute("profileForm",
                new ProfileUpdateRequest(user.getName(), user.getPhone(), user.getAddress()));
        model.addAttribute("passwordForm", new PasswordChangeRequest("", "", ""));
        return "guest/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileUpdateRequest request,
                                BindingResult result,
                                Authentication authentication,
                                Model model,
                                HttpServletResponse response) {
        User user = profileService.getProfile(authentication.getName());

        if (result.hasErrors()) {
            support.addCommonAttributes(model, user);
            model.addAttribute("passwordForm", new PasswordChangeRequest("", "", ""));
            return "guest/profile";
        }

        User updated = profileService.updateProfile(user.getId(), request);
        support.refreshUserCookie(updated.getId(), updated.getName(), updated.getEmail(),
                updated.getRole().name(), updated.getAvatar(), response);
        return "redirect:/admin/profile?saved=1";
    }

    @PostMapping("/profile/avatar")
    public String updateAvatar(@RequestParam String avatar, Authentication authentication, HttpServletResponse response) {
        User user = profileService.getProfile(authentication.getName());
        User updated = profileService.updateAvatar(user.getId(), avatar);
        support.refreshUserCookie(updated.getId(), updated.getName(), updated.getEmail(),
                updated.getRole().name(), updated.getAvatar(), response);
        return "redirect:/admin/profile?photoSaved=1";
    }

    @PostMapping("/profile/password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeRequest request,
                                 BindingResult result,
                                 Authentication authentication,
                                 Model model) {
        User user = profileService.getProfile(authentication.getName());

        if (result.hasErrors()) {
            support.addCommonAttributes(model, user);
            model.addAttribute("profileForm",
                    new ProfileUpdateRequest(user.getName(), user.getPhone(), user.getAddress()));
            model.addAttribute("openPasswordSection", true);
            return "guest/profile";
        }

        GuestProfileService.PasswordChangeResult changeResult =
                profileService.changePassword(user.getId(), request);

        if (!changeResult.ok) {
            support.addCommonAttributes(model, user);
            model.addAttribute("profileForm",
                    new ProfileUpdateRequest(user.getName(), user.getPhone(), user.getAddress()));
            model.addAttribute("passwordError", changeResult.message);
            model.addAttribute("openPasswordSection", true);
            return "guest/profile";
        }

        return "redirect:/admin/profile?passwordChanged=1";
    }
}