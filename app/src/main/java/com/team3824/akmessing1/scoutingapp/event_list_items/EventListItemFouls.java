package com.team3824.akmessing1.scoutingapp.event_list_items;

public class EventListItemFouls {
    public int mRank, mTeamNumber, mFouls, mTechFouls, mYellowCards, mRedCards;

    public EventListItemFouls(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mFouls = 0;
        mTechFouls = 0;
        mYellowCards = 0;
        mRedCards = 0;
    }

}
