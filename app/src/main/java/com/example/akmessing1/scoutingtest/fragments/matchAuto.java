package com.example.akmessing1.scoutingtest.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.ScoutValue;
import com.example.akmessing1.scoutingtest.views.CustomScoutView;

import java.util.Map;

public class MatchAuto extends MatchFragment {
    public MatchAuto() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_auto, container, false);
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        return view;
    }
}
