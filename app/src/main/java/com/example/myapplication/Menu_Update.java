package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Menu_Update extends AppCompatActivity {

    private EditText editTextBreakfast;
    private EditText editTextLunch;
    private EditText editTextDinner;
    private Button updateMenuButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_update);

        db = FirebaseFirestore.getInstance();

        editTextBreakfast = findViewById(R.id.editTextBreakfast);
        editTextLunch = findViewById(R.id.editTextLunch);
        editTextDinner = findViewById(R.id.editTextDinner);
        updateMenuButton = findViewById(R.id.updateMenuButton);

        updateMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save menu items to Firestore
                updateMenuItems();
            }
        });
    }

    private void updateMenuItems() {
        String breakfast = editTextBreakfast.getText().toString();
        String lunch = editTextLunch.getText().toString();
        String dinner = editTextDinner.getText().toString();

        // Create a map to represent the menu data
        Map<String, Object> menuData = new HashMap<>();
        menuData.put("breakfast", breakfast);
        menuData.put("lunch", lunch);
        menuData.put("dinner", dinner);

        // Add the menu data to Firestore
        DocumentReference menuRef = db.collection("menus").document("main_menu");
        menuRef.set(menuData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Menu_Update.this, "Menu Updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Menu_Update.this, "Failed to update menu.", Toast.LENGTH_SHORT).show();
                });
    }
}
