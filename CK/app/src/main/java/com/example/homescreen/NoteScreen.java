package com.example.homescreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

public class NoteScreen extends Fragment {
    Button btnBack;
    ImageButton btnPen, btnTable, btnImage, btnBulletList, btnText, btnCheckList, btnShare;
    MainScreen mainScreen;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_note, container, false);
        btnBack = view.findViewById(R.id.btnBack);
        mainScreen = (MainScreen)getActivity();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backIntent = new Intent(getActivity(), MainScreen.class);
                startActivity(backIntent);
            }
        });
        return view;
    }

}