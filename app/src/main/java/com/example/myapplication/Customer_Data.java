package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Customer_Data extends AppCompatActivity {
    Button delete, update;
    EditText id, name, mob, c_name;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_data);

        id = findViewById(R.id.c_id);
        name = findViewById(R.id.name);
        mob = findViewById(R.id.c_mob);
        c_name = findViewById(R.id.c_name);

        db = FirebaseFirestore.getInstance();

        // Getting Name From View_List To Find Which Student Is Clicked
        Intent intent = getIntent();
        String selectedUserName = intent.getStringExtra("userName");

        // Finding Data Of The Student With Passed Name
        db.collection("students")
                .whereEqualTo("name", selectedUserName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String idValue = document.getString("id");
                            String nameValue = document.getString("name");
                            String mobileValue = document.getString("mob");
                            String cNameValue = document.getString("cname");

                            id.setText(idValue);
                            name.setText(nameValue);
                            mob.setText(mobileValue);
                            c_name.setText(cNameValue);
                        }
                    } else {
                        Toast.makeText(Customer_Data.this, "Error fetching user details", Toast.LENGTH_SHORT).show();
                    }
                });

        delete = findViewById(R.id.delete);
        update = findViewById(R.id.update);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = id.getText().toString();

                // Fetch all months from the "bills" collection
                db.collection("bills")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot monthDocument : task.getResult()) {
                                    String monthPath = monthDocument.getId();
                                    // Delete the student document from each month
                                    deleteStudentFromMonth(userId, monthPath);
                                }
                            } else {
                                Toast.makeText(Customer_Data.this, "Error fetching months", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = id.getText().toString();
                String nameValue = name.getText().toString();
                String mobValue = mob.getText().toString();
                String cNameValue = c_name.getText().toString();

                db.collection("students").document(userId)
                        .update("name", nameValue, "mob", mobValue, "cname", cNameValue)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Customer_Data.this, "Student Information Updated", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                            } else {
                                Toast.makeText(Customer_Data.this, "Error Updating Student Information", Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        });
            }
        });
    }

    private void deleteStudentFromMonth(String userId, String monthPath) {
        // Form the path to the student document within the specific month in "bills" collection
        String billsStudentPath = "bills/" + monthPath + "/students/" + userId;

        // Delete the student document from "bills" collection
        db.document(billsStudentPath)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    //Toast.makeText(Customer_Data.this, "Student deleted from the month in bills", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    //Toast.makeText(Customer_Data.this, "Error deleting student from the month in bills", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

        // Form the path to the student document in "students" collection
        String studentsPath = "students/" + userId;

        // Delete the student document from "students" collection
        db.document(studentsPath)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Customer_Data.this, "Student Deleted ! ", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                  //  Toast.makeText(Customer_Data.this, "Error deleting student from students collection", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

}
