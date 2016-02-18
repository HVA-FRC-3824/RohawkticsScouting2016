package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_MatchScout;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

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
        teamNumber = extras.getInt(Constants.TEAM_NUMBER);
        matchNumber = extras.getInt(Constants.MATCH_NUMBER);

        setTitle("Match Number: " + matchNumber + " Team Number: " + teamNumber);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        allianceColor = sharedPreferences.getString(Constants.ALLIANCE_COLOR, "");

        // Set up tabs and pages for different fragments of a match
        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.match_view_pager);
        adapter = new FPA_MatchScout(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout)findViewById(R.id.match_tab_layout);
        if(allianceColor.equals(Constants.BLUE))
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
        eventId = sharedPreferences.getString(Constants.EVENT_ID,"");
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
            int allianceNum = sharedPreferences.getInt(Constants.ALLIANCE_NUMBER, 0);
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
        if(nextCursor != null && nextCursor.getCount() > 0)
        {
            int allianceNum = sharedPreferences.getInt(Constants.ALLIANCE_NUMBER, 0);
            nextTeamNumber = nextCursor.getInt(nextCursor.getColumnIndex(allianceColor.toLowerCase()+allianceNum));
            Log.d(TAG,"Next Team: "+nextTeamNumber);
        }
        else
        {
            Log.d(TAG,"No next team");
        }

        Utilities.setupUI(this, findViewById(android.R.id.content));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_overflow, menu);
        if(prevTeamNumber == -1) {
            menu.removeItem(R.id.previous);
        }
        if(nextTeamNumber == -1) {
            menu.removeItem(R.id.next);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                home_press();
                break;
            case R.id.back:
                back_press();
                break;
            case R.id.previous:
                previous_press();
                break;
            case R.id.next:
                next_press();
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private class SaveTask extends AsyncTask<Map<String, ScoutValue>, Void, Void> {

        @Override
        protected Void doInBackground(Map<String, ScoutValue>... maps) {
            Map<String, ScoutValue> data = maps[0];

            MatchScoutDB matchScoutDB = new MatchScoutDB(MatchScouting.this, eventId);
            data.put(MatchScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
            data.put(MatchScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
            data.put(MatchScoutDB.KEY_ID, new ScoutValue(String.format("%d_%d", matchNumber, teamNumber)));
            // Store values to the database
            matchScoutDB.updateMatch(data);
            return null;
        }
    }

    private void home_press()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                Map<String, ScoutValue> data = new HashMap<>();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if(error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    // Go to the next match
                    Intent intent = new Intent(MatchScouting.this, StartScreen.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();

                }
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
                Intent intent = new Intent(MatchScouting.this, StartScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void back_press()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                Map<String, ScoutValue> data = new HashMap<>();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if(error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    // Go to the next match
                    Intent intent = new Intent(MatchScouting.this, MatchList.class);
                    intent.putExtra(Constants.NEXT_PAGE, Constants.MATCH_SCOUTING);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
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
                Intent intent = new Intent(MatchScouting.this, MatchList.class);
                intent.putExtra(Constants.NEXT_PAGE,Constants.MATCH_SCOUTING);
                startActivity(intent);
            }
        });
        builder.show();
    }


    private void previous_press()
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
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if(error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    // Go to the next match
                    Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                    intent.putExtra(Constants.TEAM_NUMBER, prevTeamNumber);
                    intent.putExtra(Constants.MATCH_NUMBER, matchNumber - 1);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
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
                intent.putExtra(Constants.TEAM_NUMBER, prevTeamNumber);
                intent.putExtra(Constants.MATCH_NUMBER, matchNumber - 1);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void next_press()
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
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if(error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    // Go to the next match
                    Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                    intent.putExtra(Constants.TEAM_NUMBER, nextTeamNumber);
                    intent.putExtra(Constants.MATCH_NUMBER, matchNumber + 1);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
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
                intent.putExtra(Constants.TEAM_NUMBER, nextTeamNumber);
                intent.putExtra(Constants.MATCH_NUMBER, matchNumber + 1);
                startActivity(intent);
            }
        });
        builder.show();
    }
}
