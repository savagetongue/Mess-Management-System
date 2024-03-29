package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class bills_unpaid extends AppCompatActivity {
    ListView unpaid_list;
    EditText searchEditText;
    FirebaseFirestore db;
    String selectedMonth;
    String studentId;
    UnpaidItemAdapter adapter;
    List<UnpaidStudent> originalStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_unpaid);

        db = FirebaseFirestore.getInstance();

        unpaid_list = findViewById(R.id.unpaid_list);
        searchEditText = findViewById(R.id.searchEditText);

        originalStudentList = new ArrayList<>();
        adapter = new UnpaidItemAdapter(this, originalStudentList);
        unpaid_list.setAdapter(adapter);

        fetchData();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        unpaid_list.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStudentName = originalStudentList.get(position).getStudentName();
            fetchStudentId(selectedStudentName);
        });
    }

    private void fetchData() {
        selectedMonth = getIntent().getStringExtra("selectedMonth");
        String formattedMonth = formatMonth(selectedMonth);

        String billsPath = "bills/" + formattedMonth + "/students";

        db.collection(billsPath)
                .whereGreaterThan("unpaidAmount", 0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<UnpaidStudent> studentList = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        studentId = document.getId();
                        Double unpaidAmount = document.getDouble("unpaidAmount");

                        fetchStudentInfo(studentId, unpaidAmount, studentList);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void fetchStudentInfo(String studentId, Double unpaidAmount, ArrayList<UnpaidStudent> studentList) {
        db.collection("students")
                .document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");
                        studentList.add(new UnpaidStudent(studentName));
                        displayData(studentList);
                    } else {
                        System.out.println("Document for studentId " + studentId + " does not exist.");
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void displayData(ArrayList<UnpaidStudent> studentList) {
        originalStudentList.clear();
        originalStudentList.addAll(studentList);
        adapter.notifyDataSetChanged();
    }

    private void fetchStudentId(String selectedStudentName) {
        db.collection("students")
                .whereEqualTo("name", selectedStudentName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String selectedStudentId = document.getId();
                        navigateToUnpaidAmount(selectedStudentId, selectedMonth);
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void filterStudents(String query) {
        if (query.isEmpty()) {
            // If the search query is empty, fetch the original data again
            fetchData();
        } else {
            // If there's a search query, filter the items based on the query
            ArrayList<UnpaidStudent> filteredList = new ArrayList<>();
            for (UnpaidStudent student : originalStudentList) {
                if (student.getStudentName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(student);
                }
            }
            adapter.clear();
            adapter.addAll(filteredList);
            adapter.notifyDataSetChanged();
        }
    }



    private void navigateToUnpaidAmount(String studentId, String selectedMonth) {
        Intent intent = new Intent(this, unpaid_amount.class);
        intent.putExtra("selectedStudentId", studentId);
        intent.putExtra("selectedMonth", selectedMonth);
        startActivity(intent);
    }

    private String formatMonth(String selectedMonth) {
        if (selectedMonth == null) {
            return "MAR_2024";
        }
        String[] parts = selectedMonth.split("-");
        String year = parts[0];
        String month = new DateFormatSymbols().getShortMonths()[Integer.parseInt(parts[1]) - 1];
        return month + "_" + year;
    }
}
