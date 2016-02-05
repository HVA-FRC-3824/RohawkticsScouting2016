package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class DatabaseManagement extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private MatchScoutDB matchScoutDB;
    private PitScoutDB pitScoutDB;
    private SuperScoutDB superScoutDB;
    private DriveTeamFeedbackDB driveTeamFeedbackDB;
    private StatsDB statsDB;
    private SyncDB syncDB;
    private ScheduleDB scheduleDB;
    private SharedPreferences sharedPreferences;

    private int db_index = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_management);


        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        matchScoutDB = new MatchScoutDB(this,eventId);
        pitScoutDB = new PitScoutDB(this,eventId);
        superScoutDB = new SuperScoutDB(this,eventId);
        driveTeamFeedbackDB = new DriveTeamFeedbackDB(this,eventId);
        syncDB = new SyncDB(this,eventId);
        statsDB = new StatsDB(this,eventId);
        scheduleDB = new ScheduleDB(this,eventId);

        ((Button)findViewById(R.id.back)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_match_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_pit_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_super_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_driveteam_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_stats_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_sync_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.reset_schedule_data)).setOnClickListener(this);
        ((Button)findViewById(R.id.remove_all_event_data)).setOnClickListener(this);
        //((Button)findViewById(R.id.query_send)).setOnClickListener(this);
/*
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.database_dropdown, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        */
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
            /*
            case R.id.query_send:

                String queryString = String.valueOf(((EditText)findViewById(R.id.query_input)).getText());
                switch (db_index)
                {
                    case Constants.MATCH_SCOUT_DB:
                        break;
                    case Constants.PIT_SCOUT_DB:
                        break;
                    case Constants.SUPER_SCOUT_DB:
                        break;
                    case Constants.DRIVE_TEAM_FEEDBACK_DB:
                        break;
                    case Constants.STATS_DB:
                        break;
                    case Constants.SYNC_DB:
                        break;
                    case Constants.SCHEDULE_DB:
                        break;
                }
                break;
            */
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        db_index = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
