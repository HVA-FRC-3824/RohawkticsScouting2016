package com.team3824.akmessing1.scoutingapp.fragments.PickLists;

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
import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Andrew Messing
 * @version 1
 *
 * Fragment that implements a do not pick list
 */
//TODO: remove teams on do not pick list / decline list from pick lists
public class DNP extends Fragment implements View.OnClickListener {

    private final String TAG = "DNP";

    private Spinner spinner;
    private DNPSpinnerAdapter dnpSpinnerAdapter;
    private DNPListViewAdapter dnpListViewAdapter;
    private StatsDB statsDB;

    public DNP() {
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
        final String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        PitScoutDB pitScoutDB = new PitScoutDB(context, eventID);
        statsDB = new StatsDB(context,eventID);

        Cursor cursor = statsDB.getStats();

        ArrayList<Integer> listViewTeams = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            if(cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_DNP)) > 0)
            {
                listViewTeams.add(cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER)));
            }

        }

        ArrayList<Integer> teams = pitScoutDB.getTeamNumbers();
        teams.removeAll(listViewTeams);

        ListView listView = (ListView) view.findViewById(R.id.dnp_list);
        dnpListViewAdapter = new DNPListViewAdapter(context, listViewTeams, this, statsDB);
        listView.setAdapter(dnpListViewAdapter);

        spinner = (Spinner) view.findViewById(R.id.team_list);
        dnpSpinnerAdapter = new DNPSpinnerAdapter(context, teams);
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
                map.put(StatsDB.KEY_DNP,1);
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

    public ArrayList<Integer> getDNP() {
        if(dnpListViewAdapter == null)
            return new ArrayList<>();
        return dnpListViewAdapter.getArrayList();
    }
}
