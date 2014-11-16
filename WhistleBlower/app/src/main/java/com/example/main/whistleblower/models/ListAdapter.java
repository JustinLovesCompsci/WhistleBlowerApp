package com.example.main.whistleblower.models;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.main.whistleblower.*;

/**
 * Created by daniel on 11/15/14.
 */

public class ListAdapter extends ArrayAdapter<Data> {


    public ListAdapter(Context context, List<Data> objects) {

        super(context, R.layout.item_layout, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // The data
        Data msg = getItem(position);

        convertView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_layout, parent, false);

        // Data for the headers
        TextView category = (TextView) convertView.findViewById(R.id.categoryBox);
        TextView timestamp = (TextView) convertView.findViewById(R.id.timestampBox);
        TextView type = (TextView) convertView.findViewById(R.id.typeBox);

        // Where message will be displayed
        TextView content = (TextView) convertView.findViewById(R.id.message_box);

        category.setText(msg.getCategory());
        timestamp.setText(msg.getTimeStamp());
        type.setText(msg.getType());
        content.setText(msg.getMessage());

        return convertView;
    }
}