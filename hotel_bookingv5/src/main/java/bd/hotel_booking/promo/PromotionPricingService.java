package bd.hotel_booking.promo;

import bd.hotel_booking.promo.codes.PromoExpiryService;
import bd.hotel_booking.room.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionPricingService {

    private final PromotionRepository promotionRepository;
    private final PromoExpiryService promoExpiryService;

    public record EffectiveDiscount(int percentOff, BigDecimal fixedOff) {
    }

    public EffectiveDiscount effectiveDiscount(Room room) {
        promoExpiryService.syncPromotions();

        LocalDate today = LocalDate.now();
        List<Promotion> active = promotionRepository.findByStatus("active");

        int percentOff = room.getDiscount() == null ? 0 : room.getDiscount();
        BigDecimal fixedOff = BigDecimal.ZERO;

        for (Promotion p : active) {
            boolean appliesToThisRoom = p.getRoomId() == null || p.getRoomId().isBlank()
                    || p.getRoomId().equals(String.valueOf(room.getId()));
            if (!appliesToThisRoom) continue;

            boolean started = p.getStartDate() == null || !p.getStartDate().isAfter(today);
            boolean notEnded = p.getEndDate() == null || !p.getEndDate().isBefore(today);
            if (!started || !notEnded) continue;

            if ("fixed".equalsIgnoreCase(p.getType())) {
                fixedOff = fixedOff.add(p.getPercent());
            } else {
                percentOff += p.getPercent() == null ? 0 : p.getPercent().intValue();
            }
        }

        percentOff = Math.min(percentOff, 100);
        return new EffectiveDiscount(percentOff, fixedOff);
    }
}