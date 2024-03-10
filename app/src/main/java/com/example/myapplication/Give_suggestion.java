package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Give_suggestion extends AppCompatActivity {

    private EditText editTextSuggestion;
    private Button btnSubmitSuggestion;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.give_suggestion);

        editTextSuggestion = findViewById(R.id.editTextSuggestion);
        btnSubmitSuggestion = findViewById(R.id.btnSubmitSuggestion);
        firestore = FirebaseFirestore.getInstance();

        btnSubmitSuggestion.setOnClickListener(v -> {
            String suggestionText = editTextSuggestion.getText().toString();

            // Check if selectedImageBitmap is not null
            // Since Give_suggestion doesn't seem to involve images, we can skip the image handling part

            // Save the suggestion details to Firestore
            saveSuggestionToFirestore(suggestionText);

            // Display a toast message (optional)
            Toast.makeText(Give_suggestion.this, "Suggestion submitted!", Toast.LENGTH_SHORT).show();

            // Optionally, you can clear the editText for a new suggestion
            editTextSuggestion.getText().clear();
        });
    }

    private void saveSuggestionToFirestore(String suggestionText) {
        // Get current timestamp as Firestore Timestamp
        Timestamp currentDate = Timestamp.now();

        Map<String, Object> suggestionData = new HashMap<>();
        suggestionData.put("text", suggestionText);
        suggestionData.put("date", currentDate);

        // Add the suggestion data to the "suggestions" collection in Firestore
        firestore.collection("suggestions")
                .add(suggestionData)  // Use add() to generate a unique document ID
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Suggestion added successfully
                        // You can perform any additional actions if needed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure to add suggestion
                        Toast.makeText(Give_suggestion.this, "Failed to submit suggestion", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
