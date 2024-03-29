package com.example.myapplication;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PaidItemAdapter extends ArrayAdapter<PaidStudent> {
    private final Activity context;
    private final ArrayList<PaidStudent> paidList;

    public PaidItemAdapter(Activity context, ArrayList<PaidStudent> paidList) {
        super(context, R.layout.each_paid_item, paidList);
        this.context = context;
        this.paidList = paidList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.each_paid_item, null, true);

        TextView nameTextView = view.findViewById(R.id.tVName);
        nameTextView.setText(paidList.get(position).getStudentName());

        return view;
    }
}
