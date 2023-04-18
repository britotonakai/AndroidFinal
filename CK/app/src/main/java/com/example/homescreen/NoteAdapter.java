package com.example.homescreen;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteRecyclerView> implements PopupMenu.OnMenuItemClickListener{
    Context context;
    List<Note> listNote;

    public NoteAdapter(Context context){
        this.context = context;
    }

    public void setNoteData(List<Note> listNote){
        this.listNote = listNote;
        notifyDataSetChanged();
    }

    public NoteAdapter getAdapter(){
        return new NoteAdapter(context);
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
                popupMenu.setOnMenuItemClickListener(NoteAdapter.this);
                popupMenu.inflate(R.menu.list_note_button);
                popupMenu.show();
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

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        DatabaseReference removeNote = FirebaseDatabase.getInstance().getReference("Note");

        switch(menuItem.getItemId()){
            case R.id.btnEdit:

                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
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
                                        listNote.remove(j);
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
