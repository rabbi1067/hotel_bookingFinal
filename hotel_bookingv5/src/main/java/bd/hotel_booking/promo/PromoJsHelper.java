package bd.hotel_booking.promo;

import bd.hotel_booking.promo.codes.PromoCodeRepository;
import bd.hotel_booking.promo.codes.PromoExpiryService;
import bd.hotel_booking.promo.dto.PromotionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PromoJsHelper {

    private final PromotionRepository promotionRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final PromoExpiryService promoExpiryService;

    public List<PromotionDTO> promosForJs() {
        promoExpiryService.syncPromotions();
        LocalDate today = LocalDate.now();
        return promotionRepository.findAll().stream()
                .filter(p -> "active".equalsIgnoreCase(p.getStatus())
                        || "upcoming".equalsIgnoreCase(p.getStatus()))
                .filter(p -> p.getEndDate() == null || !p.getEndDate().isBefore(today))
                .map(PromotionDTO::fromEntity)
                .toList();
    }

    public List<Map<String, Object>> promoCodesForJs() {
        promoExpiryService.syncPromoCodes();
        LocalDate today = LocalDate.now();
        return promoCodeRepository.findAll().stream()
                .filter(c -> "active".equalsIgnoreCase(c.getStatus()))
                .filter(c -> c.getEndDate() == null || !c.getEndDate().isBefore(today))
                .filter(c -> c.getUsageLimit() == null || c.getUsageLimit() == 0
                        || (c.getUsed() != null && c.getUsed() < c.getUsageLimit()))
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("code", c.getCode());
                    m.put("type", c.getType());
                    m.put("value", c.getValue());
                    m.put("minAmount", c.getMinAmount());
                    m.put("end", c.getEndDate());
                    m.put("status", c.getStatus());
                    m.put("desc", c.getDescription());
                    return m;
                })
                .toList();
    }
}