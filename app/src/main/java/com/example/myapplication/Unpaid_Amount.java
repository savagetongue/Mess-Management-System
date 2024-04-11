package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.util.Date;

public class Unpaid_Amount extends AppCompatActivity {
    SmsManager sms;
    EditText id, name, bill;
    FirebaseFirestore db;
    Button reminder, update;
    String studentId, studentName, studentPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unpaid_amount);

        db = FirebaseFirestore.getInstance();
        sms = SmsManager.getDefault();
        reminder = findViewById(R.id.reminder);
        update = findViewById(R.id.update);

        id = findViewById(R.id.c_id);
        name = findViewById(R.id.c_name);
        bill = findViewById(R.id.c_unpaid_et);

        Intent i = getIntent();
        studentId = i.getStringExtra("selectedStudentId");
        String selectedMonth = i.getStringExtra("selectedMonth");

        // Display the received studentId to check if it's correct
        //Toast.makeText(this, "Received studentId: " + studentId, Toast.LENGTH_SHORT).show();

        // Query Firestore to get the document reference based on student's ID from "students" collection
        DocumentReference studentRef = db.collection("students").document(studentId);

        studentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Retrieve student data from "students" collection
                studentName = task.getResult().getString("name");
                studentPhoneNumber = task.getResult().getString("mob");

                // Display the fetched data
                id.setText(studentId);
                name.setText(studentName);

                // Fetch and display the unpaid amount
                fetchUnpaidAmount(studentId, formatMonth(selectedMonth));
            } else {
                // Handle the case when the student document is not found
                Toast.makeText(Unpaid_Amount.this, "Student not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Set up the Save (Update) button
        update.setOnClickListener(v -> {
            // Update only the unpaid amount in Firestore
            updateUnpaidAmount(studentId, formatMonth(selectedMonth));
        });

        // Set up the Reminder button
        reminder.setOnClickListener(v -> sendReminderMessage());
    }

    private void fetchUnpaidAmount(String studentId, String formattedMonth) {
        // Query Firestore to get the unpaid amount from the "bills" subcollection
        DocumentReference billsRef = db.collection("bills").document(formattedMonth).collection("students").document(studentId);

        billsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Retrieve unpaid amount from the "bills" subcollection
                double unpaidAmount = task.getResult().getDouble("unpaidAmount");

                // Display the fetched unpaid amount
                bill.setText(String.valueOf(unpaidAmount));
            } else {
                // Handle the case when the bills document or student document is not found
                Toast.makeText(Unpaid_Amount.this, "Unpaid amount not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUnpaidAmount(String studentId, String formattedMonth) {
        // Update the unpaid amount in Firestore
        double updatedUnpaidAmount = Double.parseDouble(bill.getText().toString());

        DocumentReference billsRef = db.collection("bills").document(formattedMonth).collection("students").document(studentId);
        billsRef.update("unpaidAmount", updatedUnpaidAmount)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Unpaid_Amount.this, "Updated", Toast.LENGTH_SHORT).show();
                    // If the unpaid amount is 0, store the current date in the database
                    if (updatedUnpaidAmount == 0) {
                        // Get the current date
                        String currentDate = DateFormat.getDateInstance().format(new Date());
                        // Update the date field in the database
                        billsRef.update("paidDate", currentDate)
                                .addOnSuccessListener(aVoid1 ->
                                        Toast.makeText(Unpaid_Amount.this, "Paid date updated", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e ->
                                        Toast.makeText(Unpaid_Amount.this, "Error updating paid date", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(Unpaid_Amount.this, "Error updating unpaid amount", Toast.LENGTH_SHORT).show());
    }

    private String formatMonth(String selectedMonth) {
        // Convert "2024-03" to "Mar_2024"
        String[] parts = selectedMonth.split("-");
        String year = parts[0];
        String month = new DateFormatSymbols().getShortMonths()[Integer.parseInt(parts[1]) - 1];
        return month.substring(0, 1).toUpperCase() + month.substring(1) + "_" + year;
    }

    private void sendReminderMessage() {
        String reminderMessage = "Dear " + studentName + "," +
                " Your mess bill " + bill.getText().toString() + " is pending. " +
                "Please pay your bill as soon as possible.";

        // Check if the phone number is not empty before sending the message
        if (studentPhoneNumber != null && !studentPhoneNumber.isEmpty()) {
            // Send reminder message
            sms.sendTextMessage(studentPhoneNumber, null, reminderMessage, null, null);
            Toast.makeText(Unpaid_Amount.this, "Reminder message sent to " + studentPhoneNumber, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Unpaid_Amount.this, "Phone number not found for the selected student", Toast.LENGTH_SHORT).show();
        }
    }
}
