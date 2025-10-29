package com.net.sphuta_tms.mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.sphuta_tms.dto.TemplateInfo;
import com.net.sphuta_tms.dto.EmployeeReminderDto;
import com.net.sphuta_tms.dto.ManagerApprovalDto;
import com.net.sphuta_tms.enums.ReminderType;
import com.net.sphuta_tms.constants.TemplateRegistry;
import com.net.sphuta_tms.service.EmailService;
import com.net.sphuta_tms.util.ReminderPayloadMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmailService} using Mockito.
 *
 * This test class covers both valid and invalid scenarios for sending
 * reminder emails based on reminder numbers and payloads.
 */

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ReminderPayloadMapper payloadMapper;

    @Mock
    private TemplateRegistry templateRegistry;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    // ==================== ✅ VALID TEST CASES ====================

    /*
     * Parameterized test for valid reminder numbers.
     * Verifies that the correct email is sent for each reminder type.
 */
    @ParameterizedTest
    @MethodSource("validReminderScenarios")
    void sendReminderByNumber_ValidReminder_SendsEmailSuccessfully(
            int reminderNumber, ReminderType expectedType, String expectedTemplate) throws Exception {

        Map<String, Object> payload = Map.of(
                "to", "test@example.com",
                "employeeName", "John Doe",
                "weekDate", "2024-01-15",
                "timesheetLink", "https://example.com/timesheet",
                "subject", "Test Subject"
        );

        Object mockDto = createMockDto(reminderNumber);
        String renderedHtml = createHtmlWithSubject("Test Subject");

        // Prepare TemplateInfo that TemplateRegistry should return (3-arg constructor)
        TemplateInfo tinfo = new TemplateInfo(reminderNumber, "Some Template", expectedTemplate);

        when(templateRegistry.getByReminderNumber(reminderNumber)).thenReturn(tinfo);

        // mock the static ReminderType.fromCode(...) to return expectedType and then behaviour
        try (MockedStatic<ReminderType> typeMock = mockStatic(ReminderType.class)) {
            typeMock.when(() -> ReminderType.fromCode(reminderNumber)).thenReturn(expectedType);

            when(payloadMapper.toDto(expectedType, payload)).thenReturn(mockDto);
            when(templateEngine.process(eq(expectedTemplate), any(Context.class))).thenReturn(renderedHtml);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(objectMapper.convertValue(mockDto, Map.class)).thenReturn(payload);

            emailService.sendReminderByNumber(reminderNumber, payload);

            verify(payloadMapper).toDto(expectedType, payload);
            verify(templateEngine).process(eq(expectedTemplate), any(Context.class));
            verify(mailSender).send(mimeMessage);
        }
    }
 /*
    * Test for sending Employee Reminder email.
    * Verifies that the email is sent successfully with valid payload.
  */
    @Test
    void sendReminderByNumber_EmployeeReminder_ValidPayload_SendsEmail() throws Exception {
        int reminderNumber = 1;
        ReminderType expectedType = ReminderType.EMPLOYEE_REMINDER;

        Map<String, Object> payload = Map.of(
                "to", "employee@company.com",
                "employeeName", "John Doe",
                "weekDate", "Jan 15-21, 2024",
                "timesheetLink", "https://sphuta-app.com/timesheet",
                "subject", "Timesheet Reminder"
        );

        // EmployeeReminderDto record has 8 components — supply dummy values for all
        EmployeeReminderDto mockDto = new EmployeeReminderDto(
                "employee@company.com",
                "John Doe",
                "Jan 15-21, 2024",
                "https://sphuta-app.com/timesheet",
                "2024-01-22T17:00",
                "support-number",
                "https://help.example",
                "it-support@example.com"
        );

        String renderedHtml = createHtmlWithSubject("Timesheet Reminder");

        TemplateInfo tinfo = new TemplateInfo(reminderNumber, "Employee Reminder", "emails/employee/timesheet-reminder");

        when(templateRegistry.getByReminderNumber(reminderNumber)).thenReturn(tinfo);

        try (MockedStatic<ReminderType> typeMock = mockStatic(ReminderType.class)) {
            typeMock.when(() -> ReminderType.fromCode(reminderNumber)).thenReturn(expectedType);

            when(payloadMapper.toDto(expectedType, payload)).thenReturn(mockDto);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn(renderedHtml);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(objectMapper.convertValue(mockDto, Map.class)).thenReturn(payload);

            emailService.sendReminderByNumber(reminderNumber, payload);

            verify(mailSender).send(mimeMessage);
        }
    }

    /*
     * Test for sending Manager Reminder email.
     * Verifies that the email is sent successfully with valid payload.
     */
    @Test
    void sendReminderByNumber_ManagerReminder_ValidPayload_SendsEmail() throws Exception {
        int reminderNumber = 4;
        ReminderType expectedType = ReminderType.MANAGER_READY_FOR_APPROVAL;

        Map<String, Object> payload = Map.of(
                "to", "manager@company.com",
                "managerName", "Sarah Wilson",
                "teamName", "Development Team",
                "weekDate", "Jan 15-21, 2024",
                "managerApprovalDeadline", "Jan 22, 2024, 3:00 PM",
                "managerDashboardLink", "https://sphuta-app.com/manager/approvals",
                "subject", "Timesheets Ready for Approval"
        );

        // ManagerApprovalDto record has 7 components — supply dummy values for all
        ManagerApprovalDto mockDto = new ManagerApprovalDto(
                "manager@company.com",
                "Sarah Wilson",
                "Development Team",
                "Jan 15-21, 2024",
                "Jan 22, 2024, 3:00 PM",
                "https://sphuta-app.com/manager/approvals",
                "2024-01-22T15:00"
        );

        String renderedHtml = createHtmlWithSubject("Timesheets Ready for Approval");

        TemplateInfo tinfo = new TemplateInfo(reminderNumber, "Manager Ready", "emails/manager/timesheet-ready-approval");

        when(templateRegistry.getByReminderNumber(reminderNumber)).thenReturn(tinfo);

        try (MockedStatic<ReminderType> typeMock = mockStatic(ReminderType.class)) {
            typeMock.when(() -> ReminderType.fromCode(reminderNumber)).thenReturn(expectedType);

            when(payloadMapper.toDto(expectedType, payload)).thenReturn(mockDto);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn(renderedHtml);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(objectMapper.convertValue(mockDto, Map.class)).thenReturn(payload);

            emailService.sendReminderByNumber(reminderNumber, payload);

            verify(mailSender).send(mimeMessage);
        }
    }
