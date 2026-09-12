package bd.hotel_booking.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Validates payment details the way a real gateway's client-facing form
 * would, before the booking is actually marked paid. There is no OTP step
 * (by design, matching the simplified checkout flow) and no real money
 * moves - this project has no merchant account with bKash/Nagad/a card
 * processor - but the format checks below are real, not decorative.
 */
@Slf4j
@Service
public class PaymentValidationService {

    // Bangladeshi mobile numbers: 01[3-9]XXXXXXXX (11 digits total)
    private static final Pattern BD_MOBILE = Pattern.compile("^01[3-9]\\d{8}$");
    private static final Pattern PIN = Pattern.compile("^\\d{4,5}$");
    private static final Pattern CARD_NUMBER = Pattern.compile("^\\d{16}$");
    private static final Pattern CVV = Pattern.compile("^\\d{3,4}$");
    private static final Pattern EXPIRY = Pattern.compile("^(0[1-9]|1[0-2])/\\d{2}$");

    public static class ValidationResult {
        public final boolean ok;
        public final String message;

        private ValidationResult(boolean ok, String message) {
            this.ok = ok;
            this.message = message;
        }

        static ValidationResult ok() {
            return new ValidationResult(true, null);
        }

        static ValidationResult fail(String message) {
            log.warn("Payment validation failed: {}", message);
            return new ValidationResult(false, message);
        }
    }

    public ValidationResult validateMobileWallet(String phone, String pin) {
        if (phone == null || !BD_MOBILE.matcher(phone.trim()).matches()) {
            return ValidationResult.fail("Enter a valid Bangladeshi mobile number (e.g. 01712345678).");
        }
        if (pin == null || !PIN.matcher(pin.trim()).matches()) {
            return ValidationResult.fail("PIN must be 4-5 digits.");
        }
        return ValidationResult.ok();
    }

    public ValidationResult validateCard(String cardNumber, String expiry, String cvv, String cardHolder) {
        String digitsOnly = cardNumber == null ? "" : cardNumber.replaceAll("\\s+", "");
        if (!CARD_NUMBER.matcher(digitsOnly).matches()) {
            return ValidationResult.fail("Card number must be 16 digits.");
        }
        if (!luhnCheck(digitsOnly)) {
            return ValidationResult.fail("That card number doesn't look valid - please check it and try again.");
        }
        if (expiry == null || !EXPIRY.matcher(expiry.trim()).matches()) {
            return ValidationResult.fail("Expiry must be in MM/YY format.");
        }
        if (isExpired(expiry.trim())) {
            return ValidationResult.fail("This card has expired.");
        }
        if (cvv == null || !CVV.matcher(cvv.trim()).matches()) {
            return ValidationResult.fail("CVV must be 3 or 4 digits.");
        }
        if (cardHolder == null || cardHolder.trim().length() < 2) {
            return ValidationResult.fail("Enter the name on the card.");
        }
        return ValidationResult.ok();
    }

    /** Standard Luhn checksum - the same algorithm real card networks use to reject typos. */
    private boolean luhnCheck(String digits) {
        int sum = 0;
        boolean alternate = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int n = digits.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    private boolean isExpired(String expiry) {
        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        java.time.YearMonth cardMonth = java.time.YearMonth.of(year, month);
        return cardMonth.isBefore(java.time.YearMonth.now());
    }
}
