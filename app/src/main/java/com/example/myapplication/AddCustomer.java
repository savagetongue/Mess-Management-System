package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AddCustomer extends AppCompatActivity {

    private EditText name, mob;
    private Spinner c_name;
    private Button submit;
    private FirebaseFirestore db;
    private int studentCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        String college[] = {"SMSMPITR (Diploma),Akluj", "SMSMPITR (Degree),Akluj"};
        submit = findViewById(R.id.submit);
        name = findViewById(R.id.name);
        mob = findViewById(R.id.mob);
        c_name = findViewById(R.id.c_name);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, college);
        c_name.setAdapter(adapter);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Fetch the latest student ID from Firestore to increment
        fetchLatestStudentCounter();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_1 = name.getText().toString();
                String clg_name = c_name.getSelectedItem().toString();
                String mobile = mob.getText().toString();

                if (name_1.isEmpty() || clg_name.isEmpty() || mobile.isEmpty()) {
                    Toast.makeText(AddCustomer.this, "All fields are Required...!!!", Toast.LENGTH_SHORT).show();
                } else if (mobile.length() != 10) {
                    Toast.makeText(AddCustomer.this, "Enter a 10-digit valid number", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a student object
                    Student student = new Student(String.valueOf(studentCounter), name_1, clg_name, mobile);
                    // Add the student to Firestore
                    addStudentToFirestore(student);
                }
            }
        });
    }

    // Fetch latest student counter from Firestore
    private void fetchLatestStudentCounter() {
        db.collection("students")
                .orderBy("id", Query.Direction.DESCENDING) // Order by student ID in descending order
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve the highest studentId and increment it
                        DocumentSnapshot lastStudentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        Student lastStudent = lastStudentSnapshot.toObject(Student.class);
                        studentCounter = Integer.parseInt(lastStudent.getId()) + 1;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCustomer.this, "Error getting student counter", Toast.LENGTH_SHORT).show();
                });
    }

    private void addStudentToFirestore(Student student) {
        // Add the student to Firestore
        db.collection("students")
                .document(String.valueOf(studentCounter))
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    // Increment student counter
                    studentCounter++;
                    // Notify user about successful addition
                    Toast.makeText(AddCustomer.this, "Student added successfully", Toast.LENGTH_SHORT).show();
                    // Clear input fields
                    name.setText("");
                    mob.setText("");
                    // Focus on name field
                    name.requestFocus();
                })
                .addOnFailureListener(e -> {
                    // Notify user about failure
                    Toast.makeText(AddCustomer.this, "Failed to add student", Toast.LENGTH_SHORT).show();
                });
    }
}
