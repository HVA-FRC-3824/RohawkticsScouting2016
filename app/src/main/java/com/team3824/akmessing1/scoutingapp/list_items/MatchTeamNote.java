package com.team3824.akmessing1.scoutingapp.list_items;

/**
 * @author Andrew Messing
 * @version %I%
 */
public class MatchTeamNote {

    private final String TAG = "MatchTeamNote";

    int matchNumber, teamNumber;
    String note;

    public MatchTeamNote(int mn, int tn, String n)
    {
        matchNumber = mn;
        teamNumber = tn;
        note = n;
    }

    public int getMatchNumber()
    {
        return matchNumber;
    }

    public int getTeamNumber()
    {
        return teamNumber;
    }

    public String getNote()
    {
        return note;
    }

}
