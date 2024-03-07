package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class bills_unpaid extends AppCompatActivity {
    ListView unpaid_list;
    FirebaseFirestore db;
    String selectedMonth;
    String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_unpaid);

        db = FirebaseFirestore.getInstance();

        // Assuming you have a button or some trigger to fetch data
        fetchData();
        unpaid_list = findViewById(R.id.unpaid_list);
    }

    private void fetchData() {
        selectedMonth = getIntent().getStringExtra("selectedMonth");
        String formattedMonth = formatMonth(selectedMonth); // Convert to MAR_2024 format

        String billsPath = "bills/" + formattedMonth + "/students";

        db.collection(billsPath)
                .whereGreaterThan("unpaidAmount", 0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<UnpaidStudent> studentList = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        studentId = document.getId();
                        Double unpaidAmount = document.getDouble("unpaidAmount");

                        // Now that we have the studentId and unpaidAmount, fetch student info
                        fetchStudentInfo(studentId, unpaidAmount, studentList);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    e.printStackTrace(); // Log the error
                });
    }

    private void fetchStudentInfo(String studentId, Double unpaidAmount, ArrayList<UnpaidStudent> studentList) {
        db.collection("students")
                .document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");

                        // Only display the name in the ListView
                        studentList.add(new UnpaidStudent(studentName));

                        // Update the ListView
                        displayData(studentList);
                    } else {
                        // Log if the document doesn't exist
                        System.out.println("Document for studentId " + studentId + " does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    e.printStackTrace(); // Log the error
                });
    }

    private void displayData(ArrayList<UnpaidStudent> studentList) {
        // Update your ListView or any other UI component with the fetched data
        UnpaidItemAdapter adapter = new UnpaidItemAdapter(this, studentList);

        unpaid_list.setAdapter(adapter);

        // Set the item click listener to get the selected item's data
        unpaid_list.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected student name
            String selectedStudentName = studentList.get(position).getStudentName();

            // Now you have the selected student name, fetch the ID and send it in the intent
            fetchStudentId(selectedStudentName);
        });
    }

    private void fetchStudentId(String selectedStudentName) {
        // Fetch the student ID based on the selected name
        db.collection("students")
                .whereEqualTo("name", selectedStudentName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String selectedStudentId = document.getId();

                        // Now you have the selected student ID, you can use it as needed
                        navigateToUnpaidAmount(selectedStudentId, selectedMonth);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    e.printStackTrace(); // Log the error
                });
    }

    private void navigateToUnpaidAmount(String studentId, String selectedMonth) {
        Intent intent = new Intent(this, unpaid_amount.class);
        intent.putExtra("selectedStudentId", studentId);
        intent.putExtra("selectedMonth", selectedMonth);
        startActivity(intent);
    }

    private String formatMonth(String selectedMonth) {
        // Check if selectedMonth is null
        if (selectedMonth == null) {
            // Handle the null case, for example, return a default value or throw an exception
            return "MAR_2024"; // Replace with your desired default value
        }

        // Convert "2024-03" to "MAR_2024"
        String[] parts = selectedMonth.split("-");
        String year = parts[0];
        String month = new DateFormatSymbols().getShortMonths()[Integer.parseInt(parts[1]) - 1];
        return month + "_" + year;
    }
}
