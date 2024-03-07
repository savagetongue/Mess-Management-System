package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class View_suggestion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_suggestion);

        // Retrieve suggestions from Firestore
        fetchSuggestionsFromFirestore();
    }

    private void fetchSuggestionsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("suggestions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Suggestion> suggestionList = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult()) {
                            String suggestionText = document.getString("text");
                            String date = document.getString("date");

                            Suggestion suggestion = new Suggestion(suggestionText, date);
                            suggestionList.add(suggestion);
                        }

                        // Sort the suggestionList based on date and time
                        Collections.sort(suggestionList, (s1, s2) -> {
                            String date1 = s1.getDate();
                            String date2 = s2.getDate();

                            // Handle null values to avoid NullPointerException
                            if (date1 == null && date2 == null) {
                                return 0;  // Both dates are null, consider them equal
                            } else if (date1 == null) {
                                return 1;  // If date1 is null, consider it greater than date2
                            } else if (date2 == null) {
                                return -1;  // If date2 is null, consider it greater than date1
                            }

                            // Compare non-null dates
                            return date2.compareTo(date1);  // Reverse order to sort in descending order
                        });


                        // Display suggestions in ListView
                        displaySuggestions(suggestionList);
                    } else {
                        // Handle errors here
                    }

                });
    }

    private void displaySuggestions(List<Suggestion> suggestionList) {
        ListView listView = findViewById(R.id.listViewSuggestions);

        // Use the default layout for each item
        ArrayAdapter<Suggestion> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_2, android.R.id.text1, suggestionList);
        listView.setAdapter(adapter);
    }
}
