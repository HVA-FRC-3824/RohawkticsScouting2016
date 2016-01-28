package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Auto;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Defenses;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Foul;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_IndividualDefense;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Points;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Shot;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_Auto;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_Defenses;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_Fouls;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_IndividualDefense;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_Points;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_Shots;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EventView extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static String TAG = "EventView";
    private ListView listView;
    private StatsDB statsDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        CustomHeader customHeader = (CustomHeader)findViewById(R.id.event_view_header);
        customHeader.removeHome();
        customHeader.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventView.this, StartScreen.class);
                startActivity(intent);
            }
        });

        Spinner spinner = (Spinner)findViewById(R.id.event_view_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_view_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        listView = (ListView)findViewById(R.id.event_view_list);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");

        statsDB = new StatsDB(this,eventID);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Cursor cursor = statsDB.getStats();
        switch(pos)
        {
            case 0: // Points
                points(cursor);
                break;
            case 1: // Defenses
                defenses(cursor);
                break;
            case 2: // Low Bar
                individual_defense(cursor,"low_bar");
                break;
            case 3: // Portcullis
                individual_defense(cursor,"portcullis");
                break;
            case 4: // Cheval de Frise
                individual_defense(cursor,"cheval_de_frise");
                break;
            case 5: // Moat
                individual_defense(cursor,"moat");
                break;
            case 6: // Ramparts
                individual_defense(cursor,"ramparts");
                break;
            case 7: // Drawbridge
                individual_defense(cursor,"drawbridge");
                break;
            case 8: // Sally Port
                individual_defense(cursor,"sally_port");
                break;
            case 9: // Rock Wall
                individual_defense(cursor,"rock_wall");
                break;
            case 10: // Rough Terrain
                individual_defense(cursor,"rough_terrain");
                break;
            case 11: // High Goal
                goal(cursor,"high");
                break;
            case 12: // Low Goal
                goal(cursor,"low");
                break;
            case 13: // Driver Ability
                break;
            case 14: // Defense Ability
                break;
            case 15: // Auto
                auto(cursor);
                break;
            case 16: // Endgame
                endgame(cursor);
                break;
            case 17: // Fouls
                fouls(cursor);
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {}

    private void points(Cursor cursor)
    {
        ArrayList<ELI_Points> teams = new ArrayList<>();
        while(!cursor.isAfterLast())
        {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Points team = new ELI_Points(teamNumber);

            float totalMatches = cursor.getColumnIndex("total_matches");

            if(totalMatches > 0) {
                totalMatches = cursor.getInt(cursor.getColumnIndex("total_matches"));
                team.mHighPoints = cursor.getInt(cursor.getColumnIndex("total_teleop_high_hit")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_high_hit")) * 10;
                team.mLowPoints = cursor.getInt(cursor.getColumnIndex("total_teleop_low_hit")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_low_hit")) * 5;
                team.mEndgamePoints = cursor.getInt(cursor.getColumnIndex("total_challenge")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_scale")) * 15;
                team.mDefensePoints = cursor.getInt(cursor.getColumnIndex("total_auto_portcullis_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_cheval_de_frise_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_moat_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_ramparts_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_drawbridge_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_sally_port_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rough_terrain_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rock_wall_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_portcullis_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_cheval_de_frise_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_moat_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_ramparts_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_drawbridge_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_sally_port_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rough_terrain_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rock_wall_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_portcullis")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_cheval_de_frise")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_moat")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_ramparts")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_drawbridge")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_sally_port")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_rough_terrain")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_telop_rock_wall")) * 5;

                team.mTeleopPoints = cursor.getInt(cursor.getColumnIndex("total_teleop_high_hit")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_low_hit")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_portcullis")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_cheval_de_frise")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_moat")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_ramparts")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_drawbridge")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_sally_port")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_teleop_rough_terrain")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_telop_rock_wall")) * 5;

                team.mAutoPoints = cursor.getInt(cursor.getColumnIndex("total_auto_high_hit")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_low_hit")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_low_bar")) * 5 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_portcullis_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_cheval_de_frise_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_moat_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_ramparts_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_drawbridge_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_sally_port_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rough_terrain_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rock_wall_reach")) * 2 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_portcullis_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_cheval_de_frise_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_moat_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_ramparts_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_drawbridge_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_sally_port_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rough_terrain_cross")) * 10 +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rock_wall_cross")) * 10;

                team.mFoulPoints = cursor.getInt(cursor.getColumnIndex("total_fouls")) * -5 +
                        cursor.getInt(cursor.getColumnIndex("total_tech_fouls")) * -5;
                team.mTotalPoints = team.mEndgamePoints + team.mTeleopPoints + team.mAutoPoints;
                team.mAvgPoints = (totalMatches == 0.0f) ? 0.0f : (float) team.mTotalPoints / totalMatches;
            }
            teams.add(team);

            cursor.moveToNext();
        }
        Collections.sort(teams, new Comparator<ELI_Points>() {
            @Override
            public int compare(ELI_Points lhs, ELI_Points rhs) {
                float delta = lhs.mAvgPoints - rhs.mAvgPoints;
                if (delta < 0)
                    return 1;
                else if (delta > 0)
                    return -1;
                else
                    return 0;
            }
        });
        teams.add(0, new ELI_Points(-1));
        ELA_Points eventPointsListAdapter = new ELA_Points(this, R.layout.list_item_event_points, teams);
        listView.setAdapter(eventPointsListAdapter);
    }

    private void defenses(Cursor cursor)
    {
        ArrayList<ELI_Defenses> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Defenses team = new ELI_Defenses(teamNumber);

            float totalMatches = cursor.getColumnIndex("total_matches");

            if (totalMatches > 0) {
                team.cPortcullis = cursor.getInt(cursor.getColumnIndex("total_teleop_portcullis")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_portcullis_cross"));
                team.sPortcullis = cursor.getInt(cursor.getColumnIndex("total_seen_portcullis"));
                team.cChevalDeFrise = cursor.getInt(cursor.getColumnIndex("total_teleop_cheval_de_frise")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_cheval_de_frise_cross"));
                team.sChevalDeFrise = cursor.getInt(cursor.getColumnIndex("total_seen_cheval_de_frise"));
                team.cMoat = cursor.getInt(cursor.getColumnIndex("total_teleop_moat")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_moat_cross"));
                team.sMoat = cursor.getInt(cursor.getColumnIndex("total_seen_moat"));
                team.cRamparts = cursor.getInt(cursor.getColumnIndex("total_teleop_ramparts")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_ramparts_cross"));
                team.sRamparts = cursor.getInt(cursor.getColumnIndex("total_seen_ramparts"));
                team.cDrawbridge = cursor.getInt(cursor.getColumnIndex("total_teleop_drawbridge")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_drawbridge_cross"));
                team.sDrawbridge = cursor.getInt(cursor.getColumnIndex("total_seen_drawbridge"));
                team.cSallyPort = cursor.getInt(cursor.getColumnIndex("total_teleop_sally_port")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_sally_port_cross"));
                team.sSallyPort = cursor.getInt(cursor.getColumnIndex("total_seen_sally_port"));
                team.cRoughTerrain = cursor.getInt(cursor.getColumnIndex("total_teleop_rough_terrain")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rough_terrain_cross"));
                team.sRoughTerrain = cursor.getInt(cursor.getColumnIndex("total_seen_rough_terrain"));
                team.cRockWall = cursor.getInt(cursor.getColumnIndex("total_teleop_rock_wall")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rock_wall_cross"));
                team.sRockWall = cursor.getInt(cursor.getColumnIndex("total_seen_rock_wall"));
                team.cLowBar = cursor.getInt(cursor.getColumnIndex("total_teleop_low_bar")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_low_bar_cross"));
                team.sLowBar = cursor.getInt(cursor.getColumnIndex("total_seen_low_bar"));
                team.totalCrosses = team.cPortcullis + team.cChevalDeFrise + team.cMoat +
                        team.cRamparts + team.cDrawbridge + team.cSallyPort + team.cRoughTerrain +
                        team.cRockWall + team.cLowBar;
            }
            teams.add(team);
            cursor.moveToNext();
        }

        // Compares based on average total crosses
        Collections.sort(teams, new Comparator<ELI_Defenses>() {
            @Override
            public int compare(ELI_Defenses lhs, ELI_Defenses rhs) {
                // tertiary statement make the values 0 if the team has been in no matches
                float delta = ((lhs.sLowBar == 0)?0:(lhs.totalCrosses/(float)lhs.sLowBar)) - ((rhs.sLowBar == 0)?0:(rhs.totalCrosses/(float)rhs.sLowBar));
                if(delta < 0)
                    return 1;
                else if(delta > 0)
                    return -1;
                else
                    return 0;
            }
        });
        teams.add(0, new ELI_Defenses(-1));
        ELA_Defenses ela_Defenses = new ELA_Defenses(this, R.layout.list_item_event_defenses, teams);
        listView.setAdapter(ela_Defenses);
    }

    private void individual_defense(Cursor cursor, String defense)
    {
        ArrayList<ELI_IndividualDefense> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_IndividualDefense team = new ELI_IndividualDefense(teamNumber);

            float totalMatches = cursor.getColumnIndex("total_matches");

            if (totalMatches > 0) {
                team.mTeleopCross = cursor.getInt(cursor.getColumnIndex("total_teleop_"+defense));
                team.mAutoCross = cursor.getInt(cursor.getColumnIndex("total_auto_"+defense+"_cross"));
                team.mAutoReach = cursor.getInt(cursor.getColumnIndex("total_auto_"+defense+"_reach"));
                team.mSeen = cursor.getInt(cursor.getColumnIndex("total_seen_"+defense));
                team.mAvg = (team.mSeen == 0)?0.0f:(float)(team.mAutoCross+team.mTeleopCross)/(team.mSeen);
            }
            teams.add(team);
            cursor.moveToNext();
        }


        Collections.sort(teams, new Comparator<ELI_IndividualDefense>() {
            @Override
            public int compare(ELI_IndividualDefense lhs, ELI_IndividualDefense rhs) {
                // tertiary statement make the values 0 if the team has been in no matches
                float delta = ((lhs.mSeen == 0)?0:((float)(lhs.mAutoCross+lhs.mTeleopCross)/(float)lhs.mSeen)) - ((rhs.mSeen == 0)?0:((float)(rhs.mAutoCross+rhs.mTeleopCross)/(float)rhs.mSeen));
                if(delta < 0)
                    return 1;
                else if(delta > 0)
                    return -1;
                else
                    return 0;
            }
        });

        teams.add(0, new ELI_IndividualDefense(-1));
        ELA_IndividualDefense eventIndividualDefenseListAdapter = new ELA_IndividualDefense(this, R.layout.list_item_event_individual_defense, teams);
        listView.setAdapter(eventIndividualDefenseListAdapter);
    }

    private void goal(Cursor cursor, String goal)
    {
        ArrayList<ELI_Shots> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Shots team = new ELI_Shots(teamNumber);

            float totalMatches = cursor.getColumnIndex("total_matches");

            if (totalMatches > 0) {
                team.mAutoMade = cursor.getInt(cursor.getColumnIndex("total_auto_"+goal+"_hit"));
                team.mAutoTaken = cursor.getInt(cursor.getColumnIndex("total_auto_"+goal+"_miss")) + team.mAutoMade;
                team.mAutoPercentage = (team.mAutoTaken == 0)?0:(float)team.mAutoMade/(float)team.mAutoTaken * 100.0f;

                team.mTeleopMade = cursor.getInt(cursor.getColumnIndex("total_teleop_"+goal+"_hit"));
                team.mTeleopTaken = cursor.getInt(cursor.getColumnIndex("total_teleop_"+goal+"_miss")) + team.mTeleopMade;
                team.mTeleopPercentage = (team.mTeleopTaken == 0)?0:(float)team.mTeleopMade/(float)team.mTeleopTaken * 100.0f;
            }
            teams.add(team);
            cursor.moveToNext();
        }


        Collections.sort(teams, new Comparator<ELI_Shots>() {
            @Override
            public int compare(ELI_Shots lhs, ELI_Shots rhs) {

                if(lhs.mTeleopTaken > 10 && rhs.mTeleopTaken > 10)
                {
                    if(lhs.mTeleopPercentage > rhs.mTeleopPercentage)
                    {
                        return -1;
                    }
                    else if(rhs.mTeleopPercentage > lhs.mTeleopPercentage)
                    {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
                else if(lhs.mTeleopTaken > 10)
                {
                    return -1;
                }
                else if(rhs.mTeleopTaken > 10)
                {
                    return -1;
                }
                else
                {
                    return rhs.mTeleopMade - lhs.mTeleopMade;
                }
            }
        });

        teams.add(0, new ELI_Shots(-1));
        ELA_Shot eventShotListAdapter = new ELA_Shot(this, R.layout.list_item_event_shot, teams);
        listView.setAdapter(eventShotListAdapter);
    }

    private void auto(Cursor cursor)
    {
        ArrayList<ELI_Auto> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Auto team = new ELI_Auto(teamNumber);

            float totalMatches = cursor.getColumnIndex("total_matches");

            if (totalMatches > 0) {
                team.mDefenses.cPortcullis = cursor.getInt(cursor.getColumnIndex("total_auto_portcullis_cross"));
                team.mDefenses.sPortcullis = cursor.getInt(cursor.getColumnIndex("total_seen_portcullis"));
                team.mDefenses.cChevalDeFrise = cursor.getInt(cursor.getColumnIndex("total_auto_cheval_de_frise_cross"));
                team.mDefenses.sChevalDeFrise = cursor.getInt(cursor.getColumnIndex("total_seen_cheval_de_frise"));
                team.mDefenses.cMoat = cursor.getInt(cursor.getColumnIndex("total_auto_moat_cross"));
                team.mDefenses.sMoat = cursor.getInt(cursor.getColumnIndex("total_seen_moat"));
                team.mDefenses.cRamparts = cursor.getInt(cursor.getColumnIndex("total_auto_ramparts_cross"));
                team.mDefenses.sRamparts = cursor.getInt(cursor.getColumnIndex("total_seen_ramparts"));
                team.mDefenses.cDrawbridge = cursor.getInt(cursor.getColumnIndex("total_auto_drawbridge_cross"));
                team.mDefenses.sDrawbridge = cursor.getInt(cursor.getColumnIndex("total_seen_drawbridge"));
                team.mDefenses.cSallyPort = cursor.getInt(cursor.getColumnIndex("total_auto_sally_port_cross"));
                team.mDefenses.sSallyPort = cursor.getInt(cursor.getColumnIndex("total_seen_sally_port"));
                team.mDefenses.cRoughTerrain = cursor.getInt(cursor.getColumnIndex("total_teleop_rough_terrain")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rough_terrain_cross"));
                team.mDefenses.sRoughTerrain = cursor.getInt(cursor.getColumnIndex("total_seen_rough_terrain"));
                team.mDefenses.cRockWall = cursor.getInt(cursor.getColumnIndex("total_teleop_rock_wall")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_rock_wall_cross"));
                team.mDefenses.sRockWall = cursor.getInt(cursor.getColumnIndex("total_seen_rock_wall"));
                team.mDefenses.cLowBar = cursor.getInt(cursor.getColumnIndex("total_teleop_low_bar")) +
                        cursor.getInt(cursor.getColumnIndex("total_auto_low_bar_cross"));
                team.mDefenses.sLowBar = cursor.getInt(cursor.getColumnIndex("total_seen_low_bar"));
                team.mDefenses.totalCrosses = team.mDefenses.cPortcullis + team.mDefenses.cChevalDeFrise + team.mDefenses.cMoat +
                        team.mDefenses.cRamparts + team.mDefenses.cDrawbridge + team.mDefenses.cSallyPort + team.mDefenses.cRoughTerrain +
                        team.mDefenses.cRockWall + team.mDefenses.cLowBar;

                team.mHigh.mAutoMade = cursor.getInt(cursor.getColumnIndex("total_auto_high_hit"));
                team.mHigh.mAutoTaken = cursor.getInt(cursor.getColumnIndex("total_auto_high_miss")) + team.mHigh.mAutoMade;
                team.mHigh.mAutoPercentage = (team.mHigh.mAutoTaken == 0)?0:(float)team.mHigh.mAutoMade/(float)team.mHigh.mAutoTaken * 100.0f;

                team.mLow.mAutoMade = cursor.getInt(cursor.getColumnIndex("total_auto_low_hit"));
                team.mLow.mAutoTaken = cursor.getInt(cursor.getColumnIndex("total_auto_low_miss")) + team.mLow.mAutoMade;
                team.mLow.mAutoPercentage = (team.mLow.mAutoTaken == 0)?0:(float)team.mLow.mAutoMade/(float)team.mLow.mAutoTaken * 100.0f;

            }
            teams.add(team);
            cursor.moveToNext();
        }

        // Compares based on average total crosses
        Collections.sort(teams, new Comparator<ELI_Auto>() {
            @Override
            public int compare(ELI_Auto lhs, ELI_Auto rhs) {
                // tertiary statement make the values 0 if the team has been in no matches
                if(lhs.mDefenses.sLowBar == 0 && rhs.mDefenses.sLowBar == 0) {
                    return 0;
                }
                else if(lhs.mDefenses.sLowBar == 0)
                {
                    if(rhs.mDefenses.totalCrosses > 0 || rhs.mLow.mAutoMade > 0 || rhs.mHigh.mAutoMade > 0)
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }
                else if(rhs.mDefenses.sLowBar == 0)
                {
                    if(lhs.mDefenses.totalCrosses > 0 || lhs.mLow.mAutoMade > 0 || lhs.mHigh.mAutoMade > 0)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
                else
                {
                    float delta = ((rhs.mDefenses.totalCrosses*10.0f + rhs.mHigh.mAutoMade * 10.0f + rhs.mLow.mAutoMade * 5.0f) / (float)rhs.mDefenses.sLowBar) -
                            ((lhs.mDefenses.totalCrosses*10.0f + lhs.mHigh.mAutoMade * 10.0f + lhs.mLow.mAutoMade * 5.0f) / (float)lhs.mDefenses.sLowBar);
                    if(delta < 0)
                        return 1;
                    else if(delta > 0)
                        return -1;
                    else
                        return 0;
                }
            }
        });
        teams.add(0, new ELI_Auto(-1));
        ELA_Auto ela_Auto = new ELA_Auto(this, R.layout.list_item_event_auto, teams);
        listView.setAdapter(ela_Auto);
    }

    private void endgame(Cursor cursor)
    {

    }

    private void fouls(Cursor cursor)
    {
        ArrayList<ELI_Fouls> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Fouls team = new ELI_Fouls(teamNumber);

            float totalMatches = cursor.getColumnIndex("total_matches");

            if (totalMatches > 0) {
                team.mFouls = cursor.getInt(cursor.getColumnIndex("total_fouls"));
                team.mTechFouls = cursor.getInt(cursor.getColumnIndex("total_tech_fouls"));
                team.mYellowCards = cursor.getInt(cursor.getColumnIndex("total_yellow_cards"));
                team.mRedCards = cursor.getInt(cursor.getColumnIndex("total_red_cards"));
            }
            teams.add(team);
            cursor.moveToNext();
        }


        Collections.sort(teams, new Comparator<ELI_Fouls>() {
            @Override
            public int compare(ELI_Fouls lhs, ELI_Fouls rhs) {
                return (lhs.mRedCards*4 + lhs.mYellowCards*3 + lhs.mTechFouls*2 + lhs.mFouls) - (rhs.mRedCards*4 + rhs.mYellowCards*3 + rhs.mTechFouls*2 + rhs.mFouls);
            }
        });

        teams.add(0, new ELI_Fouls(-1));
        ELA_Foul eventFoulListAdapter = new ELA_Foul(this, R.layout.list_item_event_shot, teams);
        listView.setAdapter(eventFoulListAdapter);
    }


}
