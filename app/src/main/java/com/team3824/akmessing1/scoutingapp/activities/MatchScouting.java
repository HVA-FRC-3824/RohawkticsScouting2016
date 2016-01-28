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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_MatchScout;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MatchScouting extends AppCompatActivity {

    private static String TAG = "MatchScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_MatchScout adapter;

    private int teamNumber;
    private int matchNumber;
    private String allianceColor;

    private int prevTeamNumber = -1;
    private int nextTeamNumber = -1;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.match_scouting_toolbar);
        setSupportActionBar(toolbar);

        // Get Match Number and Team Number from the intent
        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt("team_number");
        matchNumber = extras.getInt("match_number");

        setTitle("Match Number: " + matchNumber + " Team Number: " + teamNumber);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        allianceColor = sharedPreferences.getString("alliance_color", "");

        // Set up tabs and pages for different fragments of a match
        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.match_view_pager);
        adapter = new FPA_MatchScout(getFragmentManager());
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
        eventId = sharedPreferences.getString("event_id","");
        MatchScoutDB matchScoutDB = new MatchScoutDB(this,eventId);
        Map<String, ScoutValue> map = matchScoutDB.getTeamMatchInfo(teamNumber, matchNumber);
        if(map != null) {
            adapter.setValueMap(map);
        }

        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
        // First match doesn't need a previous menu option
        if(matchNumber != 1)
        {
            Cursor prevCursor = scheduleDB.getMatch(matchNumber - 1);
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            prevTeamNumber = prevCursor.getInt(prevCursor.getColumnIndex(allianceColor.toLowerCase() + allianceNum));
            Log.d(TAG,"Prev Team: "+prevTeamNumber);
        }
        else
        {
            Log.d(TAG,"No previous team");
        }

        // Determine if the last match or not
        Cursor nextCursor = scheduleDB.getMatch(matchNumber + 1);

        //Last match doesn't need a next button
        if(nextCursor != null)
        {
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            nextTeamNumber = nextCursor.getInt(nextCursor.getColumnIndex(allianceColor.toLowerCase()+allianceNum));
            Log.d(TAG,"Next Team: "+nextTeamNumber);
        }
        else
        {
            Log.d(TAG,"No next team");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_scouting_overflow, menu);
        if(prevTeamNumber == -1) {
            menu.removeItem(R.id.match_scouting_previous);
        }
        if(nextTeamNumber == -1) {
            menu.removeItem(R.id.match_scouting_next);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.match_scouting_home:
                Intent intent = new Intent(this, StartScreen.class);
                startActivity(intent);
                break;
            case R.id.match_scouting_back:
                Intent intent2 = new Intent(this, MatchList.class);
                intent2.putExtra("nextPage","match_scouting");
                startActivity(intent2);
                break;
            case R.id.match_scouting_previous:
                previous_press();
                break;
            case R.id.match_scouting_next:
                next_pressed();
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    void previous_press()
    {
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

    void next_pressed()
    {
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
                MatchScoutDB matchScoutDB = new MatchScoutDB(MatchScouting.this, eventId);
                data.put(MatchScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                data.put(MatchScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                data.put(MatchScoutDB.KEY_ID, new ScoutValue(String.valueOf(matchNumber)+"_"+String.valueOf(teamNumber)));
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
}
