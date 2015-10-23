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

        // Check if event ID is set and ask for one if it is not
        if(eventID.equals(""))
        {
            Log.d(TAG, "No Event ID");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final View view = inflater.inflate(R.layout.dialog_set_event_id, null);
            builder.setView(view);

            // Save button saves new event id
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            TextView textView = (TextView) view.findViewById(R.id.set_event_id);
                            String eventId = String.valueOf(textView.getText());
                            Log.d(TAG,"Event ID: "+eventId);
                            if (!eventId.equals("")) {
                                SharedPreferences.Editor prefEditor = getSharedPreferences("appData", Context.MODE_PRIVATE).edit();
                                prefEditor.putString("event_id", eventId);
                                prefEditor.commit();
                            }
                            Intent intent = new Intent(MatchSchedule.this,MatchSchedule.class);
                            startActivity(intent);
                        }
                    });

            // Back button goes back to the start screen
            builder.setNeutralButton("Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MatchSchedule.this, StartScreen.class);
                    startActivity(intent);
                }
            });
            builder.show();
            return;
        }
        else {
            Log.d(TAG, "Event ID found");
            final ScheduleDB scheduleDB = new ScheduleDB(this, eventID);

            // If schedule is empty then try to get one from the blue alliance
            if (scheduleDB.getNumMatches() == 0) {
                Log.d(TAG, "Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/matches?X-TBA-App-Id=amessing:scoutingTest:v2";
                Log.d(TAG, "url: " + url);
                JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Schedule received");
                        scheduleDB.createSchedule(response);
                        displayListView(scheduleDB);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Schedule builder
                        Log.d(TAG, "Error: " + error.getMessage());

                    }
                });

                queue.add(jsonReq);

            } else {
                Log.d(TAG, "Table not empty");

                displayListView(scheduleDB);
            }
        }
    }

    // back button goes to home page
    public void back(View view)
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
