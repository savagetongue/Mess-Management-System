package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    private EditText taskTitleEditText;
    private EditText taskDescriptionEditText;
    private Button saveTaskButton;

    private ArrayList<Task> taskList;

    // SharedPreferences keys
    private static final String SHARED_PREFERENCES_NAME = "task_preferences";
    private static final String TASK_LIST_KEY = "task_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskTitleEditText = findViewById(R.id.taskTitleEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        saveTaskButton = findViewById(R.id.saveTaskButton);

        taskList = loadTaskList();
        if (taskList == null) {
            taskList = new ArrayList<>();
        }

        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        String taskTitle = taskTitleEditText.getText().toString().trim();
        String taskDescription = taskDescriptionEditText.getText().toString().trim();

        if (!taskTitle.isEmpty()) {
            // Save the task details to your list
            Task newTask = new Task(taskTitle, taskDescription, false);

            // Make sure taskList is not null
            if (taskList == null) {
                taskList = new ArrayList<>();
            }

            taskList.add(newTask);

            // Save the updated task list to SharedPreferences
            saveTaskList(taskList);

            // Display a Toast message
            Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();

            // Clear input fields
            taskTitleEditText.getText().clear();
            taskDescriptionEditText.getText().clear();
        } else {
            Toast.makeText(this, "Please Enter Title", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveTaskList(ArrayList<Task> taskList) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);

        editor.putString(TASK_LIST_KEY, json);
        editor.apply();
    }

    private ArrayList<Task> loadTaskList() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(TASK_LIST_KEY, "");

        Type type = new TypeToken<ArrayList<Task>>() {}.getType();

        // Handle the case where the loaded task list is null
        return gson.fromJson(json, type) != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}
