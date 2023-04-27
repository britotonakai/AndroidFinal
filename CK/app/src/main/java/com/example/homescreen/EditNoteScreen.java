package com.example.homescreen;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditNoteScreen extends Fragment {

    Button btnSave;
    ImageButton btnPen, btnTable, btnImage, btnBulletList, btnText, btnCheckList, btnShare;
    EditText editTextNoteTitle, editTextNoteContent;
    TextView textViewCurrentDay;
    MainScreen mainScreen;
    String noteTitle = "", noteContent = "";
    List<Note> noteList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_note, container, false);
//        btnSave = view.findViewById(R.id.btnEditSave);
        btnShare = view.findViewById(R.id.btnEditShare);
        editTextNoteTitle = view.findViewById(R.id.editTitle);
        editTextNoteContent = view.findViewById(R.id.editContent);
        textViewCurrentDay = view.findViewById(R.id.textViewEditCurrentDay);

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
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Note");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                    String noteTitle = String.valueOf(snapshot1.child("noteTitle").getValue());
                    String noteContent = String.valueOf(snapshot1.child("noteContent").getValue());
                    String noteDateTime = String.valueOf(snapshot1.child("noteDateTime").getValue());
                    editTextNoteContent.setText(noteContent);
                    editTextNoteTitle.setText(noteTitle);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }

//    public String getNoteTitle(){
//        return noteTitle;
//    }
//
//    public String getNoteContent(){
//        return noteContent;
//    }
//
//    public void readNote(){
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference databaseReference = firebaseDatabase.getReference("Note");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot snapshot1 : snapshot.getChildren()){
//                    String noteID = String.valueOf(snapshot1.child("noteID").getValue());
//                    String noteTitle = String.valueOf(snapshot1.child("noteTitle").getValue());
//                    String noteContent = String.valueOf(snapshot1.child("noteContent").getValue());
//                    String noteDateTime = String.valueOf(snapshot1.child("noteDateTime").getValue());
//                    noteList.add(new Note(noteID,noteTitle, noteContent, noteDateTime));
//                }
//                IntentFilter positionFilter = new IntentFilter();
//                positionFilter.addAction("Send ID");
//                getContext().registerReceiver(new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        int position = intent.getIntExtra("ID", 0);
//
//                        Log.d("noteID Home Screen", noteList.get(position).getNoteID());
//
//                        Log.d("noteTitle Home Screen", noteList.get(position).getNoteTitle());
//
//                        Log.d("noteContent Home Screen", noteList.get(position).getNoteContent());
//                    }
//                }, positionFilter);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }
