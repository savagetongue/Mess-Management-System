package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class Custhome extends AppCompatActivity {


    private ListView listViewMenu;

    private FirebaseFirestore db;
    DrawerLayout drawerLayout;
    ImageButton imgbtn;

    NavigationView navigationView;
    private TextView breakfastItem, lunchItem, dinnerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custhome);

        breakfastItem=findViewById(R.id.breakfastItem);
        lunchItem=findViewById(R.id.lunchItem);
        dinnerItem=findViewById(R.id.dinnerItem);
        db = FirebaseFirestore.getInstance();


        // Retrieve Student_Home_Screen data from Firestore and update ListView
        loadMenuData();

        drawerLayout = findViewById(R.id.drawer);
        imgbtn = findViewById(R.id.imageButton);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                switch (item.getItemId()) {

                    case R.id.Raise_Complaints:
                        Intent intent3 = new Intent(Custhome.this, Raise_complaint.class);
                        startActivity(intent3);
                        return true;

                    case R.id.Give_Suggestions:
                        Intent intent4 = new Intent(Custhome.this, Give_suggestion.class);
                        startActivity(intent4);
                        return true;

                    case R.id.logout:
                        Intent intent2 = new Intent(Custhome.this, Sign_In.class);
                        startActivity(intent2);
                        return true;

                }

                return false;
            }

        });

    }

    private void loadMenuData() {
        db.collection("menus").document("main_menu").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract Student_Home_Screen items
                        String breakfast = documentSnapshot.getString("breakfast");
                        String lunch = documentSnapshot.getString("lunch");
                        String dinner = documentSnapshot.getString("dinner");

                        // Update TextViews with Student_Home_Screen items
                        breakfastItem.setText(breakfast);
                        lunchItem.setText(lunch);
                        dinnerItem.setText(dinner);
                    } else {
                        // Handle case when Student_Home_Screen data is not available
                        Toast.makeText(Custhome.this, "Menu data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to retrieve Student_Home_Screen data
                    Toast.makeText(Custhome.this, "Failed to load Student_Home_Screen data", Toast.LENGTH_SHORT).show();
                });
    }


    }






