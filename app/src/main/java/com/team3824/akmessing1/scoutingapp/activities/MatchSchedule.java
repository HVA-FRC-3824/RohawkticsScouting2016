package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.MatchScheduleAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.list_items.Match;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;

/**
 * Displays the match schedule for the current event with markers for teams that we will play with,
 * against, both in future matches as well as our matches.
 */
public class MatchSchedule extends Activity {
    private static final String TAG = "MatchSchedule";

    private SimpleCursorAdapter dataAdapter;

    /**
     * Sets up the page
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_schedule);

        CustomHeader header = (CustomHeader) findViewById(R.id.match_schedule_header);
        header.removeHome();
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchSchedule.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        String userType = sharedPreferences.getString(Constants.Settings.USER_TYPE, "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventID);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.key);
        TextView textView = (TextView) viewGroup.findViewById(R.id.schedule_matchNum);
        textView.setText("Us");
        textView.setBackgroundColor(Color.BLUE);
        textView.setTextColor(Color.WHITE);

        textView = (TextView) viewGroup.findViewById(R.id.schedule_blue1);
        textView.setText("Future Opponent");
        textView.setBackgroundColor(Color.RED);
        textView.setTextColor(Color.WHITE);

        textView = (TextView) viewGroup.findViewById(R.id.schedule_blue2);
        textView.setText("Future Ally");
        textView.setBackgroundColor(Color.GREEN);
        textView.setTextColor(Color.BLACK);

        textView = (TextView) viewGroup.findViewById(R.id.schedule_blue3);
        textView.setText("Future Opponent and Ally");
        textView.setBackgroundColor(Color.YELLOW);
        textView.setTextColor(Color.BLACK);

        viewGroup.findViewById(R.id.schedule_red1).setVisibility(View.GONE);
        viewGroup.findViewById(R.id.schedule_red2).setVisibility(View.GONE);
        viewGroup.findViewById(R.id.schedule_red3).setVisibility(View.GONE);

        displayListView(scheduleDB);

        if (userType.equals(Constants.User_Types.ADMIN)) {
            Button button = (Button) findViewById(R.id.edit);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MatchSchedule.this, ScheduleBuilder.class);
                    startActivity(intent);
                }
            });
        }
        scheduleDB.close();
    }

    /**
     * Populates the list view with the match rows
     *
     * @param scheduleDB The schedule database table helper
     */
    private void displayListView(ScheduleDB scheduleDB) {
        ListView listview = (ListView) findViewById(R.id.schedule_list);
        Cursor cursor = scheduleDB.getSchedule();
        ArrayList<Match> matches = new ArrayList<>();
        ArrayList<Integer> opponents = new ArrayList<>();
        ArrayList<Integer> allies = new ArrayList<>();
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Match match = new Match();
            match.matchNumber = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_MATCH_NUMBER));
            match.setTeams(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)),
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)),
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)),
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)),
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)),
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)));
            match.setFutureOpponent(opponents);
            match.setFutureAlly(allies);
            if (match.teams[0] == 3824) {
                allies.add(match.teams[1]);
                allies.add(match.teams[2]);
                opponents.add(match.teams[3]);
                opponents.add(match.teams[4]);
                opponents.add(match.teams[5]);
            } else if (match.teams[1] == 3824) {
                allies.add(match.teams[0]);
                allies.add(match.teams[2]);
                opponents.add(match.teams[3]);
                opponents.add(match.teams[4]);
                opponents.add(match.teams[5]);
            } else if (match.teams[2] == 3824) {
                allies.add(match.teams[0]);
                allies.add(match.teams[1]);
                opponents.add(match.teams[3]);
                opponents.add(match.teams[4]);
                opponents.add(match.teams[5]);
            } else if (match.teams[3] == 3824) {
                allies.add(match.teams[4]);
                allies.add(match.teams[5]);
                opponents.add(match.teams[0]);
                opponents.add(match.teams[1]);
                opponents.add(match.teams[2]);
            } else if (match.teams[4] == 3824) {
                allies.add(match.teams[3]);
                allies.add(match.teams[5]);
                opponents.add(match.teams[0]);
                opponents.add(match.teams[1]);
                opponents.add(match.teams[2]);
            } else if (match.teams[5] == 3824) {
                allies.add(match.teams[3]);
                allies.add(match.teams[4]);
                opponents.add(match.teams[0]);
                opponents.add(match.teams[1]);
                opponents.add(match.teams[2]);
            }
            matches.add(0, match);
        }

        MatchScheduleAdapter matchScheduleAdapter = new MatchScheduleAdapter(this, matches);
        listview.setAdapter(matchScheduleAdapter);
    }
}
