package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;

public class TeamNotes extends Fragment {
    public TeamNotes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_notes, container, false);

        Bundle args = getArguments();
        int teamNumber = args.getInt("teamNumber", -1);
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");
        MatchScoutDB matchScoutDB = new MatchScoutDB(activity,eventID);
        String notes = "";
        Cursor cursor = matchScoutDB.getTeamInfo(teamNumber);
        while(!cursor.isAfterLast())
        {
            if(cursor.getColumnIndex("post_notes") != -1) {
                notes += "Match "+ String.valueOf(cursor.getInt(cursor.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER))) + ": ";
                notes += cursor.getString(cursor.getColumnIndex("post_notes"));
                notes += "\n";
            }
            cursor.moveToNext();
        }
        TextView textView = (TextView)view.findViewById(R.id.notes);
        textView.setText(notes);
        return view;
    }
}
