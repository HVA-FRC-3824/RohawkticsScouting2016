package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.PendingIntents;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.services.AggregateService;
import com.team3824.akmessing1.scoutingapp.services.SyncService;

import org.json.JSONArray;

import java.util.Arrays;

public class Settings extends AppCompatActivity {

    private String TAG = "Settings";

    // Populate the settings fields with their respective values
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = getSharedPreferences( Constants.APP_DATA, Context.MODE_PRIVATE );

        final Spinner colorSelector = (Spinner)findViewById(R.id.colorSelector);
        String[] colors = new String[]{Constants.BLUE, Constants.RED};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colors);
        colorSelector.setAdapter(adapter1);
        colorSelector.setSelection(Arrays.asList(colors).indexOf(sharedPref.getString(Constants.ALLIANCE_COLOR, Constants.BLUE)));


        final Spinner numSelector = (Spinner)findViewById(R.id.numSelector);
        String[] numbers = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
        numSelector.setAdapter(adapter2);
        numSelector.setSelection(Arrays.asList(numbers).indexOf(Integer.toString(sharedPref.getInt(Constants.ALLIANCE_NUMBER, 1))));


        Spinner typeSelector = (Spinner)findViewById(R.id.typeSelector);
        String[] types = new String[]{Constants.MATCH_SCOUT, Constants.PIT_SCOUT, Constants.SUPER_SCOUT, Constants.DRIVE_TEAM, Constants.STRATEGY, Constants.SERVER, Constants.ADMIN};
        ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        typeSelector.setAdapter(adapter0);
        typeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (!selected.equals(Constants.MATCH_SCOUT) && !selected.equals(Constants.ADMIN)) {
                    findViewById(R.id.textView3).setVisibility(View.GONE);
                    findViewById(R.id.textView2).setVisibility(View.GONE);
                    colorSelector.setVisibility(View.GONE);
                    numSelector.setVisibility(View.GONE);
                } else {
                    findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                    findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                    colorSelector.setVisibility(View.VISIBLE);
                    numSelector.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        typeSelector.setSelection(Arrays.asList(types).indexOf(sharedPref.getString(Constants.USER_TYPE, Constants.MATCH_SCOUT)));
        if(!sharedPref.getString(Constants.USER_TYPE,"").equals(""))
        {
            Button homeButton = (Button)findViewById(R.id.homeButton);
            homeButton.setVisibility(View.VISIBLE);
        }
        EditText eventID = (EditText)findViewById(R.id.eventID);
        eventID.setText(sharedPref.getString(Constants.EVENT_ID,""));

        Utilities.setupUI(this, findViewById(android.R.id.content));
    }

    // back button goes to the start screen
    public void home(View view)
    {
        Intent intent = new Intent(this,StartScreen.class);
        startActivity(intent);
    }
    
    // Save the current settings to shared preferences
    public void save_settings(View view)
    {
        Spinner typeSelector = (Spinner)findViewById(R.id.typeSelector);
        Spinner colorSelector = (Spinner)findViewById(R.id.colorSelector);
        Spinner numSelector = (Spinner)findViewById(R.id.numSelector);
        EditText eventID = (EditText)findViewById(R.id.eventID);

        SharedPreferences.Editor prefEditor = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE ).edit();

        String eventId = String.valueOf(eventID.getText());
        if(!eventId.equals("")) {
            prefEditor.putString(Constants.EVENT_ID, String.valueOf(eventID.getText()));
            String type = String.valueOf(typeSelector.getSelectedItem());
            prefEditor.putString(Constants.USER_TYPE, type);
            if (String.valueOf(typeSelector.getSelectedItem()).equals(Constants.MATCH_SCOUT) || String.valueOf(typeSelector.getSelectedItem()).equals(Constants.ADMIN)) {
                prefEditor.putString(Constants.ALLIANCE_COLOR, String.valueOf(colorSelector.getSelectedItem()));
                prefEditor.putInt(Constants.ALLIANCE_NUMBER, Integer.parseInt(String.valueOf(numSelector.getSelectedItem())));
            }
            prefEditor.commit();
            Button homeButton = (Button)findViewById(R.id.homeButton);
            homeButton.setVisibility(View.VISIBLE);

            final StatsDB statsDB = new StatsDB(this,eventId);
            final PitScoutDB pitScoutDB = new PitScoutDB(this,eventId);

            // If there are no matches in the database pull from schedule the blue alliance
            if (pitScoutDB.getNumTeams() == 0) {
                Log.d(TAG, "Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventId + "/teams?X-TBA-App-Id=amessing:scoutingTest:v3";
                Log.d(TAG, "url: " + url);

                //Request schedule
                JsonRequest jsonReq = new Utilities.JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Team List received");
                        pitScoutDB.createTeamList(response);
                        statsDB.createTeamList(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Allow team list building
                        Log.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(Settings.this, "Error: receiving team list", Toast.LENGTH_LONG);
                    }
                });
                queue.add(jsonReq);
            }

            final ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
            // If schedule is empty then try to get one from the blue alliance
            if (scheduleDB.getNumMatches() == 0) {
                Log.d(TAG, "Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventId + "/matches?X-TBA-App-Id=amessing:scoutingTest:v3";
                Log.d(TAG, "url: " + url);
                JsonRequest jsonReq = new Utilities.JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Schedule received");

                        scheduleDB.createSchedule(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Schedule builder
                        Log.d(TAG, "Error: " + error.getMessage());
                        Toast.makeText(Settings.this, "Error: receiving schedule", Toast.LENGTH_LONG);
                    }
                });
                queue.add(jsonReq);

            }

            if(type.equals(Constants.SERVER) || type.equals(Constants.ADMIN)) {
                if(PendingIntents.aggregatePIntent == null) {
                    Log.d(TAG,"Creating Aggregate Service");
                    Intent intent = new Intent(this, AggregateService.class);
                    intent.putExtra(Constants.UPDATE, false);
                    startService(intent);
                    PendingIntents.aggregatePIntent = PendingIntent.getService(this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    // Run every 30 minutes afterward
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, PendingIntents.aggregatePIntent);
                }
            }
            else
            {
                if (PendingIntents.aggregatePIntent != null)
                {
                    Log.d(TAG,"Removing Aggregate Service");
                    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(PendingIntents.aggregatePIntent);
                    PendingIntents.aggregatePIntent = null;
                }
            }

            if(!type.equals(Constants.SERVER))
            {
                if(PendingIntents.syncPIntent == null) {
                    Log.d(TAG,"Creating Sync Service");
                    Intent intent = new Intent(this, SyncService.class);
                    startService(intent);
                    PendingIntents.syncPIntent = PendingIntent.getService(this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    // Run every 30 minutes afterward
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, PendingIntents.syncPIntent);
                }
            }
            else
            {
                if (PendingIntents.syncPIntent != null)
                {
                    Log.d(TAG,"Removing Sync Service");
                    AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(PendingIntents.syncPIntent);
                    PendingIntents.syncPIntent = null;
                }
            }

            Toast toast =Toast.makeText(this, "Saved", Toast.LENGTH_SHORT);
            toast.show();

        }
        else
        {
            Button homeButton = (Button)findViewById(R.id.homeButton);
            homeButton.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Event ID must be entered", Toast.LENGTH_LONG);
        }
    }
}
