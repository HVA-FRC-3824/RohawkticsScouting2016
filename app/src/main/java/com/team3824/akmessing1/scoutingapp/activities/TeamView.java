package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
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
import com.team3824.akmessing1.scoutingapp.adapters.FPA_TeamView;


public class TeamView extends AppCompatActivity {

    final private String TAG = "TeamView";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_TeamView adapter;

    private int teamNumber;
    private int previousTeamNumber, nextTeamNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_view);

        Toolbar toolbar = (Toolbar)findViewById(R.id.team_view_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt(Constants.TEAM_NUMBER);
        setTitle("Team Number: " + teamNumber);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.team_view_view_pager);
        adapter = new FPA_TeamView(getFragmentManager(),teamNumber);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout)findViewById(R.id.team_view_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        PitScoutDB pitScoutDB = new PitScoutDB(this, eventId);

        previousTeamNumber = pitScoutDB.getPreviousTeamNumber(teamNumber);
        nextTeamNumber = pitScoutDB.getNextTeamNumber(teamNumber);

        pitScoutDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_overflow, menu);
        if(previousTeamNumber == -1) {
            menu.removeItem(R.id.previous);
        }
        if(nextTeamNumber == -1) {
            menu.removeItem(R.id.next);
        }
        menu.removeItem(R.id.reset);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(this, StartScreen.class);
                startActivity(intent);
                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.previous:
                Log.d(TAG, "previous team pressed");
                // Go to the next match
                intent = new Intent(TeamView.this, TeamView.class);
                intent.putExtra(Constants.TEAM_NUMBER, previousTeamNumber);
                startActivity(intent);
                break;
            case R.id.next:
                Log.d(TAG, "next team pressed");

                // Go to the next match
                intent = new Intent(TeamView.this, TeamView.class);
                intent.putExtra(Constants.TEAM_NUMBER, nextTeamNumber);
                startActivity(intent);
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

}
