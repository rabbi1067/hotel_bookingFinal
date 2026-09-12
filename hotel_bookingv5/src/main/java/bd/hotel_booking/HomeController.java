package bd.hotel_booking;

import bd.hotel_booking.content.SiteContentService;
import bd.hotel_booking.gallery.GalleryService;
import bd.hotel_booking.promo.PromoJsHelper;
import bd.hotel_booking.room.Room;
import bd.hotel_booking.room.RoomJsonMapper;
import bd.hotel_booking.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RoomService roomService;
    private final GalleryService galleryService;
    private final PromoJsHelper promoJsHelper;
    private final SiteContentService contentService;

    @GetMapping("/" )
    public String home(Model model) {
        addHomeData(model);
        return "home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        addHomeData(model);
        return "home";
    }

    private void addHomeData(Model model) {
        /*
         * প্রতিবার Home page load হলে database থেকে latest rooms আসবে।
         */
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        model.addAttribute(
                "galleryForJs",
                galleryService.toFrontendList(
                        galleryService.findAll()
                )
        );

        model.addAttribute(
                "promosForJs",
                promoJsHelper.promosForJs()
        );

        model.addAttribute(
                "promoCodesForJs",
                promoJsHelper.promoCodesForJs()
        );

        model.addAttribute(
                "siteContent",
                contentService.getContent()
        );
    }

    @GetMapping("/api/public/rooms")
    public ResponseEntity<?> publicRooms() {
        return ResponseEntity.ok()
                .cacheControl(
                        CacheControl.noStore()
                                .noCache()
                                .mustRevalidate()
                                .cachePrivate()
                                .sMaxAge(0, TimeUnit.SECONDS)
                )
                .body(
                        RoomJsonMapper.toFrontendList(
                                roomService.findAll()
                        )
                );
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute(
                "siteContent",
                contentService.getContent()
        );

        return "public/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute(
                "siteContent",
                contentService.getContent()
        );

        return "public/contact";
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        return "public/rooms";
    }

    @GetMapping("/room-details")
    public String roomDetails(
            @RequestParam(value = "id", required = false) Long id,
            Model model
    ) {
        if (id == null) {
            return "redirect:/404";
        }

        List<Room> rooms = roomService.findAll();

        boolean exists = rooms.stream()
                .anyMatch(room -> room.getId().equals(id));

        if (!exists) {
            return "redirect:/404";
        }

        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(rooms)
        );

        model.addAttribute(
                "selectedRoomId",
                String.valueOf(id)
        );

        model.addAttribute("detailsSource", "home");

        model.addAttribute(
                "promosForJs",
                promoJsHelper.promosForJs()
        );

        model.addAttribute(
                "promoCodesForJs",
                promoJsHelper.promoCodesForJs()
        );

        return "public/room-details";
    }

    @GetMapping("/offers")
    public String offers(Model model) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        model.addAttribute(
                "promosForJs",
                promoJsHelper.promosForJs()
        );

        model.addAttribute(
                "promoCodesForJs",
                promoJsHelper.promoCodesForJs()
        );

        return "public/offers";
    }

    @GetMapping("/booking")
    public String booking(
            @RequestParam(value = "room", required = false) Long roomId,
            Model model
    ) {
        model.addAttribute(
                "roomsForJs",
                RoomJsonMapper.toFrontendList(
                        roomService.findAll()
                )
        );

        return "public/booking";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "public/403";
    }

    @GetMapping("/404")
    public String notFound() {
        return "public/404";
    }
}
