package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class DNPSpinnerAdapter extends ArrayAdapter<Integer> {

    private ArrayList<Integer> teams;

    public DNPSpinnerAdapter(Context context, ArrayList<Integer> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        teams = objects;
    }

    public void remove(Integer teamNumber)
    {
        teams.remove(teamNumber);
        notifyDataSetChanged();

    }

    public void add(int teamNumber)
    {
        teams.add(teamNumber);
        Collections.sort(teams);
        notifyDataSetChanged();
    }
}
