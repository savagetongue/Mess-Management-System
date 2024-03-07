package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class menu extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton imgbtn;

    NavigationView navigationView;
    private ArrayList<User> userArrayList;
    private MyAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lv);

        db = FirebaseFirestore.getInstance();
        userArrayList = new ArrayList<>();

        ListView listView = findViewById(R.id.listView);
        adapter = new MyAdapter(this, userArrayList);
        listView.setClickable(true);
        listView.setAdapter(adapter);

        // Retrieve menu items from Firestore
        retrieveMenuItems();
    }

    private void retrieveMenuItems() {
        // Assuming "main_menu" is the document containing breakfast, lunch, and dinner fields
        DocumentReference menuRef = db.collection("menus").document("main_menu");

        menuRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve menu items from Firestore
                        String breakfast = document.getString("breakfast");
                        String lunch = document.getString("lunch");
                        String dinner = document.getString("dinner");

                        // Update User objects
                        userArrayList.clear();
                        userArrayList.add(new User("Breakfast", breakfast, R.drawable.breakfast));
                        userArrayList.add(new User("Lunch", lunch, R.drawable.lunch));
                        userArrayList.add(new User("Dinner", dinner, R.drawable.dn));

                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
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
                        Intent intent3 = new Intent(menu.this, Raise_complaint.class);
                        startActivity(intent3);
                        return true;

                    case R.id.Give_Suggestions:
                        Intent intent4 = new Intent(menu.this, Give_suggestion.class);
                        startActivity(intent4);
                        return true;

                    case R.id.logout:
                        Intent intent2 = new Intent(menu.this, Sign_1.class);
                        startActivity(intent2);
                        return true;

                }

                return false;
            }

        });

    }
    }


