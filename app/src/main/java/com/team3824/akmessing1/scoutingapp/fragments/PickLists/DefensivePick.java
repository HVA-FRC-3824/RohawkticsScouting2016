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
    @Override
    protected Team setupTeam(Team team, Cursor statsCursor) {
        if (statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES) > -1 && statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES)) > 0) {
            int numberOfTeams = statsCursor.getCount();

            String bottomText = "";
            int rankValue = 0;
            for(int i = 0; i < Constants.Qualitative_Rankings.QUALITATIVE_LABELS.length; i++)
            {
                String ranking = statsCursor.getString(statsCursor.getColumnIndex(Constants.Qualitative_Rankings.QUALITATIVE_RANKING[i]));
                bottomText += String.format("%s: %s ", Constants.Qualitative_Rankings.QUALITATIVE_LABELS[i],ranking);
                int ranking_number;
                if(ranking.charAt(0) == 'T')
                {
                    ranking_number = Integer.parseInt(ranking.substring(1));
                }
                else
                {
                    ranking_number = Integer.parseInt(ranking);
                }
                rankValue += (numberOfTeams - ranking_number);
            }

            team.setMapElement(Constants.Pick_List.DEFENSIVE_PICKABILITY, new ScoutValue(rankValue));
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
