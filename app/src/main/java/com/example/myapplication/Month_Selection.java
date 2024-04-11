package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Month_Selection extends AppCompatActivity {
    private ListView monthListView;
    private ArrayList<MonthItem> monthsList;
    private MonthItemAdapter monthsAdapter;
    private FirebaseFirestore db;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.month_selection);

        monthListView = findViewById(R.id.monthListView);
        monthsList = new ArrayList<>();
        monthsAdapter = new MonthItemAdapter(this, monthsList);
        monthListView.setAdapter(monthsAdapter);

        db = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024); // Starting from February 2024

        monthListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedMonth = monthsList.get(position).getMonthName();

                Intent intent = new Intent(Month_Selection.this, Bills.class);
                intent.putExtra("selectedMonth", selectedMonth);
                startActivity(intent);
            }
        });

        Button addmonth = findViewById(R.id.addmonth);
        addmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Month_Selection.this, "Added New Month !", Toast.LENGTH_SHORT).show();
                addNewMonth();
            }
        });

        fetchMonthsFromFirestore();
    }

    private void fetchMonthsFromFirestore() {
        CollectionReference monthsRef = db.collection("months");

        monthsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<MonthItem> monthsData = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String month = document.getId();
                        monthsData.add(new MonthItem(month));
                    }

                    monthsList.clear();
                    monthsList.addAll(monthsData);
                    monthsAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addNewMonth() {
        // Reference to the "months" collection
        CollectionReference monthsCollection = db.collection("months");

        // Fetch the latest month from the "months" collection
        monthsCollection.orderBy(FieldPath.documentId(), Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String latestMonth = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                latestMonth = document.getId();
                            }

                            // Dividing Month 2024-03 -> 2024,03 storing in parts array of string
                            String[] parts = latestMonth.split("-");
                            int year = Integer.parseInt(parts[0]); //
                            int month = Integer.parseInt(parts[1]);

                            if (month == 12) {
                                year++;
                                month = 1;
                            } else {
                                month++;
                            }
                            // Adds Next Month In Format YYYY (%04d) and MM (%02d)
                            String nextMonthYear = String.format("%04d-%02d", year, month);

                            // Add the new month to the "months" collection
                            monthsCollection.document(nextMonthYear)
                                    .set(new HashMap<>()) // Creates Doc As Name Of Month 2024-03 Type & It's Empty
                                    .addOnSuccessListener(aVoid ->
                                            Log.d("Firestore", "Document created for " + nextMonthYear + " in months"))
                                    .addOnFailureListener(e ->
                                            Log.e("Firestore", "Error creating document", e));

                            // Add the new month to the "bills" collection
                            addNewMonthInBills(nextMonthYear);
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addNewMonthInBills(String nextMonthYear) {
        // Reference to the "bills" collection
        CollectionReference billsCollection = db.collection("bills");

        // Format the month name for the "bills" collection
        String billsMonth = new DateFormatSymbols().getShortMonths()[Integer.parseInt(nextMonthYear.split("-")[1]) - 1] + "_" + nextMonthYear.split("-")[0];

        // Add the new month to the "bills" collection
        billsCollection.document(billsMonth)
                .set(new HashMap<>())
                .addOnSuccessListener(aVoid ->
                        Log.d("Firestore", "Document created for " + billsMonth + " in bills"))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Error creating document", e));

        // Add "students" collection with dummy data (studentId and unpaidAmount)
        DocumentReference monthDocument = billsCollection.document(billsMonth);
        CollectionReference studentsCollection = monthDocument.collection("students");

        db.collection("students")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot studentSnapshot : task.getResult()) {
                                String studentId = studentSnapshot.getId();

                                Map<String, Object> studentInfo = new HashMap<>();
                                studentInfo.put("studentId", studentId);
                                studentInfo.put("unpaidAmount", 2200.0);

                                studentsCollection.document(studentId)
                                        .set(studentInfo)
                                        .addOnSuccessListener(aVoid ->
                                                Log.d("Firestore", "Document created for " + studentId + " in " + billsMonth))
                                        .addOnFailureListener(e ->
                                                Log.e("Firestore", "Error creating document", e));
                            }
                        }
                    }
                });
    }
}
