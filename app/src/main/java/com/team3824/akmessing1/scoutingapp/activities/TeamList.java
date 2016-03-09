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
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

/**
 * Activity to show all the teams as buttons that lead to the Team View
 *
 * @author Andrew Messing
 * @version %I%
 */
public class TeamList extends Activity {

    private static final String TAG = "TeamList";

    /**
     * Sets up the list view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        CustomHeader header = (CustomHeader) findViewById(R.id.team_list_header);
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeamList.this,HomeScreen.class);
                startActivity(intent);
            }
        });
        header.removeHome();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        Bundle extras = getIntent().getExtras();
        String nextPage = extras.getString(Constants.Intent_Extras.NEXT_PAGE);

        PitScoutDB pitScoutDB = new PitScoutDB(this, eventID);
        if(nextPage.equals(Constants.Intent_Extras.TEAM_VIEWING)) {
            displayListView(pitScoutDB, -1, nextPage);
        }
        else if(nextPage.equals(Constants.Intent_Extras.PIT_SCOUTING))
        {
            displayListView(pitScoutDB,sharedPreferences.getInt(Constants.Settings.PIT_GROUP_NUMBER,0), nextPage);
        }
        pitScoutDB.close();
    }

    /**
     * Adds all the buttons with the team numbers and attaches their on click listener
     * @param pitScoutDB The pit scouting database helper
     * @param pitGroupNumber Group number used for spliting up the pit scouting assignments
     * @param nextPage The next page to go to once a button is clicked
     */
    private void displayListView(PitScoutDB pitScoutDB, int pitGroupNumber, final String nextPage) {
        Cursor cursor = pitScoutDB.getAllTeamsInfo();

        if (cursor != null && cursor.getCount() > 0) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.team_list);

            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 4, 4, 4);

            // Sets up the different groups with different teams to pit scout
            //TODO: modify so the assignments are more even. Currently the last group will have fewer
            // teams to scout than the others if the number of teams is not divisible by 6.
            int numTeams = cursor.getCount();
            int numGroupTeams = numTeams / 6;
            int extra = numTeams % 6;
            int startPosition = 0;
            int endPosition = -1;
            if(nextPage.equals(Constants.Intent_Extras.PIT_SCOUTING)) {
                startPosition = numGroupTeams * (pitGroupNumber - 1);
                endPosition = numGroupTeams * pitGroupNumber;
                if (extra > 0) {
                    startPosition += pitGroupNumber - 1;
                    endPosition += pitGroupNumber;
                }
            }

            for (cursor.moveToPosition(startPosition); !cursor.isAfterLast(); cursor.moveToNext()) {
                int position = cursor.getPosition();
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
                        Intent intent = null;
                        if (nextPage.equals(Constants.Intent_Extras.PIT_SCOUTING)) {
                            intent = new Intent(TeamList.this, PitScouting.class);
                        } else if (nextPage.equals(Constants.Intent_Extras.TEAM_VIEWING)) {
                            intent = new Intent(TeamList.this, TeamView.class);
                        } else {
                            assert false;
                        }
                        intent.putExtra(Constants.Intent_Extras.TEAM_NUMBER, teamNumber);
                        startActivity(intent);
                    }
                });

                if(nextPage.equals(Constants.Intent_Extras.PIT_SCOUTING)) {
                    // Buttons are green if the team has been scouted and red if it hasn't
                    if (cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_COMPLETE)) != 0) {
                        button.setBackgroundColor(Color.GREEN);
                    } else {
                        button.setBackgroundColor(Color.RED);
                    }
                }

                linearLayout.addView(button);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(TeamList.this,HomeScreen.class);
        startActivity(intent);
    }
}
