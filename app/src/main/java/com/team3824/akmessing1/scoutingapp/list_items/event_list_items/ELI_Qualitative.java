package com.team3824.akmessing1.scoutingapp.list_items.event_list_items;

public class ELI_Qualitative {

    public int mTeamNumber;
    public String[] mRank;

    public ELI_Qualitative(int teamNumber, int numCol)
    {
        mTeamNumber = teamNumber;
        mRank = new String[numCol];
    }
}
