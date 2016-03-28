package com.team3824.akmessing1.scoutingapp.list_items.event_list_items;

/**
 * @author Andrew Messing
 */
public class ELI_PositionalShot {

    private final String TAG = "ELI_PositionalShot";

    public int mRank, mTeamNumber, mMade, mTotal;
    public float mPercent, mTime;

    public ELI_PositionalShot(int teamNumber) {
        mRank = 0;
        mTeamNumber = teamNumber;
        mMade = 0;
        mTotal = 0;
        mPercent = 0.0f;
        mTime = 0.0f;
    }

}
