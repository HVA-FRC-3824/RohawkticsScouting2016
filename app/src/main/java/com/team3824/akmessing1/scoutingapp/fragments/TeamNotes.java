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

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

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
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
        MatchScoutDB matchScoutDB = new MatchScoutDB(activity,eventID);
        String notes = "";
        Cursor cursor = matchScoutDB.getTeamInfo(teamNumber);
        while(!cursor.isAfterLast())
        {
            if(cursor.getColumnIndex(Constants.POST_NOTES) != -1 && !cursor.getString(cursor.getColumnIndex(Constants.POST_NOTES)).equals("")) {
                notes += "Match "+ String.valueOf(cursor.getInt(cursor.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER))) + ": ";
                notes += cursor.getString(cursor.getColumnIndex(Constants.POST_NOTES));
                notes += "\n";
            }
            cursor.moveToNext();
        }
        if(notes.equals(""))
        {
            notes = "None";
        }
        TextView textView = (TextView)view.findViewById(R.id.notes);
        textView.setText(notes);
        notes = "";
        SuperScoutDB superScoutDB = new SuperScoutDB(activity,eventID);
        cursor = superScoutDB.getTeamNotes(teamNumber);
        if(cursor != null) {
            while (!cursor.isAfterLast()) {
                if (cursor.getColumnIndex(Constants.SUPER_NOTES) != -1 && !cursor.getString(cursor.getColumnIndex(Constants.SUPER_NOTES)).equals("")) {
                    notes += "Match " + String.valueOf(cursor.getInt(cursor.getColumnIndex(SuperScoutDB.KEY_MATCH_NUMBER))) + ": ";
                    notes += cursor.getString(cursor.getColumnIndex(Constants.SUPER_NOTES));
                    notes += "\n";
                }
                cursor.moveToNext();
            }
        }
        if(notes.equals(""))
        {
            notes = "None";
        }
        textView = (TextView)view.findViewById(R.id.super_notes);
        textView.setText(notes);
        return view;
    }
}
