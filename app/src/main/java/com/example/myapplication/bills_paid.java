package com.example.myapplication;

import android.os.Bundle;
import android.widget.ListView;
import java.text.DateFormatSymbols;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class bills_paid extends AppCompatActivity {
    ListView paid_list;
    FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_paid);

        db = FirebaseFirestore.getInstance();

        // Assuming you have a button or some trigger to fetch data
        fetchData();
    }

    private void fetchData() {
        String selectedMonth = getIntent().getStringExtra("selectedMonth");
        String formattedMonth = formatMonth(selectedMonth); // Convert to MAR_2024 format
        String studentsPath = "bills/" + formattedMonth + "/students";

        db.collection(studentsPath)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<PaidStudent> paidStudentList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String studentId = document.getString("studentId");
                        Double unpaidAmount = document.getDouble("unpaidAmount");

                        // Assuming unpaidAmount being 0 indicates the bill is paid
                        if (unpaidAmount != null && unpaidAmount == 0.0) {
                            // Now, let's fetch the student's name using the studentId
                            fetchStudentName(studentId, paidStudentList);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    e.printStackTrace(); // Log the error
                });
    }

    private void fetchStudentName(String studentId, ArrayList<PaidStudent> paidStudentList) {
        db.collection("students")
                .document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");
                        paidStudentList.add(new PaidStudent(studentName));

                        // Update the ListView
                        displayData(paidStudentList);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    e.printStackTrace(); // Log the error
                });
    }

    private void displayData(ArrayList<PaidStudent> paidStudentList) {
        // Update your ListView or any other UI component with the fetched data
        PaidItemAdapter adapter = new PaidItemAdapter(this, paidStudentList);


        paid_list = findViewById(R.id.paid_list);
        paid_list.setAdapter(adapter);
    }

    private String formatMonth(String selectedMonth) {
        // Convert "2024-03" to "Mar_2024"
        String[] parts = selectedMonth.split("-");
        String year = parts[0];
        String month = new DateFormatSymbols().getShortMonths()[Integer.parseInt(parts[1]) - 1];
        return month.substring(0, 1).toUpperCase() + month.substring(1) + "_" + year;
    }
}
