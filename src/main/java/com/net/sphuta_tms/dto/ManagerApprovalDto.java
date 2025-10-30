package com.net.sphuta_tms.dto;

public record ManagerApprovalDto(
        String to,
        String managerName,
        String teamName,
        String employeeName,
        String weekDate,
        String managerApprovalDeadline,
        String managerDashboardLink,
        String deadlineDateTime
) { }