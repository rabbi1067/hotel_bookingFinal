package bd.hotel_booking.gallery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;

    public List<GalleryItem> findAll() {
        return galleryRepository.findAll();
    }

    public GalleryItem add(String imageDataUrl, String label, String category) {
        GalleryItem item = GalleryItem.builder()
                .imageUrl(imageDataUrl)
                .label(label)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();
        GalleryItem saved = galleryRepository.save(item);
        log.info("Gallery photo '{}' saved with id {}", label, saved.getId());
        return saved;
    }

    public void deleteById(Long id) {
        galleryRepository.deleteById(id);
        log.info("Gallery photo {} deleted", id);
    }

    public Map<String, Object> toFrontendMap(GalleryItem g) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(g.getId()));
        m.put("img", g.getImageUrl());
        m.put("title", g.getLabel());
        m.put("category", g.getCategory());
        m.put("cat", g.getCategory());
        m.put("featured", false);
        return m;
    }

    public List<Map<String, Object>> toFrontendList(List<GalleryItem> items) {
        return items.stream().map(this::toFrontendMap).collect(Collectors.toList());
    }
}