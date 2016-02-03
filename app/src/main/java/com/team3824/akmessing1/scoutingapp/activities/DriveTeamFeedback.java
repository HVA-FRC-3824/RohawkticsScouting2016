package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;

/**
 * Created by Gretel on 2/2/16.
 */
public class DriveTeamFeedback extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveteam_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.driveteam_feedback_toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        int matchnumber = extras.getInt("match_number");
        setTitle("Match Number: " + matchnumber);
        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
        Cursor cursor = scheduleDB.getMatch(matchnumber);

        int team1 = -1, team2 = -1;

        if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2));
            team2 =cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2))==3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1));
            team2 =cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3));}

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3))==3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1));
            team2 =cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1))==3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2));
            team2 =cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2))==3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1));
            team2 =cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3))==3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1));
            team2 =cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2));
        }

        TextView textview = (TextView) findViewById(R.id.Team_Number_1);
        textview.setText(String.valueOf(team1));
        textview = (TextView) findViewById(R.id.Team_Number_2);
        textview.setText(String.valueOf(team2));

    }

}
