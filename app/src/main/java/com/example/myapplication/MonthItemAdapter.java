package com.example.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MonthItemAdapter extends ArrayAdapter<MonthItem> {
    private final Activity context;
    private final ArrayList<MonthItem> monthList;

    public MonthItemAdapter(Activity context, ArrayList<MonthItem> monthList) {
        super(context, R.layout.each_month_item, monthList);
        this.context = context;
        this.monthList = monthList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.each_month_item, null);

        CircleImageView image = view.findViewById(R.id.profile_image);
        TextView name = view.findViewById(R.id.tVName);

        name.setText(monthList.get(position).getMonthName());
        image.setImageResource(monthList.get(position).getImg());

        return view;
    }
}
