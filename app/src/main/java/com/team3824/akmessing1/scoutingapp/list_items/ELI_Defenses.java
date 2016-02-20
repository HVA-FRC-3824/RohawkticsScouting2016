package com.team3824.akmessing1.scoutingapp.list_items;

public class ELI_Defenses {

    public int mRank, mTeamNumber, totalCrosses;

    public int seens[], crosses[], notCrosses[];
    public float time[];

    public ELI_Defenses(int teamNumber)
    {
        mTeamNumber = teamNumber;
        totalCrosses = 0;
        seens = new int[9];
        crosses = new int[9];
        notCrosses = new int[9];
        time = new float[9];
        for(int i = 0; i < 9; i++)
        {
            seens[i] = 0;
            crosses[i] = 0;
            notCrosses[i] = 0;
            time[i] = -1;
        }
    }

}
