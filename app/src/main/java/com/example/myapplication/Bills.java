package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Bills extends AppCompatActivity {
    Button btn_paid, btn_unpaid;
    String selectedMonth;
    String studentId;  // Assuming you have the student ID

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);

        btn_paid = findViewById(R.id.b_paid);
        btn_unpaid = findViewById(R.id.unpaid_b);

        db = FirebaseFirestore.getInstance();

        // Get the selected month and student ID from the intent
        Intent intent = getIntent();
        if (intent.hasExtra("selectedMonth")) {
            selectedMonth = intent.getStringExtra("selectedMonth");
        }
        if (intent.hasExtra("studentId")) {
            studentId = intent.getStringExtra("studentId");
        }

        btn_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click for the "Paid" button
                Intent intent = new Intent(Bills.this, bills_paid.class);
                intent.putExtra("selectedMonth", selectedMonth);
                intent.putExtra("studentId", studentId);
                startActivity(intent);
            }
        });

        btn_unpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click for the "Unpaid" button
                Intent intent = new Intent(Bills.this, bills_unpaid.class);
                intent.putExtra("selectedMonth", selectedMonth);
                startActivity(intent);

            }
        });
    }



    private void initializeUnpaidAmount() {
        Map<String, Object> initialUnpaid = new HashMap<>();
        initialUnpaid.put("amount", 2000.0);  // Initial unpaid amount, adjust as needed

        db.collection("students")
                .document(studentId)
                .collection("unpaid_amount")
                .document(selectedMonth)
                .set(initialUnpaid)
                .addOnSuccessListener(aVoid -> {
                    // Now proceed with your logic to open Bills Unpaid activity
                    Intent intent = new Intent(Bills.this, bills_unpaid.class);
                    intent.putExtra("selectedMonth", selectedMonth);
                    intent.putExtra("studentId", studentId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Handle failure to add initial unpaid amount
                    Toast.makeText(Bills.this, "Error initializing unpaid amount", Toast.LENGTH_SHORT).show();
                });
    }
}
