package bd.hotel_booking.promo;

import bd.hotel_booking.promo.codes.PromoExpiryService;
import bd.hotel_booking.promo.dto.PromotionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionRepository promotionRepository;
    private final PromoExpiryService promoExpiryService;

    @GetMapping
    public List<PromotionDTO> list() {
        promoExpiryService.syncPromotions();
        return promotionRepository.findAll()
                .stream()
                .map(PromotionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PromotionDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Offer title is required."));
        }

        Promotion entity = dto.toEntity(new Promotion());
        if (entity.getStartDate() == null) entity.setStartDate(LocalDate.now());
        if (entity.getEndDate() == null) entity.setEndDate(LocalDate.now().plusDays(30));
        if (entity.getStatus() == null || entity.getStatus().isBlank()) entity.setStatus("active");

        Promotion saved = promotionRepository.save(entity);
        return ResponseEntity.ok(PromotionDTO.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromotionDTO dto) {
        Promotion existing = promotionRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Offer title is required."));
        }

        Promotion saved = promotionRepository.save(dto.toEntity(existing));
        return ResponseEntity.ok(PromotionDTO.fromEntity(saved));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        Promotion existing = promotionRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if ("expired".equalsIgnoreCase(existing.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message", "This discount has expired. Edit it and set a new end date to reactivate."));
        }
        existing.setStatus("active".equalsIgnoreCase(existing.getStatus()) ? "paused" : "active");
        Promotion saved = promotionRepository.save(existing);
        return ResponseEntity.ok(PromotionDTO.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!promotionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        promotionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }
}