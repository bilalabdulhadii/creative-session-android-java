package com.example.creativesession;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<Task> {

    public TaskAdapter(@NonNull Context context, List<Task> tasks) {
        super(context, 0, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_task, parent, false);
        }

        // Get the current Task object
        Task currentTask = getItem(position);

        // Find the TextViews and CheckBox in the list_item_task layout
        TextView titleTextView = listItemView.findViewById(R.id.taskTitle_TextView);
        TextView sessionsTextView = listItemView.findViewById(R.id.sessions_TextView);
        /*TextView createdAtTextView = listItemView.findViewById(R.id.taskCreatedAt_TextView);*/
        CheckBox completedCheckBox = listItemView.findViewById(R.id.taskCompleted_Checkbox);

        // Set the task title, createdAt, and completed state
        if (currentTask != null) {
            titleTextView.setText(currentTask.getTitle());
            String sessions = currentTask.getCompletedSessions() + "/" + currentTask.getDesiredSessions();
            sessionsTextView.setText(sessions);
            /*createdAtTextView.setText(currentTask.getCreatedAt());*/
            completedCheckBox.setChecked(currentTask.isCompleted());

            // Set strikethrough initially if task is completed
            if (currentTask.isCompleted()) {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                titleTextView.setPaintFlags(titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    titleTextView.setPaintFlags(titleTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    titleTextView.setPaintFlags(titleTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                if (currentTask.getId() > 0) {
                    DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                    boolean isUpdated = databaseHelper.updateIsCompleted(currentTask.getId(), isChecked);
                    if (isUpdated) {
                        currentTask.setCompleted(isChecked);
                    }
                }

                TasksActivity.isTasksChecked = true;
            });
        }

        return listItemView;
    }
}
