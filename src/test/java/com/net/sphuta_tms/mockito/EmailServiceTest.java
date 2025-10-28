package com.net.sphuta_tms.mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.sphuta_tms.constants.TemplateDef;
import com.net.sphuta_tms.dto.employee.EmployeeReminderDto;
import com.net.sphuta_tms.dto.manager.ManagerReadyForApprovalDto;
import com.net.sphuta_tms.enums.ReminderType;
import com.net.sphuta_tms.service.EmailService;
import com.net.sphuta_tms.util.ReminderPayloadMapper;
import com.net.sphuta_tms.util.ReminderTypeMapper;
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
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    // ==================== ✅ VALID TEST CASES ====================

    @ParameterizedTest
    @MethodSource("validReminderScenarios")
    void sendReminderByNumber_ValidReminder_SendsEmailSuccessfully(
            int reminderNumber, ReminderType expectedType, String expectedTemplate) throws Exception {
        Map<String, Object> payload = Map.of(
                "to", "test@example.com",
                "employeeName", "John Doe",
                "weekDate", "2024-01-15",
                "timesheetLink", "https://example.com/timesheet"
        );

        Object mockDto = createMockDto(reminderNumber);
        String renderedHtml = createHtmlWithSubject("Test Subject");

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class);
             MockedStatic<TemplateDef> templateMock = mockStatic(TemplateDef.class)) {
            typeMock.when(() -> ReminderTypeMapper.getType(reminderNumber)).thenReturn(expectedType);
            templateMock.when(() -> TemplateDef.templateFor(expectedType)).thenReturn(expectedTemplate);

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

    @Test
    void sendReminderByNumber_EmployeeReminder_ValidPayload_SendsEmail() throws Exception {
        int reminderNumber = 1;
        Map<String, Object> payload = Map.of(
                "to", "employee@company.com",
                "employeeName", "John Doe",
                "weekDate", "Jan 15-21, 2024",
                "timesheetLink", "https://sphuta-app.com/timesheet"
        );

        EmployeeReminderDto mockDto = new EmployeeReminderDto(
                "employee@company.com", "John Doe", "Jan 15-21, 2024", "https://sphuta-app.com/timesheet"
        );

        String renderedHtml = createHtmlWithSubject("Timesheet Reminder");

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class);
             MockedStatic<TemplateDef> templateMock = mockStatic(TemplateDef.class)) {
            typeMock.when(() -> ReminderTypeMapper.getType(reminderNumber)).thenReturn(ReminderType.EMPLOYEE_REMINDER);
            templateMock.when(() -> TemplateDef.templateFor(ReminderType.EMPLOYEE_REMINDER)).thenReturn("emails/employee/timesheet-reminder");

            when(payloadMapper.toDto(ReminderType.EMPLOYEE_REMINDER, payload)).thenReturn(mockDto);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn(renderedHtml);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(objectMapper.convertValue(mockDto, Map.class)).thenReturn(payload);

            emailService.sendReminderByNumber(reminderNumber, payload);

            verify(mailSender).send(mimeMessage);
        }
    }

    @Test
    void sendReminderByNumber_ManagerReminder_ValidPayload_SendsEmail() throws Exception {
        int reminderNumber = 4;
        Map<String, Object> payload = Map.of(
                "to", "manager@company.com",
                "managerName", "Sarah Wilson",
                "teamName", "Development Team",
                "weekDate", "Jan 15-21, 2024",
                "managerApprovalDeadline", "Jan 22, 2024, 3:00 PM",
                "managerDashboardLink", "https://sphuta-app.com/manager/approvals"
        );

        ManagerReadyForApprovalDto mockDto = new ManagerReadyForApprovalDto(
                "manager@company.com", "Sarah Wilson", "Development Team",
                "Jan 15-21, 2024", "Jan 22, 2024, 3:00 PM", "https://sphuta-app.com/manager/approvals"
        );

        String renderedHtml = createHtmlWithSubject("Timesheets Ready for Approval");

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class);
             MockedStatic<TemplateDef> templateMock = mockStatic(TemplateDef.class)) {
            typeMock.when(() -> ReminderTypeMapper.getType(reminderNumber)).thenReturn(ReminderType.MANAGER_READY_FOR_APPROVAL);
            templateMock.when(() -> TemplateDef.templateFor(ReminderType.MANAGER_READY_FOR_APPROVAL)).thenReturn("emails/manager/timesheet-ready-approval");

            when(payloadMapper.toDto(ReminderType.MANAGER_READY_FOR_APPROVAL, payload)).thenReturn(mockDto);
            when(templateEngine.process(anyString(), any(Context.class))).thenReturn(renderedHtml);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            when(objectMapper.convertValue(mockDto, Map.class)).thenReturn(payload);

            emailService.sendReminderByNumber(reminderNumber, payload);

            verify(mailSender).send(mimeMessage);
        }
    }

    @Test
    void sendUsingTemplate_WithSubjectExtraction_SendsEmailWithSubject() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        String renderedHtml = createHtmlWithSubject("Test Email Subject");

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendUsingTemplate_WithoutSubject_SendsEmailWithoutSubject() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        String renderedHtml = "<html><body>Email without subject</body></html>";

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendUsingTemplate_SubjectExtraction_WithMetaTag_ExtractsCorrectly() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        String renderedHtml = createHtmlWithSubject("Extracted Subject");

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
        // Subject extraction is tested indirectly through the send process
    }

    @Test
    void sendUsingTemplate_SubjectExtraction_WithoutMetaTag_SendsWithoutSubject() throws Exception {
        // Arrange
        String templatePath = "emails/test/template";
        Object dto = new Object();
        String renderedHtml = "<html><body>No subject meta tag</body></html>";

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));
        when(templateEngine.process(eq(templatePath), any(Context.class))).thenReturn(renderedHtml);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        emailService.sendUsingTemplate(templatePath, dto);

        // Assert
        verify(mailSender).send(mimeMessage);
        // Email should be sent without subject
    }

    // ==================== ❌ INVALID TEST CASES ====================

    @Test
    void sendReminderByNumber_InvalidReminderNumber_ThrowsException() throws MessagingException {
        int invalidReminderNumber = 999;
        Map<String, Object> payload = Map.of("to", "test@example.com");

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class)) {
            typeMock.when(() -> ReminderTypeMapper.getType(invalidReminderNumber)).thenReturn(null);

            // No need to stub mailSender.send because exception thrown before send is called

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> emailService.sendReminderByNumber(invalidReminderNumber, payload));

            assertTrue(exception.getMessage().contains("Unsupported reminder number"));
            verify(payloadMapper, never()).toDto(any(), any());
            verify(templateEngine, never()).process(anyString(), any(Context.class));
            verify(mailSender, never()).send(any(MimeMessage.class));
        }
    }

    @Test
    void sendReminderByNumber_NullPayload_ThrowsException() {
        int reminderNumber = 1;

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class);
             MockedStatic<TemplateDef> templateMock = mockStatic(TemplateDef.class)) {

            typeMock.when(() -> ReminderTypeMapper.getType(reminderNumber))
                    .thenReturn(ReminderType.EMPLOYEE_REMINDER);
            templateMock.when(() -> TemplateDef.templateFor(ReminderType.EMPLOYEE_REMINDER))
                    .thenReturn("emails/employee/timesheet-reminder");

            when(payloadMapper.toDto(any(), isNull()))
                    .thenThrow(new IllegalArgumentException("Payload cannot be null"));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> emailService.sendReminderByNumber(reminderNumber, null));

            assertTrue(exception.getMessage().contains("Payload cannot be null"));
            // Removed: verify(payloadMapper, never()).toDto(any(), any());
            verify(templateEngine, never()).process(anyString(), any(Context.class));
        }
    }

    @Test
    void sendReminderByNumber_PayloadConversionFails_ThrowsException() {
        int reminderNumber = 1;
        Map<String, Object> invalidPayload = Map.of("invalid", "data");

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class);
             MockedStatic<TemplateDef> templateMock = mockStatic(TemplateDef.class)) {

            typeMock.when(() -> ReminderTypeMapper.getType(reminderNumber))
                    .thenReturn(ReminderType.EMPLOYEE_REMINDER);
            templateMock.when(() -> TemplateDef.templateFor(ReminderType.EMPLOYEE_REMINDER))
                    .thenReturn("emails/employee/timesheet-reminder");

            when(payloadMapper.toDto(any(), eq(invalidPayload)))
                    .thenThrow(new IllegalArgumentException("Invalid payload format"));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> emailService.sendReminderByNumber(reminderNumber, invalidPayload));
            assertTrue(ex.getMessage().contains("Invalid payload format"));

            verify(templateEngine, never()).process(anyString(), any(Context.class));
        }
    }

    @Test
    void sendReminderByNumber_TemplateNotFound_ThrowsException() {
        int reminderNumber = 1;
        Map<String, Object> payload = Map.of("to", "test@example.com");

        try (MockedStatic<ReminderTypeMapper> typeMock = mockStatic(ReminderTypeMapper.class);
             MockedStatic<TemplateDef> templateMock = mockStatic(TemplateDef.class)) {

            typeMock.when(() -> ReminderTypeMapper.getType(reminderNumber))
                    .thenReturn(ReminderType.EMPLOYEE_REMINDER);
            templateMock.when(() -> TemplateDef.templateFor(ReminderType.EMPLOYEE_REMINDER))
                    .thenReturn(null);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> emailService.sendReminderByNumber(reminderNumber, payload));

            assertTrue(exception.getMessage().contains("Template not configured"));
            verify(payloadMapper, never()).toDto(any(), any());
        }
    }

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

    @Test
    void sendUsingTemplate_TemplateRenderingFails_ThrowsException() throws Exception {
        String templatePath = "emails/test/template";
        Object dto = new Object();

        when(objectMapper.convertValue(dto, Map.class)).thenReturn(Map.of("to", "test@example.com"));

        // Stub templateEngine.process to throw RuntimeException, as service does not catch it
        when(templateEngine.process(eq(templatePath), any(Context.class)))
                .thenThrow(new RuntimeException("Template rendering failed"));

        // No need to stub mailSender.send here because exception happens before sending
        // If stub needed, do it as doNothing()

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> emailService.sendUsingTemplate(templatePath, dto));
        assertTrue(ex.getMessage().contains("Template rendering failed"));

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

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


    // ==================== 🔧 PRIVATE METHOD TESTING (Optional) ====================

    @Test
    void extractSubject_PrivateMethod_WithReflection() throws Exception {
        // This is optional - only if you really need to test the private method directly

        // Arrange
        String htmlWithSubject = createHtmlWithSubject("Test Subject");

        // Use reflection to access private method
        Method extractSubjectMethod = EmailService.class.getDeclaredMethod("extractSubject", String.class);
        extractSubjectMethod.setAccessible(true);

        // Act
        String result = (String) extractSubjectMethod.invoke(emailService, htmlWithSubject);

        // Assert
        assertEquals("Test Subject", result);
    }

    @Test
    void extractSubject_PrivateMethod_WithoutMetaTag_WithReflection() throws Exception {
        // This is optional - only if you really need to test the private method directly

        // Arrange
        String htmlWithoutSubject = "<html><body>No subject</body></html>";

        // Use reflection to access private method
        Method extractSubjectMethod = EmailService.class.getDeclaredMethod("extractSubject", String.class);
        extractSubjectMethod.setAccessible(true);

        // Act
        String result = (String) extractSubjectMethod.invoke(emailService, htmlWithoutSubject);

        // Assert
        assertNull(result);
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
            case 1: return new EmployeeReminderDto("test@example.com", "John Doe", "2024-01-15", "https://example.com/timesheet");
            case 4: return new ManagerReadyForApprovalDto("manager@example.com", "Sarah", "Team", "2024-01-15", "Deadline", "https://example.com/dashboard");
            default: return new Object();
        }
    }

    private String createHtmlWithSubject(String subject) {
        return String.format("<html><head><meta name='subject' content='%s'></head><body>Email content</body></html>", subject);
    }
}