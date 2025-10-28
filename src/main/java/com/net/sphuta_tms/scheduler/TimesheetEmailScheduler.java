package com.net.sphuta_tms.scheduler;

import com.net.sphuta_tms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TimesheetEmailScheduler - example scheduler that triggers service-level jobs.
 * NOTE: scheduler methods should be adapted to produce DTOs / query DB for real recipients.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimesheetEmailScheduler {

    private final EmailService emailService;

    // For testing you can change this cron to "0 */1 * * * *" (every minute)
    @Scheduled(cron = "0 0 0 ? * FRI", zone = "Asia/Kolkata")
    public void weeklyReminder() {
        log.info("Weekly scheduler triggered (placeholder).");
        try {
            // Implement real DB-driven logic in EmailService and call it here, e.g.:
            // emailService.sendWeekEndReminders();
            log.debug("weeklyReminder completed (no-op default).");
        } catch (Exception ex) {
            log.error("Error in weeklyReminder: {}", ex.getMessage(), ex);
        }
    }

    @Scheduled(cron = "0 0 0 15 * ?", zone = "Asia/Kolkata")
    public void monthMidReminder() {
        log.info("Month-mid scheduler triggered (placeholder).");
        try {
            // emailService.sendMonthMidReminders();
            log.debug("monthMidReminder completed (no-op default).");
        } catch (Exception ex) {
            log.error("Error in monthMidReminder: {}", ex.getMessage(), ex);
        }
    }

    @Scheduled(cron = "0 0 0 L * ?", zone = "Asia/Kolkata")
    public void monthEndReminder() {
        log.info("Month-end scheduler triggered (placeholder).");
        try {
            // emailService.sendMonthEndReminders();
            log.debug("monthEndReminder completed (no-op default).");
        } catch (Exception ex) {
            log.error("Error in monthEndReminder: {}", ex.getMessage(), ex);
        }
    }
}
