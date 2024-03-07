package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Sign_1 extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = FirebaseFirestore.getInstance();

        Button buttonSignIn = findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEditText = findViewById(R.id.editTextUsername);
                EditText passwordEditText = findViewById(R.id.editTextPassword);

                // Get the entered username and password
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                // Check if the entered username and password are empty
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Sign_1.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Query Firestore to check if the username exists and the password is correct
                db.collection("login")
                        .document(username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    if (document.exists() && password.equals(document.getString("password"))) {
                                        // If the entered username and password are correct, determine the activity to start
                                        Class<?> destinationActivity = (username.equals("manager")) ? Activity_Home_Screen.class : menu.class;

                                        // Start the appropriate activity
                                        Intent intent = new Intent(Sign_1.this, destinationActivity);
                                        startActivity(intent);
                                    } else {
                                        // If the entered username or password is incorrect, show an error message
                                        Toast.makeText(Sign_1.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle errors accessing Firestore
                                    Toast.makeText(Sign_1.this, "Error accessing Firestore", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
