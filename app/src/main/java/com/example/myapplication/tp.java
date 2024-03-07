package com.example.myapplication;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class tp extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tp);

        // Enable strict mode for debugging (remove in production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new document with some data
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        data.put("age", 25);

        // Add the document to a collection (replace 'yourCollection' with your actual collection name)
        db.collection("yourCollection")
                .add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Document added with ID: " + task.getResult().getId());

                            // Fetch the document to check read functionality
                            DocumentReference docRef = db.collection("yourCollection").document(task.getResult().getId());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            // Get the TextView reference
                                            textView = findViewById(R.id.textView);

                                            // Display document data in TextView
                                            String documentData = "Name: " + document.getString("name") + "\n" +
                                                    "Age: " + document.getLong("age");
                                            textView.setText(documentData);

                                            System.out.println("Document data: " + document.getData());
                                            // Document exists, read successful
                                        } else {
                                            System.out.println("No such document");
                                        }
                                    } else {
                                        System.err.println("Error getting document: " + task.getException());
                                    }
                                }
                            });
                        } else {
                            System.err.println("Error adding document: " + task.getException());
                        }
                    }
                });
    }
}
