package com.team3824.akmessing1.scoutingapp.event_list_items;

public class ELI_Points {
    public int mRank, mTeamNumber, mTotalPoints, mDefensePoints, mHighPoints, mLowPoints, mAutoPoints,
            mTeleopPoints, mEndgamePoints, mFoulPoints;
    public float mAvgPoints;

    public ELI_Points(int teamNumber)
    {
        mTeamNumber = teamNumber;
        mTotalPoints = 0;
        mDefensePoints = 0;
        mHighPoints = 0;
        mLowPoints = 0;
        mAutoPoints = 0;
        mTeleopPoints = 0;
        mEndgamePoints = 0;
        mFoulPoints = 0;
        mAvgPoints = 0.0f;
    }

    public ELI_Points(int teamNumber, int totalPoints, int defensePoints, int highPoints,
                      int lowPoints, int autoPoints, int teleopPoints, int endgamePoints,
                      int foulPoints, float avgPoints)
    {
        mTeamNumber = teamNumber;
        mTotalPoints = totalPoints;
        mDefensePoints = defensePoints;
        mHighPoints = highPoints;
        mLowPoints = lowPoints;
        mAutoPoints = autoPoints;
        mTeleopPoints = teleopPoints;
        mEndgamePoints = endgamePoints;
        mFoulPoints = foulPoints;
        mAvgPoints = avgPoints;
    }

}
