package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Timer;


public class Activity_Home_Screen extends AppCompatActivity {

    Button add;
    Button view;
    Button bills;
    Button expense;
    Timer timer;
    DBManager db;

    DrawerLayout drawerLayout;
    ImageButton imgbtn;

NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        db = new DBManager(Activity_Home_Screen.this);
                        add = findViewById(R.id.add);

                        add.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Activity_Home_Screen.this, AddCustomer.class);
                                startActivity(i);
                            }
                        });

                        bills = findViewById(R.id.bills);
                        bills.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Activity_Home_Screen.this, MonthSelectionActivity.class);
                                startActivity(i);
                            }
                        });

                        view = findViewById(R.id.view);
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ArrayList<String> users = db.get_users();
                                Intent i = new Intent(Activity_Home_Screen.this, activity_view_list.class);
                                i.putStringArrayListExtra("users", users);
                                startActivity(i);
                            }
                        });

                        expense = findViewById(R.id.expense);
                        expense.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Activity_Home_Screen.this, expense_book.class);
                                ArrayList<String> list = db.get_date();
                                i.putStringArrayListExtra("list", list);
                                startActivity(i);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

        drawerLayout = findViewById(R.id.drawer);
        imgbtn = findViewById(R.id.imageButton);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });
        navigationView =findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId=item.getItemId();
                switch (item.getItemId()) {
                    case R.id.Set_The_Menu:
                        Log.d("MenuItemClicked", "Set The Menu Clicked");
                        Intent intent = new Intent(Activity_Home_Screen.this, Menu_Update.class);
                        startActivity(intent);
                        return true;

                    case R.id.View_Complaints:
                        Intent intent3 = new Intent(Activity_Home_Screen.this,View_complaint.class);
                        startActivity(intent3);
                        return true;

                    case R.id.View_Suggestions:
                        Intent intent4 = new Intent(Activity_Home_Screen.this,View_suggestion.class);
                        startActivity(intent4);
                        return true;

                    case R.id.logout:
                        Intent intent2 = new Intent(Activity_Home_Screen.this,Sign_1.class);
                        startActivity(intent2);
                        return true;

                }

                return false;
            }
        });
    }

}
