package com.example.homescreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrashScreen extends Fragment {
    RecyclerView trashList;
    TrashAdapter trashAdapter;
    List<Note> listNote = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_trash_screen, container, false);
        trashList = view.findViewById(R.id.TrashList);
        trashAdapter = new TrashAdapter(getContext());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Note");databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNote.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String noteStatus = String.valueOf(snapshot1.child("noteStatus").getValue());
                    String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                    String noteTitle = String.valueOf(snapshot1.child("noteTitle").getValue());
                    String noteContent = String.valueOf(snapshot1.child("noteContent").getValue());
                    String noteDateTime = String.valueOf(snapshot1.child("noteDateTime").getValue());
                    if(noteStatus.equals("Deleted")){
                        listNote.add(new Note(noteID,noteTitle, noteContent, noteDateTime));
                    }
                }
                trashAdapter.setTrashData(listNote);
                trashAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        trashList.setLayoutManager(linearLayoutManager);
        trashList.setAdapter(trashAdapter);
        return view;
    }
}