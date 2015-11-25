package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;

import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

public class TeamList extends AppCompatActivity {

    private static final String TAG = "TeamList";
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_list);

        CustomHeader header = (CustomHeader)findViewById(R.id.team_list_header);
        header.removeHome();

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString("event_id", "");

        PitScoutDB pitScoutDB = new PitScoutDB(this,eventID);
        displayListView(pitScoutDB);
        pitScoutDB.close();
    }

    // Setup list view with the schedule
    private void displayListView(PitScoutDB pitScoutDB)
    {
        Cursor cursor = pitScoutDB.getAllTeams();

        if(cursor != null && cursor.getCount() > 0)
        {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.team_list);

            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,4,4,4);

            // Add buttons

            do{

                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int teamNumber = cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_TEAM_NUMBER));
                button.setText(String.valueOf(teamNumber));

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TeamList.this, TeamView.class);
                        intent.putExtra("team_number", teamNumber);
                        startActivity(intent);
                    }
                });

                button.setBackgroundColor(Color.GRAY);
                button.setTextColor(Color.WHITE);


                linearLayout.addView(button);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }
    }
}
