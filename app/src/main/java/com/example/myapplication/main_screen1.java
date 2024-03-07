package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.FirebaseApp;

import java.util.Timer;
import java.util.TimerTask;

public class main_screen1 extends AppCompatActivity {

    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main_screen1);

        Intent i=new Intent(main_screen1.this,SignIn.class);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        },1000);

    }
}