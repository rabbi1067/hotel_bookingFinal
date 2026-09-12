package bd.hotel_booking.promo.codes;

import bd.hotel_booking.promo.Promotion;
import bd.hotel_booking.promo.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromoExpiryService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromotionRepository promotionRepository;

    @PostConstruct
    public void onStartup() {
        syncPromoCodes();
        syncPromotions();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void scheduledSync() {
        syncPromoCodes();
        syncPromotions();
    }

    public void syncPromoCodes() {
        LocalDate today = LocalDate.now();
        List<PromoCode> codes = promoCodeRepository.findAll();
        for (PromoCode c : codes) {
            if ("expired".equalsIgnoreCase(c.getStatus())) continue;

            boolean pastEnd = c.getEndDate() != null && c.getEndDate().isBefore(today);
            boolean usedUp = c.getUsageLimit() != null && c.getUsageLimit() > 0
                    && c.getUsed() != null && c.getUsed() >= c.getUsageLimit();

            if (pastEnd || usedUp) {
                c.setStatus("expired");
                promoCodeRepository.save(c);
            }
        }
    }

    public void syncPromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> promos = promotionRepository.findAll();
        for (Promotion p : promos) {
            boolean pastEnd = p.getEndDate() != null && p.getEndDate().isBefore(today);

            if (!"expired".equalsIgnoreCase(p.getStatus()) && pastEnd) {
                p.setStatus("expired");
                promotionRepository.save(p);
                continue;
            }

            boolean started = p.getStartDate() == null || !p.getStartDate().isAfter(today);
            if ("upcoming".equalsIgnoreCase(p.getStatus()) && started && !pastEnd) {
                p.setStatus("active");
                promotionRepository.save(p);
            }
        }
    }
}