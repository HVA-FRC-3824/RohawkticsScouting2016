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

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_PitScout;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class PitScouting extends AppCompatActivity {

    final private String TAG = "PitScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_PitScout adapter;

    private int teamNumber;

    private String eventId;
    private String userType;
    private int prevTeamNumber = -1;
    private int nextTeamNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.pit_scouting_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt(Constants.TEAM_NUMBER);
        setTitle("Team Number: " + teamNumber);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.pit_view_pager);
        adapter = new FPA_PitScout(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.pit_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        userType = sharedPreferences.getString(Constants.USER_TYPE,"");
        PitScoutDB pitScoutDB = new PitScoutDB(this, eventId);
        Map<String, ScoutValue> map = pitScoutDB.getTeamMap(teamNumber);
        if(map.get(PitScoutDB.KEY_COMPLETE).getInt() > 0)
        {
            adapter.setValueMap(map);
        }

        prevTeamNumber = pitScoutDB.getPreviousTeamNumber(teamNumber);
        nextTeamNumber = pitScoutDB.getNextTeamNumber(teamNumber);

        Utilities.setupUI(this, findViewById(android.R.id.content));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_overflow, menu);
        if(prevTeamNumber == -1) {
            menu.removeItem(R.id.previous);
        }
        if(nextTeamNumber == -1) {
            menu.removeItem(R.id.next);
        }
        if(!userType.equals(Constants.ADMIN))
        {
            menu.removeItem(R.id.reset);
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
            case R.id.reset:
                reset();
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void home_press()
    {
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

                Intent intent = new Intent(PitScouting.this, StartScreen.class);
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
                Intent intent = new Intent(PitScouting.this, StartScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void back_press()
    {
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

                Intent intent2 = new Intent(PitScouting.this, PitList.class);
                startActivity(intent2);
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
                Intent intent2 = new Intent(PitScouting.this, PitList.class);
                startActivity(intent2);
            }
        });
        builder.show();
    }


    private void previous_press()
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
                intent.putExtra(Constants.TEAM_NUMBER,prevTeamNumber);
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
                intent.putExtra(Constants.TEAM_NUMBER, prevTeamNumber);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void next_press()
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
                intent.putExtra(Constants.TEAM_NUMBER,nextTeamNumber);
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
                intent.putExtra(Constants.TEAM_NUMBER, nextTeamNumber);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void reset()
    {
        Log.d(TAG,"team delete pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
        builder.setTitle("Reset pit data?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PitScoutDB pitScoutDB = new PitScoutDB(PitScouting.this, eventId);
                pitScoutDB.resetTeam(teamNumber);
                Map<String, ScoutValue> map = new Hashtable<String, ScoutValue>();
                Intent intent = new Intent(PitScouting.this, PitScouting.class);
                intent.putExtra(Constants.TEAM_NUMBER,teamNumber);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

}
