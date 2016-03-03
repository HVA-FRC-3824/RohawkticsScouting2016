package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BracketResultsAdapter extends ArrayAdapter<String> {

    ArrayList<String> alliances;

    public BracketResultsAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
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
}
