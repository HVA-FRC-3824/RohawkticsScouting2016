package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
 *  Activity that has a buttons for each team that leads to the pit scouting for that team
 */
public class PitList extends Activity {

    private static final String TAG = "PitList";
    private SimpleCursorAdapter dataAdapter;

    /**
     * Sets up the header and list view of the team number buttons
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_list);

        CustomHeader header = (CustomHeader) findViewById(R.id.pit_list_header);
        header.removeHome();
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PitList.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
        int pitGroupNumber = sharedPreferences.getInt(Constants.PIT_GROUP_NUMBER, 1);

        PitScoutDB pitScoutDB = new PitScoutDB(this, eventID);
        displayListView(pitScoutDB, pitGroupNumber);
        pitScoutDB.close();
    }

    /**
     * Setup list view with the team number buttons
     * @param pitScoutDB Pit Scouting Database Helper
     * @param pitGroupNumber Group number used for spliting up the pit scouting assignments
     */
    private void displayListView(PitScoutDB pitScoutDB, int pitGroupNumber) {
        Cursor cursor = pitScoutDB.getAllTeams();

        if (cursor != null && cursor.getCount() > 0) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pit_list);

            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 4, 4, 4);

            // Sets up the different groups with different teams to pit scout
            //TODO: modify so the assignments are more even. Currently the last group will have fewer
            // teams to scout than the others if the number of teams is not divisible by 6.
            int numTeams = cursor.getCount();
            int numGroupTeams = numTeams / 6;
            int extra = numTeams % 6;
            int startPosition = numGroupTeams * (pitGroupNumber - 1);
            int endPosition = numGroupTeams * pitGroupNumber;
            if (extra > 0) {
                startPosition += pitGroupNumber - 1;
                endPosition += pitGroupNumber;
            }

            for (cursor.moveToPosition(startPosition); !cursor.isAfterLast(); cursor.moveToNext()) {
                int position = cursor.getPosition();
                Log.d(TAG, String.valueOf(position));
                if (position == endPosition) {
                    break;
                }

                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int teamNumber = cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_TEAM_NUMBER));
                button.setText(String.valueOf(teamNumber));

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PitList.this, PitScouting.class);
                        intent.putExtra(Constants.TEAM_NUMBER, teamNumber);
                        startActivity(intent);
                    }
                });

                // Buttons are green if the team has been scouted and red if it hasn't
                if (cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_COMPLETE)) != 0) {
                    button.setBackgroundColor(Color.GREEN);
                } else {
                    button.setBackgroundColor(Color.RED);
                }

                linearLayout.addView(button);
            }
        }
    }

}
