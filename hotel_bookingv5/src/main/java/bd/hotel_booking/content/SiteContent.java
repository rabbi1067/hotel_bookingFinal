package bd.hotel_booking.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "site_content")
@Getter
@Setter
@NoArgsConstructor
public class SiteContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String hotelName;

    @Column(length = 255)
    private String tagline;

    @Column(length = 255)
    private String heroTitle;

    @Column(columnDefinition = "TEXT")
    private String heroSub;

    @Column(length = 100)
    private String heroBtn;

    @Column(length = 255)
    private String aboutTitle;

    @Column(columnDefinition = "TEXT")
    private String aboutText;

    @Column(columnDefinition = "TEXT")
    private String aboutPoints;

    @Column(length = 100)
    private String roomsValue;

    @Column(length = 100)
    private String roomsLabel;

    @Column(length = 100)
    private String guestsValue;

    @Column(length = 100)
    private String guestsLabel;

    @Column(length = 100)
    private String yearsValue;

    @Column(length = 100)
    private String yearsLabel;

    @Column(length = 100)
    private String ratingValue;

    @Column(length = 100)
    private String ratingLabel;

    @Column(columnDefinition = "TEXT")
    private String footerAbout;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 150)
    private String supportHours;

    @Column(length = 50)
    private String latitude;

    @Column(length = 50)
    private String longitude;

    @Column(length = 500)
    private String facebookUrl;

    @Column(length = 500)
    private String instagramUrl;

    @Column(length = 500)
    private String twitterUrl;

    @Column(length = 500)
    private String youtubeUrl;
}
