package com.net.sphuta_tms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Template information stored in registry.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateInfo {
    private Integer templateId;      // optional numeric id for template
    private String templateName;     // human friendly name
    private String templatePath;     // thymeleaf path e.g. "emails/employee/timesheet-reminder"
}

