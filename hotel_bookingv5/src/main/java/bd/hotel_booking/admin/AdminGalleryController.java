package bd.hotel_booking.admin;

import bd.hotel_booking.gallery.GalleryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminGalleryController {

    private final GalleryService galleryService;

    @GetMapping("/gallery-management")
    public String galleryManagement(Model model) {
        model.addAttribute("galleryForJs", galleryService.toFrontendList(galleryService.findAll()));
        return "admin/gallery-management";
    }

    @PostMapping("/gallery-management/add")
    public String addGalleryPhoto(@RequestParam("photo") MultipartFile photo,
                                  @RequestParam String label,
                                  @RequestParam String category,
                                  Model model) {

        if (photo.isEmpty()) {
            log.warn("Gallery upload attempted with no photo selected");
            model.addAttribute("galleryForJs", galleryService.toFrontendList(galleryService.findAll()));
            model.addAttribute("error", "Please choose a photo to upload");
            return "admin/gallery-management";
        }

        try {
            String base64 = Base64.getEncoder().encodeToString(photo.getBytes());
            String dataUrl = "data:" + photo.getContentType() + ";base64," + base64;
            galleryService.add(dataUrl, label, category);
        } catch (IOException e) {
            log.error("Could not read uploaded photo", e);
            model.addAttribute("galleryForJs", galleryService.toFrontendList(galleryService.findAll()));
            model.addAttribute("error", "Could not read the uploaded photo");
            return "admin/gallery-management";
        }

        return "redirect:/admin/gallery-management";
    }

    @PostMapping("/gallery-management/{id}/delete")
    public String deleteGalleryPhoto(@PathVariable Long id) {
        galleryService.deleteById(id);
        return "redirect:/admin/gallery-management";
    }
}