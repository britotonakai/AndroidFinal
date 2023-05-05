package com.example.homescreen;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskScreen_AddNewTask extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText newTaskText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task);

        mDatabase = FirebaseDatabase.getInstance().getReference("Task");

        newTaskText = findViewById(R.id.newTaskText);
        Button btnSaveTask = findViewById(R.id.btnSaveTask);
        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        String task = newTaskText.getText().toString().trim();

        if (TextUtils.isEmpty(task)) {
            Toast.makeText(this, "Please enter a task", Toast.LENGTH_LONG).show();
        } else {
            String taskId = mDatabase.push().getKey();
            Task newTask = new Task(taskId, 0, task);
            mDatabase.child(taskId).setValue(newTask);

            Toast.makeText(this, "Task added successfully", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}