package com.net.sphuta_tms.dto.manager;

/**
 * Data Transfer Object (DTO) for notifying managers
 * that their team's timesheets are ready for approval.
 *
 * <p>This DTO encapsulates all necessary information
 * required to inform a manager about pending approvals,
 * including deadlines and relevant links.</p>
 */
public record ManagerReadyForApprovalDto(
        String to,
        String managerName,
        String teamName,
        String weekDate,
        String managerApprovalDeadline,
        String managerDashboardLink
) { }
