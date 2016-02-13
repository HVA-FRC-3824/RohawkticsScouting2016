package com.team3824.akmessing1.scoutingapp.fragments;

import android.database.Cursor;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.list_items.Team;

/**
 * Created by akmessing1 on 2/12/16.
 */
public class OffensivePick extends ScoutPick {



    private String TAG = "OffensivePick";

    public OffensivePick() {
        setPickType("offensive");
    }

    @Override
    protected Team setupTeam(Team team, Cursor statsCursor)
    {
        float averagePoints = 0.0f;
        String defensesFast = "";
        String defensesMedium = "";

        if(statsCursor.getColumnIndex(Constants.TOTAL_MATCHES) > -1 && statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_MATCHES)) > 0) {

            int autoPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_AUTO_HIGH_HIT)) * 10 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_AUTO_LOW_HIT)) * 5;
            for (int i = 0; i < 9; i++) {
                autoPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_REACHED[i])) * 2;
                autoPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i])) * 10;
            }

            int teleopPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_TELEOP_LOW_HIT)) * 2;
            for (int i = 0; i < 9; i++) {
                teleopPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i])) * 5;
            }

            int endgamePoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_CHALLENGE)) * 5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_SCALE)) * 15;

            int foulPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_FOULS)) * -5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_TECH_FOULS)) * -5;

            int totalMatches = statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_MATCHES));
            if (totalMatches != 0) {
                averagePoints = ((float)(autoPoints + teleopPoints + endgamePoints + foulPoints)) / ((float) totalMatches);
            }

            int defenseWeight = 0;
            for (int i = 0; i < 9; i++) {
                float time = statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[i]));
                if (time < 5 && time > 0) {
                    defenseWeight+=5;
                    defensesFast += Constants.DEFENSES_ABREV[i] + ", ";
                }
                else if(time >= 5 && time < 10)
                {
                    defenseWeight+=2;
                    defensesMedium += Constants.DEFENSES_ABREV[i] + ", ";
                }
            }
            if(defensesFast.length() > 2) {
                defensesFast = defensesFast.substring(0, defensesFast.length() - 2);
            }
            if(defensesMedium.length() > 2) {
                defensesMedium = defensesMedium.substring(0, defensesMedium.length() - 2);
            }
            if (statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX])) < 5 && statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX])) > 0) {
                defenseWeight += 5;
            }
            if (statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX])) < 5 && statsCursor.getFloat(statsCursor.getColumnIndex(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX])) > 0) {
                defenseWeight += 5;
            }
            team.setMapElement(Constants.OFFENSIVE_PICKABILITY, new ScoutValue(((int)averagePoints)+defenseWeight));

            String bottomText = String.format("Offense Pickability: %d Avg Points: %.2f Fast Defense: ",team.getMapElement(Constants.OFFENSIVE_PICKABILITY).getInt(),averagePoints);
            if(defensesFast == "")
            {
                bottomText += "None";
            }
            else
            {
                bottomText += defensesFast;
            }
            bottomText += " Medium Defense: ";
            if(defensesMedium == "")
            {
                bottomText += "None";
            }
            else
            {
                bottomText += defensesMedium;
            }
            team.setMapElement(Constants.BOTTOM_TEXT, new ScoutValue(bottomText));
        }
        else
        {
            team.setMapElement(Constants.OFFENSIVE_PICKABILITY, new ScoutValue(0));
            team.setMapElement(Constants.BOTTOM_TEXT,new ScoutValue("No matches yet"));
        }

        return team;
    }


}
