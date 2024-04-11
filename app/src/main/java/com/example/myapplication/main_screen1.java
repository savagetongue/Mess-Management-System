
//Initial Activity Splash/Buffer Screen

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class main_screen1 extends AppCompatActivity {

    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_screen1);

        Intent i=new Intent(main_screen1.this, First.class);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(i);
                finish();
            }
        },1500);

        //It Starts Our First Activity First Activity After 1500ms (1.5s)

    }
}