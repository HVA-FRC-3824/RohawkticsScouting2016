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

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.adapters.SuperScoutFragmentPagerAdapter;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperScouting extends AppCompatActivity {

    final private String TAG = "SuperScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SuperScoutFragmentPagerAdapter adapter;

    private int matchNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_scouting);

        CustomHeader header = (CustomHeader)findViewById(R.id.super_header);
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuperScouting.this, MatchList.class);
                intent.putExtra("nextPage","super_scouting");
                startActivity(intent);
            }
        });

        Bundle extras = getIntent().getExtras();
        matchNumber = extras.getInt("match_number");

        TextView tv = (TextView)findViewById(R.id.match_num);
        tv.setText("Match Number: " + matchNumber);

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString("event_id", "");
        final ScheduleDB scheduleDB = new ScheduleDB(this, eventId);

        Cursor cursor = scheduleDB.getMatch(matchNumber);
        final ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)));

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.super_view_pager);
        adapter = new SuperScoutFragmentPagerAdapter(getFragmentManager(), arrayList);
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

        // First match doesn't need a previous button
        Button previous = (Button)findViewById(R.id.previous_match);
        if(matchNumber == 1)
        {
            previous.setVisibility(View.INVISIBLE);
        }
        // Setup dialog box for going to the previous match
        else
        {
            Cursor prevCursor = scheduleDB.getMatch(matchNumber - 1);
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            String eventId = sharedPreferences.getString("event_id","");
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
            });
        }

        // Determine if the last match or not
        Cursor nextCursor = scheduleDB.getMatch(matchNumber + 1);

        //Last match doesn't need a next button
        Button next = (Button)findViewById(R.id.next_match);
        if(nextCursor == null)
        {
            next.setVisibility(View.INVISIBLE);
        }
        // Otherwise setup dialog box for moving to the next match that checks about saving
        else
        {
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            String eventId = sharedPreferences.getString("event_id","");
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
            });
        }
    }

}
