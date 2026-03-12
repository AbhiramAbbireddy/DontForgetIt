package com.example.dontforget.util;

import com.example.dontforget.model.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class ReminderDateUtils {

    private static final SimpleDateFormat STORAGE_DATE_FORMAT =
            new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
    private static final SimpleDateFormat HEADER_DAY_FORMAT =
            new SimpleDateFormat("EEEE", Locale.getDefault());
    private static final SimpleDateFormat HEADER_DATE_FORMAT =
            new SimpleDateFormat("MMMM d", Locale.getDefault());

    private ReminderDateUtils() {
    }

    public static Calendar parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            STORAGE_DATE_FORMAT.setLenient(false);
            Date parsed = STORAGE_DATE_FORMAT.parse(value);
            if (parsed == null) {
                return null;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsed);
            resetToStartOfDay(calendar);
            return calendar;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatStorageDate(Calendar calendar) {
        return STORAGE_DATE_FORMAT.format(calendar.getTime());
    }

    public static String formatHeaderDay(Calendar calendar) {
        return HEADER_DAY_FORMAT.format(calendar.getTime());
    }

    public static String formatHeaderDate(Calendar calendar) {
        return HEADER_DATE_FORMAT.format(calendar.getTime());
    }

    public static int countRemindersForDate(List<Reminder> reminders, Calendar selectedDate) {
        int count = 0;
        if (reminders == null) {
            return 0;
        }

        for (Reminder reminder : reminders) {
            if (occursOn(reminder, selectedDate)) {
                count++;
            }
        }
        return count;
    }

    public static boolean occursOn(Reminder reminder, Calendar selectedDate) {
        if (reminder == null || selectedDate == null) {
            return false;
        }

        Calendar reminderDate = parseDate(reminder.getDate());
        if (reminderDate == null) {
            return false;
        }

        Calendar targetDate = (Calendar) selectedDate.clone();
        resetToStartOfDay(targetDate);

        if (targetDate.before(reminderDate)) {
            return false;
        }

        String recurrence = reminder.getRecurrenceType();
        if (recurrence == null || "NONE".equals(recurrence)) {
            return isSameDay(reminderDate, targetDate);
        }

        if ("DAILY".equals(recurrence)) {
            return true;
        }

        if ("WEEKLY".equals(recurrence)) {
            long diffDays = daysBetween(reminderDate, targetDate);
            return diffDays % 7 == 0;
        }

        if ("MONTHLY".equals(recurrence)) {
            return reminderDate.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH)
                    && monthsBetween(reminderDate, targetDate) >= 0;
        }

        return false;
    }

    private static boolean isSameDay(Calendar first, Calendar second) {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR);
    }

    private static long daysBetween(Calendar start, Calendar end) {
        long millisPerDay = 24L * 60L * 60L * 1000L;
        return (end.getTimeInMillis() - start.getTimeInMillis()) / millisPerDay;
    }

    private static int monthsBetween(Calendar start, Calendar end) {
        return (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12
                + (end.get(Calendar.MONTH) - start.get(Calendar.MONTH));
    }

    private static void resetToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
