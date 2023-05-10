package com.example.homescreen;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.TrashRecyclerView> {

    Context context;
    List<Note> listNote;

    public TrashAdapter(Context context){
        this.context = context;
    }

    public void setTrashData(List<Note> listNote){
        this.listNote = listNote;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrashRecyclerView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trash_list, parent, false);
        return new TrashRecyclerView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrashRecyclerView holder, int position) {
        DatabaseReference databaseNote = FirebaseDatabase.getInstance().getReference("Note");

        int notePosition = holder.getAdapterPosition();
        Note note = listNote.get(notePosition);
        if(note == null){
            return;
        }
        holder.trashNoteTitle.setText(note.getNoteTitle());
        holder.trashNoteContent.setText(note.getNoteContent());
        holder.trashNoteDay.setText(note.getNoteDateTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete note");
                builder.setMessage("This note will be deleted. This action cannot be undone");
                builder.setPositiveButton("Delete Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String noteID = listNote.get(notePosition).getNoteID();
                        Query removeNote = databaseNote.orderByChild("noteID").equalTo(noteID);
                        removeNote.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot noteSnapshot: snapshot.getChildren()) {
                                    noteSnapshot.getRef().removeValue();

                                }
                                if (notePosition >= 0 && notePosition < listNote.size()) {
                                    listNote.remove(notePosition);
                                    notifyDataSetChanged();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
//        String noteID = listNote.get(notePosition).getNoteID();
//        Query removeQuery = databaseReference.orderByChild("noteID").equalTo(noteID);
//        removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot noteSnapshot: snapshot.getChildren()) {
//                    noteSnapshot.getRef().removeValue();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        listNote.remove(notePosition);
//        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(listNote != null){
            return listNote.size();
        }
        return 0;
    }

    public class TrashRecyclerView extends RecyclerView.ViewHolder{

        TextView trashNoteTitle, trashNoteContent, trashNoteDay;

        public TrashRecyclerView(@NonNull View itemView) {
            super(itemView);
            trashNoteTitle = itemView.findViewById(R.id.trashNoteTitle);
            trashNoteContent = itemView.findViewById(R.id.trashNoteContent);
            trashNoteDay = itemView.findViewById(R.id.trashNoteDay);
        }
    }
}
