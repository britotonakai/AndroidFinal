package com.example.homescreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigation extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        AllNotesScreen allNotesScreen = new AllNotesScreen();
        NoteScreen noteScreen = new NoteScreen();
        HomeScreen homeScreen = new HomeScreen();
        TrashScreen trashScreen = new TrashScreen();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeScreen).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homeScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeScreen).commit();
                        return true;
                    case R.id.allNotesScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, allNotesScreen).commit();
                        return true;
                    case R.id.noteScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, noteScreen).commit();
                        return true;
                    case R.id.trashScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, trashScreen).commit();
                        return true;
                    default:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeScreen).commit();
                        return true;
                }
            }
        });

    }

}