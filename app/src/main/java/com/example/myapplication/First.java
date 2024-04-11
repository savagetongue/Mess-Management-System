
// The First Activity
// User Shall Choose Between Student Or Manager

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
public class First extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        Button student = findViewById(R.id.student);
        Button manager = findViewById(R.id.manager);

        //If Clicked On Student It'll Redirect To Next Activity Sign_In
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(First.this, Sign_In.class);
                startActivity(i);
            }
        });

        //If Clicked On Student It'll Redirect To Next Activity Sign_In
        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(First.this, Sign_In.class);
                startActivity(intent);
            }
        });
    }
}