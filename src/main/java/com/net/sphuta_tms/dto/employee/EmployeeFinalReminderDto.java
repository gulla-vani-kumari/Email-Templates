package com.net.sphuta_tms.dto.employee;

/**
 * Data Transfer Object for Employee Final Reminder Email.
 *
 * <p>This DTO encapsulates all necessary information required to
 * generate and send a final reminder email to employees regarding
 * their timesheet submissions.</p>
 *
 * <p>Fields included:
 * <ul>
 *   <li>to: Recipient email address</li>
 *   <li>employeeName: Name of the employee</li>
 *   <li>weekDate: The week date for which the timesheet is due</li>
 *   <li>timesheetLink: URL link to access the timesheet</li>
 *   <li>deadlineTime: Deadline time for submission</li>
 *   <li>supportContact: Contact information for support</li>
 * </ul>
 * </p>
 */
public record EmployeeFinalReminderDto(
        String to,
        String employeeName,
        String weekDate,
        String timesheetLink,
        String deadlineTime,
        String supportContact
) { }
