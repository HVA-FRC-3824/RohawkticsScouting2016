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

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_SuperScout;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperScouting extends AppCompatActivity {

    final private String TAG = "SuperScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_SuperScout adapter;

    private int matchNumber;

    private String eventId;

    private ArrayList<Integer> arrayList;

    private boolean nextMatch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_scouting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.super_scouting_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        matchNumber = extras.getInt("match_number");
        setTitle("Match Number: " + matchNumber);


        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString("event_id", "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);

        Cursor cursor = scheduleDB.getMatch(matchNumber);
        arrayList = new ArrayList<Integer>();
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)));

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.super_view_pager);
        adapter = new FPA_SuperScout(getFragmentManager(), arrayList);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.super_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        // Get data from super scout db
        SuperScoutDB superScoutDB = new SuperScoutDB(this,eventId);
        Map<String, ScoutValue> map = superScoutDB.getMatchInfo(matchNumber);
        if(map != null) {
            adapter.setValueMap(map);
        }

        if(scheduleDB.getNumMatches() != matchNumber+1)
            nextMatch = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.super_scouting_overflow, menu);
        if(matchNumber == 1) {
            menu.removeItem(R.id.super_scouting_previous);
        }
        if(!nextMatch) {
            menu.removeItem(R.id.super_scouting_next);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.super_scouting_home:
                home_pres();
                break;
            case R.id.super_scouting_back:
                back_press();
                break;
            case R.id.super_scouting_previous:
                previous_press();
                break;
            case R.id.super_scouting_next:
                next_press();
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void home_pres()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
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
                SuperScoutDB superScoutDB = new SuperScoutDB(SuperScouting.this, eventId);
                data.put(SuperScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                data.put(SuperScoutDB.KEY_BLUE1, new ScoutValue(arrayList.get(0)));
                data.put(SuperScoutDB.KEY_BLUE2, new ScoutValue(arrayList.get(1)));
                data.put(SuperScoutDB.KEY_BLUE3, new ScoutValue(arrayList.get(2)));
                data.put(SuperScoutDB.KEY_RED1, new ScoutValue(arrayList.get(3)));
                data.put(SuperScoutDB.KEY_RED2, new ScoutValue(arrayList.get(4)));
                data.put(SuperScoutDB.KEY_RED3, new ScoutValue(arrayList.get(5)));
                // Store values to the database
                superScoutDB.updateMatch(data);

                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, StartScreen.class);
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
                Intent intent = new Intent(SuperScouting.this, StartScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void back_press()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
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
                SuperScoutDB superScoutDB = new SuperScoutDB(SuperScouting.this, eventId);
                data.put(SuperScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                data.put(SuperScoutDB.KEY_BLUE1, new ScoutValue(arrayList.get(0)));
                data.put(SuperScoutDB.KEY_BLUE2, new ScoutValue(arrayList.get(1)));
                data.put(SuperScoutDB.KEY_BLUE3, new ScoutValue(arrayList.get(2)));
                data.put(SuperScoutDB.KEY_RED1, new ScoutValue(arrayList.get(3)));
                data.put(SuperScoutDB.KEY_RED2, new ScoutValue(arrayList.get(4)));
                data.put(SuperScoutDB.KEY_RED3, new ScoutValue(arrayList.get(5)));
                // Store values to the database
                superScoutDB.updateMatch(data);

                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, MatchList.class);
                intent.putExtra("nextPage","super_scouting");
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
                Intent intent = new Intent(SuperScouting.this, MatchList.class);
                intent.putExtra("nextPage","super_scouting");
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void previous_press()
    {
        Log.d(TAG,"previous match pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
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
                SuperScoutDB superScoutDB = new SuperScoutDB(SuperScouting.this, eventId);
                data.put(SuperScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                data.put(SuperScoutDB.KEY_BLUE1, new ScoutValue(arrayList.get(0)));
                data.put(SuperScoutDB.KEY_BLUE2, new ScoutValue(arrayList.get(1)));
                data.put(SuperScoutDB.KEY_BLUE3, new ScoutValue(arrayList.get(2)));
                data.put(SuperScoutDB.KEY_RED1, new ScoutValue(arrayList.get(3)));
                data.put(SuperScoutDB.KEY_RED2, new ScoutValue(arrayList.get(4)));
                data.put(SuperScoutDB.KEY_RED3, new ScoutValue(arrayList.get(5)));
                // Store values to the database
                superScoutDB.updateMatch(data);

                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
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
                Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
                intent.putExtra("match_number", matchNumber - 1);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void next_press()
    {
        Log.d(TAG, "next match pressed");

        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
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
                SuperScoutDB superScoutDB = new SuperScoutDB(SuperScouting.this, eventId);
                data.put(SuperScoutDB.KEY_MATCH_NUMBER, new ScoutValue(matchNumber));
                data.put(SuperScoutDB.KEY_BLUE1, new ScoutValue(arrayList.get(0)));
                data.put(SuperScoutDB.KEY_BLUE2, new ScoutValue(arrayList.get(1)));
                data.put(SuperScoutDB.KEY_BLUE3, new ScoutValue(arrayList.get(2)));
                data.put(SuperScoutDB.KEY_RED1, new ScoutValue(arrayList.get(3)));
                data.put(SuperScoutDB.KEY_RED2, new ScoutValue(arrayList.get(4)));
                data.put(SuperScoutDB.KEY_RED3, new ScoutValue(arrayList.get(5)));
                // Store values to the database
                superScoutDB.updateMatch(data);

                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
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
                Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
                intent.putExtra("match_number", matchNumber + 1);
                startActivity(intent);
            }
        });
        builder.show();
    }


}
