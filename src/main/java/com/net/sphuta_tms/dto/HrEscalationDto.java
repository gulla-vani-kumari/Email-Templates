package com.net.sphuta_tms.dto;

public record HrEscalationDto(
        String to,
        String hrContactName,
        String employeeName,
        String weekDate,
        String hrDashboardLink
) { }