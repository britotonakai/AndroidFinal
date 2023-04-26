package com.example.homescreen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class EditScreenDialog extends Dialog {
    Context context;
    TextView textViewEditCurrentDay;
    EditText editTitle, editContent;
    Button btnEdit, btnCancel;

    public EditScreenDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textViewEditCurrentDay = findViewById(R.id.textViewEditCurrentDay);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnEdit = findViewById(R.id.btnConfirmEdit);
        btnCancel = findViewById(R.id.btnCancel);

    }
}
