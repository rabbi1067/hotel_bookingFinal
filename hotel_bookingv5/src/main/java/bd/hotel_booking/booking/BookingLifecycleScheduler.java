package bd.hotel_booking.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Keeps booking lifecycle state honest without relying on staff to always
 * remember to click "Check-out". Runs hourly (and once at startup, in case
 * the app was down when a checkout date passed) - see
 * BookingService#autoCompletePastCheckouts for what actually happens.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingLifecycleScheduler {

    private final BookingService bookingService;

    @Scheduled(initialDelay = 30_000, fixedRate = 60 * 60 * 1000) // 30s after startup, then hourly
    public void completePastCheckouts() {
        int completed = bookingService.autoCompletePastCheckouts();
        if (completed > 0) {
            log.info("Auto-completed {} booking(s) whose checkout date had passed.", completed);
        }
    }
}
