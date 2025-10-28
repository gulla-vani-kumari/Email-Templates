package com.net.sphuta_tms.dto.admin;

/**
 * Data Transfer Object for Admin Escalation Emails.
 *
 * <p>This DTO encapsulates all necessary information required to send
 * escalation emails to administrators regarding employee timesheet deadlines.</p>
 *
 * <p>Fields included:
 * <ul>
 *   <li>{@code to}: Recipient email address</li>
 *   <li>{@code adminName}: Name of the administrator</li>
 *   <li>{@code employeeName}: Name of the employee</li>
 *   <li>{@code managerName}: Name of the employee's manager</li>
 *   <li>{@code teamName}: Name of the team</li>
 *   <li>{@code weekDate}: The week date related to the escalation</li>
 *   <li>{@code deadlineDate}: The deadline date for timesheet submission</li>
 *   <li>{@code adminDashboardLink}: Link to the admin dashboard for further action</li>
 * </ul>
 * </p>
 */
public record AdminEscalationDto(
        String to,
        String adminName,
        String employeeName,
        String managerName,
        String teamName,
        String weekDate,
        String deadlineDate,
        String adminDashboardLink
) { }
