package bd.hotel_booking.content;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteContentForm {

    private String hotelName;
    private String tagline;

    private String heroTitle;
    private String heroSub;
    private String heroBtn;

    private String aboutTitle;
    private String aboutText;
    private String aboutPoints;

    private String roomsValue;
    private String roomsLabel;

    private String guestsValue;
    private String guestsLabel;

    private String yearsValue;
    private String yearsLabel;

    private String ratingValue;
    private String ratingLabel;

    private String footerAbout;

    private String address;
    private String phone;
    private String email;
    private String supportHours;

    private String latitude;
    private String longitude;

    private String facebookUrl;
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;

    public static SiteContentForm fromEntity(SiteContent content) {
        SiteContentForm form = new SiteContentForm();

        form.setHotelName(content.getHotelName());
        form.setTagline(content.getTagline());

        form.setHeroTitle(content.getHeroTitle());
        form.setHeroSub(content.getHeroSub());
        form.setHeroBtn(content.getHeroBtn());

        form.setAboutTitle(content.getAboutTitle());
        form.setAboutText(content.getAboutText());
        form.setAboutPoints(content.getAboutPoints());

        form.setRoomsValue(content.getRoomsValue());
        form.setRoomsLabel(content.getRoomsLabel());

        form.setGuestsValue(content.getGuestsValue());
        form.setGuestsLabel(content.getGuestsLabel());

        form.setYearsValue(content.getYearsValue());
        form.setYearsLabel(content.getYearsLabel());

        form.setRatingValue(content.getRatingValue());
        form.setRatingLabel(content.getRatingLabel());

        form.setFooterAbout(content.getFooterAbout());

        form.setAddress(content.getAddress());
        form.setPhone(content.getPhone());
        form.setEmail(content.getEmail());
        form.setSupportHours(content.getSupportHours());

        form.setLatitude(content.getLatitude());
        form.setLongitude(content.getLongitude());

        form.setFacebookUrl(content.getFacebookUrl());
        form.setInstagramUrl(content.getInstagramUrl());
        form.setTwitterUrl(content.getTwitterUrl());
        form.setYoutubeUrl(content.getYoutubeUrl());

        return form;
    }

    public void copyTo(SiteContent content) {
        content.setHotelName(clean(hotelName));
        content.setTagline(clean(tagline));

        content.setHeroTitle(clean(heroTitle));
        content.setHeroSub(clean(heroSub));
        content.setHeroBtn(clean(heroBtn));

        content.setAboutTitle(clean(aboutTitle));
        content.setAboutText(clean(aboutText));
        content.setAboutPoints(clean(aboutPoints));

        content.setRoomsValue(clean(roomsValue));
        content.setRoomsLabel(clean(roomsLabel));

        content.setGuestsValue(clean(guestsValue));
        content.setGuestsLabel(clean(guestsLabel));

        content.setYearsValue(clean(yearsValue));
        content.setYearsLabel(clean(yearsLabel));

        content.setRatingValue(clean(ratingValue));
        content.setRatingLabel(clean(ratingLabel));

        content.setFooterAbout(clean(footerAbout));

        content.setAddress(clean(address));
        content.setPhone(clean(phone));
        content.setEmail(clean(email));
        content.setSupportHours(clean(supportHours));

        content.setLatitude(clean(latitude));
        content.setLongitude(clean(longitude));

        content.setFacebookUrl(clean(facebookUrl));
        content.setInstagramUrl(clean(instagramUrl));
        content.setTwitterUrl(clean(twitterUrl));
        content.setYoutubeUrl(clean(youtubeUrl));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
