package com.example.homescreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationScreen extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment homeScreen, noteScreen, trashScreen, allFolderScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        noteScreen = new NoteScreen();
        homeScreen = new HomeScreen();
        trashScreen = new TrashScreen();
        allFolderScreen = new AllFolderScreen();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeScreen).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homeScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeScreen).commit();
                        return true;
                    case R.id.allFolderScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, allFolderScreen).commit();
                        return true;
                    case R.id.noteScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, noteScreen).commit();
                        return true;
                    case R.id.trashScreen:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, trashScreen).commit();
                        return true;
                }
                return false;
            }
        });

    }

}