package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        fetchLatestStudentId();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name_1 = name.getText().toString();
                String clg_name = c_name.getSelectedItem().toString();
                String mobile = mob.getText().toString();

                if (name_1.isEmpty() || clg_name.isEmpty() || mobile.isEmpty()) {
                    Toast.makeText(AddCustomer.this, "All Fields Are Required...!!!", Toast.LENGTH_SHORT).show();
                } else if (mobile.length() != 10) {
                    Toast.makeText(AddCustomer.this, "Enter A 10-digit Valid Number", Toast.LENGTH_SHORT).show();
                } else {
                    addCustomerToFirestore(name_1, clg_name, mobile);
                    Toast.makeText(AddCustomer.this, "Student Information is Recorded...", Toast.LENGTH_SHORT).show();

                    // Clear input fields after successful addition
                    name.setText("");
                    mob.setText("");
                    // Keep the focus on the name field for convenience
                    name.requestFocus();
                }
            }
        });
    }

    private void fetchLatestStudentId() {
        // Fetch the latest student ID from Firestore to increment
        db.collection("students")
                .orderBy("id")
                .limitToLast(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    if (!documents.isEmpty()) {
                        // Retrieve the highest studentId and increment it
                        DocumentSnapshot lastStudentSnapshot = documents.get(0);
                        Student lastStudent = lastStudentSnapshot.toObject(Student.class);
                        studentCounter = lastStudent.getStudentId() + 1;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCustomer.this, "Error getting student counter", Toast.LENGTH_SHORT).show();
                });
    }

    private void addCustomerToFirestore(String name, String cName, String mobile) {
        // Create a new student document with the incremented student ID
        Student newStudent = new Student(String.valueOf(studentCounter), name, cName, mobile);

        // Specify the document ID using the student's ID
        String studentId = String.valueOf(studentCounter);

        // Add the student to the "students" collection in Firestore with the specified document ID
        db.collection("students")
                .document(studentId)
                .set(newStudent)
                .addOnSuccessListener(aVoid -> {
                    // Increment the student counter for the next student
                    studentCounter++;

                    createBillsCollection(studentId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddCustomer.this, "Error Adding Student", Toast.LENGTH_SHORT).show();
                });
    }

    private void createBillsCollection(String studentId) {
        // Reference to the "bills" collection
        CollectionReference billsCollection = db.collection("bills");

        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024); // Starting from February 2024
        int currentMonth = calendar.get(Calendar.MONTH);

        // Fetch the existing months from the "bills" collection
        billsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot monthDocument : task.getResult()) {
                            String existingMonth = monthDocument.getId();

                            // Calculate the month and year for the existing month
                            int existingMonthIndex = Arrays.asList(new DateFormatSymbols().getShortMonths()).indexOf(existingMonth.substring(0, 3));
                            int existingYear = Integer.parseInt(existingMonth.substring(existingMonth.indexOf("_") + 1));

                            // Check if the existing month is in the future (including the current month)
                            if ((existingYear > calendar.get(Calendar.YEAR)) ||
                                    (existingYear == calendar.get(Calendar.YEAR) && existingMonthIndex >= currentMonth)) {
                                // Reference to the subcollection for students within the existing month document
                                CollectionReference existingMonthStudentsCollection = monthDocument.getReference().collection("students");

                                // Add information about the bills for the specific student in the existing month
                                Map<String, Object> existingMonthStudentInfo = new HashMap<>();
                                existingMonthStudentInfo.put("studentId", studentId);
                                existingMonthStudentInfo.put("unpaidAmount", 2200.0); // Adjust the unpaidAmount value as needed

                                // Add the student information to the subcollection for the existing month
                                existingMonthStudentsCollection.document(studentId)
                                        .set(existingMonthStudentInfo)
                                        .addOnSuccessListener(aVoid ->
                                                Log.d("Firestore", "Document created for " + studentId + " in " + existingMonth))
                                        .addOnFailureListener(e ->
                                                Log.e("Firestore", "Error creating document", e));
                            }
                        }
                    }
                });
    }
}
