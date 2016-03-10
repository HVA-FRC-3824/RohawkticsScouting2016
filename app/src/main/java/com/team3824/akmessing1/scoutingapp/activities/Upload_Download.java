package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrew Messing
 */
public class Upload_Download extends Activity implements View.OnClickListener {

    private final String TAG = "Upload_Download";

    String eventId;

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    ScheduleDB scheduleDB;
    StatsDB statsDB;
    SyncDB syncDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomHeader header = (CustomHeader) findViewById(R.id.header);
        header.removeHome();

        findViewById(R.id.upload).setOnClickListener(this);
        findViewById(R.id.download).setOnClickListener(this);
        findViewById(R.id.upload_file).setOnClickListener(this);
        findViewById(R.id.download_file).setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        matchScoutDB = new MatchScoutDB(this, eventId);
        pitScoutDB = new PitScoutDB(this, eventId);
        superScoutDB = new SuperScoutDB(this, eventId);
        driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventId);
        scheduleDB = new ScheduleDB(this, eventId);
        statsDB = new StatsDB(this, eventId);
        syncDB = new SyncDB(this, eventId);
    }

    @Override
    public void onClick(View v) {
        JSONObject jsonObject;
        switch (v.getId()) {
            case R.id.upload:
                String matchText = Utilities.CursorToJsonString(matchScoutDB.getAllInfo());
                String pitText = Utilities.CursorToJsonString(pitScoutDB.getAllTeamsInfo());
                String superText = Utilities.CursorToJsonString(superScoutDB.getAllMatches());
                String feedbackText = Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllComments());
                String scheduleText = Utilities.CursorToJsonString(scheduleDB.getSchedule());
                String statsText = Utilities.CursorToJsonString(statsDB.getStats());

                jsonObject = new JSONObject();
                try {
                    jsonObject.put("message","upload_db");
                    jsonObject.put(Constants.Settings.EVENT_ID, eventId);
                    jsonObject.put(Constants.Intent_Extras.MATCH_SCOUTING, matchText);
                    jsonObject.put(Constants.Intent_Extras.PIT_SCOUTING, pitText);
                    jsonObject.put(Constants.Intent_Extras.SUPER_SCOUTING, superText);
                    jsonObject.put(Constants.Intent_Extras.DRIVE_TEAM_FEEDBACK, feedbackText);
                    jsonObject.put("schedule", scheduleText);
                    jsonObject.put("Stats", statsText);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);

                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = "";
                    JsonRequest jsonReq = new Utilities.JsonUTF8Request(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if(response.length() > 0)
                            {
                                try {
                                    String r = response.getString(0);
                                    Log.d(TAG, r);
                                    Toast.makeText(Upload_Download.this, r, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {

                                }

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });

                } catch (JSONException e) {
                }
                break;
            case R.id.download:
                jsonObject = new JSONObject();
                try {
                    jsonObject.put("message","download_db");
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(jsonObject);

                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = "";
                    JsonRequest jsonReq = new Utilities.JsonUTF8Request(Request.Method.POST, url, jsonArray, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if(response.length() > 0)
                            {
                                try {
                                    String r = response.getString(0);
                                    Log.d(TAG, r);
                                    Toast.makeText(Upload_Download.this, r, Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {

                                }

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                } catch (JSONException e) {
                }
                break;
            case R.id.upload_file:

                break;
            case R.id.download_file:

                break;
        }
    }
}
