package com.net.sphuta_tms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.sphuta_tms.constants.TemplateDef;
import com.net.sphuta_tms.enums.ReminderType;
import com.net.sphuta_tms.util.ReminderPayloadMapper;
import com.net.sphuta_tms.util.ReminderTypeMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ==========================================================
 * {@code EmailService}
 * ==========================================================
 *
 * <p>Service class responsible for sending emails using
 * Thymeleaf templates. Supports sending reminder emails
 * based on numeric reminder codes and dynamic payloads.</p>
 *
 * <p>Features provided:
 * <ul>
 *   <li>Mapping numeric reminder codes to reminder types.</li>
 *   <li>Resolving appropriate email templates for each reminder type.</li>
 *   <li>Converting raw payload maps into strongly-typed DTOs.</li>
 *   <li>Rendering email content using Thymeleaf templates.</li>
 *   <li>Extracting email subjects from template metadata.</li>
 *   <li>Sending HTML emails via JavaMailSender.</li>
 * </ul>
 *
 * <p>This service uses:
 * <ul>
 *   <li>{@link JavaMailSender} for email delivery.</li>
 *   <li>{@link SpringTemplateEngine} for template rendering.</li>
 *   <li>{@link ObjectMapper} for payload to DTO conversion.</li>
 *   <li>{@link ReminderPayloadMapper} for mapping payloads to DTOs.</li>
 * </ul>
 *
 * <p>Logging is provided via SLF4J for tracing and debugging.</p>
 */
@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderPayloadMapper payloadMapper; // new injection

    // existing META pattern (or use Jsoup as you prefer)
    private static final Pattern META_SUBJECT =
            Pattern.compile("<meta\\s+[^>]*name\\s*=\\s*['\"]subject['\"][^>]*content\\s*=\\s*['\"](.*?)['\"][^>]*/?>",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    /**
     * Facade: accept numeric reminder code + raw payload map,
     * convert payload -> dto, resolve template and send email.
     *
     * Throws IllegalArgumentException for bad input (invalid number, missing template, invalid payload)
     * Throws MessagingException for email delivery errors
     */
    public void sendReminderByNumber(int reminderNumber, Map<String, Object> payload) throws MessagingException {
        log.info("EmailService.sendReminderByNumber number={} payloadKeys={}", reminderNumber, payload == null ? "null" : payload.keySet());

        // 1) map number -> type
        ReminderType type = ReminderTypeMapper.getType(reminderNumber);
        if (type == null) {
            log.warn("Unsupported reminder number: {}", reminderNumber);
            throw new IllegalArgumentException("Unsupported reminder number: " + reminderNumber);
        }

        // 2) resolve template
        String template = TemplateDef.templateFor(type);
        if (template == null) {
            log.error("No template mapped for reminder type: {}", type);
            throw new IllegalArgumentException("Template not configured for reminder type: " + type);
        }

        // 3) payload -> DTO conversion using existing mapper
        Object dto;
        try {
            dto = payloadMapper.toDto(type, payload);
        } catch (IllegalArgumentException ex) {
            log.warn("Payload -> DTO conversion failed for reminderNumber={}: {}", reminderNumber, ex.getMessage());
            throw ex;
        }

        // 4) reuse existing rendering & sending
        sendUsingTemplate(template, dto);
        log.info("EmailService: triggered send for reminderNumber={} type={} template={}", reminderNumber, type, template);
    }

    // --- existing sendUsingTemplate / extractSubject / sendHtml methods remain unchanged ---
    public void sendUsingTemplate(String templatePath, Object dto) throws MessagingException {
        log.info("Preparing email using template '{}'", templatePath);

        Map<String, Object> variables = objectMapper.convertValue(dto, Map.class);
        Context ctx = new Context();
        if (variables != null) ctx.setVariables(variables);
        String html = templateEngine.process(templatePath, ctx);
        log.debug("Rendered template '{}' ({} chars)", templatePath, html == null ? 0 : html.length());

        String subject = extractSubject(html);
        if (subject != null && !subject.isBlank()) {
            log.info("Extracted subject: {}", subject);
        } else {
            log.warn("No subject found in template '{}'; sending without subject", templatePath);
            subject = null;
        }

        String cleanedHtml = html == null ? "" : html.replaceAll("(?i)<meta[^>]*?name\\s*=\\s*['\"]subject['\"][^>]*?>", "");
        String to = variables != null && variables.get("to") != null ? variables.get("to").toString() : null;
        if (to == null || to.isBlank()) {
            log.error("Recipient 'to' missing for template '{}'. Aborting send.", templatePath);
            return;
        }

        sendHtml(to, subject, cleanedHtml);
        log.info("Email sent to {} using template '{}'", to, templatePath);
    }

    private String extractSubject(String html) {
        if (html == null) return null;
        Matcher m = META_SUBJECT.matcher(html);
        return m.find() ? m.group(1).trim() : null;
    }

    private void sendHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                msg,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(to);
        if (subject != null && !subject.isBlank()) helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(msg);
    }
}
