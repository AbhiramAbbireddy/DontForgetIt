package com.example.dontforget.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.dontforget.activities.ReminderReceiver;
import com.example.dontforget.model.Reminder;

import java.util.Calendar;

public class ReminderScheduler {

    public static void scheduleInitial(Context context, Reminder reminder) {
        Calendar triggerTime = computeInitialTrigger(reminder);
        if (triggerTime == null) return;
        scheduleAt(context, reminder, triggerTime);
    }

    public static void scheduleNextRecurring(Context context, Reminder reminder) {
        Calendar next = computeNextForRecurrence(reminder);
        if (next == null) return;
        scheduleAt(context, reminder, next);
    }

    private static Calendar computeInitialTrigger(Reminder reminder) {
        try {
            String[] timeParts = reminder.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar baseTime = Calendar.getInstance();
            baseTime.set(Calendar.HOUR_OF_DAY, hour);
            baseTime.set(Calendar.MINUTE, minute);
            baseTime.set(Calendar.SECOND, 0);

            Calendar triggerTime = (Calendar) baseTime.clone();
            triggerTime.add(Calendar.MINUTE, -reminder.getRemindBefore());

            Calendar now = Calendar.getInstance();

            if (triggerTime.before(now)) {
                if (baseTime.after(now)) {
                    triggerTime = (Calendar) baseTime.clone();
                } else {
                    baseTime.add(Calendar.DATE, 1);
                    triggerTime = (Calendar) baseTime.clone();
                    triggerTime.add(Calendar.MINUTE, -reminder.getRemindBefore());
                }
            }
            return triggerTime;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Calendar computeNextForRecurrence(Reminder reminder) {
        try {
            String[] timeParts = reminder.getTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            Calendar base = Calendar.getInstance();
            base.set(Calendar.HOUR_OF_DAY, hour);
            base.set(Calendar.MINUTE, minute);
            base.set(Calendar.SECOND, 0);

            String type = reminder.getRecurrenceType();
            if ("DAILY".equals(type)) {
                base.add(Calendar.DATE, 1);
            } else if ("WEEKLY".equals(type)) {
                base.add(Calendar.DATE, 7);
            } else if ("MONTHLY".equals(type)) {
                base.add(Calendar.MONTH, 1);
            } else {
                return null;
            }

            base.add(Calendar.MINUTE, -reminder.getRemindBefore());
            return base;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void scheduleAt(Context context, Reminder reminder, Calendar triggerTime) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", reminder.getTitle());
        intent.putExtra("id", reminder.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.getId(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        long triggerAt = triggerTime.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent);
        }
    }
}

