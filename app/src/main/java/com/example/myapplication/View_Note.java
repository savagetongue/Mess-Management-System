package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class View_Note extends AppCompatActivity {

    TextView date_tv;
    Button save_notes, delete_notes;
    String id, date, notes;
    EditText notes_et;
    DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note);

        db = new DBManager(this);
        save_notes = findViewById(R.id.save_notes);
        delete_notes = findViewById(R.id.delete_notes);
        date_tv = findViewById(R.id.date_tv);
        notes_et = findViewById(R.id.notes_et);

        Intent i = getIntent();
        id = i.getStringExtra("id");
        date = i.getStringExtra("date");
        notes = i.getStringExtra("notes");

        date_tv.setText(date);
        notes_et.setText(notes);

        save_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u_notes = notes_et.getText().toString();
                db.update_notes(date, u_notes);
                Toast.makeText(View_Note.this, "Updated", Toast.LENGTH_SHORT).show();

                // Send back the updated note to Expense_Book activity
                Intent intent = new Intent();
                intent.putExtra("updatedNote", u_notes);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        delete_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delete_notes(id.trim());
                Toast.makeText(View_Note.this, "Deleted" + date, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
