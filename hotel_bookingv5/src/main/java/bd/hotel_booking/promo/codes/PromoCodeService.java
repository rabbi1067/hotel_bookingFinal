package bd.hotel_booking.promo.codes;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoExpiryService promoExpiryService;

    public record Result(boolean ok, String message, PromoCode promo, BigDecimal discount) {
        public static Result fail(String message) {
            return new Result(false, message, null, BigDecimal.ZERO);
        }
        public static Result ok(PromoCode promo, BigDecimal discount, String message) {
            return new Result(true, message, promo, discount);
        }
    }

    public Result validate(String rawCode, BigDecimal subtotal) {
        if (rawCode == null || rawCode.isBlank()) {
            return Result.fail("Enter a code to check it.");
        }

        promoExpiryService.syncPromoCodes();

        PromoCode p = promoCodeRepository.findByCodeIgnoreCase(rawCode.trim()).orElse(null);
        if (p == null) {
            return Result.fail("Invalid promo code.");
        }

        LocalDate today = LocalDate.now();
        if (p.getStartDate() != null && p.getStartDate().isAfter(today)) {
            return Result.fail("This promo code is not valid yet.");
        }
        if (p.getEndDate() != null && p.getEndDate().isBefore(today)) {
            return Result.fail("This promo code has expired.");
        }
        if ("expired".equalsIgnoreCase(p.getStatus())) {
            return Result.fail("This promo code has expired.");
        }
        if ("paused".equalsIgnoreCase(p.getStatus())) {
            return Result.fail("This promo code is not currently active.");
        }
        if (p.getUsageLimit() != null && p.getUsageLimit() > 0
                && p.getUsed() != null && p.getUsed() >= p.getUsageLimit()) {
            return Result.fail("This promo code has reached its usage limit.");
        }

        BigDecimal sub = subtotal == null ? BigDecimal.ZERO : subtotal;
        if (p.getMinAmount() != null && sub.compareTo(p.getMinAmount()) < 0) {
            return Result.fail("Minimum spend of " + p.getMinAmount() + " required for this code.");
        }

        BigDecimal discount = computeDiscount(p, sub);
        return Result.ok(p, discount, "Promo applied: " + p.getCode());
    }

    public BigDecimal computeDiscount(PromoCode p, BigDecimal subtotal) {
        BigDecimal sub = subtotal == null ? BigDecimal.ZERO : subtotal;
        BigDecimal discount;
        if ("fixed".equalsIgnoreCase(p.getType())) {
            discount = p.getValue();
        } else {
            discount = sub.multiply(p.getValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return discount.min(sub).max(BigDecimal.ZERO);
    }

    public Result redeem(String rawCode, BigDecimal subtotal) {
        Result r = validate(rawCode, subtotal);
        if (!r.ok()) return r;

        PromoCode p = r.promo();
        p.setUsed((p.getUsed() == null ? 0 : p.getUsed()) + 1);
        if (p.getUsageLimit() != null && p.getUsageLimit() > 0 && p.getUsed() >= p.getUsageLimit()) {
            p.setStatus("expired");
        }
        promoCodeRepository.save(p);
        return r;
    }
}