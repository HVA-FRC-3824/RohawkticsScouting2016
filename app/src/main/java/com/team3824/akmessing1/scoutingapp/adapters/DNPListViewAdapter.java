package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.fragments.PickLists.DNP;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Andrew Messing
 * @version 1
 *
 * List View Adapter for the do not pick list and the decline list.
 */

public class DNPListViewAdapter extends ArrayAdapter<Integer> {

    private final String TAG = "DNPListViewAdapter";

    private ArrayList<Integer> teams;
    private DNP doNotPick;
    private StatsDB statsDB;


    public DNPListViewAdapter(Context context, ArrayList<Integer> objects, DNP dnp, StatsDB sdb) {
        super(context, R.layout.list_item_team_list_builder, objects);
        teams = objects;
        doNotPick = dnp;
        statsDB = sdb;
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
        convertView.findViewById(R.id.remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNotPick.addToSpinner(teams.get(pos));
                ScoutMap map = new ScoutMap();
                map.put(StatsDB.KEY_TEAM_NUMBER, teams.get(pos));
                switch (doNotPick.getType()) {
                    case Constants.Pick_List.DNP:
                        map.put(StatsDB.KEY_DNP, 0);
                        break;
                    case Constants.Pick_List.DECLINE:
                        map.put(StatsDB.KEY_DECLINE, 0);
                        break;
                    default:
                        assert false;
                }
                statsDB.updateStats(map);
                teams.remove(pos);
                DNPListViewAdapter.this.notifyDataSetChanged();
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
