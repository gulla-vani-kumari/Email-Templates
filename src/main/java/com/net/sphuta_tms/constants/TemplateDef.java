package com.net.sphuta_tms.constants;

import com.net.sphuta_tms.enums.ReminderType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * Defines email template paths for different reminder types.
 *
 * <p>This class provides a mapping between {@link ReminderType}
 * and the corresponding email template file paths used
 * for sending notifications.</p>
 */
public final class TemplateDef {

    private static final Map<ReminderType, String> MAP;

    static {
        Map<ReminderType, String> m = new HashMap<>();
        // employee
        m.put(ReminderType.EMPLOYEE_REMINDER, "emails/employee/timesheet-reminder");
        m.put(ReminderType.EMPLOYEE_FINAL_REMINDER, "emails/employee/timesheet-final-call");
        m.put(ReminderType.EMPLOYEE_MISSED_DEADLINE, "emails/employee/timesheet-missed-deadline");

        // manager
        m.put(ReminderType.MANAGER_READY_FOR_APPROVAL, "emails/manager/timesheet-ready-approval");
        m.put(ReminderType.MANAGER_APPROVAL_OVERDUE, "emails/manager/timesheet-approval-overdue");
        m.put(ReminderType.MANAGER_ESCALATION, "emails/manager/timesheet-escalation");

        // admin
        m.put(ReminderType.ADMIN_ESCALATION, "emails/admin/timesheet-admin-escalation");

        // hr
        m.put(ReminderType.HR_ESCALATION, "emails/hr/timesheet-hr-escalation");

        MAP = Collections.unmodifiableMap(m);
    }

    private TemplateDef() {}

    public static String templateFor(ReminderType type) {
        return MAP.get(type);
    }
}
