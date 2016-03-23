package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BracketResultsAdapter extends ArrayAdapter<String> {

    private ArrayList<String> alliances;

    public BracketResultsAdapter(Context context, ArrayList<String> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        alliances = objects;
    }

    public void remove(String s)
    {
        alliances.remove(s);
        notifyDataSetChanged();
    }

    public void add(String s)
    {
        alliances.add(s);
        notifyDataSetChanged();
    }

    public boolean contains(String s)
    {
        return alliances.contains(s);
    }
}
