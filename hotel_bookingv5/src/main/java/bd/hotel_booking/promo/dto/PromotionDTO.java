package bd.hotel_booking.promo.dto;

import bd.hotel_booking.promo.Promotion;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PromotionDTO {

    private Long id;
    private String title;
    private String description;
    private String type;
    private BigDecimal value;
    private LocalDate start;
    private LocalDate end;
    private String code;
    private Integer minStay;
    private String status;
    private Boolean featured;
    private String roomId;

    public static PromotionDTO fromEntity(Promotion p) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setType(p.getType() == null || p.getType().isBlank() ? "percent" : p.getType());
        dto.setValue(p.getPercent());
        dto.setStart(p.getStartDate());
        dto.setEnd(p.getEndDate());
        dto.setCode(p.getCode());
        dto.setMinStay(p.getMinStay());
        dto.setStatus(p.getStatus());
        dto.setFeatured(p.getFeatured());
        dto.setRoomId(p.getRoomId());
        return dto;
    }

    public Promotion toEntity(Promotion target) {
        target.setTitle(this.title);
        target.setDescription(this.description);
        target.setType(this.type == null || this.type.isBlank() ? "percent" : this.type);
        target.setPercent(this.value == null ? BigDecimal.ZERO : this.value);
        if (this.start != null) target.setStartDate(this.start);
        if (this.end != null) target.setEndDate(this.end);
        target.setCode(this.code);
        target.setMinStay(this.minStay == null ? 0 : this.minStay);
        if (this.status != null && !this.status.isBlank()) target.setStatus(this.status);
        target.setFeatured(this.featured != null && this.featured);
        target.setRoomId(this.roomId);
        return target;
    }
}