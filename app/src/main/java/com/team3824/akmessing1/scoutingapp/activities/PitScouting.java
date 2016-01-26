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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.PitScoutFragmentPagerAdapter;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PitScouting extends AppCompatActivity {

    final private String TAG = "PitScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PitScoutFragmentPagerAdapter adapter;

    private int teamNumber;

    private String eventId;
    private int prevTeamNumber = -1;
    private int nextTeamNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.pit_scouting_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt("team_number");
        setTitle("Team Number: " + teamNumber);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.pit_view_pager);
        adapter = new PitScoutFragmentPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.pit_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString("event_id", "");
        PitScoutDB pitScoutDB = new PitScoutDB(this, eventId);
        Map<String, ScoutValue> map = pitScoutDB.getTeamMap(teamNumber);
        if(map.get(PitScoutDB.KEY_COMPLETE).getInt() > 0)
        {
            adapter.setValueMap(map);
        }

        prevTeamNumber = pitScoutDB.getPreviousTeamNumber(teamNumber);
        nextTeamNumber = pitScoutDB.getNextTeamNumber(teamNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pit_scouting_overflow, menu);
        if(prevTeamNumber == -1) {
            menu.removeItem(R.id.pit_scouting_previous);
        }
        if(nextTeamNumber == -1) {
            menu.removeItem(R.id.pit_scouting_next);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pit_scouting_home:
                Intent intent = new Intent(this, StartScreen.class);
                startActivity(intent);
                break;
            case R.id.pit_scouting_back:
                Intent intent2 = new Intent(this, PitList.class);
                startActivity(intent2);
                break;
            case R.id.pit_scouting_previous:
                prev_press();
                break;
            case R.id.pit_scouting_next:
                next_press();
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    void prev_press()
    {
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
                PitScoutDB pitScoutDB = new PitScoutDB(PitScouting.this, eventId);
                pitScoutDB.updatePit(data);

                // Go to the next match
                Intent intent = new Intent(PitScouting.this, PitScouting.class);
                intent.putExtra("team_number",prevTeamNumber);
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
                intent.putExtra("team_number", prevTeamNumber);
                startActivity(intent);
            }
        });
        builder.show();
    }

    void next_press()
    {
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
                PitScoutDB pitScoutDB = new PitScoutDB(PitScouting.this, eventId);
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

}
