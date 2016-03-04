package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.TeamListBuilderAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;

/**
 * Admin activity to add and remove teams from the team list
 *
 * @author Andrew Messing
 * @version %I%
 */
public class TeamListBuilder extends Activity implements View.OnClickListener {

    private TeamListBuilderAdapter teamListBuilderAdapter;
    private PitScoutDB pitScoutDB;
    private StatsDB statsDB;
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "TeamListBuilder";

    /**
     * Sets the list view's adapter and the add button.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list_builder);

        ((CustomHeader) findViewById(R.id.header)).removeHome();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        pitScoutDB = new PitScoutDB(this, eventID);
        statsDB = new StatsDB(this, eventID);

        LinearLayout layout = (LinearLayout) findViewById(R.id.team_list);
        ArrayList<Integer> teams = pitScoutDB.getTeamNumbers();

        ListView listView = (ListView) findViewById(R.id.listview);
        teamListBuilderAdapter = new TeamListBuilderAdapter(this, teams, pitScoutDB, statsDB);
        listView.setAdapter(teamListBuilderAdapter);

        Button add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);

    }

    /**
     * Implements the add button's action
     *
     * @param v The view that is clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                try {
                    EditText editText = (EditText) findViewById(R.id.newTeamNumber);
                    int teamNumber = Integer.parseInt(String.valueOf(editText.getText()));
                    pitScoutDB.addTeamNumber(teamNumber);
                    statsDB.addTeamNumber(teamNumber);
                    teamListBuilderAdapter.add(teamNumber);
                    editText.setText("");
                } catch (NumberFormatException e) {
                    Log.d(TAG, e.getMessage());
                }
                break;
        }
    }
}
