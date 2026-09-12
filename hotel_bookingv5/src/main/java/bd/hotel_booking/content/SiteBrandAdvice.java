package bd.hotel_booking.content;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class SiteBrandAdvice {
    private final SiteContentService contentService;

    @ModelAttribute("siteBrand")
    public SiteContent siteBrand() {
        return contentService.getContent();
    }
}