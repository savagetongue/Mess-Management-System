package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Sign_In extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        db = FirebaseFirestore.getInstance();
        Button buttonSignIn = findViewById(R.id.buttonSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEditText = findViewById(R.id.editTextUsername);
                EditText passwordEditText = findViewById(R.id.editTextPassword);
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    showToast("Please Enter Both Username And Password");
                    return;
                }


                db.collection("login").document(username).get()

                        //Need To Use OnCompleteListener And Task Otherwise We'll Get IllegalStateException
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists() && password.equals(document.getString("password"))) {
                                        Intent intent = new Intent(Sign_In.this, username.equals("manager") ? Manager_Home_Screen.class : Student_Home_Screen.class);
                                        startActivity(intent);
                                    } else {
                                        showToast("Invalid Username Or Password");
                                    }
                                } else {
                                    showToast("Failed to Authenticate. Please try again.");
                                }
                            }
                        });
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(Sign_In.this, message, Toast.LENGTH_SHORT).show();
    }
}
