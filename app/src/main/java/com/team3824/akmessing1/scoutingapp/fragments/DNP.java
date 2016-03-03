package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.DNPListViewAdapter;
import com.team3824.akmessing1.scoutingapp.adapters.DNPSpinnerAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

import java.util.ArrayList;

/**
 * Fragment that implements a do not pick list
 */
//TODO: remove teams on do not pick list / decline list from pick lists
public class DNP extends Fragment implements View.OnClickListener {

    Spinner spinner;
    DNPSpinnerAdapter dnpSpinnerAdapter;
    DNPListViewAdapter dnpListViewAdapter;
    StatsDB statsDB;

    int type;

    /**
     *
     */
    public DNP() {
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_do_not_pick, container, false);

        Context context = getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
        PitScoutDB pitScoutDB = new PitScoutDB(context, eventID);
        statsDB = new StatsDB(context,eventID);

        Cursor cursor = statsDB.getStats();

        ArrayList<Integer> listViewTeams = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            if(type == Constants.DNP && cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_DNP)) > 0)
            {
                listViewTeams.add(cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER)));
            }
            else if(type == Constants.DECLINE && cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_DECLINE)) > 0)
            {
                listViewTeams.add(cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER)));
            }
        }

        ArrayList<Integer> teams = pitScoutDB.getTeamNumbers();
        teams.removeAll(listViewTeams);

        ListView listView = (ListView) view.findViewById(R.id.dnp_list);
        dnpListViewAdapter = new DNPListViewAdapter(context, R.layout.list_item_team_list_builder, listViewTeams, this, statsDB);
        listView.setAdapter(dnpListViewAdapter);

        spinner = (Spinner) view.findViewById(R.id.team_list);
        dnpSpinnerAdapter = new DNPSpinnerAdapter(context, android.R.layout.simple_spinner_dropdown_item, teams);
        spinner.setAdapter(dnpSpinnerAdapter);

        Button button = (Button) view.findViewById(R.id.add_team);
        button.setOnClickListener(this);

        return view;
    }

    /**
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_team:
                int teamNumber = Integer.parseInt(String.valueOf(spinner.getSelectedItem()));
                dnpListViewAdapter.add(teamNumber);
                dnpSpinnerAdapter.remove(teamNumber);
                ScoutMap map = new ScoutMap();
                map.put(StatsDB.KEY_TEAM_NUMBER,teamNumber);
                switch (type)
                {
                    case Constants.DNP:
                        map.put(StatsDB.KEY_DNP,1);
                        break;
                    case Constants.DECLINE:
                        map.put(StatsDB.KEY_DECLINE,1);
                        break;
                    default:
                        assert false;
                }
                statsDB.updateStats(map);
                break;
            default:
                assert false;
        }
    }

    /**
     * @param teamNumber
     */
    public void addToSpinner(int teamNumber) {
        dnpSpinnerAdapter.add(teamNumber);
    }

    public void setDNP() {
        type = Constants.DNP;
    }

    public void setDecline() {
        type = Constants.DECLINE;
    }

    public int getType()
    {
        return type;
    }


}
