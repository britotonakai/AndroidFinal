package com.example.homescreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ChecklistActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView tasks;
    private AdapterTask adapterTask;

    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_activity);
        getSupportActionBar().hide();

        taskList = new ArrayList<>();

        tasks = findViewById(R.id.tasks);
        tasks.setLayoutManager(new LinearLayoutManager(this));
        adapterTask = new AdapterTask(this);
        tasks.setAdapter(adapterTask);

        Task task = new Task();
        task.setTask("This is a Test Task");
        task.setStatus(0);
        task.setId(1);

        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);
        taskList.add(task);

        adapterTask.setTasks(taskList);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){

    }
}