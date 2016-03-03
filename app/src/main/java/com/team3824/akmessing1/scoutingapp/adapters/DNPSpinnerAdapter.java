package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class DNPSpinnerAdapter extends ArrayAdapter<Integer> {

    ArrayList<Integer> teams;

    public DNPSpinnerAdapter(Context context, int resource, ArrayList<Integer> objects) {
        super(context, resource, objects);
        teams = objects;
    }

    public void remove(Integer teamNumber)
    {
        teams.remove((Object)teamNumber);
        notifyDataSetChanged();

    }

    public void add(int teamNumber)
    {
        teams.add(teamNumber);
        Collections.sort(teams);
        notifyDataSetChanged();
    }
}
