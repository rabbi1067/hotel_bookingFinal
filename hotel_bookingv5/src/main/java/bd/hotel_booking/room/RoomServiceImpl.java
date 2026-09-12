package bd.hotel_booking.room;

import bd.hotel_booking.room.dto.RoomActionResult;
import bd.hotel_booking.room.dto.RoomFormRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> findFeatured() {
        return roomRepository.findByFeaturedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Room findById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: id=" + id));
    }

    @Override
    @Transactional
    public Room save(Room room) {
        if (room.getCreatedAt() == null) {
            room.setCreatedAt(LocalDateTime.now());
        }
        if (room.getAmenities() == null) room.setAmenities(new ArrayList<>());
        if (room.getImages() == null) room.setImages(new ArrayList<>());

        return roomRepository.saveAndFlush(room);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return roomRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status).size();
    }

    @Override
    @Transactional
    public RoomActionResult createOrUpdate(RoomFormRequest request) {
        Room room = request.id() != null ? findById(request.id()) : new Room();

        boolean numberChanged = !request.number().equalsIgnoreCase(room.getNumber() == null ? "" : room.getNumber());
        if (numberChanged && roomRepository.existsByNumber(request.number())) {
            return RoomActionResult.failure("Room number \"" + request.number() + "\" is already in use by another room.");
        }

        room.setNumber(request.number());
        room.setName(request.name());
        room.setType(request.type());
        room.setDescription(request.description());
        room.setPrice(request.price());
        room.setDiscount(request.discount() == null ? 0 : request.discount());
        room.setCapacity(request.capacity());
        room.setBeds(request.beds());
        room.setSize(request.size());
        room.setFloor(request.floor());
        room.setView(request.view());
        room.setRating(request.rating() == null ? 4.5 : request.rating());
        room.setReviews(request.reviews() == null ? 0 : request.reviews());
        room.setFeatured(Boolean.TRUE.equals(request.featured()));
        room.setPetFriendly(Boolean.TRUE.equals(request.petFriendly()));
        room.setAmenities(request.amenities() == null ? new ArrayList<>() : request.amenities());
        room.setImages(request.images() == null ? new ArrayList<>() : request.images());
        room.setStatus(request.status() == null ? RoomStatus.AVAILABLE : request.status());

        try {
            Room saved = save(room);
            log.info("Room {}: id={} number={}", request.id() != null ? "updated" : "created", saved.getId(), saved.getNumber());
            return RoomActionResult.success(request.id() != null ? "Room updated." : "Room added.", saved);
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to save room: {}", ex.getMessage());
            return RoomActionResult.failure("Could not save this room - please check the form and try again.");
        }
    }

    @Override
    @Transactional
    public RoomActionResult deleteRoom(Long id) {
        try {
            roomRepository.deleteById(id);
            roomRepository.flush();
            log.info("Room deleted: id={}", id);
            return RoomActionResult.success("Room deleted.", null);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Refused to delete room {}: it still has booking history.", id);
            return RoomActionResult.failure("This room can't be deleted because it has existing bookings. Set it to Maintenance instead.");
        } catch (EmptyResultDataAccessException ex) {
            log.warn("Attempted to delete room {} but it no longer exists.", id);
            return RoomActionResult.failure("This room was already deleted.");
        }
    }

    @Override
    @Transactional
    public RoomActionResult duplicateRoom(Long id) {
        Room source;
        try {
            source = findById(id);
        } catch (IllegalArgumentException ex) {
            return RoomActionResult.failure("Room not found.");
        }

        Room copy = Room.builder()
                .number(generateDuplicateNumber(source.getNumber()))
                .name(source.getName() + " (copy)")
                .type(source.getType())
                .description(source.getDescription())
                .price(source.getPrice())
                .discount(source.getDiscount())
                .capacity(source.getCapacity())
                .beds(source.getBeds())
                .size(source.getSize())
                .floor(source.getFloor())
                .view(source.getView())
                .rating(source.getRating())
                .reviews(source.getReviews())
                .featured(false)
                .petFriendly(source.getPetFriendly())
                .amenities(new ArrayList<>(source.getAmenities()))
                .images(new ArrayList<>(source.getImages()))
                .status(RoomStatus.MAINTENANCE)
                .build();

        try {
            Room saved = save(copy);
            log.info("Room duplicated: source id={} -> new id={} number={}", id, saved.getId(), saved.getNumber());
            return RoomActionResult.success("Room duplicated as " + saved.getNumber() + ".", saved);
        } catch (RuntimeException ex) {
            log.error("Failed to duplicate room {}: {}", id, ex.getMessage());
            return RoomActionResult.failure("Could not duplicate this room. Please try again.");
        }
    }

    @Override
    @Transactional
    public RoomActionResult toggleAvailability(Long id) {
        try {
            Room room = findById(id);
            room.setStatus(room.getStatus() == RoomStatus.AVAILABLE ? RoomStatus.MAINTENANCE : RoomStatus.AVAILABLE);
            Room saved = save(room);
            return RoomActionResult.success("Availability updated.", saved);
        } catch (RuntimeException ex) {
            log.error("Failed to toggle availability for room {}: {}", id, ex.getMessage());
            return RoomActionResult.failure("Could not update this room's availability.");
        }
    }

    private String generateDuplicateNumber(String baseNumber) {
        for (int i = 1; i <= 999; i++) {
            String suffix = i == 1 ? "-C" : "-C" + i;
            int maxBaseLen = 10 - suffix.length();
            String base = baseNumber.length() > maxBaseLen ? baseNumber.substring(0, maxBaseLen) : baseNumber;
            String candidate = base + suffix;
            if (!roomRepository.existsByNumber(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Could not generate a unique room number for the duplicate.");
    }
}