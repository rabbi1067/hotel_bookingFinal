package bd.hotel_booking.promo.offers;

import bd.hotel_booking.promo.codes.PromoCodeRepository;
import bd.hotel_booking.promo.PromotionRepository;
import bd.hotel_booking.promo.dto.PromoCodeDTO;
import bd.hotel_booking.promo.dto.PromotionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only, unauthenticated endpoints for the public-facing site
 * (home page, offers page). Only ever exposes active / upcoming,
 * not-yet-expired items — never the full admin dataset.
 */
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicPromoController {

    private final PromotionRepository promotionRepository;
    private final PromoCodeRepository promoCodeRepository;

    @GetMapping("/promotions")
    public List<PromotionDTO> activePromotions() {
        LocalDate today = LocalDate.now();
        return promotionRepository.findAll().stream()
                .filter(p -> "active".equalsIgnoreCase(p.getStatus())
                        || "upcoming".equalsIgnoreCase(p.getStatus()))
                .filter(p -> p.getEndDate() == null || !p.getEndDate().isBefore(today))
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/promo-codes")
    public List<PromoCodeDTO> activePromoCodes() {
        LocalDate today = LocalDate.now();
        return promoCodeRepository.findAll().stream()
                .filter(c -> "active".equalsIgnoreCase(c.getStatus()))
                .filter(c -> c.getEndDate() == null || !c.getEndDate().isBefore(today))
                .filter(c -> c.getUsageLimit() == null || c.getUsageLimit() == 0
                        || (c.getUsed() != null && c.getUsed() < c.getUsageLimit()))
                .map(PromoCodeDTO::fromEntity)
                .collect(Collectors.toList());
    }
}