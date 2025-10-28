package com.net.sphuta_tms.scheduler;

import com.net.sphuta_tms.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TimesheetEmailScheduler {

    @Autowired
    private EmailService emailService;

    private Map<String, Object> buildPayload(String recipient) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", recipient);
        payload.put("name", "Team Member");
        payload.put("message", "Automated Reminder from Scheduler");
        return payload;
    }

    // Runs every day at 10 AM and sends reminder on last working day of month
    @Scheduled(cron = "0 0 10 * * *")
    public void sendMonthEndReminder() {
        LocalDate today = LocalDate.now();
        YearMonth ym = YearMonth.now();
        LocalDate lastDay = ym.atEndOfMonth();

        DayOfWeek dow = lastDay.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY) lastDay = lastDay.minusDays(1);
        else if (dow == DayOfWeek.SUNDAY) lastDay = lastDay.minusDays(2);

        if (today.equals(lastDay)) {
            log.info("Running Month End Reminder for date: {}", today);
            triggerReminder(1, "accounts@example.com");
        }
    }

    // Runs every Friday at 5 PM
    @Scheduled(cron = "0 0 17 * * FRI")
    public void sendWeekEndReminder() {
        log.info("Running End-of-Week Reminder (Friday)");
        triggerReminder(2, "team@example.com");
    }

    // Runs on 15th day of every month at 10 AM
    @Scheduled(cron = "0 0 10 15 * ?")
    public void sendMidMonthReminder() {
        log.info("Running Mid-Month Reminder for 15th");
        triggerReminder(3, "hr@example.com");
    }

    // Runs every minute at second 0
    @Scheduled(cron = "0 0 0 * * *")
    public void sendEveryMinuteReminder() {
        log.info("Running Every Minute Reminder");
        triggerReminder(1, "test@example.com"); // use a test reminder number
    }

    private void triggerReminder(int reminderNumber, String toMail) {
        try {
            Map<String, Object> payload = buildPayload(toMail);
            emailService.sendReminderByNumber(reminderNumber, payload);
        } catch (MessagingException e) {
            log.error("Failed to send reminder {}: {}", reminderNumber, e.getMessage(), e);
        }
    }
}
