package com.net.sphuta_tms.util;

import com.net.sphuta_tms.enums.ReminderType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
/**
 * ==========================================================
 * {@code ReminderTypeMapper}
 * ==========================================================
 *
 * <p>Utility class for mapping between integer codes and
 * {@link ReminderType} enum values.</p>
 *
 * <p>This class provides methods to:</p>
 * <ul>
 *   <li>Get {@link ReminderType} from its corresponding integer code.</li>
 *   <li>Get integer code from a {@link ReminderType}.</li>
 *   <li>Retrieve all mappings as an unmodifiable map.</li>
 * </ul>
 *
 * <p>Mappings are initialized statically for various reminder types,
 * including employee-level, manager-level, admin-level, and HR-level reminders.</p>
 */
@Slf4j
public final class ReminderTypeMapper {

    private static final Map<Integer, ReminderType> NUMBER_TO_TYPE = new HashMap<>();
    private static final Map<ReminderType, Integer> TYPE_TO_NUMBER = new HashMap<>();

    static {
        // Employee-level
        register(1, ReminderType.EMPLOYEE_REMINDER);
        register(2, ReminderType.EMPLOYEE_FINAL_REMINDER);
        register(3, ReminderType.EMPLOYEE_MISSED_DEADLINE);

        // Manager-level
        register(4, ReminderType.MANAGER_READY_FOR_APPROVAL);
        register(5, ReminderType.MANAGER_APPROVAL_OVERDUE);
        register(6, ReminderType.MANAGER_ESCALATION);

        // Admin-level
        register(7, ReminderType.ADMIN_ESCALATION);

        // HR-level
        register(8, ReminderType.HR_ESCALATION);
    }

    private ReminderTypeMapper() {}

    private static void register(int code, ReminderType type) {
        NUMBER_TO_TYPE.put(code, type);
        TYPE_TO_NUMBER.put(type, code);
    }

    public static ReminderType getType(int number) {
        ReminderType type = NUMBER_TO_TYPE.get(number);
        if (type == null) log.warn("ReminderTypeMapper: unknown reminder number {}", number);
        return type;
    }

    public static Integer getNumber(ReminderType type) {
        return TYPE_TO_NUMBER.get(type);
    }

    public static Map<Integer, ReminderType> getAllMappings() {
        return Map.copyOf(NUMBER_TO_TYPE);
    }
}
