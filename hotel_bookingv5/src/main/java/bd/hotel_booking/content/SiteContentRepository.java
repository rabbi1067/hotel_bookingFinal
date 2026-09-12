package bd.hotel_booking.content;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteContentRepository
        extends JpaRepository<SiteContent, Long> {

    Optional<SiteContent> findFirstByOrderByIdAsc();
}
