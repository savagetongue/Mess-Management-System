package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UnpaidItemAdapter extends ArrayAdapter<UnpaidStudent> {
    private final Activity context;
    private final ArrayList<UnpaidStudent> unpaidList;

    public UnpaidItemAdapter(Activity context, ArrayList<UnpaidStudent> unpaidList) {
        super(context, R.layout.each_unpaid_item, unpaidList);
        this.context = context;
        this.unpaidList = unpaidList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.each_unpaid_item, null);

        CircleImageView image = view.findViewById(R.id.profile_image);
        TextView name = view.findViewById(R.id.tVName);

        // Assuming you have an image for each unpaid student, you can set it here
        // image.setImageResource(R.drawable.student_image);

        name.setText(unpaidList.get(position).getStudentName());

        return view;
    }
}