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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCustomer extends AppCompatActivity {

    private EditText name, mob;
    private Spinner c_name;
    private Button submit;
    private FirebaseFirestore db; // Firestore instance
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
                    // Handle errors
                    Toast.makeText(AddCustomer.this, "Error getting student counter", Toast.LENGTH_SHORT).show();
                });

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
                    addCustomerToFirestore(name_1, clg_name, mobile);
                    Toast.makeText(AddCustomer.this, "Customer Information is Recorded...", Toast.LENGTH_SHORT).show();

                    name.setText("");
                    mob.setText("");
                }
            }
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
                    // Document added successfully
                    createBillsCollection(studentId);
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    Toast.makeText(AddCustomer.this, "Error adding customer", Toast.LENGTH_SHORT).show();
                });
    }

    private void createBillsCollection(String studentId) {
        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024); // Starting from February 2024
        int currentMonth = calendar.get(Calendar.MONTH);

        // Create a document for the current month under "bills" collection
        String monthName = new DateFormatSymbols().getShortMonths()[currentMonth % 12];
        String monthYear = monthName + "_" + calendar.get(Calendar.YEAR);

        // Reference to the "bills" collection
        CollectionReference billsCollection = db.collection("bills");

        // Reference to the specific month's document
        DocumentReference monthDocument = billsCollection.document(monthYear);

        // Reference to the subcollection for students within the month document
        CollectionReference studentsCollection = monthDocument.collection("students");

        // Add information about the bills for the specific student
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("studentId", studentId);
        studentInfo.put("unpaidAmount", 2000.0);

        // Add the student information to the subcollection
        studentsCollection.document(studentId)
                .set(studentInfo)
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "Document created for " + studentId + " in " + monthYear))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error creating document", e));
    }

}
