package com.example.creativesession;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class NewTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        EditText titleEditText = findViewById(R.id.title_EditText);
        EditText desiredSessionsEditText = findViewById(R.id.desiredSessions_EditText);

        ImageButton goBackImgBtn = findViewById(R.id.goBack_ImgBtn);
        goBackImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button storeTaskBtn = findViewById(R.id.storeTask_Btn);
        storeTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String desiredSessionsText = desiredSessionsEditText.getText().toString();

                if (!TextUtils.isEmpty(title)) {
                    if ((!TextUtils.isEmpty(desiredSessionsText))) {
                        int desiredSessions = Integer.parseInt(desiredSessionsText);
                        DatabaseHelper dbHelper = new DatabaseHelper(NewTaskActivity.this);
                        Task newTask = new Task(title, desiredSessions);
                        if(dbHelper.addNewTask(newTask)){
                            setResult(RESULT_OK);
                            finish();
                        }
                    } else {
                        Toast.makeText(NewTaskActivity.this, "Please enter the number of desired sessions.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewTaskActivity.this, "Please enter a title.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}