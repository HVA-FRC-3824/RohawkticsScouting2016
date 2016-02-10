package com.team3824.akmessing1.scoutingapp.list_items;


import java.util.ArrayList;

public class Match {
    public int matchNumber;
    public int teams[];
    public boolean futureOpponent[];
    public boolean futureAlly[];

    public Match()
    {
        teams = new int[6];
        futureOpponent = new boolean[6];
        futureAlly = new boolean[6];
    }

    public void setTeams(int blue1, int blue2, int blue3, int red1, int red2, int red3)
    {
        teams[0] = blue1;
        teams[1] = blue2;
        teams[2] = blue3;
        teams[3] = red1;
        teams[4] = red2;
        teams[5] = red3;
    }

    public void setFutureOpponent(ArrayList<Integer> opponents)
    {
        for(int i = 0; i < 6; i++)
            futureOpponent[i] = opponents.contains(teams[i]);
    }

    public void setFutureAlly(ArrayList<Integer> allies)
    {
        for(int i = 0; i < 6; i++)
        futureAlly[i] = allies.contains(teams[i]);
    }
}
