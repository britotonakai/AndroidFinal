package com.example.homescreen;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.LeadingMarginSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDatabaseActivity extends AppCompatActivity {

    EditText editText;
    Button btn_bullet, btn_number, btn_increase, btn_decrease;
    private boolean hasBullet = false;
    private int INDENT_LENGTH = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        btn_bullet = findViewById(R.id.btn_bullet);
        btn_number = findViewById(R.id.btn_number);
        editText = findViewById(R.id.editText);
        btn_increase = findViewById(R.id.btn_increase);
        btn_decrease = findViewById(R.id.btn_decrease);
        btn_decrease.setEnabled(false);

        btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                Editable text = editText.getText();
                String indent = "     "; // 5 spaces for each indent level

                if (selectionStart == selectionEnd) {
                    // Insert an indent at the start of the current line
                    int lineStart = selectionStart;
                    while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
                        lineStart--;
                    }
                    text.insert(lineStart, indent);
                    editText.setSelection(selectionStart + indent.length());
                } else {
                    // Indent each line of the selected text
                    String selectedText = text.subSequence(selectionStart, selectionEnd).toString();
                    String[] lines = selectedText.split("\\r?\\n");
                    StringBuilder sb = new StringBuilder();
                    for (String line : lines) {
                        sb.append(indent).append(line).append("\n");
                    }
                    text.replace(selectionStart, selectionEnd, sb.toString());
                    editText.setSelection(selectionEnd + indent.length() * lines.length);
                }

                // Enable the decrease button after indenting
                btn_decrease.setEnabled(true);
            }
        });

        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();
                Editable text = editText.getText();
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
                        editText.setSelection(selectionStart - indent.length());
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
                    editText.setSelection(selectionEnd - indent.length() * lines.length);

                    // Disable decrease button if no lines have indent left
                    if (!hasIndent) {
                        btn_decrease.setEnabled(false);
                    }
                }

                // Disable decrease button if cursor is at start of text
                if (editText.getSelectionStart() == 0) {
                    btn_decrease.setEnabled(false);
                }
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    int cursorPos = editText.getSelectionStart();
                    int prevLineStart = cursorPos - 1;
                    String indent = "";
                    Editable text = editText.getText();

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
                    editText.setSelection(cursorPos + indent.length() + 1);
                    return true;
                }
                return false;
            }
        });
    }
}