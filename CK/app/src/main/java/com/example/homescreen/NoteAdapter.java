package com.example.homescreen;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteRecyclerView>{
    Context context;
    List<Note> listNote = new ArrayList<>();
    OnPopupMenuItemClickListener mListener;


    public NoteAdapter(Context context){
        this.context = context;
    }

    public NoteAdapter(OnPopupMenuItemClickListener listener, List<Note> noteItem){
        this.mListener = listener;
        this.listNote = noteItem;
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
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.list_note_button, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int position = holder.getAdapterPosition();
                        String noteID = listNote.get(position).getNoteID();

                        Intent sendPosition = new Intent();
                        sendPosition.setAction("Send ID");
                        sendPosition.putExtra("ID", noteID);
                        view.getContext().sendBroadcast(sendPosition);

                        if (mListener != null) {
                            mListener.onPopupMenuItemClick(item);
                        }
                        return true;
                    }
                });
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

    public interface OnPopupMenuItemClickListener {
        void onPopupMenuItemClick(MenuItem item);
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


