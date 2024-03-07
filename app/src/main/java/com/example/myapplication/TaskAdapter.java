package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> taskList;
    private Context context;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task currentTask = taskList.get(position);

        holder.titleTextView.setText(currentTask.getTitle());
        holder.descriptionTextView.setText(currentTask.getDescription());
        holder.checkBox.setChecked(currentTask.isChecked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentTask.setChecked(isChecked);
            saveTaskListToSharedPreferences();
        });

        holder.deleteButton.setOnClickListener(view -> {
            taskList.remove(currentTask);
            saveTaskListToSharedPreferences();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView descriptionTextView;
        private final CheckBox checkBox;
        private final Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.taskTitleTextView);
            descriptionTextView = itemView.findViewById(R.id.taskDescriptionTextView);
            checkBox = itemView.findViewById(R.id.completeCheckBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private void saveTaskListToSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("task_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);

        editor.putString("task_list", json);
        editor.apply();
    }
}
