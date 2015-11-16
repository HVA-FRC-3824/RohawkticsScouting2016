package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingapp.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.StatsDB;

public class TeamVisuals extends Fragment {
    public TeamVisuals()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_visuals, container, false);
        Bundle args = getArguments();
        int teamNumber = args.getInt("teamNumber", -1);
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("eventID", "");
        PitScoutDB pitScoutDB = new PitScoutDB(activity,eventID);
        MatchScoutDB matchScoutDB = new MatchScoutDB(activity,eventID);
        StatsDB statsDB = new StatsDB(activity,eventID);

        return view;
    }
}
