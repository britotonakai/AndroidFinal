package com.example.homescreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdapterNote extends RecyclerView.Adapter<AdapterNote.NoteRecyclerView>{
    Context context;
    List<Note> listNote;

    public AdapterNote(Context context){
        this.context = context;
    }

    public void setNoteData(List<Note> listNote){
        this.listNote = listNote;
        notifyDataSetChanged();
    }

    public AdapterNote getAdapter(){
        return new AdapterNote(context);
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
                        DatabaseReference removeNote = FirebaseDatabase.getInstance().getReference("Note");

                        switch(menuItem.getItemId()){
                            case R.id.btnEdit:
                                Dialog editDialog = new Dialog(context);
                                LayoutInflater edit = LayoutInflater.from(context);
                                View editView = edit.inflate(R.layout.edit_note, null);
                                editDialog.setContentView(editView);
                                editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//                                EditScreenDialog editScreenDialog = new EditScreenDialog(context);
                                Log.d("Note Tilte Edit", note.getNoteTitle());
//                                editScreenDialog.editTitle.setText(note.getNoteTitle());
//                                editScreenDialog.editContent.setText(note.getNoteContent());
//                                editScreenDialog.show();
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
                                                for(int i = 0; i < listNote.size(); i++){
                                                    which = i;
                                                }
                                                for(int j = listNote.size() - 1; j >= 0; j--){
                                                    if(j == which){
                                                        String noteID = listNote.get(j).getNoteID();
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
                                                        listNote.remove(j);
                                                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                notifyDataSetChanged();
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
                                Toast.makeText(context, "Pin", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.btnLock:
                                Toast.makeText(context, "Lock", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
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
