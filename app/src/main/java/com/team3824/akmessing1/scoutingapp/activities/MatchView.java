package com.team3824.akmessing1.scoutingapp.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.fragments.TeamViewFragment;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

public class MatchView extends AppCompatActivity {

    private static final String TAG = "MatchView";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        Bundle extras = getIntent().getExtras();
        final int matchNumber = extras.getInt("match_number");

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString("event_id", "");

        CustomHeader header = (CustomHeader)findViewById(R.id.match_view_header);
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchView.this, MatchList.class);
                intent.putExtra("nextPage","match_viewing");
                startActivity(intent);
            }
        });

        TextView tv = (TextView)findViewById(R.id.match_num);
        tv.setText("Match Number: " + matchNumber);

        ScheduleDB scheduleDB = new ScheduleDB(this,eventId);
        Cursor cursor = scheduleDB.getMatch(matchNumber);
        FragmentManager fm = getFragmentManager();
        TeamViewFragment blue1 = (TeamViewFragment) fm.findFragmentById(R.id.blue1);
        blue1.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)));
        TeamViewFragment blue2 = (TeamViewFragment) fm.findFragmentById(R.id.blue2);
        blue2.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)));
        TeamViewFragment blue3 = (TeamViewFragment) fm.findFragmentById(R.id.blue3);
        blue3.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)));
        TeamViewFragment red1 = (TeamViewFragment) fm.findFragmentById(R.id.red1);
        red1.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)));
        TeamViewFragment red2 = (TeamViewFragment) fm.findFragmentById(R.id.red2);
        red2.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)));
        TeamViewFragment red3 = (TeamViewFragment) fm.findFragmentById(R.id.red3);
        red3.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)));

        // First match doesn't need a previous button
        Button previous = (Button)findViewById(R.id.previous_match);
        if(matchNumber == 1)
        {
            previous.setVisibility(View.INVISIBLE);
        }
        else
        {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go to the next match
                    Intent intent = new Intent(MatchView.this, MatchView.class);
                    intent.putExtra("match_number", matchNumber - 1);
                    startActivity(intent);
                }
            });
        }

        Cursor nextCursor = scheduleDB.getMatch(matchNumber + 1);
        //Last match doesn't need a next button
        Button next = (Button)findViewById(R.id.next_match);
        if(nextCursor == null)
        {
            next.setVisibility(View.INVISIBLE);
        }
        else
        {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Go to the next match
                    Intent intent = new Intent(MatchView.this, MatchView.class);
                    intent.putExtra("match_number", matchNumber + 1);
                    startActivity(intent);
                }
            });
        }
    }
}
