package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

/**
 * Activity to show all the teams as buttons that lead to the Team View
 */
//TODO: Merge with PitList
public class TeamList extends Activity {

    private static final String TAG = "TeamList";
    private SimpleCursorAdapter dataAdapter;

    /**
     * Sets up the list view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        CustomHeader header = (CustomHeader) findViewById(R.id.team_list_header);
        header.removeHome();

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        PitScoutDB pitScoutDB = new PitScoutDB(this, eventID);
        displayListView(pitScoutDB);
        pitScoutDB.close();
    }

    /**
     * Adds all the buttons with the team numbers and attaches their on click listener
     * @param pitScoutDB The pit scouting database helper
     */
    private void displayListView(PitScoutDB pitScoutDB) {
        Cursor cursor = pitScoutDB.getAllTeamsInfo();

        if (cursor != null && cursor.getCount() > 0) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.team_list);

            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 4, 4, 4);

            // Add buttons

            do {

                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int teamNumber = cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_TEAM_NUMBER));
                button.setText(String.valueOf(teamNumber));

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TeamList.this, TeamView.class);
                        intent.putExtra(Constants.TEAM_NUMBER, teamNumber);
                        startActivity(intent);
                    }
                });

                button.setBackgroundColor(Color.GRAY);
                button.setTextColor(Color.WHITE);


                linearLayout.addView(button);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
    }
}
