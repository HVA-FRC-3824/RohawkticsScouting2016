package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

// Activity which displays each match and corresponding team based on the selected alliance color
// and number as button which lead to match scouting
public class MatchList extends AppCompatActivity {

    private static final String TAG = "MatchList";
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        Bundle extras = getIntent().getExtras();
        boolean scouting = extras.getBoolean("scouting");

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString("event_id", "");

        CustomHeader header = (CustomHeader)findViewById(R.id.match_list_header);
        header.removeHome();
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchList.this, StartScreen.class);
                startActivity(intent);
            }
        });

        final ScheduleDB scheduleDB = new ScheduleDB(this, eventID);
        displayListView(scheduleDB, sharedPreferences, scouting);
        scheduleDB.close();
    }

    // Setup list view with the schedule
    private void displayListView(ScheduleDB scheduleDB, SharedPreferences sharedPreferences, final boolean scouting)
    {
        Cursor cursor = scheduleDB.getSchedule();
        if(cursor != null)
        {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.match_list);
            int alliance_number = -1;
            String alliance_color = "";
            if(scouting) {
                alliance_number = sharedPreferences.getInt("alliance_number", 0);
                alliance_color = sharedPreferences.getString("alliance_color", "");
            }
            cursor.moveToFirst();
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,4,4,4);

            // Add buttons
            do{
                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int matchNumber = cursor.getInt(0);
                int tempTeamNumber = -1; // fixes issue with final and possible noninitialization
                if(scouting) {
                    tempTeamNumber = cursor.getInt(cursor.getColumnIndex(alliance_color.toLowerCase() + alliance_number));
                    button.setText("Match " + matchNumber + ": " + tempTeamNumber);
                }
                else {
                    button.setText("Match " + matchNumber);
                }
                final int teamNumber = tempTeamNumber; // fixes issue with final and possible noninitialization
                switch (alliance_color.toLowerCase())
                {
                    case "blue":
                        button.setBackgroundColor(Color.BLUE);
                        break;
                    case "red":
                        button.setBackgroundColor(Color.RED);
                        break;
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = null;

                        if(scouting)
                        {
                            intent = new Intent(MatchList.this, MatchScouting.class);
                            intent.putExtra("team_number", teamNumber);
                        }
                        else
                        {
                            intent = new Intent(MatchList.this, MatchView.class);
                        }
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
