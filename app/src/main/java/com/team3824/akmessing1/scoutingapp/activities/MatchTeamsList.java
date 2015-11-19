package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;


public class MatchTeamsList extends AppCompatActivity {

    private static final String TAG = "MatchTeamsList";
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_teams_list);

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString("event_id", "");

        CustomHeader header = (CustomHeader)findViewById(R.id.match_teams_list_header);
        header.removeHome();

        final ScheduleDB scheduleDB = new ScheduleDB(this, eventID);
        displayListView(scheduleDB, sharedPreferences);
        scheduleDB.close();
    }

    // Setup list view with the schedule
    private void displayListView(ScheduleDB scheduleDB, SharedPreferences sharedPreferences)
    {
        Cursor cursor = scheduleDB.getSchedule();
        if(cursor != null)
        {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.match_teams_list);

            cursor.moveToFirst();
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,4,4,4);

            // Add buttons
            do{
                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int matchNumber = cursor.getInt(0);
                button.setText("Match " + matchNumber);
                button.setBackgroundColor(Color.GRAY);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatchTeamsList.this, MatchView.class);
                        intent.putExtra("match_number",matchNumber);
                        startActivity(intent);
                    }
                });
                linearLayout.addView(button);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }
    }
}
