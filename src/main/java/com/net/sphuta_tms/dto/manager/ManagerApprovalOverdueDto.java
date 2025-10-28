package com.net.sphuta_tms.dto.manager;

/**
 * Data Transfer Object (DTO) for notifying managers about overdue approvals.
 *
 * <p>This DTO encapsulates all necessary information required to inform a manager
 * that they have pending approvals that are overdue. It includes details such as
 * the manager's name, team name, relevant dates, and a link to the manager's dashboard.</p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li><b>to</b>: The email address of the manager to notify.</li>
 *   <li><b>managerName</b>: The name of the manager.</li>
 *   <li><b>teamName</b>: The name of the team under the manager's supervision.</li>
 *   <li><b>weekDate</b>: The week date for which approvals are overdue.</li>
 *   <li><b>deadlineDateTime</b>: The deadline date and time for the approvals.</li>
 *   <li><b>managerDashboardLink</b>: A URL link to the manager's dashboard for quick access.</li>
 * </ul>
 */
public record ManagerApprovalOverdueDto(
        String to,
        String managerName,
        String teamName,
        String weekDate,
        String deadlineDateTime,
        String managerDashboardLink
) { }
