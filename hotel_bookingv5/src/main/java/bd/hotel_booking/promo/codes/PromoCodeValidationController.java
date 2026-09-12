package bd.hotel_booking.promo.codes;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
public class PromoCodeValidationController {

    private final PromoCodeService promoCodeService;

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(
            @RequestParam String code,
            @RequestParam(required = false) BigDecimal subtotal) {

        PromoCodeService.Result r = promoCodeService.validate(code, subtotal);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", r.ok());
        body.put("message", r.message());
        if (r.ok()) {
            body.put("code", r.promo().getCode());
            body.put("type", r.promo().getType());
            body.put("value", r.promo().getValue());
            body.put("discount", r.discount());
        }
        return r.ok() ? ResponseEntity.ok(body) : ResponseEntity.badRequest().body(body);
    }
}