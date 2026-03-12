package com.example.dontforget.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String date;
    private String time;
    private int remindBefore;
    private boolean completed;
    private String recurrenceType; // NONE, DAILY, WEEKLY, MONTHLY

    public Reminder(String title, String date, String time, int remindBefore) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.remindBefore = remindBefore;
        this.completed = false;
        this.recurrenceType = "NONE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getRemindBefore() {
        return remindBefore;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getRecurrenceType() {
        return recurrenceType;
    }

    public void setRecurrenceType(String recurrenceType) {
        this.recurrenceType = recurrenceType;
    }
}
