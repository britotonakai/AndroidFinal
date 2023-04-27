package com.example.homescreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends Fragment implements NoteAdapter.OnPopupMenuItemClickListener  {
    RecyclerView noteList;
    NoteAdapter noteAdapter;
    List<Note> list = new ArrayList<>();
//    EditNoteScreen editNoteScreen = new EditNoteScreen();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        noteList = view.findViewById(R.id.noteList);

        noteAdapter = new NoteAdapter(this::onPopupMenuItemClick, list);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        noteList.setLayoutManager(gridLayoutManager);
        noteAdapter.setNoteData(readNote());
        noteList.setAdapter(noteAdapter);
        return view;
    }


    private List<Note> readNote() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Note");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                    String noteTitle = String.valueOf(snapshot1.child("noteTitle").getValue());
                    String noteContent = String.valueOf(snapshot1.child("noteContent").getValue());
                    String noteDateTime = String.valueOf(snapshot1.child("noteDateTime").getValue());
                    list.add(new Note(noteID,noteTitle, noteContent, noteDateTime));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPopupMenuItemClick(MenuItem item) {
        DatabaseReference removeNote = FirebaseDatabase.getInstance().getReference();
        DatabaseReference editNote = FirebaseDatabase.getInstance().getReference("Note");

        switch(item.getItemId()){
            case R.id.btnEdit:

//                AlertDialog.Builder editBuilder = new AlertDialog.Builder(getContext());
//                editBuilder.setTitle("Edit Note");
//                LayoutInflater editLayoutInflater = LayoutInflater.from(getContext());
//                View editNoteView = editLayoutInflater.inflate(R.layout.edit_note, null);
//                editBuilder.setView(editNoteView);
//                EditText editTextTitle = editNoteView.findViewById(R.id.editTitle);
//                EditText editTextContent = editNoteView.findViewById(R.id.editContent);
//                editTextTitle.setText("Note Title");
//                editTextContent.setText("Note Content");
//                editBuilder
//                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        });
//                AlertDialog editDialog = editBuilder.create();
//                editDialog.getWindow().setLayout(10,20);
//                editDialog.show();
//                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.ConstrainLayout, editNoteScreen);
//                fragmentTransaction.commit();

//                IntentFilter intentFilter = new IntentFilter();
//                intentFilter.addAction("Send ID");
//                getContext().registerReceiver(new BroadcastReceiver() {
//                    @Override
//                    public void onReceive(Context context, Intent intent) {
//                        int position = intent.getIntExtra("ID", 0);
//                        Log.d("noteID Home Screen", list.get(position).getNoteID());
//                        DatabaseReference databaseReference = editNote.child(list.get(position).getNoteID());
//                        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d("ID Database", " " + databaseReference.getKey());
//
//                                    if(list.get(position).getNoteID() == databaseReference.getKey()){
//
//                                        fragmentTransaction.replace(R.id.ConstrainLayout, editNoteScreen);
//                                        editNoteScreen.editTextNoteTitle.setText(list.get(position).getNoteTitle());
//                                        editNoteScreen.editTextNoteContent.setText(list.get(position).getNoteContent());
//                                        Log.d("noteTitle", list.get(position).getNoteTitle());
//                                        Log.d("noteContent", list.get(position).getNoteContent());
//                                        fragmentTransaction.commit();
//
//                                    }
////                                    DataSnapshot document = task.getResult();
////                                    Log.d("Quan", document.);
//                                }
//                            }
//                        });
//                    }
//                }, intentFilter);


                break;
            case R.id.btnDelete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete note");
                builder.setMessage("Are you sure to delete this note ?");
                builder.setPositiveButton("Delete",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for(int i = 0; i < list.size(); i++){
                                    which = i;
                                }
                                for(int j = list.size() - 1; j >= 0; j--){
                                    if(j == which){
                                        String noteID = list.get(j).getNoteID();
                                        Query removeQuery = removeNote.child("Note").orderByChild("noteID").equalTo(noteID);
                                        removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot noteSnapshot: snapshot.getChildren()) {
                                                    noteSnapshot.getRef().removeValue();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        list.remove(j);
                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                noteAdapter.notifyDataSetChanged();
                            }
                        });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btnPin:
                Toast.makeText(getContext(), "Pin", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLock:
                Toast.makeText(getContext(), "Lock", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}