package com.team3824.akmessing1.scoutingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingapp.HideKeyboard;
import com.team3824.akmessing1.scoutingapp.R;

/**
 * Created by tym on 1/16/2016.
 */
public class MatchFouls extends ScoutFragment{
    public MatchFouls() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_fouls, container, false);
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        HideKeyboard.setupUI(getActivity(), view);
        return view;
    }
}

