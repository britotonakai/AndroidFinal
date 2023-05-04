package com.example.homescreen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AllFolderScreen_NewNote extends Fragment {
    Button btnSave;
    ImageButton btnPen, btnTable, btnImage, btnBulletList, btnText, btnCheckList, btnShare,
            btnBold, btnItalic, btnUnderline, btnAlignRight, btnAlignLeft;
    TextView btnHeading, btnBody, btnSubheading, btnTitle;
    LinearLayout textBottomSheet;
    EditText editTextNoteTitle, editTextNoteContent;
    TextView textViewCurrentDay;
    List<Note> noteList;
    List<String> folders = new ArrayList<>();
    BottomSheetBehavior bottomSheetBehavior;
    boolean hasBullet = false, hasBold = false, hasItalic =false, hasUnderline = false,
            hasTitle = false, hasHeading = false, hasSubheading = false, hasBody = false;


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
        btnTitle = view.findViewById(R.id.btnTextTitle);
        btnHeading = view.findViewById(R.id.btnTextHeading);
        btnSubheading = view.findViewById(R.id.btnTextSubheading);
        btnBody = view.findViewById(R.id.btnTextBody);
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
        DatabaseReference databaseReference = firebaseDatabase.getReference("Folder");

        btnText.setOnClickListener(view1 -> {
            if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                RelativeSizeSpan titleSizeSpan = new RelativeSizeSpan(1.5f);
                RelativeSizeSpan headingSizeSpan = new RelativeSizeSpan(1.3f);
                RelativeSizeSpan subheadingSizeSpan = new RelativeSizeSpan(1.2f);
                RelativeSizeSpan bodySizeSpan = new RelativeSizeSpan(1.0f);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                btnBold.setOnClickListener(view11 -> {
                    hasBold = !hasBold;
                    applyTextChange();
                });
                btnItalic.setOnClickListener(view112 -> {
                    hasItalic = !hasItalic;
                    applyTextChange();
                });
                btnUnderline.setOnClickListener(view113 -> {
                    hasUnderline = !hasUnderline;
                    applyTextChange();
                });
                btnTitle.setOnClickListener(view114 -> {
                    int selectionStart = editTextNoteContent.getSelectionStart();
                    String text = editTextNoteContent.getText().toString();
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
                    editTextNoteContent.setText(builder);
                    editTextNoteContent.setSelection(lineEnd);
                });
                btnHeading.setOnClickListener(view115 -> {
                    int selectionStart = editTextNoteContent.getSelectionStart();
                    String text = editTextNoteContent.getText().toString();
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
                    editTextNoteContent.setText(builder);
                    editTextNoteContent.setSelection(lineEnd);
                });
                btnSubheading.setOnClickListener(view116 -> {
                    int selectionStart = editTextNoteContent.getSelectionStart();
                    String text = editTextNoteContent.getText().toString();
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
                    editTextNoteContent.setText(builder);
                    editTextNoteContent.setSelection(lineEnd);
                });
                btnBody.setOnClickListener(view117 -> {
                    int selectionStart = editTextNoteContent.getSelectionStart();
                    String text = editTextNoteContent.getText().toString();
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
                    editTextNoteContent.setText(builder);
                    editTextNoteContent.setSelection(lineEnd);
                });
            }else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        btnShare.setOnClickListener(view12 -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, editTextNoteTitle.getText().toString());
            shareIntent.putExtra(Intent.EXTRA_TEXT, editTextNoteContent.getText().toString());
            shareIntent.setType("text/plain");

            if(shareIntent.resolveActivity(getActivity().getPackageManager()) != null){
                startActivity(shareIntent);
            }

        });

        btnSave.setOnClickListener(view13 -> {
            String key = databaseReference.push().getKey();
            DatabaseReference noteDatabase = databaseReference.child("Note");
            noteList = new ArrayList<>();
//            Note note = new Note(key,editTextNoteTitle.getText().toString(), editTextNoteContent.getText().toString(),textViewCurrentDay.getText().toString());
//            noteList.add(note);
//            noteDatabase.child(key).setValue(note);


            Fragment current = FragmentManager.findFragment(view13);
            FragmentTransaction transaction = current.getParentFragmentManager().beginTransaction();
            AllFolderScreen_AllNote allNote = new AllFolderScreen_AllNote();
            transaction.replace(R.id.newNoteLayout, allNote);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        //bullet method
        btnBulletList.setOnClickListener(view14 -> {
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
        });

        //bullet and numbering lines
        editTextNoteContent.setOnKeyListener((v, keyCode, event) -> {
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
        });

        return view;
    }

    private void applyTextChange(){
        int selectionStart = editTextNoteContent.getSelectionStart();
        String text = editTextNoteContent.getText().toString();
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
        editTextNoteContent.setText(builder);
        editTextNoteContent.setSelection(lineEnd);
    }
}