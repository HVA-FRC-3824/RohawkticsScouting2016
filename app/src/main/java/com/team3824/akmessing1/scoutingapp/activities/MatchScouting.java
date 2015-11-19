package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.MatchScoutFragmentPagerAdapter;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MatchScouting extends AppCompatActivity {

    private static String TAG = "MatchScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MatchScoutFragmentPagerAdapter adapter;
    //private Toolbar toolbar;

    private int teamNumber;
    private int matchNumber;
    private String allianceColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);

        // Get Match Number and Team Number from the intent
        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt("team_number");
        matchNumber = extras.getInt("match_number");

        // Write Match Number and Team Number to the screen
        TextView tv = (TextView)findViewById(R.id.team_num);
        tv.setText("Team Number: "+teamNumber);
        tv = (TextView)findViewById(R.id.match_num);
        tv.setText("Match Number: " + matchNumber);

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        allianceColor = sharedPreferences.getString("alliance_color", "");

        // Set up tabs and pages for different fragments of a match
        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.match_view_pager);
        adapter = new MatchScoutFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.match_tab_layout);
        if(allianceColor.equals("Blue"))
        {
            tabLayout.setBackgroundColor(Color.BLUE);
        }
        else
        {
            tabLayout.setBackgroundColor(Color.RED);
        }
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        // Restore any values from the database if this team/match combo has been scouted before
        // (basically if updating)
        String eventId = sharedPreferences.getString("event_id","");
        MatchScoutDB matchScoutDB = new MatchScoutDB(this,eventId);
        Map<String, ScoutValue> map = matchScoutDB.getTeamMatchInfo(teamNumber, matchNumber);
        if(map != null) {
            adapter.setValueMap(map);
        }

        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
        // First match doesn't need a previous button
        if(matchNumber == 1)
        {
            Button previous = (Button)findViewById(R.id.previous_match);
            previous.setVisibility(View.INVISIBLE);
        }
        // TODO: Set up previous button to ask about saving like the next button does
        // Setup dialog box for going to the previous match
        else
        {
            Cursor prevCursor = scheduleDB.getMatch(matchNumber-1);
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            Log.d(TAG,allianceColor.toLowerCase() + allianceNum+"->"+prevCursor.getColumnIndex(allianceColor.toLowerCase() + allianceNum));
            final int prevTeamNumber = prevCursor.getInt(prevCursor.getColumnIndex(allianceColor.toLowerCase() + allianceNum));
            Button prev = (Button)findViewById(R.id.previous_match);
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"previous match pressed");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
                    builder.setTitle("Save match data?");

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
                            String eventId = sharedPreferences.getString("event_id","");
                            MatchScoutDB matchScoutDB = new MatchScoutDB(MatchScouting.this, eventId);
                            data.put(MatchScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                            data.put(MatchScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                            data.put(MatchScoutDB.KEY_ID,new ScoutValue(String.valueOf(matchNumber)+"_"+String.valueOf(teamNumber)));
                            // Store values to the database
                            matchScoutDB.updateMatch(data);

                            // Go to the next match
                            Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                            intent.putExtra("team_number",prevTeamNumber);
                            intent.putExtra("match_number",matchNumber-1);
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
                            Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                            intent.putExtra("team_number", prevTeamNumber);
                            intent.putExtra("match_number", matchNumber - 1);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }

        // Determine if the last match or not
        Cursor nextCursor = scheduleDB.getMatch(matchNumber + 1);

        //Last match doesn't need a next button
        if(nextCursor == null)
        {
            Button next = (Button)findViewById(R.id.next_match);
            next.setVisibility(View.INVISIBLE);
        }
        // Otherwise setup dialog box for moving to the next match that checks about saving
        else
        {
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            final int nextTeamNumber = nextCursor.getInt(nextCursor.getColumnIndex(allianceColor.toLowerCase()+allianceNum));
            Button next = (Button)findViewById(R.id.next_match);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "next match pressed");

                    AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
                    builder.setTitle("Save match data?");

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
                            String eventId = sharedPreferences.getString("event_id","");
                            MatchScoutDB matchScoutDB = new MatchScoutDB(MatchScouting.this, eventId);
                            data.put(MatchScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                            data.put(MatchScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                            // Store values to the database
                            matchScoutDB.updateMatch(data);

                            // Go to the next match
                            Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                            intent.putExtra("team_number",nextTeamNumber);
                            intent.putExtra("match_number",matchNumber+1);
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
                            Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                            intent.putExtra("team_number", nextTeamNumber);
                            intent.putExtra("match_number", matchNumber + 1);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}
