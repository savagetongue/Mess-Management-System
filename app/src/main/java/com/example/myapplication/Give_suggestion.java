package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Give_suggestion extends AppCompatActivity {

    private EditText editTextSuggestion;
    private Button btnSubmitSuggestion;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.give_suggestion);

        editTextSuggestion = findViewById(R.id.editTextSuggestion);
        btnSubmitSuggestion = findViewById(R.id.btnSubmitSuggestion);
        firestore = FirebaseFirestore.getInstance();

        btnSubmitSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suggestionText = editTextSuggestion.getText().toString();

                // Create a new suggestion document in Firestore
                addSuggestionToFirestore(suggestionText);

                // Display a toast message (optional)
                Toast.makeText(Give_suggestion.this, "Suggestion submitted!", Toast.LENGTH_SHORT).show();

                // Optionally, you can clear the editText for a new suggestion
                editTextSuggestion.getText().clear();
            }
        });
    }

    private void addSuggestionToFirestore(String suggestionText) {
        // Get current timestamp as Firestore Timestamp
        Timestamp currentDate = Timestamp.now();

        Map<String, Object> suggestionData = new HashMap<>();
        suggestionData.put("text", suggestionText);
        suggestionData.put("date", currentDate);

        // Add a new document with a unique ID
        firestore.collection("suggestions")
                .add(suggestionData)
                .addOnSuccessListener(documentReference -> {
                    // Document added successfully
                    // You can perform any additional actions if needed
                })
                .addOnFailureListener(e -> {
                    // Handle errors here
                    Toast.makeText(Give_suggestion.this, "Failed to submit suggestion", Toast.LENGTH_SHORT).show();
                });
    }
}
