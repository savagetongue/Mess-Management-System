package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Create_Note extends AppCompatActivity {

    Button save;
    EditText note_et;
    DBManager db;
    String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);


        LocalDate today = LocalDate.now(); //Gets Todays Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = today.format(formatter); // Convert To dd-MM-yyyy Format

        save=findViewById(R.id.save);

        note_et=findViewById(R.id.note_et);


        db=new DBManager(Create_Note.this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text=note_et.getText().toString();
                Toast.makeText(Create_Note.this, ""+date+"     "+text, Toast.LENGTH_SHORT).show();
                db.insert_note(date,text);
                Toast.makeText(Create_Note.this, "Note Saved", Toast.LENGTH_SHORT).show();
            }
        });

    }
}