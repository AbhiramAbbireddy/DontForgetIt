package com.example.dontforget.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dontforget.model.Reminder;

@Database(entities = {Reminder.class}, version = 2)
public abstract class ReminderDatabase extends RoomDatabase {

    private static ReminderDatabase instance;

    public abstract ReminderDao reminderDao();

    public static synchronized ReminderDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ReminderDatabase.class, "reminder_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
