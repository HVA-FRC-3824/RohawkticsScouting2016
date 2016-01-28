package com.team3824.akmessing1.scoutingapp.event_list_items;

public class ELI_IndividualDefense {

    public int mRank, mTeamNumber, mAutoReach, mAutoCross, mSeen, mTeleopCross;
    public float mAvg;

    public ELI_IndividualDefense(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mAutoReach = 0;
        mAutoCross = 0;
        mSeen = 0;
        mTeleopCross = 0;
        mAvg = 0.0f;
    }

}
