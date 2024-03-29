package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

public class bills_paid extends AppCompatActivity {
    private ListView paidListView;
    private EditText searchEditText;
    private FirebaseFirestore db;
    private static final int REQUEST_CODE_UPDATE_DELETE = 1;

    private ArrayList<PaidStudent> paidStudentList;
    private PaidItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_paid);

        db = FirebaseFirestore.getInstance();

        paidListView = findViewById(R.id.paid_list);
        searchEditText = findViewById(R.id.searchEditText);

        paidStudentList = new ArrayList<>();
        adapter = new PaidItemAdapter(this, paidStudentList);
        paidListView.setAdapter(adapter);

        fetchDataFromFirestore();

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
    }

    private void fetchDataFromFirestore() {
        String selectedMonth = getIntent().getStringExtra("selectedMonth");
        String formattedMonth = formatMonth(selectedMonth); // Convert to MAR_2024 format
        String studentsPath = "bills/" + formattedMonth + "/students";

        db.collection(studentsPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String studentId = document.getString("studentId");
                                Double unpaidAmount = document.getDouble("unpaidAmount");
                                if (unpaidAmount != null && unpaidAmount == 0.0) {
                                    fetchStudentName(studentId);
                                }
                            }
                        } else {
                            Toast.makeText(bills_paid.this, "Error fetching students: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchStudentName(String studentId) {
        db.collection("students")
                .document(studentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String studentName = document.getString("name");
                                paidStudentList.add(new PaidStudent(studentName));
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(bills_paid.this, "Error fetching student name: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void filterStudents(String query) {
        if (query.isEmpty()) {
            paidStudentList.clear();
            fetchDataFromFirestore(); // Reload the entire list of students
        } else {
            ArrayList<PaidStudent> filteredList = new ArrayList<>();
            for (PaidStudent student : paidStudentList) {
                if (student.getStudentName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(student);
                }
            }
            adapter.clear();
            adapter.addAll(filteredList);
            adapter.notifyDataSetChanged();
        }
    }

    private String formatMonth(String selectedMonth) {
        // Convert "2024-03" to "Mar_2024"
        String[] parts = selectedMonth.split("-");
        String year = parts[0];
        String month = new DateFormatSymbols().getShortMonths()[Integer.parseInt(parts[1]) - 1];
        return month.substring(0, 1).toUpperCase() + month.substring(1) + "_" + year;
    }
}
