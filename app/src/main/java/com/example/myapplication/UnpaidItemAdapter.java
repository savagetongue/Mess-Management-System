package com.example.myapplication;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UnpaidItemAdapter extends ArrayAdapter<UnpaidStudent> {
    private final Context context;
    private final List<UnpaidStudent> unpaidList;

    public UnpaidItemAdapter(Context context, List<UnpaidStudent> unpaidList) {
        super(context, R.layout.each_unpaid_item, unpaidList);
        this.context = context;
        this.unpaidList = unpaidList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.each_unpaid_item, parent, false);

        CircleImageView image = view.findViewById(R.id.profile_image);
        TextView name = view.findViewById(R.id.tVName);

        // Assuming you have an image for each unpaid student, you can set it here
        // image.setImageResource(R.drawable.student_image);

        name.setText(unpaidList.get(position).getStudentName());

        return view;
    }
}
