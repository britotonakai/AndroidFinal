package com.example.homescreen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteScreen extends Fragment {
    Button btnSave;
    ImageButton btnPen, btnTable, btnImage, btnBulletList, btnText, btnCheckList, btnShare;
    EditText editTextNoteTitle, editTextNoteContent;
    TextView textViewCurrentDay;
    MainScreen mainScreen;
    List<Note> noteList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_new_note, container, false);
        btnSave = view.findViewById(R.id.btnSave);
        btnShare = (ImageButton) view.findViewById(R.id.btnShare);
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = view.findViewById(R.id.editTextNoteContent);
        textViewCurrentDay = view.findViewById(R.id.textViewCurrentDay);
        mainScreen = (MainScreen)getActivity();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat day_month_year_time = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
        String dateTime = day_month_year_time.format(calendar.getTime());
        textViewCurrentDay.setText(dateTime);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Note");

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, editTextNoteTitle.getText().toString());
                shareIntent.putExtra(Intent.EXTRA_TEXT, editTextNoteContent.getText().toString());
                shareIntent.setType("text/plain");

                if(shareIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(shareIntent);
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = databaseReference.push().getKey();
                noteList = new ArrayList<>();
                Note note = new Note(key,editTextNoteTitle.getText().toString(), editTextNoteContent.getText().toString(),textViewCurrentDay.getText().toString());
                noteList.add(note);
                databaseReference.child(String.valueOf(key)).setValue(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý khi lưu trữ thất bại
                                Log.d(TAG, "Data could not be saved: " + e.getMessage());
                            }
                        });
                Intent backIntent = new Intent(getActivity(), MainScreen.class);
                startActivity(backIntent);
            }
        });
        return view;
    }

}