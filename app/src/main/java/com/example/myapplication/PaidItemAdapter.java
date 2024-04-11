package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class PaidItemAdapter extends ArrayAdapter<PaidStudent> {

    public PaidItemAdapter(Context context, ArrayList<PaidStudent> paidStudentList) {
        super(context, 0, paidStudentList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.each_paid_item, parent, false);
        }

        PaidStudent currentPaidStudent = getItem(position);

        TextView nameTextView = listItemView.findViewById(R.id.tVName);
        nameTextView.setText(currentPaidStudent.getStudentName());

        TextView dateTextView = listItemView.findViewById(R.id.tVDate);
        dateTextView.setText(currentPaidStudent.getPaidDate());

        return listItemView;
    }
}
