package com.net.sphuta_tms.enums;

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
    EMPLOYEE_REMINDER,
    EMPLOYEE_FINAL_REMINDER,
    EMPLOYEE_MISSED_DEADLINE,

    // Manager-level
    MANAGER_READY_FOR_APPROVAL,
    MANAGER_APPROVAL_OVERDUE,
    MANAGER_ESCALATION,

    // Admin-level
    ADMIN_ESCALATION,

    // HR-level
    HR_ESCALATION
}
