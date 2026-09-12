package bd.hotel_booking.admin;

import bd.hotel_booking.booking.BookingJsonMapper;
import bd.hotel_booking.booking.BookingService;
import bd.hotel_booking.user.*;
import bd.hotel_booking.user.dto.AccountActionResult;
import bd.hotel_booking.user.dto.GuestCreateRequest;
import bd.hotel_booking.user.dto.GuestUpdateRequest;
import bd.hotel_booking.user.dto.StaffAccountCreateRequest;
import bd.hotel_booking.user.dto.StaffAccountUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminUserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @GetMapping("/user-management")
    public String userManagement(Model model) {
        model.addAttribute("usersForJs", UserJsonMapper.toFrontendList(userService.findGuests()));
        model.addAttribute("bookingsForJs", BookingJsonMapper.toFrontendList(bookingService.findAll()));
        return "admin/user-management";
    }

    @GetMapping("/admin-management")
    public String adminManagement(Model model) {
        model.addAttribute("usersForJs", UserJsonMapper.toFrontendList(userService.findAll()));
        return "admin/admin-management";
    }

    @PostMapping("/admin-management/create")
    public String createAdminOrStaff(@Valid @ModelAttribute StaffAccountCreateRequest request,
                                      BindingResult bindingResult,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            flashValidationError(redirectAttributes, bindingResult);
            return "redirect:/admin/admin-management";
        }
        AccountActionResult result = userService.createStaffAccount(request);
        log.info("Admin {} attempted to create an account for {} -> {}",
                authentication.getName(), request.email(), outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/admin-management";
    }

    @PostMapping("/admin-management/{id}/update")
    public String updateAdminOrStaff(@PathVariable Long id,
                                      @Valid @ModelAttribute StaffAccountUpdateRequest request,
                                      BindingResult bindingResult,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            flashValidationError(redirectAttributes, bindingResult);
            return "redirect:/admin/admin-management";
        }
        AccountActionResult result = userService.updateStaffAccount(id, request);
        log.info("Admin {} updated account id={} -> {}", authentication.getName(), id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/admin-management";
    }

    @PostMapping("/admin-management/{id}/status")
    public String setAdminStatus(@PathVariable Long id,
                                  @RequestParam String status,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        UserStatus parsedStatus = "blocked".equalsIgnoreCase(status) ? UserStatus.BLOCKED : UserStatus.ACTIVE;
        AccountActionResult result = userService.setStatus(id, parsedStatus, acting.getId());
        log.info("Admin {} set account id={} status to {} -> {}",
                authentication.getName(), id, parsedStatus, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/admin-management";
    }

    @PostMapping("/admin-management/{id}/delete")
    public String deleteAdminOrStaff(@PathVariable Long id,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        AccountActionResult result = userService.deleteStaffAccount(id, acting.getId());
        log.info("Admin {} deleted account id={} -> {}", authentication.getName(), id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/admin-management";
    }

    @PostMapping("/admin-management/{id}/block")
    public String blockAdminOrStaff(@PathVariable Long id,
                                     @RequestParam int days,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        AccountActionResult result = userService.blockAccount(id, days, acting.getId());
        log.info("Admin {} blocked account id={} for {} day(s) -> {}",
                authentication.getName(), id, days, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/admin-management";
    }

    @PostMapping("/admin-management/{id}/unblock")
    public String unblockAdminOrStaff(@PathVariable Long id,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        AccountActionResult result = userService.unblockAccount(id, acting.getId());
        log.info("Admin {} unblocked account id={} -> {}", authentication.getName(), id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/admin-management";
    }

    @PostMapping("/user-management/create")
    public String createGuestUser(@Valid @ModelAttribute GuestCreateRequest request,
                                   BindingResult bindingResult,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            flashValidationError(redirectAttributes, bindingResult);
            return "redirect:/admin/user-management";
        }
        AccountActionResult result = userService.createGuestAccount(request);
        log.info("Admin {} attempted to create a guest account for {} -> {}",
                authentication.getName(), request.email(), outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/user-management";
    }

    @PostMapping("/user-management/{id}/update")
    public String updateGuestUser(@PathVariable Long id,
                                   @Valid @ModelAttribute GuestUpdateRequest request,
                                   BindingResult bindingResult,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            flashValidationError(redirectAttributes, bindingResult);
            return "redirect:/admin/user-management";
        }
        AccountActionResult result = userService.updateGuestAccount(id, request);
        log.info("Admin {} updated guest account id={} -> {}", authentication.getName(), id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/user-management";
    }


    @PostMapping("/user-management/{id}/delete")
    public String deleteGuestUser(@PathVariable Long id,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        AccountActionResult result = userService.deleteStaffAccount(id, acting.getId());
        log.info("Admin {} deleted account id={} from User Management -> {}",
                authentication.getName(), id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/user-management";
    }

    @PostMapping("/user-management/{id}/block")
    public String blockGuestUser(@PathVariable Long id,
                                  @RequestParam int days,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        AccountActionResult result = userService.blockAccount(id, days, acting.getId());
        log.info("Admin {} blocked guest id={} for {} day(s) -> {}",
                authentication.getName(), id, days, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/user-management";
    }

    @PostMapping("/user-management/{id}/unblock")
    public String unblockGuestUser(@PathVariable Long id,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        User acting = userService.findByEmail(authentication.getName());
        AccountActionResult result = userService.unblockAccount(id, acting.getId());
        log.info("Admin {} unblocked guest id={} -> {}", authentication.getName(), id, outcome(result));
        flash(redirectAttributes, result);
        return "redirect:/admin/user-management";
    }

    private String outcome(AccountActionResult result) {
        return result.ok() ? "success" : "rejected: " + result.message();
    }

    private void flashValidationError(RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        String message = bindingResult.getFieldErrors().stream()
                .findFirst()
                .map(org.springframework.validation.FieldError::getDefaultMessage)
                .orElse("Please check the form and try again.");
        redirectAttributes.addFlashAttribute("adminActionOk", false);
        redirectAttributes.addFlashAttribute("adminActionMsg", message);
    }

    private void flash(RedirectAttributes redirectAttributes, AccountActionResult result) {
        redirectAttributes.addFlashAttribute("adminActionOk", result.ok());
        redirectAttributes.addFlashAttribute("adminActionMsg", result.message());
    }
}
