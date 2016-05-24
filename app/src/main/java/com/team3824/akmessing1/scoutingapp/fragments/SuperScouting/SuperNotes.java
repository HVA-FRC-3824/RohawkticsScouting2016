package com.team3824.akmessing1.scoutingapp.fragments.SuperScouting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

public class SuperNotes extends ScoutFragment {

    public SuperNotes(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_notes, container, false);

        // restore all values from the database
        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        Utilities.setupUI(getActivity(), view);

        return view;
    }
}
