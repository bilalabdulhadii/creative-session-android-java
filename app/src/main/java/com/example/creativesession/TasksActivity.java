package com.example.creativesession;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private ListView tasksListView;
    private TaskAdapter taskAdapter;
    private List<Task> tasksList;
    public static boolean isTasksChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tasks);
        tasksListView = findViewById(R.id.tasks_ListView);
        loadTasks();

        ImageButton goBackImgBtn = findViewById(R.id.goBack_ImgBtn);
        goBackImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton newTaskFloatingBtn = findViewById(R.id.newTask_FloatingBtn);
        newTaskFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TasksActivity.this, NewTaskActivity.class);
                startActivity(intent);
            }
        });

        tasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = tasksList.get(position);
                if (!selectedTask.isCompleted()) {
                    DatabaseHelper dbHelper = new DatabaseHelper(TasksActivity.this);
                    boolean isUpdated = dbHelper.updateLastTask(selectedTask.getId());
                    Intent intent = new Intent();
                    /*intent.putExtra("SELECT_TASK", selectedTask.getId());*/
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        // Handle long item clicks to show a PopupMenu
        tasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupMenu(view, position);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void showPopupMenu(View anchorView, int position) {
        // Inflate the custom popup layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_menu_task, null);

        // Set up the PopupWindow
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        // Get the selected task
        Task selectedTask = tasksList.get(position);

        // Populate task info
        EditText taskTitleEditText = popupView.findViewById(R.id.taskTitle_EditText);
        EditText desiredSessionsEditText = popupView.findViewById(R.id.desiredSessions_EditText);
        TextView sessionsTextView = popupView.findViewById(R.id.sessions_TextView);
        TextView createdAtTextView = popupView.findViewById(R.id.taskCreatedAt_TextView);
        ImageButton editImgBtn = popupView.findViewById(R.id.editTask_ImgBtn);
        ImageButton removeImgBtn = popupView.findViewById(R.id.removeTask_ImgBtn);

        String sessions = selectedTask.getCompletedSessions() + "/" + selectedTask.getDesiredSessions();

        taskTitleEditText.setText(selectedTask.getTitle());
        desiredSessionsEditText.setText(String.valueOf(selectedTask.getDesiredSessions()));
        sessionsTextView.setText(sessions);
        createdAtTextView.setText(selectedTask.getCreatedAt());

        editImgBtn.setOnClickListener(v -> {
            String newTitle = taskTitleEditText.getText().toString().trim();
            String desiredSessionsText = desiredSessionsEditText.getText().toString();

            if (!TextUtils.isEmpty(newTitle)) {
                if ((!TextUtils.isEmpty(desiredSessionsText))) {
                    int newDesiredSessions = Integer.parseInt(desiredSessionsText);
                    DatabaseHelper dbHelper = new DatabaseHelper(TasksActivity.this);
                    boolean isUpdated = dbHelper.updateTask(selectedTask.getId(), newTitle, newDesiredSessions);
                    if (isUpdated) { loadTasks(); }
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(TasksActivity.this, "Please enter the number of desired sessions.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(TasksActivity.this, "Please enter a title.", Toast.LENGTH_SHORT).show();
            }
        });

        removeImgBtn.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(TasksActivity.this);
            boolean isDeleted = dbHelper.deleteTask(selectedTask.getId());
            if (isDeleted) { loadTasks(); }
            popupWindow.dismiss();
        });

        // Show the PopupWindow centered
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }


    private void loadTasks() {
        tasksList = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        tasksList = databaseHelper.todoList();
        taskAdapter = new TaskAdapter(this, tasksList);
        tasksListView.setAdapter(taskAdapter);
    }
}