package bd.hotel_booking.promo.codes;

import bd.hotel_booking.promo.dto.PromoCodeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/promo-codes")
@RequiredArgsConstructor
public class PromoCodeController {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoExpiryService promoExpiryService;

    @GetMapping
    public List<PromoCodeDTO> list() {
        promoExpiryService.syncPromoCodes();
        return promoCodeRepository.findAll()
                .stream()
                .map(PromoCodeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PromoCodeDTO dto) {
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Promo code is required."));
        }
        String normalizedCode = dto.getCode().trim().toUpperCase();
        if (promoCodeRepository.findByCodeIgnoreCase(normalizedCode).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "That code already exists."));
        }

        PromoCode entity = dto.toEntity(new PromoCode());
        entity.setCode(normalizedCode);
        if (entity.getUsed() == null) entity.setUsed(0);
        if (entity.getStartDate() == null) entity.setStartDate(LocalDate.now());
        if (entity.getStatus() == null || entity.getStatus().isBlank()) entity.setStatus("active");

        PromoCode saved = promoCodeRepository.save(entity);
        return ResponseEntity.ok(PromoCodeDTO.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PromoCodeDTO dto) {
        PromoCode existing = promoCodeRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Promo code is required."));
        }

        String normalizedCode = dto.getCode().trim().toUpperCase();
        boolean codeTakenByOther = promoCodeRepository.findByCodeIgnoreCase(normalizedCode)
                .map(other -> !other.getId().equals(id))
                .orElse(false);
        if (codeTakenByOther) {
            return ResponseEntity.badRequest().body(Map.of("message", "That code already exists."));
        }

        dto.setCode(normalizedCode);
        PromoCode saved = promoCodeRepository.save(dto.toEntity(existing));
        return ResponseEntity.ok(PromoCodeDTO.fromEntity(saved));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        PromoCode existing = promoCodeRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if ("expired".equalsIgnoreCase(existing.getStatus())) {
            return ResponseEntity.badRequest().body(Map.of("message", "This code has expired. Edit it and set a new end date to reactivate."));
        }
        existing.setStatus("active".equalsIgnoreCase(existing.getStatus()) ? "paused" : "active");
        PromoCode saved = promoCodeRepository.save(existing);
        return ResponseEntity.ok(PromoCodeDTO.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!promoCodeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        promoCodeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }
}