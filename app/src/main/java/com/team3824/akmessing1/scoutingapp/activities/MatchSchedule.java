package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;


public class MatchSchedule extends AppCompatActivity {
    private static final String TAG = "MatchSchedule";

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_schedule);

        CustomHeader header = (CustomHeader)findViewById(R.id.match_schedule_header);
        header.removeHome();
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchSchedule.this, StartScreen.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventID);
        displayListView(scheduleDB);
        Button button = (Button)findViewById(R.id.edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchSchedule.this, ScheduleBuilder.class);
                startActivity(intent);
            }
        });
        scheduleDB.close();
    }

    // set up listview
    private void displayListView(ScheduleDB scheduleDB)
    {
        ListView listview = (ListView)findViewById(R.id.schedule_list);
        Cursor cursor = scheduleDB.getSchedule();
        String[] columns = new String[]{ScheduleDB.KEY_MATCH_NUMBER,ScheduleDB.KEY_BLUE1,ScheduleDB.KEY_BLUE2,ScheduleDB.KEY_BLUE3,ScheduleDB.KEY_RED1,ScheduleDB.KEY_RED2,ScheduleDB.KEY_RED3};
        int[] to = new int[]{R.id.schedule_matchNum,R.id.schedule_blue1,R.id.schedule_blue2,R.id.schedule_blue3,R.id.schedule_red1,R.id.schedule_red2,R.id.schedule_red3};
        dataAdapter = new SimpleCursorAdapter(this, R.layout.list_item_schedule_match, cursor, columns, to, 0);
        listview.setAdapter(dataAdapter);
    }
}
