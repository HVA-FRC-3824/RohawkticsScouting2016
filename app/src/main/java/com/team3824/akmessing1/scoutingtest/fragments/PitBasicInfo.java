package com.team3824.akmessing1.scoutingtest.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingtest.R;

/**
 * Created by akmessing1 on 10/22/15.
 */
public class PitBasicInfo extends ScoutFragment{
    public PitBasicInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pit_basic_info, container, false);
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        return view;
    }
}
