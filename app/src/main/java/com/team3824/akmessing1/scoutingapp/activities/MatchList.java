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
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

/**
 * Activity which displays each match as a button to lead into Match Scouting, Super Scouting, or
 * Match View. If preceding match scouting the corresponding team based on the selected alliance color
 * and number is displayed too.
 */
public class MatchList extends Activity {

    private static final String TAG = "MatchList";
    private SimpleCursorAdapter dataAdapter;

    /**
     * Set up the header, get the schedule database, and generate the list of match buttons
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        Bundle extras = getIntent().getExtras();
        String nextPage = extras.getString(Constants.NEXT_PAGE);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        CustomHeader header = (CustomHeader) findViewById(R.id.match_list_header);
        header.removeHome();
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchList.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        final ScheduleDB scheduleDB = new ScheduleDB(this, eventID);
        displayListView(scheduleDB, sharedPreferences, nextPage);
        scheduleDB.close();
    }

    /**
     * Setup list view with the scheduled matches
     *
     * @param scheduleDB The database helper for the schedule database
     * @param sharedPreferences Prefences saved in Settings
     * @param nextPage Whether the next page should be Match Scouting, Super Scouting, or Match View.
     */
    private void displayListView(ScheduleDB scheduleDB, SharedPreferences sharedPreferences, final String nextPage) {
        Cursor cursor = scheduleDB.getSchedule();
        if (cursor != null && cursor.getCount() > 0) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.match_list);
            int alliance_number = -1;
            String alliance_color = "";
            if (nextPage.equals(Constants.MATCH_SCOUTING)) {
                alliance_number = sharedPreferences.getInt(Constants.ALLIANCE_NUMBER, 0);
                alliance_color = sharedPreferences.getString(Constants.ALLIANCE_COLOR, "");
            }
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4, 4, 4, 4);

            // Add buttons
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int matchNumber = cursor.getInt(0);
                int tempTeamNumber = -1; // fixes issue with final and possible noninitialization
                if (nextPage.equals(Constants.MATCH_SCOUTING)) {
                    tempTeamNumber = cursor.getInt(cursor.getColumnIndex(alliance_color.toLowerCase() + alliance_number));
                    button.setText(String.format("Match %d : %d", matchNumber, tempTeamNumber));
                } else {
                    button.setText(String.format("Match %d", matchNumber));
                }
                final int teamNumber = tempTeamNumber; // fixes issue with final and possible noninitialization
                switch (alliance_color) {
                    case Constants.BLUE:
                        button.setBackgroundColor(Color.BLUE);
                        break;
                    case Constants.RED:
                        button.setBackgroundColor(Color.RED);
                        break;
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;

                        if (nextPage.equals(Constants.MATCH_SCOUTING)) {
                            intent = new Intent(MatchList.this, MatchScouting.class);
                            intent.putExtra(Constants.TEAM_NUMBER, teamNumber);
                        } else if (nextPage.equals(Constants.SUPER_SCOUTING)) {
                            intent = new Intent(MatchList.this, SuperScouting.class);
                        } else if (nextPage.equals(Constants.MATCH_VIEWING)) {
                            intent = new Intent(MatchList.this, MatchView.class);
                        }
                        intent.putExtra(Constants.MATCH_NUMBER, matchNumber);
                        startActivity(intent);
                    }
                });
                linearLayout.addView(button);
            }
        }
    }
}
