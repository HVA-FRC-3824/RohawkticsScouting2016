package com.team3824.akmessing1.scoutingapp.fragments.PickLists;

import android.database.Cursor;

import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.util.Objects;

/**
 * @author Andrew Messing
 * @version 1
 *
 * Fragment that is the Offensive Pick List. Currently sorting is based on average score with extra
 * weight for speedily crossing defenses.
 */
//TODO: Determine if weights for crossing defenses quickly is correct
public class OffensivePick extends ScoutPick {

    private final String TAG = "OffensivePick";

    public OffensivePick() {
        setPickType("offensive");
    }

    /**
     * Fills an individual team with all the data needed to display and sort it for this particular
     * pick type.
     *
     * @param team The team object to fill with data
     * @param statsCursor The response from the database with the data to use for the team
     * @return The filled team
     */
    @Override
    protected Team setupTeam(Team team, Cursor statsCursor) {
        float averagePoints = 0.0f;
        String defensesFast = "";
        String defensesMedium = "";

        if (statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES) > -1 && statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES)) > 0) {

            int autoPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT)) * 10 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_LOW_HIT)) * 5;
            for (int i = 0; i < 9; i++) {
                autoPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[i])) * 2;
                autoPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i])) * 10;
            }

            int teleopPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_HIT)) * 2;
            for (int i = 0; i < 9; i++) {
                teleopPoints += statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i])) * 5;
            }

            int endgamePoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_CHALLENGE)) * 5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_SCALE)) * 15;

            int foulPoints = statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_FOULS)) * -5 +
                    statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TECH_FOULS)) * -5;

            int totalMatches = statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES));
            if (totalMatches != 0) {
                averagePoints = ((float) (autoPoints + teleopPoints + endgamePoints + foulPoints)) / ((float) totalMatches);
            }

            int defenseWeight = 0;
            for (int i = 0; i < 9; i++) {
                float time = statsCursor.getFloat(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_TIME[i]));
                float seen = statsCursor.getFloat(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[i]));
                float notCrossed = statsCursor.getFloat(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[i]));

                if (seen > 0 && seen != notCrossed) {
                    float avgTime = time / (seen - notCrossed);
                    if (avgTime <= 5 && avgTime > 0) {
                        defenseWeight += 5;
                        defensesFast += Constants.Defense_Arrays.DEFENSES_ABREV[i] + ", ";

                    } else if (avgTime > 5 && avgTime < 10) {
                        defenseWeight += 2;
                        defensesMedium += Constants.Defense_Arrays.DEFENSES_ABREV[i] + ", ";
                    }
                }
            }
            if (defensesFast.length() > 2) {
                defensesFast = defensesFast.substring(0, defensesFast.length() - 2);
            }
            if (defensesMedium.length() > 2) {
                defensesMedium = defensesMedium.substring(0, defensesMedium.length() - 2);
            }

            team.setMapElement(Constants.Pick_List.OFFENSIVE_PICKABILITY, new ScoutValue(((int) averagePoints) + defenseWeight));

            String bottomText = String.format("Offense Pickability: %d Avg Points: %.2f\nFast Defense: ", team.getMapElement(Constants.Pick_List.OFFENSIVE_PICKABILITY).getInt(), averagePoints);
            if (Objects.equals(defensesFast, "")) {
                bottomText += "None";
            } else {
                bottomText += defensesFast;
            }
            bottomText += " Medium Defense: ";
            if (Objects.equals(defensesMedium, "")) {
                bottomText += "None";
            } else {
                bottomText += defensesMedium;
            }
            team.setMapElement(Constants.Pick_List.BOTTOM_TEXT, new ScoutValue(bottomText));
        } else {
            team.setMapElement(Constants.Pick_List.OFFENSIVE_PICKABILITY, new ScoutValue(0));
            team.setMapElement(Constants.Pick_List.BOTTOM_TEXT, new ScoutValue("No matches yet"));
        }

        return team;
    }


}
