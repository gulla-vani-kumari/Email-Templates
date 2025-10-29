package com.net.sphuta_tms.dto;

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
