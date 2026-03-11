package com.example.dontforget.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontforget.R;
import com.example.dontforget.model.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    List<Reminder> reminderList;

    public ReminderAdapter(List<Reminder> reminderList){
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position){

        Reminder reminder = reminderList.get(position);

        holder.title.setText(reminder.getTitle());
        holder.date.setText(reminder.getDate());
        holder.time.setText(reminder.getTime());

    }

    @Override
    public int getItemCount(){
        return reminderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,date,time;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.reminderTitle);
            date = itemView.findViewById(R.id.reminderDate);
            time = itemView.findViewById(R.id.reminderTime);
        }
    }
}