/*
    * Test for sendUsingTemplate method when subject is provided in payload.
    * Verifies that the email is sent with the correct subject.
 */
    @Test
    void sendUsingTemplate_WithSubjectFromPayload_SendsEmailWithSubject() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        Map<String, Object> vars = Map.of("to", "test@example.com", "subject", "Test Email Subject");
        String renderedHtml = createHtmlWithSubject("Ignored Meta Subject");

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(vars);
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
    }

    /*
     * Test for sendUsingTemplate method when no subject is provided in payload.
     * Verifies that the email is sent without a subject.
     */
    @Test
    void sendUsingTemplate_WithoutSubjectInPayload_SendsEmailWithoutSubject() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        Map<String, Object> vars = Map.of("to", "test@example.com");
        String renderedHtml = "<html><body>Email without subject</body></html>";

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(vars);
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
    }
/*
     * Test for sendUsingTemplate method when both meta tag and payload subject are present.
     * Verifies that the payload subject takes precedence.
     */
    @Test
    void sendUsingTemplate_MetaTagInHtml_IsIgnoredWhenSubjectInPayload() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        Map<String, Object> vars = Map.of("to", "test@example.com", "subject", "Payload Subject");
        String renderedHtml = createHtmlWithSubject("Meta Subject");

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(vars);
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
    }
