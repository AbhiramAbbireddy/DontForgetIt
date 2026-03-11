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

    public Reminder(String title, String date, String time, int remindBefore) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.remindBefore = remindBefore;
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
}
