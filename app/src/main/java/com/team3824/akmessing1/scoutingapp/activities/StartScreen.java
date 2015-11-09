package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

import com.team3824.akmessing1.scoutingapp.R;

public class StartScreen extends AppCompatActivity {

    // Buttons become visible based on the role
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type", "");
        switch (type) {
            case "Match Scout": {
                Button scheduleButton = (Button)findViewById(R.id.matchSchedule_button);
                scheduleButton.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.scoutMatch_button);
                button.setVisibility(View.VISIBLE);
                break;
            }
            case "Pit Scout": {
                Button scheduleButton = (Button)findViewById(R.id.matchSchedule_button);
                scheduleButton.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.scoutPit_button);
                button.setVisibility(View.VISIBLE);
                break;
            }
            case "Super Scout": {
                Button scheduleButton = (Button)findViewById(R.id.matchSchedule_button);
                scheduleButton.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.superScout_button);
                button.setVisibility(View.VISIBLE);
                break;
            }
            case "Drive Team": {
                Button scheduleButton = (Button)findViewById(R.id.matchSchedule_button);
                scheduleButton.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.viewTeam_button);
                button.setVisibility(View.VISIBLE);
                break;
            }
            case "Strategy": {
                Button scheduleButton = (Button)findViewById(R.id.matchSchedule_button);
                scheduleButton.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.viewTeam_button);
                button.setVisibility(View.VISIBLE);
                button = (Button) findViewById(R.id.viewPicklist_button);
                button.setVisibility(View.VISIBLE);
                break;
            }
            case "Admin": {
                Button scheduleButton = (Button)findViewById(R.id.matchSchedule_button);
                scheduleButton.setVisibility(View.VISIBLE);
                Button button = (Button) findViewById(R.id.scoutMatch_button);
                button.setVisibility(View.VISIBLE);
                button = (Button) findViewById(R.id.scoutPit_button);
                button.setVisibility(View.VISIBLE);
                button = (Button) findViewById(R.id.superScout_button);
                button.setVisibility(View.VISIBLE);
                button = (Button) findViewById(R.id.viewTeam_button);
                button.setVisibility(View.VISIBLE);
                button = (Button) findViewById(R.id.viewPicklist_button);
                button.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void match_schedule(View view)
    {
        Intent intent = new Intent(this, MatchSchedule.class);
        startActivity(intent);
    }

    public void scout_match(View view)
    {
        Intent intent = new Intent(this, MatchList.class);
        startActivity(intent);
    }

    public void scout_pit(View view)
    {
        Intent intent = new Intent(this, PitList.class);
        startActivity(intent);
    }

    public void view_team(View view)
    {
        Intent intent = new Intent(this, TeamList.class);
        startActivity(intent);
    }

    public void view_picklist(View view)
    {
        Intent intent = new Intent(this, PickList.class);
        startActivity(intent);
    }

    public void settings(View view)
    {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
