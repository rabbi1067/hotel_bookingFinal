package bd.hotel_booking.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/content-management")
public class SiteContentController {

    private final SiteContentService contentService;

    @GetMapping
    public String showForm(Model model) {
        SiteContent content = contentService.getContent();

        model.addAttribute(
                "contentForm",
                SiteContentForm.fromEntity(content)
        );

        return "admin/content-management";
    }

    @PostMapping
    public String save(
            @ModelAttribute("contentForm") SiteContentForm form
    ) {
        contentService.update(form);

        return "redirect:/admin/content-management?success";
    }
}
