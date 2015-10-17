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

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MatchScoutFragmentPagerAdapter adapter;
    private Toolbar toolbar;


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

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        allianceColor = sharedPreferences.getString("alliance_color","blue");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        adapter = new MatchScoutFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

}
