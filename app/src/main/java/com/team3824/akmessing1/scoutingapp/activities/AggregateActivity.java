package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.team3824.akmessing1.scoutingapp.AggregateUtil;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

public class AggregateActivity extends AppCompatActivity {
    private static String TAG = "AggregateActivity";

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    StatsDB statsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate);
        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString("event_id", "");

        matchScoutDB = new MatchScoutDB(this,eventID);
        pitScoutDB = new PitScoutDB(this,eventID);
        superScoutDB = new SuperScoutDB(this, eventID);
        statsDB = new StatsDB(this,eventID);

        Button update = (Button)findViewById(R.id.update_aggregate_button);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AggregateUtil.aggregate(matchScoutDB, pitScoutDB, superScoutDB, statsDB, AggregateActivity.this, eventID, true);
            }
        });

        Button reset = (Button)findViewById(R.id.update_aggregate_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AggregateUtil.aggregate(matchScoutDB, pitScoutDB, superScoutDB, statsDB, AggregateActivity.this, eventID, false);
            }
        });

    }
}
