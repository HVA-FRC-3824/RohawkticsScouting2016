package com.team3824.akmessing1.scoutingapp.fragments;

import android.database.Cursor;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.list_items.Team;

public class DefensivePick extends ScoutPick{
    public DefensivePick()
    {
        setPickType("defensive");
    }

    @Override
    protected Team setupTeam(Team team, Cursor statsCursor) {
        if (statsCursor.getColumnIndex(Constants.TOTAL_MATCHES) > -1 && statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_MATCHES)) > 0) {
            int numberOfTeams = statsCursor.getCount();

            String bottomText = "";
            String driverRank = statsCursor.getString(statsCursor.getColumnIndex(Constants.DRIVE_ABILITY_RANKING));
            bottomText += "Driver Rank: " + driverRank;
            if (driverRank.charAt(0) == 'T') {
                driverRank = driverRank.substring(1);
            }

            String defenseRank = statsCursor.getString(statsCursor.getColumnIndex(Constants.DEFENSE_ABILITY_RANKING));
            bottomText += " Defender Rank: " + defenseRank;
            if (defenseRank.charAt(0) == 'T') {
                defenseRank = defenseRank.substring(1);
            }

            team.setMapElement(Constants.DEFENSIVE_PICKABILITY, new ScoutValue((numberOfTeams - Integer.parseInt(driverRank) + 1) + (numberOfTeams - Integer.parseInt(defenseRank) + 1)));
            team.setMapElement(Constants.BOTTOM_TEXT,new ScoutValue(bottomText));
        }
        else
        {
            team.setMapElement(Constants.DEFENSIVE_PICKABILITY, new ScoutValue(0));
            team.setMapElement(Constants.BOTTOM_TEXT,new ScoutValue("No matches yet"));
        }
        return team;
    }
}
