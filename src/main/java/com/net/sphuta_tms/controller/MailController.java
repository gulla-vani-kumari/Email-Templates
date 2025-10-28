package com.net.sphuta_tms.controller;

import com.net.sphuta_tms.service.EmailService; // now has facade
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ==========================================================
 * {@code MailController}
 * ==========================================================
 *
 * REST controller for **Email Reminders**.
 *
 * <p><b>Responsibilities:</b></p>
 * - Handle requests to send email reminders. <br>
 * - Delegate email sending logic to {@link EmailService}. <br>
 *
 * <p><b>Design:</b></p>
 * - **Thin Controller** → No business logic, only request/response handling. <br>
 * - **Structured Logging** → Uses SLF4J for observability and debugging. <br>
 */
@Slf4j
@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send/{reminderNumber}")
    public ResponseEntity<String> sendByNumber(@PathVariable int reminderNumber,
                                               @RequestBody Map<String, Object> payload) throws Exception {
        log.info("Received send request for reminderNumber={}, payloadKeys={}", reminderNumber, payload == null ? "null" : payload.keySet());

        // delegate entirely to EmailService facade
        emailService.sendReminderByNumber(reminderNumber, payload);

        return ResponseEntity.ok("Triggered reminder " + reminderNumber);
    }
}
