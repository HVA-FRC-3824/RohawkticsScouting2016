package com.team3824.akmessing1.scoutingtest.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.team3824.akmessing1.scoutingtest.JsonUTF8Request;
import com.team3824.akmessing1.scoutingtest.R;
import com.team3824.akmessing1.scoutingtest.ScheduleDB;

import org.json.JSONArray;


public class MatchSchedule extends AppCompatActivity {
    private static final String TAG = "MatchSchedule";

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_schedule);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString("event_id", "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventID);
        displayListView(scheduleDB);
    }

    // home button goes to home page
    public void home(View view)
    {
        Intent intent = new Intent(this, StartScreen.class);
        startActivity(intent);
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
