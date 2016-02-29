package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.activities.DriveTeamFeedback;
import com.team3824.akmessing1.scoutingapp.activities.MatchView;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import java.util.ArrayList;

public class TeamSchedule extends Fragment{

    public TeamSchedule() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_schedule, container, false);

        Bundle args = getArguments();
        int teamNumber = args.getInt(Constants.TEAM_NUMBER, -1);

        final Context context = getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        ScheduleDB scheduleDB = new ScheduleDB(context, eventId);

        Cursor cursor = scheduleDB.getSchedule();
        final ArrayList<Integer> matches = new ArrayList<>();
        while(!cursor.isAfterLast())
        {
            if(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)) == teamNumber ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)) == teamNumber ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)) == teamNumber ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)) == teamNumber ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)) == teamNumber ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)) == teamNumber)
            {
                matches.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_MATCH_NUMBER)));
            }
            cursor.moveToNext();
        }

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.schedule);
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4, 4, 4, 4);
        for(int i = 0; i < matches.size(); i++)
        {
            Button button = new Button(context);
            button.setLayoutParams(lp);
            final int matchNumber = matches.get(i);
            button.setText("Match " + matchNumber);
            button.setBackgroundColor(Color.BLUE);
            final int iter = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MatchView.class);
                    intent.putExtra(Constants.MATCH_NUMBER, matchNumber);
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }


        return view;
    }
}
