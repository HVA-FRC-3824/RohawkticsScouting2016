package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Adapter for the Team List Builder list view
 */
public class TeamListBuilderAdapter extends ArrayAdapter<Integer> {

    ArrayList<Integer> teams;
    PitScoutDB pitScoutDB;
    StatsDB statsDB;

    /**
     * @param context
     * @param resource
     * @param objects
     * @param pitScoutDB The database helper for the pit scouting table
     * @param statsDB    The database helper for the stats table
     */
    public TeamListBuilderAdapter(Context context, int resource, ArrayList<Integer> objects, PitScoutDB pitScoutDB, StatsDB statsDB) {
        super(context, resource, objects);
        teams = objects;
        this.pitScoutDB = pitScoutDB;
        this.statsDB = statsDB;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_team_list_builder, null);
        }
        final int pos = position;
        ((TextView) convertView.findViewById(R.id.team_number)).setText(String.valueOf(teams.get(position)));
        ((Button) convertView.findViewById(R.id.remove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pitScoutDB.removeTeamNumber(teams.get(pos));
                statsDB.removeTeamNumber(teams.get(pos));
                teams.remove(pos);
                TeamListBuilderAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }

    /**
     * @param newTeamNumber The new team number to add
     */
    public void add(int newTeamNumber) {
        teams.add(newTeamNumber);
        Collections.sort(teams);
        notifyDataSetChanged();
    }
}
