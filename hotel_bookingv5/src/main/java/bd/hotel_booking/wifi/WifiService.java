package bd.hotel_booking.wifi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WifiService {

    private final WifiConfigRepository wifiConfigRepository;

    public WifiConfig get() {
        List<WifiConfig> all = wifiConfigRepository.findAllByOrderByUpdatedAtDesc();

        if (all.isEmpty()) {
            return wifiConfigRepository.save(
                    WifiConfig.builder()
                            .ssid("HotelWiFi")
                            .password("Welcome123")
                            .instructions("Connect to the network shown and enter the password.")
                            .band("5 GHz")
                            .maxDevicesPerRoom(6)
                            .bandwidthPerGuestMbps(50)
                            .security("WPA2")
                            .captivePortalEnabled(true)
                            .bandwidthLimitEnabled(true)
                            .updatedAt(LocalDateTime.now())
                            .build());
        }

        if (all.size() > 1) {
            List<WifiConfig> stale = all.subList(1, all.size());
            log.warn("Found {} stale WifiConfig row(s) - removing, keeping the most recently updated one (id={}).",
                    stale.size(), all.get(0).getId());
            wifiConfigRepository.deleteAll(stale);
        }

        return all.get(0);
    }

    public WifiConfig update(String ssid, String password, String instructions) {
        WifiConfig config = get();
        return applyUpdate(config, ssid, password, instructions, config.getBand(),
                config.getMaxDevicesPerRoom(), config.getBandwidthPerGuestMbps(),
                config.getSecurity(), config.getCaptivePortalEnabled(), config.getBandwidthLimitEnabled());
    }

    public WifiConfig update(String ssid, String password, String instructions,
                             String band, Integer maxDevicesPerRoom, Integer bandwidthPerGuestMbps,
                             String security, Boolean captivePortalEnabled, Boolean bandwidthLimitEnabled) {
        WifiConfig config = get();
        return applyUpdate(config, ssid, password, instructions, band, maxDevicesPerRoom,
                bandwidthPerGuestMbps, security, captivePortalEnabled, bandwidthLimitEnabled);
    }

    private WifiConfig applyUpdate(WifiConfig config, String ssid, String password, String instructions,
                                   String band, Integer maxDevicesPerRoom, Integer bandwidthPerGuestMbps,
                                   String security, Boolean captivePortalEnabled, Boolean bandwidthLimitEnabled) {
        config.setSsid(ssid);
        config.setPassword(password);
        config.setInstructions(instructions);
        config.setBand(band);
        config.setMaxDevicesPerRoom(maxDevicesPerRoom);
        config.setBandwidthPerGuestMbps(bandwidthPerGuestMbps);
        config.setSecurity(security);
        config.setCaptivePortalEnabled(captivePortalEnabled);
        config.setBandwidthLimitEnabled(bandwidthLimitEnabled);
        config.setUpdatedAt(LocalDateTime.now());
        return wifiConfigRepository.save(config);
    }

    public java.util.Map<String, Object> toFrontendMap() {
        WifiConfig w = get();
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("ssid", w.getSsid());
        m.put("password", w.getPassword());
        m.put("instructions", w.getInstructions());
        return m;
    }
}