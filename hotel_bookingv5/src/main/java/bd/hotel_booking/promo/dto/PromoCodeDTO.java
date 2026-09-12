package bd.hotel_booking.promo.dto;

import bd.hotel_booking.promo.codes.PromoCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PromoCodeDTO {

    private Long id;
    private String code;
    private String type;
    private BigDecimal value;
    private BigDecimal minAmount;
    private Integer usageLimit;
    private Integer used;
    private LocalDate start;
    private LocalDate end;
    private String status;
    private String desc;

    public static PromoCodeDTO fromEntity(PromoCode p) {
        PromoCodeDTO dto = new PromoCodeDTO();
        dto.setId(p.getId());
        dto.setCode(p.getCode());
        dto.setType(p.getType());
        dto.setValue(p.getValue());
        dto.setMinAmount(p.getMinAmount());
        dto.setUsageLimit(p.getUsageLimit());
        dto.setUsed(p.getUsed());
        dto.setStart(p.getStartDate());
        dto.setEnd(p.getEndDate());
        dto.setStatus(p.getStatus());
        dto.setDesc(p.getDescription());
        return dto;
    }

    public PromoCode toEntity(PromoCode target) {
        target.setCode(this.code);
        target.setType(this.type == null || this.type.isBlank() ? "percent" : this.type);
        target.setValue(this.value == null ? BigDecimal.ZERO : this.value);
        target.setMinAmount(this.minAmount == null ? BigDecimal.ZERO : this.minAmount);
        target.setUsageLimit(this.usageLimit == null ? 0 : this.usageLimit);
        if (this.used != null) target.setUsed(this.used);
        if (this.start != null) target.setStartDate(this.start);
        if (this.end != null) target.setEndDate(this.end);
        if (this.status != null && !this.status.isBlank()) target.setStatus(this.status);
        target.setDescription(this.desc);
        return target;
    }
}