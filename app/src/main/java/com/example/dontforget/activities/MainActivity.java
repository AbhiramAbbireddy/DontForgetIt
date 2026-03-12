package com.example.dontforget.activities;

import android.Manifest;
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
import com.example.dontforget.util.ReminderDateUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.dontforget.util.ReminderScheduler;

public class MainActivity extends AppCompatActivity {

    RecyclerView reminderList;
    FloatingActionButton addButton;
    TextView emptyView;
    TextView navHome;
    TextView navCalendar;
    TextView titleView;
    TextView subTitleView;
    TextView todaySummaryView;

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
        navHome = findViewById(R.id.navHome);
        navCalendar = findViewById(R.id.navCalendar);
        titleView = findViewById(R.id.title);
        subTitleView = findViewById(R.id.subTitle);
        todaySummaryView = findViewById(R.id.todaySummary);

        requestNotificationPermission();

        loadData();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
            startActivityForResult(intent, 1);
        });

        navHome.setOnClickListener(v -> {
            // Already on home; no-op
        });

        navCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
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
        adapter = new ReminderAdapter(reminderData, reminder -> {
            db.reminderDao().deleteById(reminder.getId());
            loadData();
        });
        reminderList.setLayoutManager(new LinearLayoutManager(this));
        reminderList.setAdapter(adapter);
        updateEmptyView();
        updateHeader();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String date = data.getStringExtra("date");
            String time = data.getStringExtra("time");
            String recurrence = data.getStringExtra("recurrence");

            Reminder reminder = new Reminder(title, date, time, 10);
            if (recurrence != null) {
                reminder.setRecurrenceType(recurrence);
            }
            long id = db.reminderDao().insert(reminder);
            reminder.setId((int) id);

            loadData();
            ReminderScheduler.scheduleInitial(this, reminder);
            Toast.makeText(this, "Reminder Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list so completions and deletions triggered while the app was in background
        // are reflected when the user returns.
        loadData();
    }

    private void updateHeader() {
        Calendar today = Calendar.getInstance();
        titleView.setText(ReminderDateUtils.formatHeaderDay(today));
        subTitleView.setText(ReminderDateUtils.formatHeaderDate(today));

        int todayCount = ReminderDateUtils.countRemindersForDate(reminderData, today);
        String suffix = todayCount == 1 ? " reminder today" : " reminders today";
        todaySummaryView.setText(todayCount + suffix);
    }

    private void updateEmptyView() {
        if (reminderData == null || reminderData.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    // scheduling is handled by ReminderScheduler
}
