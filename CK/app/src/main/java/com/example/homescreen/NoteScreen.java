package com.example.homescreen;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NoteScreen extends Fragment {
    Button btnSave;
    ImageButton btnPen, btnTable, btnImage, btnBulletList, btnText, btnCheckList, btnShare,
            btnBold, btnItalic, btnUnderline, btnAlignRight, btnAlignLeft;
    TextView btnHeading, btnBody, btnSubheading, btnTitle;
    LinearLayout textBottomSheet;
    EditText editTextNoteTitle, editTextNoteContent;
    TextView textViewCurrentDay;
    List<Note> noteList;
    BottomSheetBehavior bottomSheetBehavior;
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_PERMISSIONS = 123;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

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
        btnAlignRight = view.findViewById(R.id.btnAlignRight);
        btnAlignLeft = view.findViewById(R.id.btnAlignLeft);
        btnImage = view.findViewById(R.id.btnImage);
        btnTitle = view.findViewById(R.id.btnTextTitle);
        btnHeading = view.findViewById(R.id.btnTextHeading);
        btnSubheading = view.findViewById(R.id.btnTextSubheading);
        btnBody = view.findViewById(R.id.btnTextBody);
        btnCheckList = view.findViewById(R.id.btnCheckList);
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
                    RelativeSizeSpan titleSizeSpan = new RelativeSizeSpan(1.5f);
                    RelativeSizeSpan headingSizeSpan = new RelativeSizeSpan(1.3f);
                    RelativeSizeSpan subheadingSizeSpan = new RelativeSizeSpan(1.2f);
                    RelativeSizeSpan bodySizeSpan = new RelativeSizeSpan(1.0f);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btnBold.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hasBold = !hasBold;
                            applyTextChange();
                        }
                    });
                    btnItalic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hasItalic = !hasItalic;
                            applyTextChange();
                        }
                    });
                    btnUnderline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hasUnderline = !hasUnderline;
                            applyTextChange();
                        }
                    });
                    //Increase indent
                    btnAlignRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectionStart = editTextNoteContent.getSelectionStart();
                            int selectionEnd = editTextNoteContent.getSelectionEnd();
                            Editable text = editTextNoteContent.getText();
                            String indent = "     "; // 5 spaces for each indent level

                            if (selectionStart == selectionEnd) {
                                // Insert an indent at the start of the current line
                                int lineStart = selectionStart;
                                while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                                    lineStart--;
                                }
                                text.insert(lineStart, indent);
                                editTextNoteContent.setSelection(selectionStart + indent.length());
                            } else {
                                // Indent each line of the selected text
                                String selectedText = text.subSequence(selectionStart, selectionEnd).toString();
                                String[] lines = selectedText.split("\\r?\\n");
                                StringBuilder sb = new StringBuilder();
                                for (String line : lines) {
                                    sb.append(indent).append(line).append("\n");
                                }
                                text.replace(selectionStart, selectionEnd, sb.toString());
                                editTextNoteContent.setSelection(selectionEnd + indent.length() * lines.length);
                            }

                            // Enable the decrease button after indenting
                            btnAlignLeft.setEnabled(true);
                        }
                    });
                    //Decrease indent
                    btnAlignLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int selectionStart = editTextNoteContent.getSelectionStart();
                            int selectionEnd = editTextNoteContent.getSelectionEnd();
                            Editable text = editTextNoteContent.getText();
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
                                    editTextNoteContent.setSelection(selectionStart - indent.length());
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
                                editTextNoteContent.setSelection(selectionEnd - indent.length() * lines.length);

                                // Disable decrease button if no lines have indent left
                                if (!hasIndent) {
                                    btnAlignLeft.setEnabled(false);
                                }
                            }

                            // Disable decrease button if cursor is at start of text
                            if (editTextNoteContent.getSelectionStart() == 0) {
                                btnAlignLeft.setEnabled(false);
                            }
                        }
                    });
                    btnTitle.setOnClickListener(new View.OnClickListener() {
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
                        }
                    });
                    btnHeading.setOnClickListener(new View.OnClickListener() {
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
                        }
                    });
                    btnSubheading.setOnClickListener(new View.OnClickListener() {
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
                        }
                    });
                    btnBody.setOnClickListener(new View.OnClickListener() {
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
                        }
                    });
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        btnCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChecklistActivity.class);
                startActivity(intent);
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
                Intent backIntent = new Intent(getActivity(), NavigationScreen.class);
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

        //Image Upload
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                } else {
                    pickImageFromGallery();
                }
            }
        });

        //bullet, numbering lines, increase-decrease indent
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
                        int cursorPos = editTextNoteContent.getSelectionStart();
                        int prevLineStart = cursorPos - 1;
                        String indent = "";
                        Editable text = editTextNoteContent.getText();
                        if (lastLine.matches("^\\d+\\.\\s.*$")) {
                            int counter = Integer.parseInt(lastLine.split("\\.")[0]) + 1;
                            editTextNoteContent.append("\n" + counter + ". ");
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
                        editTextNoteContent.setSelection(cursorPos + indent.length() + 1);
                        return true;
                    }
                }
                return false;
            }
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
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            Bitmap bitmap = getBitmapFromUri(imageUri);
            if (bitmap != null) {
                // Create the image view
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageBitmap(bitmap);

                // Create a new line character
                String newLine = "\n";

                // Get the current cursor position
                int cursorPosition = editTextNoteContent.getSelectionStart();

                // Insert the image into the Editable text at the current cursor position
                Editable editable = editTextNoteContent.getEditableText();
                editable.insert(cursorPosition, newLine); // Insert a new line character
                ImageSpan imageSpan = new ImageSpan(getActivity(), bitmap);
                SpannableString spannableString = new SpannableString(" ");
                spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                editable.insert(cursorPosition, spannableString);

                // Insert a new line character after the image
                editable.insert(cursorPosition + 2, newLine);

                // Set the cursor to the new line
                editTextNoteContent.setSelection(cursorPosition + 3);
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            // Decode the bitmap with inJustDecodeBounds=true to check its original size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

            // Calculate the new size of the bitmap
            int width = options.outWidth;
            int height = options.outHeight;
            int newWidth = (int) (width * 0.5);
            int newHeight = (int) (height * 0.5);

            // Create a new bitmap with the desired size
            Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

            // Draw the original bitmap onto the new bitmap using a Canvas object
            Canvas canvas = new Canvas(bitmap);
            Rect src = new Rect(0, 0, width, height);
            Rect dst = new Rect(0, 0, newWidth, newHeight);
            canvas.drawBitmap(BitmapFactory.decodeFileDescriptor(fileDescriptor), src, dst, null);

            parcelFileDescriptor.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}