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
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;

/**
 * Activity that displays buttons for each of our matches that lead to the drive team feedback activity
 */
//TODO: Merge with MatchList
public class OurMatchList extends Activity {
    /**
     * Sets up buttons for each of our matches that lead to the drive team feedback activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);

        CustomHeader header = (CustomHeader) findViewById(R.id.match_list_header);
        header.removeHome();
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OurMatchList.this, HomeScreen.class);
                startActivity(intent);
            }
        });

        Cursor cursor = scheduleDB.getSchedule();
        final ArrayList<Integer> matches = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)) == Constants.OUR_TEAM_NUMBER ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)) == Constants.OUR_TEAM_NUMBER ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)) == Constants.OUR_TEAM_NUMBER ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)) == Constants.OUR_TEAM_NUMBER ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)) == Constants.OUR_TEAM_NUMBER ||
                    cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)) == Constants.OUR_TEAM_NUMBER) {
                matches.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_MATCH_NUMBER)));
            }
            cursor.moveToNext();
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.match_list);
        TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4, 4, 4, 4);
        for (int i = 0; i < matches.size(); i++) {
            Button button = new Button(this);
            button.setLayoutParams(lp);
            final int matchNumber = matches.get(i);
            button.setText("Match " + matchNumber);
            button.setBackgroundColor(Color.BLUE);
            final int iter = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OurMatchList.this, DriveTeamFeedback.class);
                    intent.putExtra(Constants.MATCH_NUMBER, matchNumber);
                    startActivity(intent);
                }
            });
            linearLayout.addView(button);
        }
    }
}
