package com.team3824.akmessing1.scoutingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.views.CustomCardinalRank6;

import java.util.ArrayList;

public class SuperBasicInfo extends ScoutFragment {

    private ArrayList<Integer> teams;

    public SuperBasicInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_basic_info, container, false);
        setupTeamLists((ViewGroup)view);
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        Utilities.setupUI(getActivity(), view);
        return view;
    }

    public void setupTeamLists(ViewGroup vg)
    {
        int childCount = vg.getChildCount();
        for(int i = 0; i < childCount; i++)
        {
            View view = vg.getChildAt(i);
            if(view instanceof CustomCardinalRank6)
            {
                ((CustomCardinalRank6)view).setArray(teams);
            }
            else if(view instanceof ViewGroup)
            {
                setupTeamLists((ViewGroup)view);
            }
        }
    }

    public void setTeams(ArrayList<Integer> teams)
    {
        this.teams = teams;
    }
}
