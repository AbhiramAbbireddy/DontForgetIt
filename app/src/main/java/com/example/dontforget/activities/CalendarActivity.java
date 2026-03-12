package com.example.dontforget.activities;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontforget.R;
import com.example.dontforget.adapter.ReminderAdapter;
import com.example.dontforget.database.ReminderDatabase;
import com.example.dontforget.model.Reminder;
import com.example.dontforget.util.ReminderDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView reminderList;
    TextView navHome;
    TextView navCalendar;
    TextView monthTitle;

    ReminderDatabase db;
    List<Reminder> reminderData;
    ReminderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = ReminderDatabase.getInstance(this);

        calendarView = findViewById(R.id.calendarView);
        reminderList = findViewById(R.id.reminderListCalendar);
        navHome = findViewById(R.id.navHome);
        navCalendar = findViewById(R.id.navCalendar);
        monthTitle = findViewById(R.id.monthTitle);

        reminderData = new ArrayList<>();
        adapter = new ReminderAdapter(reminderData, reminder -> {
            db.reminderDao().deleteById(reminder.getId());
            loadForDate(calendarView.getDate());
        });
        reminderList.setLayoutManager(new LinearLayoutManager(this));
        reminderList.setAdapter(adapter);

        navHome.setOnClickListener(v -> {
            finish();
        });

        navCalendar.setTextColor(0xFF5B5FC7);

        updateMonthTitle(calendarView.getDate());
        loadForDate(calendarView.getDate());

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth);
            loadForDate(c.getTimeInMillis());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Always reload for the currently selected date when coming back
        loadForDate(calendarView.getDate());
    }

    private void updateMonthTitle(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        String text = months[c.get(Calendar.MONTH)] + " " + c.get(Calendar.YEAR);
        monthTitle.setText(text);
    }

    private void loadForDate(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);

        reminderData.clear();
        List<Reminder> allReminders = db.reminderDao().getAllReminders();
        for (Reminder reminder : allReminders) {
            if (ReminderDateUtils.occursOn(reminder, c)) {
                reminderData.add(reminder);
            }
        }
        adapter.notifyDataSetChanged();
        updateMonthTitle(millis);
    }
}
