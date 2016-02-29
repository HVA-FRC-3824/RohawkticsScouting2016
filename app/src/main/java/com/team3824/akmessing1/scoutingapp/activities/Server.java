package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothHandler;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothSync;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.utilities.AggregateStats;
import com.team3824.akmessing1.scoutingapp.utilities.CircularBuffer;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Server extends AppCompatActivity {

    private String TAG = "Server";
    TextView logView;
    CircularBuffer circularBuffer;

    BluetoothSync bluetoothSync;
    SyncHandler handler;


    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    StatsDB statsDB;
    ScheduleDB scheduleDB;
    SyncDB syncDB;

    private class SyncHandler extends BluetoothHandler
    {
        @Override
        public void displayText(String text)
        {
            Log.d(TAG,text);
            circularBuffer.insert(text);
            logView.setText(circularBuffer.toString());
        }

        @Override
        public void receivedData(String text) {
            switch (text.charAt(0))
            {
                case Constants.MATCH_HEADER:
                    try {
                        JSONArray jsonArray = new JSONArray(text.substring(1));
                        Set<Integer> teams = new HashSet<>();
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            teams.add(jsonObject.getInt(MatchScoutDB.KEY_TEAM_NUMBER));
                        }
                        AggregateStats.updateTeams(teams,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                    } catch (JSONException e) {
                        displayText("Aggregate Error...");
                    }
                    break;
                case Constants.SUPER_HEADER:
                    try {
                        JSONArray jsonArray = new JSONArray(text.substring(1));
                        Set<Integer> matches = new HashSet<>();
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            matches.add(jsonObject.getInt(SuperScoutDB.KEY_MATCH_NUMBER));
                        }
                        AggregateStats.updateSuper(matches,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                    } catch (JSONException e) {
                        displayText("Aggregate Error...");
                    }
                    break;
            }
        }
    }

    ArrayList<String> getImageFiles(Cursor cursor)
    {
        ArrayList<String> filenames = new ArrayList<>();
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            if(cursor.getColumnIndex(Constants.PIT_ROBOT_PICTURE) != -1) {
                if(cursor.getType(cursor.getColumnIndex(Constants.PIT_ROBOT_PICTURE)) == Cursor.FIELD_TYPE_STRING) {
                    String filename = cursor.getString(cursor.getColumnIndex(Constants.PIT_ROBOT_PICTURE));
                    if(!filename.equals("")) {
                        Log.d(TAG, filename);
                        filenames.add(filename);
                    }
                }
            }
        }
        return filenames;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        CustomHeader header = (CustomHeader)findViewById(R.id.header);
        header.setBackOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Server.this.finish();
            }
        });
        header.removeHome();

        findViewById(android.R.id.content).setKeepScreenOn(true);

        logView = (TextView)findViewById(R.id.log);
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        circularBuffer = new CircularBuffer(50);

        Button button = (Button)findViewById(R.id.aggregate_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> teams = pitScoutDB.getTeamNumbers();
                AggregateStats.updateTeams(new HashSet<Integer>(teams),matchScoutDB,superScoutDB,scheduleDB,statsDB);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        handler = new SyncHandler();
        bluetoothSync = new BluetoothSync(handler, false);
        handler.setBluetoothSync(bluetoothSync);

        matchScoutDB = new MatchScoutDB(this, eventID);
        pitScoutDB = new PitScoutDB(this, eventID);
        superScoutDB = new SuperScoutDB(this, eventID);
        driveTeamFeedbackDB = new DriveTeamFeedbackDB(this,eventID);
        scheduleDB = new ScheduleDB(this,eventID);
        statsDB = new StatsDB(this, eventID);
        syncDB = new SyncDB(this, eventID);
        handler.setDatabaseHelpers(matchScoutDB,pitScoutDB,superScoutDB,driveTeamFeedbackDB,statsDB,syncDB,scheduleDB);

        handler.setContext(this);

        bluetoothSync.start();
    }

    @Override
    public void onDestroy(){
        if (bluetoothSync != null){
            bluetoothSync.stop();
        }
        super.onDestroy();
    }

}
