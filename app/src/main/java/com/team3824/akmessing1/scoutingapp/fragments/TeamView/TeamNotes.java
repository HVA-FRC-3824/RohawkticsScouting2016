package com.team3824.akmessing1.scoutingapp.fragments.TeamView;

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

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

/**
 * The fragment for the Team View which displays notes created by match scouts, super scouts, and the
 * drive team.
 *
 * @author Andrew Messing
 * @version
 */
public class TeamNotes extends Fragment {
    public TeamNotes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_notes, container, false);

        Bundle args = getArguments();
        int teamNumber = args.getInt(Constants.Intent_Extras.TEAM_NUMBER, -1);
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        MatchScoutDB matchScoutDB = new MatchScoutDB(activity, eventID);
        String notes = "";
        Cursor cursor = matchScoutDB.getTeamInfo(teamNumber);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getColumnIndex(Constants.Post_Match_Inputs.POST_NOTES) != -1 && cursor.getString(cursor.getColumnIndex(Constants.Post_Match_Inputs.POST_NOTES)) != null && !cursor.getString(cursor.getColumnIndex(Constants.Post_Match_Inputs.POST_NOTES)).equals("")) {
                    notes += String.format("Match %d: %s\n", cursor.getInt(cursor.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER)), cursor.getString(cursor.getColumnIndex(Constants.Post_Match_Inputs.POST_NOTES)));
                }
            }
        }
        if (notes.equals("")) {
            notes = "None";
        }
        ((TextView) view.findViewById(R.id.notes)).setText(notes);
        notes = "";
        SuperScoutDB superScoutDB = new SuperScoutDB(activity, eventID);
        cursor = superScoutDB.getTeamNotes(teamNumber);
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (cursor.getColumnIndex(Constants.Super_Inputs.SUPER_NOTES) != -1 && cursor.getString(cursor.getColumnIndex(Constants.Super_Inputs.SUPER_NOTES)) != null && !cursor.getString(cursor.getColumnIndex(Constants.Super_Inputs.SUPER_NOTES)).equals("")) {
                    notes += String.format("Match %d: %s\n", cursor.getInt(cursor.getColumnIndex(SuperScoutDB.KEY_MATCH_NUMBER)), cursor.getString(cursor.getColumnIndex(Constants.Super_Inputs.SUPER_NOTES)));
                }
            }
        }
        if (notes.equals("")) {
            notes = "None";
        }
        ((TextView) view.findViewById(R.id.super_notes)).setText(notes);
        DriveTeamFeedbackDB driveTeamFeedbackDB = new DriveTeamFeedbackDB(activity, eventID);
        notes = driveTeamFeedbackDB.getComments(teamNumber);
        if (notes.equals("")) {
            notes = "None";
        }
        ((TextView) view.findViewById(R.id.driveteam_comments)).setText(notes);

        return view;
    }
}
