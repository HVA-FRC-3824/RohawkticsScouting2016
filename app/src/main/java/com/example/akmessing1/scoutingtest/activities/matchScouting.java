package com.example.akmessing1.scoutingtest.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.ScheduleDB;
import com.example.akmessing1.scoutingtest.adapters.MatchScoutFragmentPagerAdapter;


public class MatchScouting extends AppCompatActivity {

    private static String TAG = "MatchScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MatchScoutFragmentPagerAdapter adapter;
    private Toolbar toolbar;

    SharedPreferences sharedPreferences;

    public int teamNumber;
    private int matchNumber;
    private String allianceColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt("team_number");
        matchNumber = extras.getInt("match_number");

        TextView tv = (TextView)findViewById(R.id.team_num);
        tv.setText("Team Number: "+teamNumber);
        tv = (TextView)findViewById(R.id.match_num);
        tv.setText("Match Number: "+matchNumber);

        sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        allianceColor = sharedPreferences.getString("alliance_color", "Blue");

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new MatchScoutFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        //viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        String eventId = sharedPreferences.getString("event_id","");
        ScheduleDB scheduleDB = new ScheduleDB(this,eventId);
        if(matchNumber == 1)
        {
            Button previous = (Button)findViewById(R.id.previous_match);
            previous.setVisibility(View.INVISIBLE);
        }
        else
        {
            Cursor prevCursor = scheduleDB.getMatch(matchNumber-1);
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            final int prevTeamNumber = prevCursor.getInt(prevCursor.getColumnIndex(allianceColor.toLowerCase() + allianceNum));
            Button prev = (Button)findViewById(R.id.previous_match);
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"next match pressed");
                    Intent intent = new Intent(MatchScouting.this,MatchScouting.class);
                    intent.putExtra("team_number",prevTeamNumber);
                    intent.putExtra("match_number",matchNumber-1);
                    startActivity(intent);
                }
            });
        }


        Cursor nextCursor = scheduleDB.getMatch(matchNumber+1);
        if(nextCursor == null)
        {
            Button next = (Button)findViewById(R.id.next_match);
            next.setVisibility(View.INVISIBLE);
        }
        else
        {
            int allianceNum = sharedPreferences.getInt("alliance_number", 0);
            final int nextTeamNumber = nextCursor.getInt(nextCursor.getColumnIndex(allianceColor.toLowerCase()+allianceNum));
            Button next = (Button)findViewById(R.id.next_match);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"next match pressed");
                    Intent intent = new Intent(MatchScouting.this,MatchScouting.class);
                    intent.putExtra("team_number",nextTeamNumber);
                    intent.putExtra("match_number",matchNumber+1);
                    startActivity(intent);
                }
            });
        }
    }
}
