package com.net.sphuta_tms.dto.manager;

/**
 * Data Transfer Object for Manager Escalation Emails.
 *
 * <p>This DTO encapsulates all necessary information required to
 * generate and send escalation emails to managers regarding their employees' timesheets.</p>
 *
 * <p>Fields included:
 * <ul>
 *   <li><b>to</b>: Recipient email address (manager's email)</li>
 *   <li><b>managerName</b>: Name of the manager receiving the email</li>
 *   <li><b>employeeName</b>: Name of the employee whose timesheet is being escalated</li>
 *   <li><b>weekDate</b>: The week ending date for the timesheet in question</li>
 *   <li><b>managerDashboardLink</b>: URL link to the manager's dashboard for quick access</li>
 * </ul>
 * </p>
 */
public record ManagerEscalationDto(
        String to,
        String managerName,
        String employeeName,
        String weekDate,
        String managerDashboardLink
) { }
