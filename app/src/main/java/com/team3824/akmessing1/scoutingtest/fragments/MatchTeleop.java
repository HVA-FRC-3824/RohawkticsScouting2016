package com.team3824.akmessing1.scoutingtest.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingtest.R;

public class MatchTeleop extends ScoutFragment {

    public MatchTeleop() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_teleop, container, false);
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        return view;
    }
}