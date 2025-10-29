package com.net.sphuta_tms.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeration of different types of reminders
 * within the Time Management System (TMS).
 *
 * <p>This enum categorizes reminders based on
 * their target audience and purpose,
 * such as employee-level reminders,
 * manager-level notifications, and escalations
 * for administrators and HR personnel.</p>
 */
public enum ReminderType {
    // Employee-level
    EMPLOYEE_REMINDER(1),
    EMPLOYEE_FINAL_REMINDER(2),
    EMPLOYEE_MISSED_DEADLINE(3),

    // Manager-level
    MANAGER_READY_FOR_APPROVAL(4),
    MANAGER_APPROVAL_OVERDUE(5),
    MANAGER_ESCALATION(6),

    // Admin-level
    ADMIN_ESCALATION(7),

    // HR-level
    HR_ESCALATION(8);

    private final int code;

    ReminderType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    // fast reverse lookup map
    private static final Map<Integer, ReminderType> BY_CODE =
            Stream.of(values()).collect(Collectors.toMap(ReminderType::getCode, r -> r));

    public static ReminderType fromCode(int code) {
        return BY_CODE.get(code); // returns null if unknown
    }
}
