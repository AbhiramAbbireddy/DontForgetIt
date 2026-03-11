package com.example.dontforget.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontforget.R;
import com.example.dontforget.adapter.ReminderAdapter;
import com.example.dontforget.database.ReminderDatabase;
import com.example.dontforget.model.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView reminderList;
    FloatingActionButton addButton;
    TextView emptyView;

    List<Reminder> reminderData;
    ReminderAdapter adapter;
    ReminderDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = ReminderDatabase.getInstance(this);
        reminderList = findViewById(R.id.reminderList);
        addButton = findViewById(R.id.addButton);
        emptyView = findViewById(R.id.emptyView);

        requestNotificationPermission();

        loadData();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void loadData() {
        reminderData = db.reminderDao().getAllReminders();
        if (reminderData == null) {
            reminderData = new ArrayList<>();
        }
        adapter = new ReminderAdapter(reminderData);
        reminderList.setLayoutManager(new LinearLayoutManager(this));
        reminderList.setAdapter(adapter);
        updateEmptyView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");

            Reminder reminder = new Reminder(title, date, time, 10);
            db.reminderDao().insert(reminder);

            loadData();
            setReminder(time, title);
            Toast.makeText(this, "Reminder Saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyView() {
        if (reminderData == null || reminderData.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void setReminder(String time, String title) {
        try {
            String[] parts = time.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar baseTime = Calendar.getInstance();
            baseTime.set(Calendar.HOUR_OF_DAY, hour);
            baseTime.set(Calendar.MINUTE, minute);
            baseTime.set(Calendar.SECOND, 0);

            // Target trigger = 10 minutes before the chosen time
            Calendar triggerTime = (Calendar) baseTime.clone();
            triggerTime.add(Calendar.MINUTE, -10);

            Calendar now = Calendar.getInstance();

            // If "10 minutes before" is already in the past, but the main time is still in future,
            // fallback to triggering at the main time instead of silently doing nothing.
            if (triggerTime.before(now)) {
                if (baseTime.after(now)) {
                    triggerTime = (Calendar) baseTime.clone();
                } else {
                    // If even the main time is in the past, schedule for tomorrow at same time - 10
                    baseTime.add(Calendar.DATE, 1);
                    triggerTime = (Calendar) baseTime.clone();
                    triggerTime.add(Calendar.MINUTE, -10);
                }
            }

            Intent intent = new Intent(this, ReminderReceiver.class);
            intent.putExtra("title", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(), // Unique ID for each alarm
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                long triggerAt = triggerTime.getTimeInMillis();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                triggerAt,
                                pendingIntent
                        );
                    } else {
                        alarmManager.set(
                                AlarmManager.RTC_WAKEUP,
                                triggerAt,
                                pendingIntent
                        );
                    }
                } else {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            triggerAt,
                            pendingIntent
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
