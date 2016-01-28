package com.team3824.akmessing1.scoutingapp.event_list_items;

public class ELI_Fouls {
    public int mRank, mTeamNumber, mFouls, mTechFouls, mYellowCards, mRedCards;

    public ELI_Fouls(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mFouls = 0;
        mTechFouls = 0;
        mYellowCards = 0;
        mRedCards = 0;
    }

}
