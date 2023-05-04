package com.example.homescreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteRecyclerView>{
    Context context;
    List<Note> listNote;

    public NoteAdapter(Context context){
        this.context = context;
    }

    public void setNoteData(List<Note> listNote){
        this.listNote = listNote;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteRecyclerView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list, parent, false);
        return new NoteRecyclerView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerView holder, int position) {
        Note note = listNote.get(position);
        if(note == null){
            return;
        }
        holder.noteTitle.setText(note.getNoteTitle());
        holder.noteDateTime.setText(note.getNoteDateTime());
        holder.textViewLastEdit.setText("Last edit");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.inflate(R.menu.list_note_button);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        DatabaseReference databaseNote = FirebaseDatabase.getInstance().getReference("Note");

                        switch(menuItem.getItemId()){
                            case R.id.btnEdit:
                                Dialog editDialog = new Dialog(context);
                                LayoutInflater edit = LayoutInflater.from(context);
                                View editView = edit.inflate(R.layout.edit_note, null);
                                editDialog.setContentView(editView);
                                editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                                EditText noteTitle = editView.findViewById(R.id.editTitle);
                                EditText noteContent = editView.findViewById(R.id.editContent);
                                TextView noteCurrentDay = editView.findViewById(R.id.textViewEditCurrentDay);
                                Button btnEdit = editView.findViewById(R.id.btnConfirmEdit);
                                Button btnCancel = editView.findViewById(R.id.btnCancel);
                                ImageButton btnShare = editView.findViewById(R.id.btnEditShare);

                                noteCurrentDay.setText(note.getNoteDateTime());
                                noteTitle.setText(note.getNoteTitle());
                                noteContent.setText(note.getNoteContent());

                                btnShare.setOnClickListener(view1 -> {
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, noteTitle.getText().toString());
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, noteContent.getText().toString());
                                    shareIntent.setType("text/plain");

                                    if(shareIntent.resolveActivity(context.getPackageManager()) != null){
                                        context.startActivity(shareIntent);
                                    }

                                });

                                btnEdit.setOnClickListener(view12 -> {
                                    DatabaseReference noteChild = databaseNote.child(note.getNoteID());
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat day_month_year_time = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                                    String dateTime = day_month_year_time.format(calendar.getTime());
                                    noteCurrentDay.setText(dateTime);

                                    Map<String, Object> updateNote = new HashMap<>();
                                    updateNote.put("noteTitle", noteTitle.getText().toString());
                                    updateNote.put("noteContent", noteContent.getText().toString());
                                    updateNote.put("noteDateTime", noteCurrentDay.getText().toString());
                                    noteChild.updateChildren(updateNote);
                                    editDialog.dismiss();
                                    notifyDataSetChanged();
                                });

                                btnCancel.setOnClickListener(view13 -> editDialog.dismiss());
                                editDialog.show();
                                return true;
                            case R.id.btnDelete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Delete note");
                                builder.setMessage("Are you sure to delete this note ?");
                                builder.setPositiveButton("Delete",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final int position = holder.getAdapterPosition();
                                                if(position != RecyclerView.NO_POSITION){
                                                    Map<String, Object> deleteNote = new HashMap<>();
                                                    deleteNote.put("noteStatus", "Deleted");
                                                    String noteID = listNote.get(position).getNoteID();
                                                    DatabaseReference databaseReference = databaseNote.child(noteID);
                                                    databaseReference.updateChildren(deleteNote)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    String noteIDTemp = noteID;
                                                                    for(int i =0; i<listNote.size(); i++){
                                                                        if(listNote.get(i).getNoteID().equals(noteIDTemp)){
                                                                            holder.itemView.setVisibility(View.GONE);
                                                                            listNote.remove(position);
                                                                            notifyDataSetChanged();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                }
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
                                int position = holder.getAdapterPosition();
                                if(position != RecyclerView.NO_POSITION){
                                    Map<String, Object> pinnedNote = new HashMap<>();
                                    pinnedNote.put("notePin", "Pinned");
                                    String noteID = listNote.get(position).getNoteID();
                                    DatabaseReference databaseReference = databaseNote.child(noteID);
                                    databaseReference.updateChildren(pinnedNote)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });
                                }
                                return true;
                            case R.id.btnLock:
                                int lockPosition = holder.getAdapterPosition();
                                if(lockPosition != RecyclerView.NO_POSITION){
                                   AlertDialog.Builder lockBuilder = new AlertDialog.Builder(context);
                                    lockBuilder.setTitle("Lock this note");
                                    lockBuilder.setMessage("Please enter password to lock this note");
                                    LayoutInflater lockLayout = LayoutInflater.from(context);
                                    View lockView = lockLayout.inflate(R.layout.lock_note_dialog, null);
                                    EditText editTextPassword = lockView.findViewById(R.id.editTextNotePassword);
                                    lockBuilder.setView(lockView);
                                    lockBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Map<String, Object> lockNote = new HashMap<>();
                                            lockNote.put("noteLock", "Locked");
                                            lockNote.put("notePassword", editTextPassword.getText().toString());
                                            String noteID = listNote.get(lockPosition).getNoteID();
                                            DatabaseReference databaseReference = databaseNote.child(noteID);
                                            databaseReference.updateChildren(lockNote)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });
                                        }
                                    })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                    AlertDialog lockDialog = lockBuilder.create();
                                    lockDialog.show();
                                }
                                return true;
                            default:
                                int defaultPosition = holder.getAdapterPosition();
                                if(defaultPosition != RecyclerView.NO_POSITION && listNote.get(defaultPosition).getNoteLock().equals("Locked")){
                                    AlertDialog.Builder lockBuilder = new AlertDialog.Builder(context);
                                    lockBuilder.setTitle("Unlock this note");
                                    lockBuilder.setMessage("Please enter password to unlock this note");
                                    LayoutInflater lockLayout = LayoutInflater.from(context);
                                    View lockView = lockLayout.inflate(R.layout.lock_note_dialog, null);
                                    EditText editTextPassword = lockView.findViewById(R.id.editTextNotePassword);
                                    lockBuilder.setView(lockView);
                                    lockBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                }
                                            });
                                    AlertDialog lockDialog = lockBuilder.create();
                                    lockDialog.show();
                                }
                        }
                        return true;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        if(listNote != null){
            return listNote.size();
        }
        return 0;
    }


    public class NoteRecyclerView extends RecyclerView.ViewHolder{

        TextView noteTitle, noteDateTime, textViewLastEdit;

        public NoteRecyclerView(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteDateTime = itemView.findViewById(R.id.noteDateTime);
            textViewLastEdit = itemView.findViewById(R.id.textViewLastEdit);
        }
    }
}
