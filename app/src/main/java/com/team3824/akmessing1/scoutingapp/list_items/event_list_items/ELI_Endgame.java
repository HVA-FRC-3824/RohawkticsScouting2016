package com.team3824.akmessing1.scoutingapp.list_items.event_list_items;

public class ELI_Endgame {

    public int mRank, mTeamNumber, mFailedChallenge, mChallenge, mFailedScale, mScale, mTotalMatches;

    public ELI_Endgame(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mFailedChallenge = 0;
        mChallenge = 0;
        mFailedScale = 0;
        mScale = 0;
        mTotalMatches = 0;
    }
}
