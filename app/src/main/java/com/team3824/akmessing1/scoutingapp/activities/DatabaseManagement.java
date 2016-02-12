package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;

import java.util.Map;
import java.util.Set;

public class DatabaseManagement extends AppCompatActivity implements View.OnClickListener
{
    private String TAG = "DatabaseManagement";

    private MatchScoutDB matchScoutDB;
    private PitScoutDB pitScoutDB;
    private SuperScoutDB superScoutDB;
    private DriveTeamFeedbackDB driveTeamFeedbackDB;
    private StatsDB statsDB;
    private SyncDB syncDB;
    private ScheduleDB scheduleDB;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_management);

        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        matchScoutDB = new MatchScoutDB(this, eventId);
        pitScoutDB = new PitScoutDB(this, eventId);
        superScoutDB = new SuperScoutDB(this, eventId);
        driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventId);
        syncDB = new SyncDB(this, eventId);
        statsDB = new StatsDB(this, eventId);
        scheduleDB = new ScheduleDB(this, eventId);

        ((Button)findViewById(R.id.back)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_match_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_pit_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_super_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_driveteam_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_stats_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_sync_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_schedule_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.remove_all_event_data)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
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
                Intent intent = new Intent(this,StartScreen.class);
                startActivity(intent);
                break;
        }
    }
}
