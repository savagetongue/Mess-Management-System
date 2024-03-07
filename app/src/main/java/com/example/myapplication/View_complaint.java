package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class View_complaint extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_complaint);

        // Display the complaints from Firestore in the layout
        displayComplaintsFromFirestore();
    }

    private void displayComplaintsFromFirestore() {
        LinearLayout linearLayout = findViewById(R.id.linearLayoutComplaints);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference complaintsRef = db.collection("complaints");

        complaintsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                    for (DocumentSnapshot document : documents) {
                        // Create a LinearLayout to hold each complaint's details
                        LinearLayout complaintLayout = new LinearLayout(this);
                        complaintLayout.setOrientation(LinearLayout.VERTICAL);

                        // Create a TextView for complaint text
                        TextView textView = new TextView(this);
                        textView.setText("Complaint: " + document.getString("complaintText") +
                                "\nDate: " + document.getString("date"));

                        // Add the TextView to the layout
                        complaintLayout.addView(textView);

                        // Decode and display the image using Glide
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            ImageView imageView = new ImageView(this);
                            // Use Glide to load and display the image
                            Glide.with(this).load(imageUrl).into(imageView);
                            // Add the ImageView to the layout
                            complaintLayout.addView(imageView);
                        }

                        // Add the complaint layout to the main linear layout
                        linearLayout.addView(complaintLayout);
                    }
                }
            } else {
                // Handle errors here
            }
        });
    }
}