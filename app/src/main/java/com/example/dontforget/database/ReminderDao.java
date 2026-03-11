package com.example.dontforget.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dontforget.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insert(Reminder reminder);

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    List<Reminder> getAllReminders();
}
