package com.team3824.akmessing1.scoutingapp.list_items.event_list_items;

public class ELI_Shots {

    public int mRank, mTeamNumber, mAutoMade, mAutoTaken, mTeleopMade, mTeleopTaken;
    public float mAutoPercentage, mTeleopPercentage;

    public ELI_Shots(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mAutoMade = 0;
        mAutoTaken = 0;
        mAutoPercentage = 0.0f;
        mTeleopMade = 0;
        mTeleopTaken = 0;
        mTeleopPercentage = 0.0f;
    }
}
