package com.team3824.akmessing1.scoutingapp.event_list_items;

public class ELI_Auto {
    public int mRank, mTeamNumber;
    public ELI_Defenses mDefenses;
    public ELI_Shots mHigh;
    public ELI_Shots mLow;

    public ELI_Auto(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mDefenses = new ELI_Defenses(teamNumber);
        mHigh = new ELI_Shots(teamNumber);
        mLow = new ELI_Shots(teamNumber);
    }
}
