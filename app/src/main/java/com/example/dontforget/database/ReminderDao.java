package com.example.dontforget.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dontforget.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    long insert(Reminder reminder);

    @Query("SELECT * FROM reminders ORDER BY id DESC")
    List<Reminder> getAllReminders();

    @Query("SELECT * FROM reminders WHERE date = :date ORDER BY time")
    List<Reminder> getRemindersForDate(String date);

    @Query("UPDATE reminders SET completed = 1 WHERE id = :id")
    void markCompleted(int id);

    @Query("DELETE FROM reminders WHERE id = :id")
    void deleteById(int id);
}
