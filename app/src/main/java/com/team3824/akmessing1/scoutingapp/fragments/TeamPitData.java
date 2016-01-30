package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;

import java.util.Map;

public class TeamPitData extends Fragment{
    public TeamPitData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_pit_data, container, false);

        Bundle args = getArguments();
        int teamNumber = args.getInt("teamNumber", -1);
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");
        PitScoutDB pitScoutDB = new PitScoutDB(activity,eventID);
        Map<String, ScoutValue> pitMap = pitScoutDB.getTeamMap(teamNumber);

        TextView textView = (TextView)view.findViewById(R.id.pit_robot_width);
        if(pitMap.containsKey(Constants.PIT_ROBOT_WIDTH))
        {
            textView.setText(String.valueOf(pitMap.get(Constants.PIT_ROBOT_WIDTH).getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.pit_robot_length);
        if(pitMap.containsKey(Constants.PIT_ROBOT_LENGTH))
        {
            textView.setText(String.valueOf(pitMap.get(Constants.PIT_ROBOT_LENGTH).getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.pit_robot_height);
        if(pitMap.containsKey(Constants.PIT_ROBOT_HEIGHT))
        {
            textView.setText(String.valueOf(pitMap.get(Constants.PIT_ROBOT_HEIGHT).getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.pit_robot_weight);
        if(pitMap.containsKey(Constants.PIT_ROBOT_WEIGHT))
        {
            textView.setText(String.valueOf(pitMap.get(Constants.PIT_ROBOT_WEIGHT).getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.pit_loading);
        if(pitMap.containsKey(Constants.PIT_LOADING))
        {
            textView.setText(pitMap.get(Constants.PIT_LOADING).getString());
        }

        textView = (TextView)view.findViewById(R.id.pit_number_cims);
        if(pitMap.containsKey(Constants.PIT_NUMBER_OF_CIMS))
        {
            textView.setText(String.valueOf(pitMap.get(Constants.PIT_NUMBER_OF_CIMS).getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.pit_drivetrain);
        if(pitMap.containsKey(Constants.PIT_DRIVETRAIN))
        {
            textView.setText(pitMap.get(Constants.PIT_DRIVETRAIN).getString());
        }

        textView = (TextView)view.findViewById(R.id.pit_notes);
        if(pitMap.containsKey(Constants.PIT_NOTES))
        {
            textView.setText(pitMap.get(Constants.PIT_NOTES).getString());
        }

        return view;
    }

}
