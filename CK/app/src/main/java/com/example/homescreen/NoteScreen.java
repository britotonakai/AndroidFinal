package com.example.homescreen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteScreen extends Fragment {
    Button btnSave;
    ImageButton btnPen, btnTable, btnImage, btnBulletList, btnText, btnCheckList, btnShare,
            btnBold, btnItalic, btnUnderline, btnAlignRight, btnAligntLeft;
    LinearLayout textBottomSheet;
    EditText editTextNoteTitle, editTextNoteContent;
    TextView textViewCurrentDay;
    List<Note> noteList;
    BottomSheetBehavior bottomSheetBehavior;
    boolean hasBullet = false, hasBold = false, hasItalic =false, hasUnderline = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_note, container, false);
        btnSave = view.findViewById(R.id.btnSave);
        btnShare = view.findViewById(R.id.btnShare);
        btnBulletList = view.findViewById(R.id.btnBulletList);
        btnText = view.findViewById(R.id.btnText);
        btnBold = view.findViewById(R.id.btnBoldText);
        btnItalic= view.findViewById(R.id.btnItalic);
        btnUnderline = view.findViewById(R.id.btnUnderline);
        textBottomSheet = view.findViewById(R.id.text_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(textBottomSheet);
        editTextNoteTitle = view.findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = view.findViewById(R.id.editTextNoteContent);
        textViewCurrentDay = view.findViewById(R.id.textViewCurrentDay);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat day_month_year_time = new SimpleDateFormat("HH:mm aaa, dd LLLL, yyyy");
        String dateTime = day_month_year_time.format(calendar.getTime());
        textViewCurrentDay.setText(dateTime);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Note");


        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                    StyleSpan italicSpan = new StyleSpan(Typeface.ITALIC);
                    UnderlineSpan underlineSpan = new UnderlineSpan();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnBold.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectionStart = editTextNoteContent.getSelectionStart();
                            String text = editTextNoteContent.getText().toString();
                            int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                            int lineEnd = text.indexOf("\n", selectionStart);
                            if (lineEnd == -1) {
                                lineEnd = text.length();
                            }

                            String currentLine = text.substring(lineStart, lineEnd);
                            SpannableString line = new SpannableString(currentLine);

                            if(!hasBold){
                                line.setSpan(boldSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                Log.d("CurrentLine", String.valueOf(line));
                                hasBold = !hasBold;
                            }
                            else{
                                line.removeSpan(boldSpan);
                                line.removeSpan(italicSpan);
                                hasBold = false;
                            }
                            SpannableStringBuilder builder = new SpannableStringBuilder(text);
                            builder.replace(lineStart, lineEnd, line);
                            editTextNoteContent.setText(builder);
                            editTextNoteContent.setSelection(lineEnd);
                        }
                    });
                    btnItalic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectionStart = editTextNoteContent.getSelectionStart();
                            String text = editTextNoteContent.getText().toString();
                            int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                            int lineEnd = text.indexOf("\n", selectionStart);
                            if (lineEnd == -1) {
                                lineEnd = text.length();
                            }

                            String currentLine = text.substring(lineStart, lineEnd);
                            SpannableString line = new SpannableString(currentLine);

                            if(!hasItalic){
                                line.setSpan(italicSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                Log.d("CurrentLine", String.valueOf(line));
                                hasItalic = !hasItalic;
                            }
                            else{
                                line.removeSpan(italicSpan);
                                hasItalic = false;
                            }
                            SpannableStringBuilder builder = new SpannableStringBuilder(text);
                            builder.replace(lineStart, lineEnd, line);
                            editTextNoteContent.setText(builder);
                            editTextNoteContent.setSelection(lineEnd);
                        }
                    });
                    btnUnderline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectionStart = editTextNoteContent.getSelectionStart();
                            String text = editTextNoteContent.getText().toString();
                            int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                            int lineEnd = text.indexOf("\n", selectionStart);
                            if (lineEnd == -1) {
                                lineEnd = text.length();
                            }

                            String currentLine = text.substring(lineStart, lineEnd);
                            SpannableString line = new SpannableString(currentLine);

                            if(!hasUnderline){
                                line.setSpan(underlineSpan, 0, line.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                Log.d("CurrentLine", String.valueOf(line));
                                hasUnderline = !hasUnderline;
                            }
                            else{
                                line.removeSpan(underlineSpan);
                                hasUnderline = false;
                            }
                            SpannableStringBuilder builder = new SpannableStringBuilder(text);
                            builder.replace(lineStart, lineEnd, line);
                            editTextNoteContent.setText(builder);
                            editTextNoteContent.setSelection(lineEnd);
                        }
                    });
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, editTextNoteTitle.getText().toString());
                shareIntent.putExtra(Intent.EXTRA_TEXT, editTextNoteContent.getText().toString());
                shareIntent.setType("text/plain");

                if(shareIntent.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivity(shareIntent);
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = databaseReference.push().getKey();
                noteList = new ArrayList<>();
                Note note = new Note(key,editTextNoteTitle.getText().toString(), editTextNoteContent.getText().toString(),textViewCurrentDay.getText().toString());
                noteList.add(note);
                databaseReference.child(String.valueOf(key)).setValue(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Xử lý khi lưu trữ thất bại
                                Log.d(TAG, "Data could not be saved: " + e.getMessage());
                            }
                        });
                Intent backIntent = new Intent(getActivity(), MainScreen.class);
                startActivity(backIntent);
            }
        });

        //bullet method
        btnBulletList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = editTextNoteContent.getSelectionStart();
                String text = editTextNoteContent.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }
                String currentLine = text.substring(lineStart, lineEnd);
                int bulletIndex = currentLine.indexOf("• ");
                hasBullet = (bulletIndex != -1);
                if (!hasBullet) {
                    editTextNoteContent.getText().insert(lineStart, "• ");
                    editTextNoteContent.setSelection(selectionStart + 2);
                    hasBullet = !hasBullet;
                } else {
                    editTextNoteContent.getText().delete(lineStart, lineStart + 2);
                    hasBullet = false;
                }
            }
        });

        //bullet and numbering lines
        editTextNoteContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    if(event.getAction() == KeyEvent.ACTION_UP){
                        int selectionStart = editTextNoteContent.getSelectionStart();
                        String text = editTextNoteContent.getText().toString();
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
                                editTextNoteContent.getText().insert(lineStart, "• ");
                                editTextNoteContent.setSelection(selectionStart + 2);
                                hasBullet = true;
                            }
                        }
                        return true;
                    } else if (event.getAction() == KeyEvent.ACTION_DOWN) {

                        String[] lines = editTextNoteContent.getText().toString().split("\n");
                        String lastLine = lines[lines.length - 1].trim();
                        String secondLastLine = lines.length > 1 ? lines[lines.length - 2].trim() : "";
                        if (lastLine.matches("^\\d+\\.\\s.*$")) {
                            int counter = Integer.parseInt(lastLine.split("\\.")[0]) + 1;
                            editTextNoteContent.append("\n" + counter + ". ");
                            return true;
                        } else if (secondLastLine.matches("^\\d+\\.\\s.*$")) {
                            return false;
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

}