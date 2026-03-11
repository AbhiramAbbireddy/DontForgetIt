package com.example.dontforget.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dontforget.R;
import com.example.dontforget.model.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {

    public interface ReminderActionsListener {
        void onDelete(Reminder reminder);
    }

    List<Reminder> reminderList;
    private final ReminderActionsListener actionsListener;

    public ReminderAdapter(List<Reminder> reminderList, ReminderActionsListener listener){
        this.reminderList = reminderList;
        this.actionsListener = listener;
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

        // Completed state styling
        if (reminder.isCompleted()) {
            holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.title.setPaintFlags(holder.title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.itemView.setAlpha(1.0f);
        }

        holder.deleteButton.setOnClickListener(v -> {
            if (actionsListener != null) {
                actionsListener.onDelete(reminder);
            }
        });

    }

    @Override
    public int getItemCount(){
        return reminderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,date,time;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.reminderTitle);
            date = itemView.findViewById(R.id.reminderDate);
            time = itemView.findViewById(R.id.reminderTime);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}