package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class View_List extends AppCompatActivity {
    private ListView customerList;
    private EditText searchEditText;
    private FirebaseFirestore db;
    private static final int REQUEST_CODE_UPDATE_DELETE = 1; //Used For Communication Betn View_List & Customer Data Activity

    private ArrayList<Student2> studentList; //Dynamic Array
    private StudentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list);

        db = FirebaseFirestore.getInstance();

        customerList = findViewById(R.id.Customer_list);
        searchEditText = findViewById(R.id.searchEditText);

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(this, studentList);
        customerList.setAdapter(adapter);

        fetchUsersFromFirestore();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
                // calling filterUsers Method When An Input For Search Is Given In EditText
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedUserName = studentList.get(position).getStudentName();
                Intent intent = new Intent(View_List.this, Customer_Data.class);
                intent.putExtra("userName", selectedUserName);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_DELETE);
            }
        });
    }

    private void fetchUsersFromFirestore() {
        CollectionReference usersRef = db.collection("students");

        usersRef.orderBy("id")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            studentList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String studentName = document.getString("name");
                                studentList.add(new Student2(studentName));
                            }
                            adapter.notifyDataSetChanged();
                            // Tells ListView To Reflect New Changes
                        } else {
                            Toast.makeText(View_List.this, "Error fetching users", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void filterUsers(String query) {
        if (query.isEmpty()) {
            fetchUsersFromFirestore(); // Reload the entire list of students
        } else {
            // Created An Array Of Filtered Students
            ArrayList<Student2> filteredList = new ArrayList<>();
            // Searching Entered String In Current Listview
            for (Student2 student : studentList) {
                //Converting Entered String To Lowercase & Matching It With ListView Items
                if (student.getStudentName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(student);
                    //Adding Student To filteredList
                }
            }
            adapter.clear();
            adapter.addAll(filteredList);
            adapter.notifyDataSetChanged();
        }
    }


    // When We Return From Customer Data To View_List Display The ListView Again
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE_DELETE && resultCode == RESULT_OK) {
            fetchUsersFromFirestore();
        }
    }
}
