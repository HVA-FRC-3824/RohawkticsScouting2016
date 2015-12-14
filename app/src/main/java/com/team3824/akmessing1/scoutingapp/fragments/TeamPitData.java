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

        TextView textView = (TextView)view.findViewById(R.id.view_robotWidth);
        if(pitMap.containsKey("robotWidth"))
        {
            textView.setText(String.valueOf(pitMap.get("robotWidth").getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.view_robotLength);
        if(pitMap.containsKey("robotLength"))
        {
            textView.setText(String.valueOf(pitMap.get("robotLength").getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.view_robotHeight);
        if(pitMap.containsKey("robotHeight"))
        {
            textView.setText(String.valueOf(pitMap.get("robotHeight").getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.view_robotWeight);
        if(pitMap.containsKey("robotWeight"))
        {
            textView.setText(String.valueOf(pitMap.get("robotWeight").getFloat()));
        }

        textView = (TextView)view.findViewById(R.id.view_canBurgular);
        if(pitMap.containsKey("canBurgular"))
        {
            if(pitMap.get("canBurgular").getInt() > 0) {
                textView.setText("Yes");
            }
            else
            {
                textView.setText("No");
            }
        }

        textView = (TextView)view.findViewById(R.id.view_cheesecake);
        if(pitMap.containsKey("cheesecake"))
        {
            if(pitMap.get("cheesecake").getInt() > 0) {
                textView.setText("Yes");
            }
            else
            {
                textView.setText("No");
            }
        }

        return view;
    }

}
