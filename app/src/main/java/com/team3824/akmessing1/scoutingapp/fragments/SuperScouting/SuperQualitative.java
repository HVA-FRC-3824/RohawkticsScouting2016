package com.team3824.akmessing1.scoutingapp.fragments.SuperScouting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.views.CustomOrdinalRank;

import java.util.ArrayList;

public class SuperQualitative extends ScoutFragment {

    private ArrayList<Integer> teams;

    public SuperQualitative() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_qualitative, container, false);
        setupTeamLists((ViewGroup)view);
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        Utilities.setupUI(getActivity(), view);
        return view;
    }

    private void setupTeamLists(ViewGroup vg)
    {
        int childCount = vg.getChildCount();
        for(int i = 0; i < childCount; i++)
        {
            View view = vg.getChildAt(i);
            if(view instanceof CustomOrdinalRank)
            {
                ((CustomOrdinalRank)view).setArray(teams);
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
