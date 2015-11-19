package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.PitScoutFragmentPagerAdapter;
import com.team3824.akmessing1.scoutingapp.adapters.TeamViewFragmentPagerAdapter;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeamView extends AppCompatActivity {

    final private String TAG = "TeamView";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TeamViewFragmentPagerAdapter adapter;

    private int teamNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt("team_number");

        TextView tv = (TextView)findViewById(R.id.team_view_team_num);
        tv.setText("Team Number: " + teamNumber);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.team_view_view_pager);
        adapter = new TeamViewFragmentPagerAdapter(getSupportFragmentManager(),teamNumber);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.team_view_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString("event_id", "");
        final PitScoutDB pitScoutDB = new PitScoutDB(this, eventId);

        final int previousTeamNumber = pitScoutDB.getPreviousTeamNumber(teamNumber);
        if(previousTeamNumber == -1)
        {
            Button previous = (Button)findViewById(R.id.team_view_previous_team);
            previous.setVisibility(View.INVISIBLE);
        }
        else
        {
            Button prev = (Button)findViewById(R.id.team_view_previous_team);
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "previous team pressed");
                    // Go to the next match
                    Intent intent = new Intent(TeamView.this, TeamView.class);
                    intent.putExtra("team_number", previousTeamNumber);
                    startActivity(intent);
                }
            });
        }

        final int nextTeamNumber = pitScoutDB.getNextTeamNumber(teamNumber);
        if(nextTeamNumber == -1)
        {
            Button next = (Button)findViewById(R.id.team_view_next_team);
            next.setVisibility(View.INVISIBLE);
        }
        else
        {
            Button next = (Button)findViewById(R.id.team_view_next_team);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "next team pressed");

                    // Go to the next match
                    Intent intent = new Intent(TeamView.this, TeamView.class);
                    intent.putExtra("team_number", nextTeamNumber);
                    startActivity(intent);

                }
            });
        }
        pitScoutDB.close();
    }
}
