package com.net.sphuta_tms.dto.employee;
/**
 * Data Transfer Object for notifying about missed employee timesheets.
 *
 * @param to               Recipient email address
 * @param employeeName     Name of the employee
 * @param weekDate         Week date for which the timesheet is missed
 * @param timesheetLink    Link to the timesheet
 * @param helpLink         Link to help resources
 * @param itSupportEmail   IT support email address
 */
public record EmployeeMissedDto(
        String to,
        String employeeName,
        String weekDate,
        String timesheetLink,
        String helpLink,
        String itSupportEmail
) { }
