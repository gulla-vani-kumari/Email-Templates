package com.net.sphuta_tms.dto.hr;

/**
 * Data Transfer Object for HR Escalation Emails.
 *
 * <p>This DTO encapsulates all necessary information required to
 * send an HR escalation email regarding employee timesheet issues.</p>
 *
 * <p>Fields included:
 * <ul>
 *   <li><b>to</b>: Recipient email address</li>
 *   <li><b>hrContactName</b>: Name of the HR contact person</li>
 *   <li><b>employeeName</b>: Name of the employee being escalated</li>
 *   <li><b>weekDate</b>: The week date related to the escalation</li>
 *   <li><b>hrDashboardLink</b>: Link to the HR dashboard for further action</li>
 * </ul>
 * </p>
 */
public record HrEscalationDto(
        String to,
        String hrContactName,
        String employeeName,
        String weekDate,
        String hrDashboardLink
) { }
