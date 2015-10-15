package com.example.akmessing1.scoutingtest.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.adapters.MatchScoutFragmentPagerAdapter;

public class MatchScouting extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pager;
    private MatchScoutFragmentPagerAdapter adapter;
    private Toolbar toolbar;


    public String teamNumber;
    private String matchNumber;
    private String allianceColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getString("team_number");
        matchNumber = extras.getString("match_number");

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        allianceColor = sharedPreferences.getString("alliance_color","blue");

        findViewById(android.R.id.content).setKeepScreenOn(true);
        pager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new MatchScoutFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        tabs = (TabLayout)findViewById(R.id.sliding_tabs);
        tabs.setupWithViewPager(pager);
    }

}
