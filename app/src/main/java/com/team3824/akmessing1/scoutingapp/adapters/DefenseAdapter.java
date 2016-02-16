package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;

import java.util.ArrayList;


public class DefenseAdapter extends ArrayAdapter<String> {

    private ArrayList<String> options;
    boolean black;

    public DefenseAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        options = objects;
        black = false;
    }

    public DefenseAdapter(Context context, int resource, ArrayList<String> objects, boolean black) {
        super(context, resource, objects);
        options = objects;
        this.black = black;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_string, null);
        }
        TextView tv = (TextView)convertView.findViewById(R.id.text);
        tv.setText(options.get(position));
        if(black)
            tv.setTextColor(Color.BLACK);
        else
            tv.setTextColor(Color.WHITE);

        return convertView;
    }

    public void setOptions(ArrayList<String> objects)
    {
        options = objects;
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

}
