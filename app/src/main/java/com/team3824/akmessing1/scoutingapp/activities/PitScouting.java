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
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PitScouting extends AppCompatActivity {

    final private String TAG = "PitScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PitScoutFragmentPagerAdapter adapter;

    private int teamNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt("team_number");

        TextView tv = (TextView)findViewById(R.id.pit_team_num);
        tv.setText("Team Number: "+teamNumber);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.pit_view_pager);
        adapter = new PitScoutFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.pit_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString("event_id", "");
        final PitScoutDB pitScoutDB = new PitScoutDB(this, eventId);
        Map<String, ScoutValue> map = pitScoutDB.getTeamMap(teamNumber);
        if(map.get(PitScoutDB.KEY_COMPLETE).getInt() != 0)
        {
            adapter.setValueMap(map);
        }

        final int previousTeamNumber = pitScoutDB.getPreviousTeamNumber(teamNumber);
        if(previousTeamNumber == -1)
        {
            Button previous = (Button)findViewById(R.id.previous_team);
            previous.setVisibility(View.INVISIBLE);
        }
        else
        {
            Button prev = (Button)findViewById(R.id.previous_team);
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "previous team pressed");
                    AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                    builder.setTitle("Save pit data?");

                    // Save option
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Collect values from all the custom elements
                            List<ScoutFragment> fragmentList = adapter.getAllFragments();
                            Map<String, ScoutValue> data = new HashMap<>();
                            for (ScoutFragment fragment : fragmentList) {
                                fragment.writeContentsToMap(data);
                            }

                            Log.d(TAG,"Saving values");
                            // Add the team and match numbers
                            data.put(PitScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                            // Store values to the database
                            pitScoutDB.updatePit(data);

                            // Go to the next match
                            Intent intent = new Intent(PitScouting.this, PitScouting.class);
                            intent.putExtra("team_number",previousTeamNumber);
                            startActivity(intent);
                        }
                    });

                    // Cancel Option
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dialogbox goes away
                        }
                    });

                    // Continue w/o Saving Option
                    builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Go to the next match
                            Intent intent = new Intent(PitScouting.this, PitScouting.class);
                            intent.putExtra("team_number", previousTeamNumber);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }

        final int nextTeamNumber = pitScoutDB.getNextTeamNumber(teamNumber);
        if(nextTeamNumber == -1)
        {
            Button next = (Button)findViewById(R.id.next_team);
            next.setVisibility(View.INVISIBLE);
        }
        else
        {
            Button next = (Button)findViewById(R.id.next_team);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "next team pressed");
                    AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
                    builder.setTitle("Save pit data?");

                    // Save option
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Collect values from all the custom elements
                            List<ScoutFragment> fragmentList = adapter.getAllFragments();
                            Map<String, ScoutValue> data = new HashMap<>();
                            for (ScoutFragment fragment : fragmentList) {
                                fragment.writeContentsToMap(data);
                            }

                            Log.d(TAG,"Saving values");
                            // Add the team and match numbers
                            data.put(PitScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                            // Store values to the database
                            pitScoutDB.updatePit(data);

                            // Go to the next match
                            Intent intent = new Intent(PitScouting.this, PitScouting.class);
                            intent.putExtra("team_number",nextTeamNumber);
                            startActivity(intent);
                        }
                    });

                    // Cancel Option
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dialog box goes away
                        }
                    });

                    // Continue w/o Saving Option
                    builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Go to the next match
                            Intent intent = new Intent(PitScouting.this, PitScouting.class);
                            intent.putExtra("team_number", nextTeamNumber);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }

    }

}
