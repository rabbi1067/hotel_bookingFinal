package bd.hotel_booking.demo;

import bd.hotel_booking.user.Role;

/**
 * The ONLY source of truth for public demo logins.
 * <p>
 * These 4 accounts do NOT exist in Postgres. They are authenticated by
 * {@link DemoAuthenticationProvider} and all their writes are kept in
 * {@link DemoSessionStore} (HttpSession). Logout = session invalidate = auto wipe.
 */
public enum DemoAccount {

    SUPER_ADMIN("demo-superadmin@hotel.com", "Demo123", Role.SUPER_ADMIN, "Demo Super Admin"),
    ADMIN("demo-admin@hotel.com", "Demo123", Role.ADMIN, "Demo Admin"),
    STAFF("demo-staff@hotel.com", "Demo123", Role.STAFF, "Demo Staff"),
    GUEST("demo-guest@hotel.com", "Demo123", Role.GUEST, "Demo Guest");

    private final String email;
    private final String password;
    private final Role role;
    private final String displayName;

    DemoAccount(String email, String password, Role role, String displayName) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    public Role role() {
        return role;
    }

    public String displayName() {
        return displayName;
    }

    /** Fixed negative id so demo users never collide with real Postgres ids. */
    public long demoId() {
        return switch (this) {
            case SUPER_ADMIN -> -101L;
            case ADMIN -> -102L;
            case STAFF -> -103L;
            case GUEST -> -104L;
        };
    }

    public static boolean isDemoEmail(String email) {
        if (email == null) {
            return false;
        }
        String normalized = email.trim().toLowerCase();
        for (DemoAccount account : values()) {
            if (account.email.equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    public static DemoAccount fromEmail(String email) {
        if (email == null) {
            return null;
        }
        String normalized = email.trim().toLowerCase();
        for (DemoAccount account : values()) {
            if (account.email.equals(normalized)) {
                return account;
            }
        }
        return null;
    }
}
