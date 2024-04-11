package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class View_Complaint extends AppCompatActivity {

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

        complaintsRef.orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    List<DocumentSnapshot> documents = querySnapshot.getDocuments();

                    // Sort the documents based on the date
                    //It'll Compare Each Doc Till All Doc Are Compared And Latest IS Found
                    Collections.sort(documents, new Comparator<DocumentSnapshot>() {
                        @Override
                        public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {
                            Object date1 = o1.get("date");
                            Object date2 = o2.get("date");

                            if (date1 instanceof Timestamp && date2 instanceof Timestamp) {
                                Timestamp timestamp1 = (Timestamp) date1;
                                Timestamp timestamp2 = (Timestamp) date2;

                                // Reverse the order for descending sorting
                                return timestamp2.compareTo(timestamp1);
                            }
                            // If TimeStamp Not Worked And IT IS In String
                            else if (date1 instanceof String && date2 instanceof String) {
                                String dateString1 = (String) date1;
                                String dateString2 = (String) date2;

                                // Parse and compare the date strings
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy h:mm:ss a");
                                try {
                                    Date dateObj1 = sdf.parse(dateString1);
                                    Date dateObj2 = sdf.parse(dateString2);

                                    // Reverse the order for descending sorting
                                    return dateObj2.compareTo(dateObj1);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            return 0;
                        }
                    });

                    // Display sorted complaints
                    for (DocumentSnapshot document : documents) {

                        LinearLayout complaintLayout = new LinearLayout(this);
                        complaintLayout.setOrientation(LinearLayout.VERTICAL);

                        // Create a TextView for complaint text
                        TextView textView = new TextView(this);
                        String complaintText = document.getString("complaintText");
                        Object dateObject = document.get("date");

                        if (complaintText != null && dateObject != null) {
                            String dateString;
                            if (dateObject instanceof Timestamp) {
                                Timestamp timestamp = (Timestamp) dateObject;
                                dateString = new SimpleDateFormat("MMM d, yyyy h:mm:ss a").format(new Date(timestamp.getSeconds() * 1000));
                            } else {
                                dateString = (String) dateObject;
                            }

                            textView.setText("Complaint: " + complaintText +
                                    "\nDate: " + dateString);
                        }

                        // Add the TextView to the layout
                        complaintLayout.addView(textView);

                        // Decode and display the image using Glide
                        String imageUrl = document.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            ImageView imageView = new ImageView(this);
                            // Use Glide to load and display the image
                            //Glide Helps TO Load Image Using URL
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
