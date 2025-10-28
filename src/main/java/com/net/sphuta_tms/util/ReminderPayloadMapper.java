package com.net.sphuta_tms.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.sphuta_tms.enums.ReminderType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ReminderPayloadMapper
 *
 * Responsibilities:
 *  - Know which DTO class corresponds to each ReminderType
 *  - Convert an incoming payload Map -> DTO record using ObjectMapper
 *
 * Benefits:
 *  - Keeps the controller free from heavy switch/case logic
 *  - Centralized place to add new DTO mappings
 */
@Slf4j
@Component
public class ReminderPayloadMapper {

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<ReminderType, Class<?>> registry = new HashMap<>();

    @PostConstruct
    private void init() {
        // Register mapping: ReminderType -> DTO class
        // Update these if you move DTO classes/packages
        registry.put(ReminderType.EMPLOYEE_REMINDER, com.net.sphuta_tms.dto.employee.EmployeeReminderDto.class);
        registry.put(ReminderType.EMPLOYEE_FINAL_REMINDER, com.net.sphuta_tms.dto.employee.EmployeeFinalReminderDto.class);
        registry.put(ReminderType.EMPLOYEE_MISSED_DEADLINE, com.net.sphuta_tms.dto.employee.EmployeeMissedDto.class);

        registry.put(ReminderType.MANAGER_READY_FOR_APPROVAL, com.net.sphuta_tms.dto.manager.ManagerReadyForApprovalDto.class);
        registry.put(ReminderType.MANAGER_APPROVAL_OVERDUE, com.net.sphuta_tms.dto.manager.ManagerApprovalOverdueDto.class);
        registry.put(ReminderType.MANAGER_ESCALATION, com.net.sphuta_tms.dto.manager.ManagerEscalationDto.class);

        registry.put(ReminderType.ADMIN_ESCALATION, com.net.sphuta_tms.dto.admin.AdminEscalationDto.class);
        registry.put(ReminderType.HR_ESCALATION, com.net.sphuta_tms.dto.hr.HrEscalationDto.class);
    }

    /**
     * Convert payload Map -> specific DTO instance for the reminder type.
     * Returns the DTO object, or throws IllegalArgumentException on failure.
     */
    public Object toDto(ReminderType type, Map<String, Object> payload) {
        Class<?> dtoClass = registry.get(type);
        if (dtoClass == null) {
            String msg = "No DTO registered for ReminderType: " + type;
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        try {
            Object dto = objectMapper.convertValue(payload, dtoClass);
            log.debug("Converted payload keys={} to DTO {} for type {}", payload == null ? "null" : payload.keySet(),
                    dtoClass.getSimpleName(), type);
            return dto;
        } catch (Exception ex) {
            String msg = "Failed to convert payload to " + dtoClass.getSimpleName() + " for type " + type + ": " + ex.getMessage();
            log.error(msg, ex);
            throw new IllegalArgumentException(msg, ex);
        }
    }
}
