package com.example.dontforget.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.dontforget.R;
import com.example.dontforget.database.ReminderDatabase;
import com.example.dontforget.model.Reminder;
import com.example.dontforget.util.ReminderScheduler;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "reminder_channel";

        int id = intent.getIntExtra("id", -1);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(channelId,"Reminders",
                            NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(channel);
        }

        String title = intent.getStringExtra("title");
        if (title == null || title.isEmpty()) {
            title = "You have a task scheduled";
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Reminder")
                        .setContentText(title)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(id > 0 ? id : 1, builder.build());

        if (id > 0) {
            ReminderDatabase db = ReminderDatabase.getInstance(context);
            Reminder reminder = db.reminderDao().getById(id);
            if (reminder != null) {
                String recurrence = reminder.getRecurrenceType();
                if (recurrence == null || "NONE".equals(recurrence)) {
                    db.reminderDao().markCompleted(id);
                } else {
                    // For recurring reminders, schedule the next occurrence
                    ReminderScheduler.scheduleNextRecurring(context, reminder);
                }
            }
        }
    }
}