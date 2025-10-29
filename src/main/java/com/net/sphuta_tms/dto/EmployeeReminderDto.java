package com.net.sphuta_tms.dto;

public record EmployeeReminderDto(
        String to,
        String employeeName,
        String weekDate,
        String timesheetLink,
        String deadlineTime,
        String supportContact,
        String helpLink,
        String itSupportEmail
) { }