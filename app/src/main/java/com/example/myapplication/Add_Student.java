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

import com.google.firebase.firestore.*;
import java.text.DateFormatSymbols;
import java.util.*;



public class Add_Student extends AppCompatActivity {

    private EditText name, mob;
    private Spinner c_name; //Used To Get A Drop Down List
    private Button submit;
    private FirebaseFirestore db;

    // Using This Var To Assign ID's To Students Starting From 1,2,3,4...n
    private int studentCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);

        String college[] = {"SMSMPITR (Diploma - FY)","SMSMPITR (Diploma - SY)","SMSMPITR (Diploma - TY)", "SMSMPITR (Degree - FY)","SMSMPITR (Degree - SY)","SMSMPITR (Degree - TY)","SMSMPITR (Degree - 4Y)"};
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
                    Toast.makeText(Add_Student.this, "All Fields Are Required...!!!", Toast.LENGTH_SHORT).show();
                } else if (mobile.length() != 10) {
                    Toast.makeText(Add_Student.this, "Enter A 10-digit Valid Number", Toast.LENGTH_SHORT).show();
                } else {
                    addCustomerToFirestore(name_1, clg_name, mobile);
                    Toast.makeText(Add_Student.this, "Student Information is Recorded...", Toast.LENGTH_SHORT).show();

                    // Clear input fields after successful addition
                    name.setText("");
                    mob.setText("");
                    // Keep the focus on the name field for convenience
                    name.requestFocus(); //It Creates That Blinking Anime In EditText |||
                }
            }
        });
    }

    private void fetchLatestStudentId() {
        // Fetch the latest student ID from Firestore to increment
        db.collection("students")
                .orderBy("id")
                .limitToLast(1)
                //Limits Query TO Get Only Last Doc With Highest ID
                .get()
                //using queryDocumentSnapshots Instead Of DocumentSnapshot As We Gotta Deal With Multiple DOC
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    //
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    if (!documents.isEmpty()) {
                        // Retrieving The Highest studentId and Incrementing It By 1
                        DocumentSnapshot lastStudentSnapshot = documents.get(0);
                        //Storing Last ID In Above Doc Snapshot By First Doc (0th Index)
                        //As It Ordered Student Id's In Descending Order High To Low
                        Student lastStudent = lastStudentSnapshot.toObject(Student.class);
                        studentCounter = lastStudent.getStudentId() + 1;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Add_Student.this, "Error getting student counter", Toast.LENGTH_SHORT).show();
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
                //Using aVoid type indicates that the Firestore operation doesn't return any specific result data,
                // hence it's represented as Void.
                .addOnSuccessListener(aVoid -> {
                    // Increment the student counter for the next student
                    studentCounter++;

                    createBillsCollection(studentId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Add_Student.this, "Error Adding Student", Toast.LENGTH_SHORT).show();
                });
    }

    private void createBillsCollection(String studentId) {
        // Reference to the "bills" collection
        CollectionReference billsCollection = db.collection("bills");

        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024); //Starting From 2024
        int currentMonth = calendar.get(Calendar.MONTH); //From Current Month

        // Fetch the existing months from the "bills" collection
        billsCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //monthDocument represents single doc in bills collection
                        for (DocumentSnapshot monthDocument : task.getResult()) {
                            String existingMonth = monthDocument.getId();
                            // stores month abbreviation in existingMonth var such as Mar_2024,Apr_2024,May_2024
                            // Calculate the month and year for the existing month
                            //Using .asList as Index Works With List Not With Array
                            int existingMonthIndex = Arrays.asList(new DateFormatSymbols().getShortMonths()).indexOf(existingMonth.substring(0, 3));
                            //Above Line First Takes Month With 3 letter using substring(0,3) for ex January->Jan
                            int existingYear = Integer.parseInt(existingMonth.substring(existingMonth.indexOf("_") + 1));

                            // Check if the existing month is in the future (including the current month)
                            if ((existingYear > calendar.get(Calendar.YEAR)) ||
                                    (existingYear == calendar.get(Calendar.YEAR) && existingMonthIndex >= currentMonth)) {
                                // Reference to the subcollection for students within the existing month document
                                CollectionReference existingMonthStudentsCollection = monthDocument.getReference().collection("students");
                                // Adding Collection students in bills collection
                                // Add information about the bills for the specific student in the existing month
                                Map<String, Object> existingMonthStudentInfo = new HashMap<>();
                               //Using HashMap It's A Java Class That Stores Elements In A Key-Value Pair
                               // Keys-> String And Values -> Object (Coz It's Fleixble For Heterogenous Data Types)
                                existingMonthStudentInfo.put("studentId", studentId);
                                existingMonthStudentInfo.put("unpaidAmount", 2200.0); // Adjust the unpaidAmount value as needed

                                // Add the student information to the doc by student id 1,2,3,4..n
                                // And Adding These Two Fields studentId & unpaidAmount For Each Doc
                                existingMonthStudentsCollection.document(studentId)
                                        .set(existingMonthStudentInfo)
                                        // sets data in existingMonthStudentsCollection to DOC
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
