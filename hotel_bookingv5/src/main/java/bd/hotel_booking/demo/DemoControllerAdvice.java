package bd.hotel_booking.demo;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Exposes {@code isDemo} / {@code demoRole} / {@code demoEmail} to every
 * Thymeleaf view without touching a single existing controller.
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DemoControllerAdvice {

    @ModelAttribute
    public void exposeDemoFlag(Model model) {
        try {
            boolean demo = DemoContext.isDemo();
            model.addAttribute("isDemo", demo);
            if (demo) {
                var auth = org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();
                if (auth != null) {
                    DemoAccount account = DemoAccount.fromEmail(auth.getName());
                    model.addAttribute("demoEmail", account == null ? auth.getName() : account.email());
                    model.addAttribute("demoRole", account == null ? "" : account.role().name());
                    model.addAttribute("demoName", account == null ? "" : account.displayName());
                }
            }
        } catch (Exception ignored) {
            // advice must never break a real page
        }
    }
}
