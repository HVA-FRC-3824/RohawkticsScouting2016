package com.team3824.akmessing1.scoutingapp.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.mobeta.android.dslv.DragSortListView;
import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.adapters.PickListAdapter;

import java.util.Comparator;

public class FirstPick extends ScoutPick{

    private String TAG = "FirstPick";

    DragSortListView list;
    PickListAdapter adapter;
    StatsDB statsDB;
    SharedPreferences sharedPref;

    public FirstPick() {
        setPickType("first");
    }

    @Override
    protected Team setupTeam(Team team, Cursor statsCursor)
    {
        float averagePoints = 0.0f;
        String defensesFast = "";

        if(statsCursor.getColumnIndex(Constants.TOTAL_MATCHES) > -1) {

            int autoPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_HIT)) * 10 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_AUTO_LOW_HIT)) * 5;
            for (int i = 0; i < 9; i++) {
                autoPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_REACHED[i])) * 2;
                autoPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i])) * 10;
            }

            Log.d(TAG, "Auto: " + String.valueOf(autoPoints));

            int teleopPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_TELEOP_LOW_HIT)) * 2;
            for (int i = 0; i < 9; i++) {
                teleopPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i])) * 5;
            }

            Log.d(TAG, "Teleop: " + String.valueOf(teleopPoints));

            int endgamePoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_CHALLENGE)) * 5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_SCALE)) * 15;

            Log.d(TAG, "Endgame: " + String.valueOf(endgamePoints));

            int foulPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_FOULS)) * -5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_TECH_FOULS)) * -5;

            Log.d(TAG, "Foul: " + String.valueOf(foulPoints));

            int totalMatches = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_MATCHES));
            if (totalMatches != 0) {
                averagePoints = ((float)(autoPoints + teleopPoints + endgamePoints + foulPoints)) / ((float) totalMatches);
            }

            Log.d(TAG, "Avg: " + String.valueOf(averagePoints));

            int numDefenseFast = 0;
            for (int i = 0; i < 9; i++) {
                float time = statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[i]));
                if (time < 5 && time > 0) {
                    numDefenseFast++;
                    defensesFast += Constants.DEFENSES[i].replace("_", " ") + ", ";
                }
            }
            if(defensesFast.length() > 2) {
                defensesFast = defensesFast.substring(0, defensesFast.length() - 2);
            }
            if (statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX])) < 5 && statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX])) > 0)
                numDefenseFast++;
            if (statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX])) < 5 && statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX])) > 0)
                numDefenseFast++;
            team.setMapElement(Constants.FIRST_PICKABILITY, new ScoutValue(((int)averagePoints)+numDefenseFast*5));
        }
        else
        {
            team.setMapElement(Constants.FIRST_PICKABILITY, new ScoutValue(0));
        }

        String bottomText = "First Pickability: "+String.valueOf(team.getMapElement(Constants.FIRST_PICKABILITY).getInt());
        bottomText += " Avg Points: "+String.valueOf(averagePoints) + " Fast Defenses: " + defensesFast;
        team.setMapElement(Constants.BOTTOM_TEXT, new ScoutValue(bottomText));

        return team;
    }
}
