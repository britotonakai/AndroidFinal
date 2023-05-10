package com.example.homescreen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.Image;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteRecyclerView>{
    Context context;
    List<Note> listNote;
    boolean hasBullet = false, hasBold = false, hasItalic =false, hasUnderline = false,
            hasTitle = false, hasHeading = false, hasSubheading = false, hasBody = false;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_PERMISSIONS = 123;
    DatabaseReference databaseNote = FirebaseDatabase.getInstance().getReference("Note");

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
        int position1= holder.getAdapterPosition();
        Note note = listNote.get(position1);
        if(note == null){
            return;
        }
        databaseNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNote.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String noteLock = String.valueOf(snapshot1.child("noteLock").getValue());
                    String noteStatus = String.valueOf(snapshot1.child("noteStatus").getValue());
                    String notePassword = String.valueOf(snapshot1.child("notePassword").getValue());
                    String notePin = String.valueOf(snapshot1.child("notePin").getValue());
                    String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                    String noteTitle = String.valueOf(snapshot1.child("noteTitle").getValue());
                    String noteContent = String.valueOf(snapshot1.child("noteContent").getValue());
                    String noteDateTime = String.valueOf(snapshot1.child("noteDateTime").getValue());
                    if(!noteStatus.equals("Deleted")){
                        listNote.add(new Note(noteID, noteTitle, noteContent, noteDateTime, notePin, noteLock, notePassword));
                    }

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                    Collections.sort(listNote, new Comparator<Note>() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

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
                        switch(menuItem.getItemId()){
                            case R.id.btnEdit:
                                if(listNote.get(position1).getNoteLock().equals("Locked")){
                                    Dialog lockDialog = new Dialog(context);
                                    LayoutInflater lock = LayoutInflater.from(context);
                                    View lockView = lock.inflate(R.layout.lock_note_dialog, null);
                                    lockDialog.setContentView(lockView);
                                    Button btnDone = lockView.findViewById(R.id.btnLockDone);
                                    Button btnCancel = lockView.findViewById(R.id.btnLockCancel);
                                    btnDone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            EditText editTextPassword = lockView.findViewById(R.id.editTextNotePassword);
                                            String password = editTextPassword.getText().toString();
                                            String passwordUser = listNote.get(position1).getNotePassword();
                                            if(password.equals(passwordUser)){
                                                lockDialog.dismiss();
                                                int EditPosition = holder.getAdapterPosition();
                                                showEditDialog(EditPosition);
                                            }else{
                                                Toast.makeText(context, "Wrong Password, please enter again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            lockDialog.dismiss();
                                        }
                                    });
                                    lockDialog.show();
                                }
                                else{
                                    databaseNote.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String noteTemp = " ";
                                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                                                Collections.sort(listNote, new Comparator<Note>() {
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
                                                noteTemp = noteID;
                                            }
                                            if(listNote.get(position1).getNoteID().equals(noteTemp)){
                                                showEditDialog(position1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                return true;
                            case R.id.btnDelete:
                                if(listNote.get(position1).getNoteLock().equals("Locked")){
                                    Dialog lockDialog = new Dialog(context);
                                    LayoutInflater lock = LayoutInflater.from(context);
                                    View lockView = lock.inflate(R.layout.lock_note_dialog, null);
                                    lockDialog.setContentView(lockView);
                                    Button btnDone = lockView.findViewById(R.id.btnLockDone);
                                    Button btnCancel = lockView.findViewById(R.id.btnLockCancel);
                                    btnCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            lockDialog.dismiss();
                                        }
                                    });
                                    btnDone.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            lockDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle("Delete note");
                                            builder.setMessage("Are you sure to delete this note ?");
                                            builder.setPositiveButton("Delete",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            databaseNote.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    String noteTemp = " ";
                                                                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                                        String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                                                                        SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                                                                        Collections.sort(listNote, new Comparator<Note>() {
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
                                                                        noteTemp = noteID;
                                                                    }
                                                                    if(listNote.get(position1).getNoteID().equals(noteTemp)){
                                                                        if(position1 != RecyclerView.NO_POSITION){
                                                                            Map<String, Object> deleteNote = new HashMap<>();
                                                                            deleteNote.put("noteStatus", "Deleted");
                                                                            DatabaseReference databaseDelete = databaseNote.child(noteTemp);
                                                                            String finalNoteTemp = noteTemp;
                                                                            databaseDelete.updateChildren(deleteNote)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            if(listNote.get(position1).getNoteID().equals(finalNoteTemp)){
                                                                                                Log.d("Success", finalNoteTemp);
                                                                                                Log.d("Success1", listNote.get(position1).getNoteID());
                                                                                                holder.itemView.setVisibility(View.GONE);
                                                                                                listNote.remove(finalNoteTemp);
                                                                                                notifyDataSetChanged();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
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
                                        }
                                    });
                                    lockDialog.show();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete note");
                                    builder.setMessage("Are you sure to delete this note ?");
                                    builder.setPositiveButton("Delete",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    databaseNote.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String noteTemp = " ";
                                                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                                                String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                                                                SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                                                                Collections.sort(listNote, new Comparator<Note>() {
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
                                                                noteTemp = noteID;
                                                            }
                                                            if(listNote.get(position1).getNoteID().equals(noteTemp)){
                                                                if(position1 != RecyclerView.NO_POSITION){
                                                                    String finalNoteTemp = noteTemp;
                                                                    Map<String, Object> deleteNote = new HashMap<>();
                                                                    deleteNote.put("noteStatus", "Deleted");
                                                                    DatabaseReference databaseDelete1 = databaseNote.child(noteTemp);
                                                                    databaseDelete1.updateChildren(deleteNote)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    if(listNote.get(position1).getNoteID().equals(finalNoteTemp)){
                                                                                        Log.d("Success", finalNoteTemp);
                                                                                        Log.d("Success1", listNote.get(position1).getNoteID());
                                                                                        holder.itemView.setVisibility(View.GONE);
                                                                                        listNote.remove(finalNoteTemp);
                                                                                        notifyDataSetChanged();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
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
                                }
                                return true;
                            case R.id.btnPin:
                                pinNote(position1);
                                return true;
                            case R.id.btnLock:
                                lockNote(position1);
                                return true;
                        }
                        return false;
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

    private void showEditDialog(int position){
        Note note = listNote.get(position);
        if(note == null){
            return;
        }

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
        ImageButton btnBold = editView.findViewById(R.id.btnEditBold);
        ImageButton btnItalic = editView.findViewById(R.id.btnEditItalic);
        ImageButton btnUnderline = editView.findViewById(R.id.btnEditUnderline);
        ImageButton btnShare = editView.findViewById(R.id.btnEditShare);
        ImageButton btnAlignLeft = editView.findViewById(R.id.btnEditAlignLeft);
        ImageButton btnAlignRight = editView.findViewById(R.id.btnEditAlignRight);
        ImageButton btnBulletList = editView.findViewById(R.id.btnEditBulletList);
        ImageButton btnImage = editView.findViewById(R.id.btnEditImage);
        ImageButton btnCheckList = editView.findViewById(R.id.btnEditCheckList);


        TextView btnTitle = editView.findViewById(R.id.btnEditTitle);
        TextView btnHeading = editView.findViewById(R.id.btnEditHeading);
        TextView btnSubheading = editView.findViewById(R.id.btnEditSubheading);
        TextView btnBody = editView.findViewById(R.id.btnEditBody);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        RelativeSizeSpan titleSizeSpan = new RelativeSizeSpan(1.5f);
        RelativeSizeSpan headingSizeSpan = new RelativeSizeSpan(1.3f);
        RelativeSizeSpan subheadingSizeSpan = new RelativeSizeSpan(1.2f);
        RelativeSizeSpan bodySizeSpan = new RelativeSizeSpan(1.0f);

        noteCurrentDay.setText(note.getNoteDateTime());
        noteTitle.setText(note.getNoteTitle());
        noteContent.setText(note.getNoteContent());

        btnBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasBold = !hasBold;
                applyTextChange(noteContent);
            }
        });
        btnItalic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasItalic = !hasItalic;
                applyTextChange(noteContent);
            }
        });
        btnUnderline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasUnderline = !hasUnderline;
                applyTextChange(noteContent);
            }
        });

        btnAlignRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                int selectionEnd = noteContent.getSelectionEnd();
                Editable text = noteContent.getText();
                String indent = "     "; // 5 spaces for each indent level

                if (selectionStart == selectionEnd) {
                    // Insert an indent at the start of the current line
                    int lineStart = selectionStart;
                    while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                        lineStart--;
                    }
                    text.insert(lineStart, indent);
                    noteContent.setSelection(selectionStart + indent.length());
                } else {
                    // Indent each line of the selected text
                    String selectedText = text.subSequence(selectionStart, selectionEnd).toString();
                    String[] lines = selectedText.split("\\r?\\n");
                    StringBuilder sb = new StringBuilder();
                    for (String line : lines) {
                        sb.append(indent).append(line).append("\n");
                    }
                    text.replace(selectionStart, selectionEnd, sb.toString());
                    noteContent.setSelection(selectionEnd + indent.length() * lines.length);
                }

                // Enable the decrease button after indenting
                btnAlignLeft.setEnabled(true);
            }
        });
        //Decrease indent
        btnAlignLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                int selectionEnd = noteContent.getSelectionEnd();
                Editable text = noteContent.getText();
                String indent = "     "; // 5 spaces for each indent level

                if (selectionStart == selectionEnd) {
                    // Remove an indent at the start of the current line
                    int lineStart = selectionStart;
                    while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                        lineStart--;
                    }
                    String line = text.subSequence(lineStart, selectionStart).toString();
                    if (line.startsWith(indent)) {
                        text.delete(lineStart, lineStart + indent.length());
                        noteContent.setSelection(selectionStart - indent.length());
                    }
                } else {
                    // Remove indent from each line of the selected text
                    String selectedText = text.subSequence(selectionStart, selectionEnd).toString();
                    String[] lines = selectedText.split("\\r?\\n");
                    StringBuilder sb = new StringBuilder();
                    boolean hasIndent = false;
                    for (String line : lines) {
                        if (line.startsWith(indent)) {
                            sb.append(line.substring(indent.length())).append("\n");
                            hasIndent = true;
                        } else {
                            sb.append(line).append("\n");
                        }
                    }
                    text.replace(selectionStart, selectionEnd, sb.toString());
                    noteContent.setSelection(selectionEnd - indent.length() * lines.length);
                    // Disable decrease button if no lines have indent left
                    if (!hasIndent) {
                        btnAlignLeft.setEnabled(false);
                    }
                }

                // Disable decrease button if cursor is at start of text
                if (noteContent.getSelectionStart() == 0) {
                    btnAlignLeft.setEnabled(false);
                }
            }
        });

        btnTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                String text = noteContent.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }

                String currentLine = text.substring(lineStart, lineEnd);
                SpannableString line = new SpannableString(currentLine);

                if(!hasTitle){
                    line.setSpan(titleSizeSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    line.setSpan(boldSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hasTitle = !hasTitle;
                }
                else{
                    line.removeSpan(titleSizeSpan);
                    line.removeSpan(boldSpan);
                    hasTitle = false;
                }
                SpannableStringBuilder builder = new SpannableStringBuilder(text);
                builder.replace(lineStart, lineEnd,line);
                noteContent.setText(builder);
                noteContent.setSelection(lineEnd);
            }
        });
        btnHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                String text = noteContent.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }

                String currentLine = text.substring(lineStart, lineEnd);
                SpannableString line = new SpannableString(currentLine);

                if(!hasHeading){
                    line.setSpan(headingSizeSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hasHeading = !hasHeading;
                }
                else{
                    line.removeSpan(headingSizeSpan);
                    hasHeading = false;
                }
                SpannableStringBuilder builder = new SpannableStringBuilder(text);
                builder.replace(lineStart, lineEnd,line);
                noteContent.setText(builder);
                noteContent.setSelection(lineEnd);
            }
        });
        btnSubheading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                String text = noteContent.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }

                String currentLine = text.substring(lineStart, lineEnd);
                SpannableString line = new SpannableString(currentLine);

                if(!hasSubheading){
                    line.setSpan(subheadingSizeSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    line.setSpan(boldSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hasSubheading = !hasSubheading;
                }
                else{
                    line.removeSpan(subheadingSizeSpan);
                    line.removeSpan(boldSpan);
                    hasSubheading = false;
                }
                SpannableStringBuilder builder = new SpannableStringBuilder(text);
                builder.replace(lineStart, lineEnd,line);
                noteContent.setText(builder);
                noteContent.setSelection(lineEnd);
            }
        });
        btnBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                String text = noteContent.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }

                String currentLine = text.substring(lineStart, lineEnd);
                SpannableString line = new SpannableString(currentLine);
                if(hasTitle){
                    line.removeSpan(titleSizeSpan);
                    hasTitle = !hasTitle;
                } else if (hasHeading) {
                    line.removeSpan(headingSizeSpan);
                    hasHeading = !hasHeading;
                } else if (hasSubheading) {
                    line.removeSpan(subheadingSizeSpan);
                    hasSubheading = !hasSubheading;
                }else{
                    line.setSpan(bodySizeSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                SpannableStringBuilder builder = new SpannableStringBuilder(text);
                builder.replace(lineStart, lineEnd,line);
                noteContent.setText(builder);
                noteContent.setSelection(lineEnd);
            }
        });

        btnBulletList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = noteContent.getSelectionStart();
                String text = noteContent.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }
                String currentLine = text.substring(lineStart, lineEnd);
                int bulletIndex = currentLine.indexOf("• ");
                hasBullet = (bulletIndex != -1);
                if (!hasBullet) {
                    noteContent.getText().insert(lineStart, "• ");
                    noteContent.setSelection(selectionStart + 2);
                    hasBullet = !hasBullet;
                } else {
                    noteContent.getText().delete(lineStart, lineStart + 2);
                    hasBullet = false;
                }
            }
        });

        btnCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChecklistActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        //bullet, numbering lines, increase-decrease indent
        noteContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    if(event.getAction() == KeyEvent.ACTION_UP){
                        int selectionStart = noteContent.getSelectionStart();
                        String text = noteContent.getText().toString();
                        int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                        int lineEnd = text.indexOf("\n", selectionStart);
                        if (lineEnd == -1) {
                            lineEnd = text.length();
                        }
                        String currentLine = text.substring(lineStart, lineEnd);
                        int bulletIndex = currentLine.indexOf("• ");
                        if (bulletIndex == -1) {
                            int previousLineEnd = text.lastIndexOf("\n", lineStart - 2);
                            if (previousLineEnd == -1) {
                                previousLineEnd = 0;
                            }
                            String previousLine = text.substring(previousLineEnd, lineStart - 1);
                            int previousBulletIndex = previousLine.indexOf("• ");
                            if (previousBulletIndex != -1) {
                                noteContent.getText().insert(lineStart, "• ");
                                noteContent.setSelection(selectionStart + 2);
                                hasBullet = true;
                            }
                        }
                        return true;
                    } else if (event.getAction() == KeyEvent.ACTION_DOWN) {

                        String[] lines = noteContent.getText().toString().split("\n");
                        String lastLine = lines[lines.length - 1].trim();
                        String secondLastLine = lines.length > 1 ? lines[lines.length - 2].trim() : "";
                        int cursorPos = noteContent.getSelectionStart();
                        int prevLineStart = cursorPos - 1;
                        String indent = "";
                        Editable text = noteContent.getText();
                        if (lastLine.matches("^\\d+\\.\\s.*$")) {
                            int counter = Integer.parseInt(lastLine.split("\\.")[0]) + 1;
                            noteContent.append("\n" + counter + ". ");
                            return true;
                        } else if (secondLastLine.matches("^\\d+\\.\\s.*$")) {
                            return false;
                        }
                        if (prevLineStart >= 0) {
                            while (prevLineStart >= 0 && text.charAt(prevLineStart) != '\n') {
                                prevLineStart--;
                            }
                            indent = text.subSequence(prevLineStart + 1, cursorPos).toString().replaceAll("[^\\s].*$", "");
                        } else {
                            int indentStart = cursorPos;
                            while (indentStart > 0 && text.charAt(indentStart - 1) == ' ') {
                                indentStart--;
                            }
                            indent = text.subSequence(indentStart, cursorPos).toString();
                        }

                        // Insert new line with the same indentation
                        text.insert(cursorPos, "\n" + indent);
                        noteContent.setSelection(cursorPos + indent.length() + 1);
                        return true;
                    }
                }
                return false;
            }
        });

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

        DatabaseReference databaseNote = FirebaseDatabase.getInstance().getReference("Note");

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
        });

        btnCancel.setOnClickListener(view13 -> editDialog.dismiss());
        editDialog.show();
    }
    private void applyTextChange(EditText noteContent){
        int selectionStart = noteContent.getSelectionStart();
        String text = noteContent.getText().toString();
        int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
        int lineEnd = text.indexOf("\n", selectionStart);
        if (lineEnd == -1) {
            lineEnd = text.length();
        }

        String currentLine = text.substring(lineStart, lineEnd);
        SpannableString line = new SpannableString(currentLine);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
        UnderlineSpan underlineSpan = new UnderlineSpan();

        if(hasBold && hasItalic){
            line.setSpan(boldSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            line.setSpan(italicSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (hasBold && hasUnderline) {
            line.setSpan(boldSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            line.setSpan(underlineSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (hasItalic && hasUnderline) {
            line.setSpan(italicSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            line.setSpan(underlineSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (hasBold) {
            line.setSpan(boldSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (hasItalic) {
            line.setSpan(italicSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (hasUnderline) {
            line.setSpan(underlineSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            line.removeSpan(boldSpan);
            line.removeSpan(italicSpan);
            line.removeSpan(underlineSpan);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.replace(lineStart, lineEnd,line);
        noteContent.setText(builder);
        noteContent.setSelection(lineEnd);
    }

    private void pinNote(int position){
        if(position != RecyclerView.NO_POSITION){
            databaseNote.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String noteTemp = "";
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                        Collections.sort(listNote, new Comparator<Note>() {
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
                        noteTemp = noteID;
                    }
                    String finalNoteTemp = noteTemp;
                    if(listNote.get(position).getNoteID().equals(finalNoteTemp)){
                        if(listNote.get(position).getNotePin().equals("Pinned")){
                            Toast.makeText(context, "This note has been Pinned", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Map<String, Object> pinnedNote = new HashMap<>();
                            pinnedNote.put("notePin", "Pinned");
                            DatabaseReference databaseReference = databaseNote.child(finalNoteTemp);
                            databaseReference.updateChildren(pinnedNote)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                        }
                                    });
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void lockNote(int position){
        if(position != RecyclerView.NO_POSITION){
            databaseNote.addValueEventListener(new ValueEventListener() {
                String noteTemp = "";
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        String noteID = String.valueOf(snapshot1.child("noteID").getValue());
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
                        Collections.sort(listNote, new Comparator<Note>() {
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
                        noteTemp = noteID;
                    }
                    String finalNoteTemp = noteTemp;
                    if(listNote.get(position).getNoteID().equals(finalNoteTemp)){
                        if(listNote.get(position).getNoteLock().equals("Locked")){
                            Dialog btnLockDialog = new Dialog(context);
                            LayoutInflater lock = LayoutInflater.from(context);
                            View btnLockView = lock.inflate(R.layout.lock_note_dialog, null);
                            btnLockDialog.setContentView(btnLockView);
                            Button btnDone = btnLockView.findViewById(R.id.btnLockDone);
                            Button btnCancel = btnLockView.findViewById(R.id.btnLockCancel);
                            String passwordUser = listNote.get(position).getNotePassword();
                            btnDone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    EditText editTextPassword = btnLockView.findViewById(R.id.editTextNotePassword);
                                    String password = editTextPassword.getText().toString();
                                    if(password.equals(passwordUser)){
                                        if(listNote.get(position).getNoteID().equals(finalNoteTemp)){
                                            Map<String, Object> unlockNote = new HashMap<>();
                                            unlockNote.put("noteLock", "Unlocked");
                                            unlockNote.remove("notePassword");
                                            DatabaseReference databaseReference = databaseNote.child(finalNoteTemp);
                                            databaseReference.updateChildren(unlockNote)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                        }
                                                    });
                                        }
                                    }
                                    btnLockDialog.dismiss();
                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    btnLockDialog.dismiss();
                                }
                            });
                            btnLockDialog.show();
                        }
                        else{
                            Dialog btnLockDialog = new Dialog(context);
                            LayoutInflater lock = LayoutInflater.from(context);
                            View btnLockView = lock.inflate(R.layout.lock_note_dialog, null);
                            btnLockDialog.setContentView(btnLockView);
                            TextView title = btnLockView.findViewById(R.id.textViewTitle);
                            Button btnDone = btnLockView.findViewById(R.id.btnLockDone);
                            Button btnCancel = btnLockView.findViewById(R.id.btnLockCancel);
                            title.setText("Please enter password to lock this note");
                            EditText editTextPassword = btnLockView.findViewById(R.id.editTextNotePassword);
                            btnDone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Map<String, Object> lockNote = new HashMap<>();
                                    lockNote.put("noteLock", "Locked");
                                    lockNote.put("notePassword", editTextPassword.getText().toString());
                                    DatabaseReference databaseReference = databaseNote.child(finalNoteTemp);
                                    databaseReference.updateChildren(lockNote)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            });
                                    btnLockDialog.dismiss();
                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    btnLockDialog.dismiss();
                                }
                            });
                            btnLockDialog.show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
