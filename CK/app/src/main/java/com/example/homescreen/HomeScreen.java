package com.example.homescreen;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeScreen extends Fragment {
    RecyclerView noteList;
    NoteAdapter noteAdapter;
    SearchView searchView;
    TextView textViewRecently, textViewPinned, textViewDay;
    List<Note> list = new ArrayList<>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Note");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        noteList = view.findViewById(R.id.noteList);
        noteAdapter = new NoteAdapter(getActivity());
        searchView = view.findViewById(R.id.searchView);
        textViewDay = view.findViewById(R.id.textViewHomeDay);
        textViewRecently = view.findViewById(R.id.textViewRecently);
        textViewPinned = view.findViewById(R.id.textViewPinned);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat day_month_year_time = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
        String dateTime = day_month_year_time.format(calendar.getTime());
        textViewDay.setText(dateTime);

        textViewRecently.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewRecently.setTextColor(Color.parseColor("#E28F83"));
                textViewRecently.setTypeface(textViewRecently.getTypeface(), Typeface.BOLD);
                textViewPinned.setTextColor(Color.BLACK);
                textViewPinned.setTypeface(null, Typeface.NORMAL);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                      for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            String noteStatus = String.valueOf(snapshot1.child("noteStatus").getValue());
                            String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                            String noteTitle = String.valueOf(snapshot1.child("noteTitle").getValue());
                            String noteContent = String.valueOf(snapshot1.child("noteContent").getValue());
                            String noteDateTime = String.valueOf(snapshot1.child("noteDateTime").getValue());
                            if(!noteStatus.equals("Deleted")){
                                list.add(new Note(noteID,noteTitle, noteContent, noteDateTime));
                            }
                        }

                        SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                        Collections.sort(list, new Comparator<Note>() {
                            @Override
                            public int compare(Note note1, Note note2) {
                                Date date1 = null;
                                Date date2 = null;
                                try {
                                    date1 = format.parse(note1.noteDateTime);
                                    date2 = format.parse(note2.noteDateTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return date2.compareTo(date1);
                            }
                        });

                        noteAdapter.setNoteData(list);
                        noteAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        textViewRecently.performClick();

        textViewPinned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewPinned.setTextColor(Color.parseColor("#8E9775"));
                textViewPinned.setTypeface(textViewRecently.getTypeface(), Typeface.BOLD);
                textViewRecently.setTextColor(Color.BLACK);
                textViewRecently.setTypeface(null, Typeface.NORMAL);
                DatabaseReference notePin = FirebaseDatabase.getInstance().getReference("Note");
                notePin.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot notePinSnapshot : snapshot.getChildren()){
                            String notePin = String.valueOf(notePinSnapshot.child("notePin").getValue());
                            String noteStatus = String.valueOf(notePinSnapshot.child("noteStatus").getValue());
                            String noteID = String.valueOf(notePinSnapshot.child("noteID").getValue());
                            String noteTitle = String.valueOf(notePinSnapshot.child("noteTitle").getValue());
                            String noteContent = String.valueOf(notePinSnapshot.child("noteContent").getValue());
                            String noteDateTime = String.valueOf(notePinSnapshot.child("noteDateTime").getValue());
                            if(!noteStatus.equals("Deleted") && notePin.equals("Pinned")){
                                list.add(new Note(noteID,noteTitle, noteContent, noteDateTime));
                            }
                        }
                        noteAdapter.setNoteData(list);
                        noteAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        noteList.setLayoutManager(gridLayoutManager);
        noteList.setAdapter(noteAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Note> filteredList = new ArrayList<>();
                for (Note note : list) {
                    if (note.getNoteTitle().toLowerCase().contains(s.toLowerCase()) || note.getNoteContent().toLowerCase().contains(s.toLowerCase())) {
                        filteredList.add(note);
                    }
                }
                noteAdapter.setNoteData(filteredList);
                return true;
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
