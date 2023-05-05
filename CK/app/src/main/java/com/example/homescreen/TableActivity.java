package com.example.homescreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TableActivity extends AppCompatActivity {
    Button btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        TableLayout tableLayout = new TableLayout(TableActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tableLayout.setLayoutParams(layoutParams);
        LayoutInflater inflater = LayoutInflater.from(TableActivity.this);
        View templateView = inflater.inflate(R.layout.table_layout, null);
        tableLayout.addView(templateView);
        LinearLayout linearLayout = findViewById(R.id.tableLinearLayout);
        linearLayout.addView(tableLayout);
        btnBack = findViewById(R.id.btnTableBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