/*
     * Test for sendUsingTemplate method when no meta tag and no payload subject are present.
     * Verifies that the email is sent without a subject.
     */
    @Test
    void sendUsingTemplate_NoMetaAndNoPayloadSubject_SendsWithoutSubject() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        Map<String, Object> vars = Map.of("to", "test@example.com");
        String renderedHtml = "<html><body>No subject meta tag</body></html>";

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(vars);
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
    }

    // ------------------  private method tests (reflection) ------------------
    /*
     * Test for private method extractSubject using reflection.
     * Verifies that the subject is correctly extracted from HTML with meta tag.
     */

    @Test
    void extractSubject_PrivateMethod_WithMetaTag_WithReflection() throws Exception {
        // Arrange
        String htmlWithSubject = createHtmlWithSubject("Reflected Subject");

        Method extractSubjectMethod = EmailService.class.getDeclaredMethod("extractSubject", String.class);
        extractSubjectMethod.setAccessible(true);

        // Act
        String result = (String) extractSubjectMethod.invoke(emailService, htmlWithSubject);

        // Assert
        assertEquals("Reflected Subject", result);
    }
/*
     * Test for private method extractSubject using reflection.
     * Verifies that null is returned when no meta tag is present.
     */
    @Test
    void extractSubject_PrivateMethod_WithoutMetaTag_WithReflection() throws Exception {
        // Arrange
        String htmlWithoutSubject = "<html><body>No subject here</body></html>";

        Method extractSubjectMethod = EmailService.class.getDeclaredMethod("extractSubject", String.class);
        extractSubjectMethod.setAccessible(true);

        // Act
        String result = (String) extractSubjectMethod.invoke(emailService, htmlWithoutSubject);

        // Assert
        assertNull(result);
    }

    // ==================== ❌ INVALID TEST CASES ====================

    /*
     * Test for sendReminderByNumber with invalid reminder number.
     * Verifies that an exception is thrown when the reminder number is not configured.
     */
    @Test
    void sendReminderByNumber_InvalidReminderNumber_ThrowsException() throws MessagingException {
        int invalidReminderNumber = 999;
        Map<String, Object> payload = Map.of("to", "test@example.com");

        when(templateRegistry.getByReminderNumber(invalidReminderNumber)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.sendReminderByNumber(invalidReminderNumber, payload));

        assertTrue(exception.getMessage().contains("Template not configured") || exception.getMessage().contains("No template configured"));
        verify(payloadMapper, never()).toDto(any(ReminderType.class), any());
        verify(templateEngine, never()).process(anyString(), any(Context.class));
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

/*
     * Test for sendReminderByNumber with null payload.
     * Verifies that an exception is thrown when the payload is null.
     */
    @Test
    void sendReminderByNumber_NullPayload_ThrowsException() {
        int reminderNumber = 1;
        TemplateInfo tinfo = new TemplateInfo(1, "Employee Reminder", "emails/employee/timesheet-reminder");

        when(templateRegistry.getByReminderNumber(reminderNumber)).thenReturn(tinfo);
        when(payloadMapper.toDto(any(ReminderType.class), isNull()))
                .thenThrow(new IllegalArgumentException("Payload cannot be null"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.sendReminderByNumber(reminderNumber, null));

        assertTrue(exception.getMessage().contains("Payload cannot be null"));
        verify(templateEngine, never()).process(anyString(), any(Context.class));
    }

    /*
     * Test for sendReminderByNumber with payload conversion failure.
     * Verifies that an exception is thrown when payload to DTO conversion fails.
     */
    @Test
    void sendReminderByNumber_PayloadConversionFails_ThrowsException() {
        int reminderNumber = 1;
        Map<String, Object> invalidPayload = Map.of("invalid", "data");
        TemplateInfo tinfo = new TemplateInfo(1, "Employee Reminder", "emails/employee/timesheet-reminder");

        when(templateRegistry.getByReminderNumber(reminderNumber)).thenReturn(tinfo);
        when(payloadMapper.toDto(any(ReminderType.class), eq(invalidPayload)))
                .thenThrow(new IllegalArgumentException("Invalid payload format"));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> emailService.sendReminderByNumber(reminderNumber, invalidPayload));
        assertTrue(ex.getMessage().contains("Invalid payload format"));

        verify(templateEngine, never()).process(anyString(), any(Context.class));
    }

    /*
     * Test for sendReminderByNumber when template is not found in registry.
     * Verifies that an exception is thrown when no template is configured.
     */
    @Test
    void sendReminderByNumber_TemplateNotFound_ThrowsException() {
        int reminderNumber = 1;
        Map<String, Object> payload = Map.of("to", "test@example.com");

        when(templateRegistry.getByReminderNumber(reminderNumber)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.sendReminderByNumber(reminderNumber, payload));

        assertTrue(exception.getMessage().contains("Template not configured") || exception.getMessage().contains("No template configured"));
        verify(payloadMapper, never()).toDto(any(ReminderType.class), any());
    }

    /*
     * Test for sendUsingTemplate with missing 'to' field in payload.
     * Verifies that an error is logged and email sending is skipped.
     */
    @Test
    void sendUsingTemplate_MissingToField_LogsErrorAndSkipsSend() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        String renderedHtml = createHtmlWithSubject("Test Subject");

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("name", "John Doe")); // No 'to' field
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /*
     * Test for sendUsingTemplate with empty 'to' field in payload.
     * Verifies that an error is logged and email sending is skipped.
     */
    @Test
    void sendUsingTemplate_EmptyToField_LogsErrorAndSkipsSend() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        String renderedHtml = createHtmlWithSubject("Test Subject");

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", ""));
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /*
     * Test for sendUsingTemplate when template rendering fails.
     * Verifies that an exception is thrown and email sending is skipped.
     */
    @Test
    void sendUsingTemplate_TemplateRenderingFails_ThrowsException() throws Exception {
        String templatePath = "emails/test/template";
        Object dto = new Object();

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));

        // Stub templateEngine.process to throw RuntimeException, as service does not catch it
        when(templateEngine.process(eq(templatePath), any(Context.class)))
                .thenThrow(new RuntimeException("Template rendering failed"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendUsingTemplate(templatePath, dto));
        assertTrue(ex.getMessage().contains("Template rendering failed"));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    /*
     * Test for sendUsingTemplate when template rendering returns null.
     * Verifies that an exception is thrown and email sending is skipped.
     */
    @Test
    void sendUsingTemplate_NullHtmlFromTemplate_HandlesGracefully() throws Exception {
        String templatePath = "emails/test/template";
        Object dto = new Object();

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));

        // Stub templateEngine.process returns null
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendUsingTemplate(templatePath, dto));
        assertTrue(ex.getMessage().toLowerCase().contains("null"));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    // ==================== 🔧 HELPER METHODS ====================

    private static Stream<Arguments> validReminderScenarios() {
        return Stream.of(
                Arguments.of(1, ReminderType.EMPLOYEE_REMINDER, "emails/employee/timesheet-reminder"),
                Arguments.of(2, ReminderType.EMPLOYEE_FINAL_REMINDER, "emails/employee/timesheet-final-call"),
                Arguments.of(3, ReminderType.EMPLOYEE_MISSED_DEADLINE, "emails/employee/timesheet-missed-deadline"),
                Arguments.of(4, ReminderType.MANAGER_READY_FOR_APPROVAL, "emails/manager/timesheet-ready-approval"),
                Arguments.of(5, ReminderType.MANAGER_APPROVAL_OVERDUE, "emails/manager/timesheet-approval-overdue"),
                Arguments.of(6, ReminderType.MANAGER_ESCALATION, "emails/manager/timesheet-escalation"),
                Arguments.of(7, ReminderType.ADMIN_ESCALATION, "emails/admin/timesheet-admin-escalation"),
                Arguments.of(8, ReminderType.HR_ESCALATION, "emails/hr/timesheet-hr-escalation")
        );
    }

    private Object createMockDto(int reminderNumber) {
        switch (reminderNumber) {
            case 1:
                return new EmployeeReminderDto(
                        "test@example.com",
                        "John Doe",
                        "2024-01-15",
                        "https://example.com/timesheet",
                        "2024-01-22T17:00",
                        "support-number",
                        "https://help.example",
                        "it-support@example.com"
                );
            case 4:
                return new ManagerApprovalDto(
                        "manager@example.com",
                        "Sarah",
                        "Team",
                        "2024-01-15",
                        "Deadline",
                        "https://example.com/dashboard",
                        "2024-01-22T15:00"
                );
            default:
                return new Object();
        }
    }

    private String createHtmlWithSubject(String subject) {
        return String.format("<html><head><meta name='subject' content='%s'></head><body>Email content</body></html>", subject);
    }
}
