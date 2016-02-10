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

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Ability;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Auto;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Defenses;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Endgame;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Foul;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_IndividualDefense;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Points;
import com.team3824.akmessing1.scoutingapp.adapters.ELA_Shot;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Ability;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Auto;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Defenses;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Endgame;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Fouls;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_IndividualDefense;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Points;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Shots;
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

        // Add the custom header and have the back button take the user to the start screen
        CustomHeader customHeader = (CustomHeader)findViewById(R.id.event_view_header);
        customHeader.removeHome();
        customHeader.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventView.this, StartScreen.class);
                startActivity(intent);
            }
        });

        // Set up the dropdown menu for all the comparison categories
        Spinner spinner = (Spinner)findViewById(R.id.event_view_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_view_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        listView = (ListView)findViewById(R.id.event_view_list);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        statsDB = new StatsDB(this,eventID);

    }

    // When a category is selected calculate all the values needed for the columns and display
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        findViewById(R.id.key1).setVisibility(View.GONE);
        findViewById(R.id.key2).setVisibility(View.GONE);
        findViewById(R.id.key3).setVisibility(View.GONE);
        Cursor cursor = statsDB.getStats();
        switch(pos)
        {
            case 0: // Points
                points(cursor);
                break;
            case 1: // Defenses
                defenses(cursor);
                findViewById(R.id.key1).setVisibility(View.VISIBLE);
                break;
            case 2: // Low Bar
                individual_defense(cursor,Constants.LOW_BAR_INDEX);
                break;
            case 3: // Portcullis
                individual_defense(cursor,Constants.PORTCULLIS_INDEX);
                break;
            case 4: // Cheval de Frise
                individual_defense(cursor,Constants.CHEVAL_DE_FRISE_INDEX);
                break;
            case 5: // Moat
                individual_defense(cursor,Constants.MOAT_INDEX);
                break;
            case 6: // Ramparts
                individual_defense(cursor,Constants.RAMPARTS_INDEX);
                break;
            case 7: // Drawbridge
                individual_defense(cursor,Constants.DRAWBRIDGE_INDEX);
                break;
            case 8: // Sally Port
                individual_defense(cursor,Constants.SALLY_PORT_INDEX);
                break;
            case 9: // Rock Wall
                individual_defense(cursor,Constants.ROCK_WALL_INDEX);
                break;
            case 10: // Rough Terrain
                individual_defense(cursor,Constants.ROUGH_TERRAIN_INDEX);
                break;
            case 11: // High Goal
                goal(cursor,"high");
                break;
            case 12: // Low Goal
                goal(cursor,"low");
                break;
            case 13: // Driver Ability
                ability(cursor,Constants.DRIVE_ABILITY_RANKING);
                break;
            case 14: // Defense Ability
                ability(cursor,Constants.DEFENSE_ABILITY_RANKING);
                break;
            case 15: // Auto
                auto(cursor);
                findViewById(R.id.key2).setVisibility(View.VISIBLE);
                findViewById(R.id.key3).setVisibility(View.VISIBLE);
                break;
            case 16: // Endgame
                endgame(cursor);
                break;
            case 17: // Fouls
                fouls(cursor);
                break;
        }
    }

    // Not possible for nothing to be selected ...
    public void onNothingSelected(AdapterView<?> parent) {}

    // Calculate all the different types of points for each team and then sort the teams
    private void points(Cursor cursor)
    {
        ArrayList<ELI_Points> teams = new ArrayList<>();
        while(!cursor.isAfterLast())
        {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Points team = new ELI_Points(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

            if(totalMatches > 0) {
                totalMatches = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_MATCHES));

                team.mHighPoints = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_HIT)) * 10;

                team.mLowPoints = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_TELEOP_LOW_HIT)) * 2 +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_LOW_HIT)) * 5;

                team.mEndgamePoints = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_CHALLENGE)) * 5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_SCALE)) * 15;

                for(int i = 0; i < 9; i++) {
                    team.mDefensePoints += cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]))*2;
                    team.mDefensePoints += cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]))*10;
                    team.mDefensePoints += cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]))*5;
                }

                team.mTeleopPoints = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_TELEOP_LOW_HIT)) * 2;
                for(int i = 0; i < 9; i++) {
                    team.mTeleopPoints += cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i])) * 5;
                }

                team.mAutoPoints = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_HIT)) * 10 +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_LOW_HIT)) * 5;
                for(int i = 0; i < 9; i++)
                {
                    team.mAutoPoints += cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]))*2;
                    team.mAutoPoints += cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]))*10;
                }


                team.mFoulPoints = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_FOULS)) * -5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_TECH_FOULS)) * -5;

                team.mTotalPoints = team.mEndgamePoints + team.mTeleopPoints + team.mAutoPoints + team.mFoulPoints;

                team.mAvgPoints = (totalMatches == 0.0f) ? 0.0f : (float) team.mTotalPoints / totalMatches;
            }
            teams.add(team);

            cursor.moveToNext();
        }

        // Sorting is based on average points
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

        // Header row hack
        teams.add(0, new ELI_Points(-1));
        ELA_Points eventPointsListAdapter = new ELA_Points(this, R.layout.list_item_event_points, teams);
        listView.setAdapter(eventPointsListAdapter);
    }

    // Calculate all the values for the defenses
    private void defenses(Cursor cursor)
    {
        ArrayList<ELI_Defenses> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Defenses team = new ELI_Defenses(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.cPortcullis = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.PORTCULLIS_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.PORTCULLIS_INDEX]));
                team.sPortcullis = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.PORTCULLIS_INDEX]));
                team.tPortcullis = (team.sPortcullis == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.PORTCULLIS_INDEX])) / (float)team.sPortcullis;

                team.cChevalDeFrise = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]));
                team.sChevalDeFrise = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.CHEVAL_DE_FRISE_INDEX]));
                team.tChevalDeFrise = (team.sChevalDeFrise == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.CHEVAL_DE_FRISE_INDEX])) / (float)team.sChevalDeFrise;

                team.cMoat = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.MOAT_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.MOAT_INDEX]));
                team.sMoat = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.MOAT_INDEX]));
                team.tMoat = (team.sMoat == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.MOAT_INDEX])) / (float)team.sMoat;

                team.cRamparts = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.RAMPARTS_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.RAMPARTS_INDEX]));
                team.sRamparts = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.RAMPARTS_INDEX]));
                team.tRamparts = (team.sRamparts == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.RAMPARTS_INDEX])) / (float)team.sRamparts;

                team.cDrawbridge = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.DRAWBRIDGE_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.DRAWBRIDGE_INDEX]));
                team.sDrawbridge = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.DRAWBRIDGE_INDEX]));
                team.tDrawbridge = (team.sDrawbridge == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX])) / (float)team.sDrawbridge;

                team.cSallyPort = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.SALLY_PORT_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.SALLY_PORT_INDEX]));
                team.sSallyPort = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.SALLY_PORT_INDEX]));
                team.tSallyPort = (team.sSallyPort == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX])) / (float)team.sSallyPort;

                team.cRoughTerrain = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROUGH_TERRAIN_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROUGH_TERRAIN_INDEX]));
                team.sRoughTerrain = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.ROUGH_TERRAIN_INDEX]));
                team.tRoughTerrain = (team.sRoughTerrain == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.ROUGH_TERRAIN_INDEX])) / (float)team.sRoughTerrain;

                team.cRockWall = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROCK_WALL_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROCK_WALL_INDEX]));
                team.sRockWall = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.ROCK_WALL_INDEX]));
                team.tRockWall = (team.sRockWall == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.ROCK_WALL_INDEX])) / (float)team.sRockWall;

                team.cLowBar = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.LOW_BAR_INDEX])) +
                        cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.LOW_BAR_INDEX]));
                team.sLowBar = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.LOW_BAR_INDEX]));
                team.tLowBar = (team.sLowBar == 0) ? -1 : cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.LOW_BAR_INDEX])) / (float)team.sLowBar;

                team.totalCrosses = team.cPortcullis + team.cChevalDeFrise + team.cMoat +
                        team.cRamparts + team.cDrawbridge + team.cSallyPort + team.cRoughTerrain +
                        team.cRockWall + team.cLowBar;
            }
            teams.add(team);
            cursor.moveToNext();
        }

        // Compares based on average total number of crosses
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

        // Header row hack
        teams.add(0, new ELI_Defenses(-1));
        ELA_Defenses ela_Defenses = new ELA_Defenses(this, R.layout.list_item_event_defenses, teams);
        listView.setAdapter(ela_Defenses);
    }

    // Calculates all the values for an individual defense
    private void individual_defense(Cursor cursor, int defense_index)
    {
        ArrayList<ELI_IndividualDefense> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_IndividualDefense team = new ELI_IndividualDefense(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mTeleopCross = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[defense_index]));
                team.mAutoCross = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[defense_index]));
                team.mAutoReach = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_REACHED[defense_index]));
                team.mSeen = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[defense_index]));
                team.mTime = (team.mSeen == 0)?-1:(float)cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[defense_index]))/(float)team.mSeen;
            }
            teams.add(team);
            cursor.moveToNext();
        }

        // Sorts based on the total number of times cross (auto and teleop) / the number of times seen
        Collections.sort(teams, new Comparator<ELI_IndividualDefense>() {
            @Override
            public int compare(ELI_IndividualDefense lhs, ELI_IndividualDefense rhs) {
                // tertiary statement make the values 0 if the team has been in no matches
                if(lhs.mAutoCross == 0 && lhs.mTeleopCross == 0 && rhs.mAutoCross == 0 && rhs.mTeleopCross == 0)
                {
                    return 0;
                }
                else if(lhs.mAutoCross == 0 && lhs.mTeleopCross == 0)
                {
                    return 1;
                }
                else if(rhs.mAutoCross == 0 && rhs.mTeleopCross == 0)
                {
                    return -1;
                }
                else
                {
                    if(lhs.mTime < rhs.mTime)
                    {
                        return -1;
                    }
                    else if(lhs.mTime > rhs.mTime)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
            }
        });

        teams.add(0, new ELI_IndividualDefense(-1));
        ELA_IndividualDefense eventIndividualDefenseListAdapter = new ELA_IndividualDefense(this, R.layout.list_item_event_individual_defense, teams);
        listView.setAdapter(eventIndividualDefenseListAdapter);
    }

    // Calculates all the values for shooting for a goal
    private void goal(Cursor cursor, String goal)
    {
        ArrayList<ELI_Shots> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Shots team = new ELI_Shots(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

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

        // Sorted based on percentage if more than 10 shots taken or
        // number of shots made if less than 10
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

    // Calculates values for auto
    private void auto(Cursor cursor)
    {
        ArrayList<ELI_Auto> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Auto team = new ELI_Auto(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);


            if (totalMatches > 0) {
                team.mDefenses.cPortcullis = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.PORTCULLIS_INDEX]));
                team.mDefenses.sPortcullis = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.PORTCULLIS_INDEX]));

                team.mDefenses.cChevalDeFrise = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]));
                team.mDefenses.sChevalDeFrise = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.CHEVAL_DE_FRISE_INDEX]));

                team.mDefenses.cMoat = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.MOAT_INDEX]));
                team.mDefenses.sMoat = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.MOAT_INDEX]));

                team.mDefenses.cRamparts = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.RAMPARTS_INDEX]));
                team.mDefenses.sRamparts = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.RAMPARTS_INDEX]));

                team.mDefenses.cDrawbridge = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.DRAWBRIDGE_INDEX]));
                team.mDefenses.sDrawbridge = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.DRAWBRIDGE_INDEX]));

                team.mDefenses.cSallyPort = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.SALLY_PORT_INDEX]));
                team.mDefenses.sSallyPort = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.SALLY_PORT_INDEX]));

                team.mDefenses.cRoughTerrain = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROUGH_TERRAIN_INDEX]));
                team.mDefenses.sRoughTerrain = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.ROUGH_TERRAIN_INDEX]));

                team.mDefenses.cRockWall = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROCK_WALL_INDEX]));
                team.mDefenses.sRockWall = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.ROCK_WALL_INDEX]));

                team.mDefenses.cLowBar = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.LOW_BAR_INDEX]));
                team.mDefenses.sLowBar = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_DEFENSES_SEEN[Constants.LOW_BAR_INDEX]));

                team.mDefenses.totalCrosses = team.mDefenses.cPortcullis + team.mDefenses.cChevalDeFrise + team.mDefenses.cMoat +
                        team.mDefenses.cRamparts + team.mDefenses.cDrawbridge + team.mDefenses.cSallyPort + team.mDefenses.cRoughTerrain +
                        team.mDefenses.cRockWall + team.mDefenses.cLowBar;

                team.mHigh.mAutoMade = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_HIT));
                team.mHigh.mAutoTaken = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_MISS)) + team.mHigh.mAutoMade;
                team.mHigh.mAutoPercentage = (team.mHigh.mAutoTaken == 0)?0:(float)team.mHigh.mAutoMade/(float)team.mHigh.mAutoTaken * 100.0f;

                team.mLow.mAutoMade = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_HIT));
                team.mLow.mAutoTaken = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_AUTO_LOW_MISS)) + team.mLow.mAutoMade;
                team.mLow.mAutoPercentage = (team.mLow.mAutoTaken == 0)?0:(float)team.mLow.mAutoMade/(float)team.mLow.mAutoTaken * 100.0f;

            }
            teams.add(team);
            cursor.moveToNext();
        }

        // Compares based on average auto points
        Collections.sort(teams, new Comparator<ELI_Auto>() {
            @Override
            public int compare(ELI_Auto lhs, ELI_Auto rhs) {
                if(lhs.mDefenses.sLowBar == 0 && rhs.mDefenses.sLowBar == 0) {
                    return 0;
                }
                else if(lhs.mDefenses.sLowBar == 0)
                {
                    if(rhs.mDefenses.totalCrosses > 0 || rhs.mLow.mAutoMade > 0 || rhs.mHigh.mAutoMade > 0)
                    {
                        return 1;
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
                        return -1;
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
                        return -1;
                    else if(delta > 0)
                        return 1;
                    else
                        return 0;
                }
            }
        });
        teams.add(0, new ELI_Auto(-1));
        ELA_Auto ela_Auto = new ELA_Auto(this, R.layout.list_item_event_auto, teams);
        listView.setAdapter(ela_Auto);
    }

    // Grabs ability rankings
    private void ability(Cursor cursor, String ability)
    {
        ArrayList<ELI_Ability> teams = new ArrayList<>();

        while(!cursor.isAfterLast())
        {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Ability team = new ELI_Ability(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mRank = cursor.getString(cursor.getColumnIndex(ability));
            }
            else
            {
                team.mRank = "N/A";
            }
            teams.add(team);
            cursor.moveToNext();
        }

        Collections.sort(teams, new Comparator<ELI_Ability>() {
            @Override
            public int compare(ELI_Ability lhs, ELI_Ability rhs) {

                if(lhs.mRank.equals("N/A") && rhs.mRank.equals("N/A"))
                {
                    return 0;
                }
                else if(lhs.mRank.equals("N/A"))
                {
                    return 1;
                }
                else if(rhs.mRank.equals("N/A"))
                {
                    return -1;
                }

                int l_rank = (lhs.mRank.charAt(0) == 'T')? Integer.parseInt(lhs.mRank.substring(1)):Integer.parseInt(lhs.mRank);
                int r_rank = (rhs.mRank.charAt(0) == 'T')? Integer.parseInt(rhs.mRank.substring(1)):Integer.parseInt(rhs.mRank);
                return l_rank - r_rank;
            }
        });
        teams.add(0, new ELI_Ability(-1));
        ELA_Ability ela_Ability = new ELA_Ability(this, R.layout.list_item_event_ability, teams);
        listView.setAdapter(ela_Ability);
    }

    // Calculates values for comparing endgames
    private void endgame(Cursor cursor)
    {
        ArrayList<ELI_Endgame> teams = new ArrayList<>();

        while(!cursor.isAfterLast())
        {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Endgame team = new ELI_Endgame(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mTotalMatches = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_MATCHES));
                team.mChallenge = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_CHALLENGE));
                team.mScale = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_SCALE));
            }
            teams.add(team);
            cursor.moveToNext();
        }

        // Sorts based on average endgame points
        Collections.sort(teams, new Comparator<ELI_Endgame>() {
            @Override
            public int compare(ELI_Endgame lhs, ELI_Endgame rhs) {
                if(lhs.mTotalMatches == 0 && rhs.mTotalMatches == 0)
                {
                    return 0;
                }
                else if(lhs.mTotalMatches == 0)
                {
                    if(rhs.mChallenge > 0 || rhs.mScale > 0)
                    {
                        return 1;
                    }
                    else
                    {
                        return 0;
                    }
                }
                else if(rhs.mTotalMatches == 0)
                {
                    if(lhs.mChallenge > 0 || lhs.mScale > 0)
                    {
                        return -1;
                    }
                    else
                    {
                        return 0;
                    }
                }

                float delta = ((lhs.mScale*15 + lhs.mChallenge*5)/(float)lhs.mTotalMatches) - ((rhs.mScale*15 + rhs.mChallenge*5)/(float)rhs.mTotalMatches);
                if(delta < 0)
                    return 1;
                else if(delta > 0)
                    return -1;
                else
                    return 0;
            }
        });
        teams.add(0, new ELI_Endgame(-1));
        ELA_Endgame ela_Endgame = new ELA_Endgame(this, R.layout.list_item_event_endgame, teams);
        listView.setAdapter(ela_Endgame);
    }

    private void fouls(Cursor cursor)
    {
        ArrayList<ELI_Fouls> teams = new ArrayList<>();

        while(!cursor.isAfterLast()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Fouls team = new ELI_Fouls(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mFouls = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_FOULS));
                team.mTechFouls = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_TECH_FOULS));
                team.mYellowCards = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_YELLOW_CARDS));
                team.mRedCards = cursor.getInt(cursor.getColumnIndex(Constants.TOTAL_RED_CARDS));
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
        ELA_Foul eventFoulListAdapter = new ELA_Foul(this, R.layout.list_item_event_fouls, teams);
        listView.setAdapter(eventFoulListAdapter);
    }


}
