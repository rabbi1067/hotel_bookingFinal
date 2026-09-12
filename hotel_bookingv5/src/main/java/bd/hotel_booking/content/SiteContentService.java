package bd.hotel_booking.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SiteContentService {

    private final SiteContentRepository repository;

    @Transactional
    public SiteContent getContent() {
        return repository.findFirstByOrderByIdAsc()
                .orElseGet(() -> repository.save(createDefaultContent()));
    }

    @Transactional
    public SiteContent update(SiteContentForm form) {
        SiteContent content = repository.findFirstByOrderByIdAsc()
                .orElseGet(this::createDefaultContent);

        form.copyTo(content);
        return repository.save(content);
    }

    private SiteContent createDefaultContent() {
        SiteContent content = new SiteContent();

        content.setHotelName("Grand Meridian Resort");
        content.setTagline("Resort & Spa");

        content.setHeroTitle("A stay worth remembering");
        content.setHeroSub("Quiet luxury beside the bay.");
        content.setHeroBtn("Explore rooms");

        content.setAboutTitle("A destination in itself");
        content.setAboutText(
                "Grand Meridian Resort welcomes travellers to a quiet stretch of shoreline."
        );
        content.setAboutPoints(
                "Effortless stays\n"
                        + "Locally rooted\n"
                        + "Quiet luxury\n"
                        + "Wellbeing"
        );

        content.setRoomsValue("96+");
        content.setRoomsLabel("Rooms");

        content.setGuestsValue("60k+");
        content.setGuestsLabel("Guests welcomed");

        content.setYearsValue("10");
        content.setYearsLabel("Years of hospitality");

        content.setRatingValue("4.9");
        content.setRatingLabel("Guest rating");

        content.setFooterAbout(
                "A calm, considered stay beside the bay."
        );

        content.setAddress("Cox's Bazar, Bangladesh");
        content.setPhone("+880 1XXX XXXXXX");
        content.setEmail("hello@grandmeridian.com");
        content.setSupportHours("Open daily, 24 hours");

        content.setLatitude("");
        content.setLongitude("");

        content.setFacebookUrl("");
        content.setInstagramUrl("");
        content.setTwitterUrl("");
        content.setYoutubeUrl("");

        return content;
    }
}
