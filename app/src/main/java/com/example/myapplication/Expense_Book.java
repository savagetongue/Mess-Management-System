package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Expense_Book extends AppCompatActivity {

    DBManager db;
    ArrayList<String> list;
    Button createnote;
    String date1, notes1;
    int id1;
    ListView notes_list;
    RecyclerView taskRecyclerView;
    TaskAdapter taskAdapter;
    ArrayList<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_book);

        db = new DBManager(this);
        notes_list = findViewById(R.id.notes_list);
        createnote = findViewById(R.id.create_note);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        // Set up RecyclerView for tasks
        taskList = loadTaskList();
        taskAdapter = new TaskAdapter(this, taskList);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskRecyclerView.setAdapter(taskAdapter);

        //From Manager_Home_Screen Dates
        Intent i = getIntent();
        list = i.getStringArrayListExtra("list");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        notes_list.setAdapter(adapter);

        notes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String date = notes_list.getItemAtPosition(i).toString();
                Intent intent = new Intent(Expense_Book.this, View_Note.class);

               // Retrieving Note From DB By Date
                Cursor cursor = db.get_notes(date);

                int idIndex = cursor.getColumnIndex("id");
                int dateIndex = cursor.getColumnIndex("date");
                int notesIndex = cursor.getColumnIndex("notes");

                while (cursor.moveToNext()) {
                    id1 = cursor.getInt(idIndex);
                    date1 = cursor.getString(dateIndex);
                    notes1 = cursor.getString(notesIndex);
                }

                intent.putExtra("id", "" + id1);
                intent.putExtra("date", date1);
                intent.putExtra("notes", notes1);
                startActivityForResult(intent, 1);
            }
        });

        createnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteTypeDialog();
            }
        });
    }

    private void showNoteTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select ");

        String[] noteTypes = {"Today's Expense", "Tasks"};
        builder.setItems(noteTypes, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Today's Expense
                    Intent todayExpenseIntent = new Intent(getApplicationContext(), Create_Note.class);
                    startActivity(todayExpenseIntent);
                    break;
                case 1:
                    // Task
                    openTaskActivity();
                    break;
            }
        });

        AlertDialog dialog = builder.create(); // Creates Build
        dialog.show(); // Displays Dialog
    }

    private void openTaskActivity() {
        Intent taskIntent = new Intent(getApplicationContext(), TaskActivity.class);
        startActivityForResult(taskIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Handle the updated note from View_Note activity
                String updatedNote = data.getStringExtra("updatedNote");
                // Update your list or perform any necessary actions
            }
        }
    }

    private ArrayList<Task> loadTaskList() {
        // SharedPreference Is A Way TO Store Key:Value Pairs In Android
        SharedPreferences sharedPreferences = getSharedPreferences("task_preferences", Context.MODE_PRIVATE);
        //retrieves task prefernce from task preference in android in built
        Gson gson = new Gson(); //Java Library Used To Convert Java<->JSON
        String json = sharedPreferences.getString("task_list", "");
        // returns list of tasks
        Type type = new TypeToken<ArrayList<Task>>() {
        }.getType();

        // Handle the case where the loaded task list is null
        return gson.fromJson(json, type) != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}
