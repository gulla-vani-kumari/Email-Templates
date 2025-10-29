package com.net.sphuta_tms.constants;


import com.net.sphuta_tms.dto.TemplateInfo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TemplateRegistry builds a Map<reminderNumber, TemplateInfo>
 * at startup and exposes lookup methods.
 */
@Component
public class TemplateRegistry {

    private final Map<Integer, TemplateInfo> registry = new HashMap<>();

    @PostConstruct
    public void init() {
        // employee
        registry.put(1, new TemplateInfo(1, "Employee Reminder", "emails/employee/timesheet-reminder"));
        registry.put(2, new TemplateInfo(2, "Employee Final Reminder", "emails/employee/timesheet-final-call"));
        registry.put(3, new TemplateInfo(3, "Employee Missed Deadline", "emails/employee/timesheet-missed-deadline"));

        // manager
        registry.put(4, new TemplateInfo(4, "Manager Ready For Approval", "emails/manager/timesheet-ready-approval"));
        registry.put(5, new TemplateInfo(5, "Manager Approval Overdue", "emails/manager/timesheet-approval-overdue"));
        registry.put(6, new TemplateInfo(6, "Manager Escalation", "emails/manager/timesheet-escalation"));

        // admin / hr
        registry.put(7, new TemplateInfo(7, "Admin Escalation", "emails/admin/timesheet-admin-escalation"));
        registry.put(8, new TemplateInfo(8, "HR Escalation", "emails/hr/timesheet-hr-escalation"));
    }

    public Map<Integer, TemplateInfo> getAll() {
        return Collections.unmodifiableMap(registry);
    }

    public TemplateInfo getByReminderNumber(int reminderNumber) {
        return registry.get(reminderNumber); // null if missing
    }
}
