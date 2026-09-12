package bd.hotel_booking.wifi;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wifi_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WifiConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String ssid;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(length = 300)
    private String instructions;


    @Column(length = 20)
    @Builder.Default
    private String band = "5 GHz";

    @Column(name = "max_devices_per_room")
    @Builder.Default
    private Integer maxDevicesPerRoom = 6;

    @Column(name = "bandwidth_per_guest_mbps")
    @Builder.Default
    private Integer bandwidthPerGuestMbps = 50;

    @Column(length = 30)
    @Builder.Default
    private String security = "WPA2";

    @Column(name = "captive_portal_enabled")
    @Builder.Default
    private Boolean captivePortalEnabled = true;

    @Column(name = "bandwidth_limit_enabled")
    @Builder.Default
    private Boolean bandwidthLimitEnabled = true;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}