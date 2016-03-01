package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

/**
 * Activity for resetting the database tables for the current event or removing all of them from the
 * database
 */
public class DatabaseManagement extends Activity implements View.OnClickListener {
    private String TAG = "DatabaseManagement";

    private MatchScoutDB matchScoutDB;
    private PitScoutDB pitScoutDB;
    private SuperScoutDB superScoutDB;
    private DriveTeamFeedbackDB driveTeamFeedbackDB;
    private StatsDB statsDB;
    private SyncDB syncDB;
    private ScheduleDB scheduleDB;
    private SharedPreferences sharedPreferences;

    /**
     * Creates the Database Management Activity, helper objects for each of the database tables for
     * the current event, and sets the click listener for each of the reset/remove buttons.
     *
     * @param savedInstanceState The Bundle containing anything saved from the last time this activity
     *                           was used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_management);

        sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        matchScoutDB = new MatchScoutDB(this, eventId);
        pitScoutDB = new PitScoutDB(this, eventId);
        superScoutDB = new SuperScoutDB(this, eventId);
        driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventId);
        syncDB = new SyncDB(this, eventId);
        statsDB = new StatsDB(this, eventId);
        scheduleDB = new ScheduleDB(this, eventId);

        ((Button) findViewById(R.id.back)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_match_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_pit_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_super_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_driveteam_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_stats_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_sync_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.reset_schedule_data)).setOnClickListener(this);
        ((Button) findViewById(R.id.remove_all_event_data)).setOnClickListener(this);
    }

    /**
     * Provides the action that happens when any of the buttons is clicked
     *
     * @param v The view that is clicked (in this case one of the buttons on the page)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.reset_match_data:
                matchScoutDB.reset();
                break;
            case R.id.reset_pit_data:
                pitScoutDB.reset();
                break;
            case R.id.reset_super_data:
                superScoutDB.reset();
                break;
            case R.id.reset_driveteam_data:
                driveTeamFeedbackDB.reset();
                break;
            case R.id.reset_stats_data:
                statsDB.reset();
                break;
            case R.id.reset_schedule_data:
                scheduleDB.reset();
                break;
            case R.id.remove_all_event_data:
                matchScoutDB.remove();
                pitScoutDB.remove();
                superScoutDB.remove();
                driveTeamFeedbackDB.remove();
                statsDB.remove();
                scheduleDB.remove();
                syncDB.remove();
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.remove(Constants.EVENT_ID);
                prefEditor.remove(Constants.ALLIANCE_NUMBER);
                prefEditor.remove(Constants.ALLIANCE_COLOR);
                prefEditor.remove(Constants.USER_TYPE);
                prefEditor.commit();
                Intent intent = new Intent(this, HomeScreen.class);
                startActivity(intent);
                break;
        }
    }
}
