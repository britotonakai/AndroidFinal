package com.example.homescreen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllFolderScreen extends Fragment {
    RecyclerView folderList;
    FolderAdapter folderAdapter;
    ImageButton btnNote, btnFolder;
    Button btnBack;
    List<Folder> folder = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_allfolder_screen, container, false);
        folderList = view.findViewById(R.id.folderList);

        btnFolder = view.findViewById(R.id.btnFolder);
        btnNote = view.findViewById(R.id.btnNote);
        btnBack  = view.findViewById(R.id.btnBack);

        btnBack.setVisibility(View.INVISIBLE);

        folderAdapter = new FolderAdapter(getContext());
        DatabaseReference databaseFolder = FirebaseDatabase.getInstance().getReference("Folder");

        btnNote.setOnClickListener(view12 -> {
            Fragment current = FragmentManager.findFragment(view12);
            FragmentTransaction transaction = current.getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.allFolderScreen, new NoteScreen());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnFolder.setOnClickListener(view13 -> {
            Dialog folderDialog = new Dialog(getContext());
            LayoutInflater folderInflater = LayoutInflater.from(getContext());
            View view1 = folderInflater.inflate(R.layout.activity_create_folder, null);
            EditText editTextFolderName = view1.findViewById(R.id.editTextFolderName);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat day_month_year_time = new SimpleDateFormat("dd/LLLL/yyyy");
            String dateTime = day_month_year_time.format(calendar.getTime());
            Button btnCreate = view1.findViewById(R.id.btnCreateFolder);
            Button btnCancel = view1.findViewById(R.id.btnCancelFolder);

            btnCreate.setOnClickListener(view2 -> {
                String key = databaseFolder.push().getKey();
                folder = new ArrayList<>();
                Folder currentFolder = new Folder(key, editTextFolderName.getText().toString(), dateTime);
                folder.add(currentFolder);
                databaseFolder.child(key).setValue(currentFolder)
                        .addOnSuccessListener(aVoid -> {

                        })
                        .addOnFailureListener(e -> {
                            // Xử lý khi lưu trữ thất bại
                            Log.d(TAG, "Data could not be saved: " + e.getMessage());
                        });
                folderDialog.dismiss();
            });

            btnCancel.setOnClickListener(view2 -> folderDialog.dismiss());

            folderDialog.setContentView(view1);
            folderDialog.getWindow().setLayout(1350, 700);
            folderDialog.show();
        });

        databaseFolder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                folder.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String folderID = String.valueOf(snapshot1.child("folderID").getValue());
                    String folderName = String.valueOf(snapshot1.child("folderName").getValue());
                    String folderDate = String.valueOf(snapshot1.child("folderDay").getValue());
                    folder.add(new Folder(folderID, folderName, folderDate));
                }
                folderAdapter.setFolderData(folder);
                folderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        folderList.setLayoutManager(linearLayoutManager);
        folderList.setAdapter(folderAdapter);
        return view;
    }


}