package com.example.dontforget.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dontforget.R;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    EditText titleInput;
    Button dateButton,timeButton,saveReminder;

    String selectedDate="";
    String selectedTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        titleInput = findViewById(R.id.titleInput);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);
        saveReminder = findViewById(R.id.saveReminder);

        dateButton.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view,y,m,d) -> {

                        selectedDate = d + "/" + (m+1) + "/" + y;
                        dateButton.setText(selectedDate);

                    },
                    year,month,day
            );

            datePicker.show();
        });

        timeButton.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePicker = new TimePickerDialog(
                    this,
                    (view,h,m) -> {

                        selectedTime = h + ":" + m;
                        timeButton.setText(selectedTime);

                    },
                    hour,minute,true
            );

            timePicker.show();
        });

        saveReminder.setOnClickListener(v -> {

            String title = titleInput.getText().toString();

            if(title.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()){
                Toast.makeText(this,"Fill all fields",Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();

            resultIntent.putExtra("title",title);
            resultIntent.putExtra("date",selectedDate);
            resultIntent.putExtra("time",selectedTime);

            setResult(RESULT_OK,resultIntent);

            finish();
        });
    }
}