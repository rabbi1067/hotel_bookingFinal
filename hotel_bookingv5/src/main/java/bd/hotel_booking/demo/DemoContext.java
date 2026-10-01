package bd.hotel_booking.demo;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Small static helper used by the demo package AND by the 3 minimal guards
 * in the existing codebase. No business logic lives here.
 */
public final class DemoContext {

    private DemoContext() {
    }

    /** True when the currently authenticated principal is one of the 4 demo accounts. */
    public static boolean isDemo() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return isDemo(auth);
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isDemo(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        Object principal = auth.getPrincipal();
        if ("anonymousUser".equals(principal)) {
            return false;
        }
        return DemoAccount.isDemoEmail(auth.getName());
    }

    public static boolean isDemoEmail(String email) {
        return DemoAccount.isDemoEmail(email);
    }
}
