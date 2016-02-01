package com.team3824.akmessing1.scoutingapp.event_list_items;

public class ELI_Defenses {

    public int mRank, mTeamNumber, totalCrosses, sPortcullis, cPortcullis, sChevalDeFrise, cChevalDeFrise,
        sMoat, cMoat, sRamparts, cRamparts, sDrawbridge, cDrawbridge, sSallyPort, cSallyPort,
            sRoughTerrain, cRoughTerrain, sRockWall, cRockWall, sLowBar,cLowBar;

    public float tPortcullis, tChevalDeFrise, tMoat, tRamparts, tDrawbridge, tSallyPort,
            tRoughTerrain, tRockWall, tLowBar;

    public ELI_Defenses(int teamNumber)
    {
        mTeamNumber = teamNumber;
        totalCrosses = 0;
        sPortcullis = 0;
        cPortcullis = 0;
        tPortcullis = -1;
        sChevalDeFrise = 0;
        cChevalDeFrise = 0;
        tChevalDeFrise = -1;
        sMoat = 0;
        cMoat = 0;
        tMoat = -1;
        sRamparts = 0;
        cRamparts = 0;
        tRamparts = -1;
        sDrawbridge = 0;
        cDrawbridge = 0;
        tDrawbridge = -1;
        sSallyPort = 0;
        cSallyPort = 0;
        tSallyPort = -1;
        sRoughTerrain = 0;
        cRoughTerrain = 0;
        tRoughTerrain = -1;
        sRockWall = 0;
        cRockWall = 0;
        tRockWall = -1;
        sLowBar = 0;
        cLowBar = 0;
        tLowBar = -1;
    }

}
