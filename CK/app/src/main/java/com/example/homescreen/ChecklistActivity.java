package com.example.homescreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChecklistActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView tasks;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private static final int REQUEST_CODE_EDIT_TASK = 1;
    private static final int MENU_ITEM_EDIT = 1;
    private static final int MENU_ITEM_DELETE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_activity);

        mDatabase = FirebaseDatabase.getInstance().getReference("Task");

        tasks = findViewById(R.id.tasks);
        tasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(this);
        tasks.setAdapter(adapter);

        taskList = new ArrayList<>();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Task task = snapshot.getValue(Task.class);
                taskList.add(task);
                adapter.setTasks(taskList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                Task task = snapshot.getValue(Task.class);
                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getId().equals(task.getId())) {
                        taskList.set(i, task);
                        break;
                    }
                }
                adapter.setTasks(taskList);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Task task = snapshot.getValue(Task.class);
                taskList.remove(task);
                adapter.setTasks(taskList);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChecklistActivity.this, TaskScreen_AddNewTask.class);
                startActivity(intent);
            }
        });
    }
}