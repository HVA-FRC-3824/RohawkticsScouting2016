package com.team3824.akmessing1.scoutingapp.fragments.PickLists;

import android.database.Cursor;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.list_items.Team;

/**
 * @author Andrew Messing
 * @version 1
 * 
 * Fragment that is the Defensive Pick List. Currently sorting is based on qualitative metrics.
 */
public class DefensivePick extends ScoutPick {

    public DefensivePick()
    {
        setPickType("defensive");
    }

    /**
     * Fills an individual team with all the data needed to display and sort it for this particular
     * pick type.
     *
     * @param team The team object to fill with data
     * @param statsCursor The response from the database with the data to use for the team
     * @return The filled team
     */
    //TODO: Fix with new qualitative rankings
    @Override
    protected Team setupTeam(Team team, Cursor statsCursor) {
        if (statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES) > -1 && statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES)) > 0) {
            int numberOfTeams = statsCursor.getCount();

            String bottomText = "";
            String driverRank = " ";//statsCursor.getString(statsCursor.getColumnIndex(Constants.DRIVER_ABILITY_RANKING));
            bottomText += "Driver Rank: " + driverRank;
            if (driverRank.charAt(0) == 'T') {
                driverRank = driverRank.substring(1);
            }

            String defenseRank = " ";//statsCursor.getString(statsCursor.getColumnIndex(Constants.DEFENSE_ABILITY_RANKING));
            bottomText += " Defender Rank: " + defenseRank;
            if (defenseRank.charAt(0) == 'T') {
                defenseRank = defenseRank.substring(1);
            }

            team.setMapElement(Constants.Pick_List.DEFENSIVE_PICKABILITY, new ScoutValue((numberOfTeams - Integer.parseInt(driverRank) + 1) + (numberOfTeams - Integer.parseInt(defenseRank) + 1)));
            team.setMapElement(Constants.Pick_List.BOTTOM_TEXT,new ScoutValue(bottomText));
        }
        else
        {
            team.setMapElement(Constants.Pick_List.DEFENSIVE_PICKABILITY, new ScoutValue(0));
            team.setMapElement(Constants.Pick_List.BOTTOM_TEXT,new ScoutValue("No matches yet"));
        }
        return team;
    }
}
