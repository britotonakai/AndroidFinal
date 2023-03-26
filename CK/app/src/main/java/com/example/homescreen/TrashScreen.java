package com.example.homescreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TrashScreen extends AppCompatActivity {
    ImageButton btnHome, btnNote, btnCreate, btnDelete, btnSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_screen);
        btnHome = findViewById(R.id.btnHome);
        btnNote = findViewById(R.id.btnNote);
        btnCreate = findViewById(R.id.btnCreate);
        btnDelete = findViewById(R.id.btnDelete);
        btnSetting = findViewById(R.id.btnSetting);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}