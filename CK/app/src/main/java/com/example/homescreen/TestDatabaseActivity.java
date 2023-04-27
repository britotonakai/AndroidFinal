package com.example.homescreen;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestDatabaseActivity extends AppCompatActivity {

    EditText editText;
    Button btn_bullet, btn_number;
    private boolean hasBullet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        btn_bullet = findViewById(R.id.btn_bullet);
        btn_number = findViewById(R.id.btn_number);
        editText = findViewById(R.id.editText);

        btn_bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionStart = editText.getSelectionStart();
                String text = editText.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }
                String currentLine = text.substring(lineStart, lineEnd);
                int bulletIndex = currentLine.indexOf("• ");
                hasBullet = (bulletIndex != -1);
                if (!hasBullet) {
                    editText.getText().insert(lineStart, "• ");
                    editText.setSelection(selectionStart + 2);
                    hasBullet = !hasBullet;
                } else {
                    editText.getText().delete(lineStart, lineStart + 2);
                    hasBullet = false;
                }
            }
        });


        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    int selectionStart = editText.getSelectionStart();
                    String text = editText.getText().toString();
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
                            editText.getText().insert(lineStart, "• ");
                            editText.setSelection(selectionStart + 2);
                            hasBullet = true;
                        }
                    }
                    return true;
                }
                return false;
            }
        });



//        editText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                int counter = 1;
//                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    String[] lines = editText.getText().toString().split("\n");
//                    String lastLine = lines[lines.length - 1].trim();
//                    String secondLastLine = lines.length > 1 ? lines[lines.length - 2].trim() : "";
//                    if (lastLine.matches("^\\d+\\.\\s.*$")) {
//                        counter = Integer.parseInt(lastLine.split("\\.")[0]) + 1;
//                        editText.append("\n" + counter + ". ");
//                        return true;
//                    } else if (secondLastLine.matches("^\\d+\\.\\s.*$")) {
//                        return false;
//                    }
//                }
//                return false;
//            }
//        });

        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectionStart = editText.getSelectionStart();
                String text = editText.getText().toString();
                int lineStart = text.lastIndexOf("\n", selectionStart - 1) + 1;
                int lineEnd = text.indexOf("\n", selectionStart);
                if (lineEnd == -1) {
                    lineEnd = text.length();
                }
                String currentLine = text.substring(lineStart, lineEnd);
                String number = getLineNumber(currentLine);
                editText.getText().insert(lineStart, number + " ");
                editText.setSelection(selectionStart + number.length() + 1);
            }

            private String getLineNumber(String line) {
                int lineNumber = 1;
                String regex = "^\\d+\\.";
                Matcher matcher = Pattern.compile(regex).matcher(line);
                if (matcher.find()) {
                    lineNumber = Integer.parseInt(matcher.group().replace(".", "")) + 1;
                }
                return lineNumber + ".";
            }

        });

    }
}