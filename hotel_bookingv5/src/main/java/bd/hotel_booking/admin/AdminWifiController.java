package bd.hotel_booking.admin;

import bd.hotel_booking.wifi.WifiConfig;
import bd.hotel_booking.wifi.WifiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminWifiController {

    private final WifiService wifiService;

    @GetMapping("/wifi-management")
    public String wifiManagement(Model model) {
        model.addAttribute("wifi", wifiService.get());
        return "admin/wifi-management";
    }

    @PostMapping("/wifi-management/save")
    public String saveWifi(@RequestParam String ssid,
                           @RequestParam String password,
                           @RequestParam(required = false) String band,
                           @RequestParam(required = false, name = "maxDevicesPerRoom") Integer maxDevicesPerRoom,
                           @RequestParam(required = false, name = "bandwidthPerGuestMbps") Integer bandwidthPerGuestMbps,
                           @RequestParam(required = false) String security,
                           @RequestParam(required = false, name = "captivePortalEnabled", defaultValue = "false") Boolean captivePortalEnabled,
                           @RequestParam(required = false, name = "bandwidthLimitEnabled", defaultValue = "false") Boolean bandwidthLimitEnabled) {

        WifiConfig current = wifiService.get();

        wifiService.update(
                ssid,
                password,
                current.getInstructions(),
                band,
                maxDevicesPerRoom,
                bandwidthPerGuestMbps,
                security,
                captivePortalEnabled,
                bandwidthLimitEnabled
        );
        return "redirect:/admin/wifi-management";
    }
}