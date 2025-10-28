package com.net.sphuta_tms.dto.employee;
/**
 * Data Transfer Object for Employee Reminder Emails.
 *
 * <p>This DTO encapsulates the necessary information to send
 * reminder emails to employees regarding their timesheet submissions.
 *
 * <p>Fields:
 * <ul>
 *   <li><b>to</b>: Recipient email address.</li>
 *   <li><b>employeeName</b>: Name of the employee.</li>
 *   <li><b>weekDate</b>: The week date for which the reminder is sent.</li>
 *   <li><b>timesheetLink</b>: URL link to the timesheet submission page.</li>
 * </ul>
 */
public record EmployeeReminderDto(
        String to,
        String employeeName,
        String weekDate,
        String timesheetLink
) { }
