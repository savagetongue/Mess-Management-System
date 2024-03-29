package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class activity_view_list extends AppCompatActivity {
    private ListView customerList;
    private FirebaseFirestore db;
    private static final int REQUEST_CODE_UPDATE_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        db = FirebaseFirestore.getInstance();

        customerList = findViewById(R.id.Customer_list);
        fetchUsersFromFirestore();
    }

    private void fetchUsersFromFirestore() {
        CollectionReference usersRef = db.collection("students");

        usersRef.orderBy("id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Student2> studentList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String studentName = document.getString("name");
                                studentList.add(new Student2(studentName));
                            }

                            StudentAdapter studentAdapter = new StudentAdapter(
                                    activity_view_list.this,
                                    studentList
                            );

                            customerList.setAdapter(studentAdapter);

                            customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                    String selectedUserName = studentList.get(position).getStudentName();
                                    Intent intent = new Intent(activity_view_list.this, customer_data.class);
                                    intent.putExtra("userName", selectedUserName);
                                    startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);
                                }
                            });
                        } else {
                            Toast.makeText(activity_view_list.this, "Error fetching users", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE_DELETE && resultCode == RESULT_OK) {
            fetchUsersFromFirestore();
        }
    }
}
