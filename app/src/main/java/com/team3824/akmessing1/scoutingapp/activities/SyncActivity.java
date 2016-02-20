package com.team3824.akmessing1.scoutingapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.services.SyncService;
import com.team3824.akmessing1.scoutingapp.utilities.CircularBuffer;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.MessageType;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.bluetooth.BluetoothSync;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SyncActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{
    private static String TAG = "SyncActivity";
    private BluetoothAdapter mBluetoothAdapter = null;
    Object[] pairedDevices;
    private TextView textView;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;
    private String selectedAddress;

    Spinner pairedSpinner, actionSpinner;

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    ScheduleDB scheduleDB;
    StatsDB statsDB;
    SyncDB syncDB;

    CircularBuffer circularBuffer;

    private final String RED = "red";
    private final String GREEN = "green";
    private final String BLUE = "blue";
    private final String CYAN = "cyan";
    private final String YELLOW = "#DAA520";
    private final String BLACK = "black";

    private class SyncHandler extends android.os.Handler
    {
        String filename = "";

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case MessageType.COULD_NOT_CONNECT:
                    rotateText(RED,"Could not connect");
                    break;
                case MessageType.DATA_SENT_OK:
                    rotateText(GREEN, "Data sent ok");
                    break;
                case MessageType.SENDING_DATA:
                    rotateText(BLACK, "Sending data");
                    break;
                case MessageType.DIGEST_DID_NOT_MATCH:
                    rotateText(RED, "Digest did not match");
                    break;
                case MessageType.INVALID_HEADER:
                    rotateText(RED, "Invalid header");
                    break;
                case MessageType.DATA_RECEIVED:
                    String message = new String((byte[]) msg.obj);
                    if(message.length() > 30)
                    {
                        Log.d(TAG, String.format("Received: %s ... %s",message.substring(0,15),message.substring(message.length()-15)));
                        rotateText(CYAN, String.format("Received: %s ... %s",message.substring(0,15),message.substring(message.length()-15)));
                    }
                    else {
                        Log.d(TAG, String.format("Received: %s",message));
                        rotateText(CYAN, String.format("Received: %s", message));
                    }

                    if (message.length() == 0)
                        return;
                    switch (message.charAt(0)) {
                        case Constants.MATCH_HEADER:
                            filename = "";
                            try {
                                JSONArray jsonArray = new JSONArray(message.substring(1));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, ScoutValue> map = new HashMap<>();
                                    Iterator<String> iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            Object value = jsonObject.get(key);
                                            if (value instanceof Integer) {
                                                map.put(key, new ScoutValue((int) value));
                                            } else if (value instanceof Float) {
                                                map.put(key, new ScoutValue((float) value));
                                            } else if (value instanceof String) {
                                                map.put(key, new ScoutValue((String) value));
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                        }
                                    }
                                    matchScoutDB.updateMatch(map);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            rotateText(CYAN, "Match Data Received");
                            break;
                        case Constants.PIT_HEADER:
                            filename = "";
                            try {
                                JSONArray jsonArray = new JSONArray(message.substring(1));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, ScoutValue> map = new HashMap<>();
                                    Iterator<String> iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            Object value = jsonObject.get(key);
                                            if (value instanceof Integer) {
                                                map.put(key, new ScoutValue((int) value));
                                            } else if (value instanceof Float) {
                                                map.put(key, new ScoutValue((float) value));
                                            } else if (value instanceof String) {
                                                map.put(key, new ScoutValue((String) value));
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                        }
                                    }
                                    if(map.get(PitScoutDB.KEY_COMPLETE).getInt() == 1) {
                                        pitScoutDB.updatePit(map);
                                    }
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            rotateText(CYAN, "Pit Data Received");
                            break;
                        case Constants.SUPER_HEADER:
                            filename = "";
                            try {
                                JSONArray jsonArray = new JSONArray(message.substring(1));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, ScoutValue> map = new HashMap<>();
                                    Iterator<String> iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            Object value = jsonObject.get(key);
                                            if (value instanceof Integer) {
                                                map.put(key, new ScoutValue((int) value));
                                            } else if (value instanceof Float) {
                                                map.put(key, new ScoutValue((float) value));
                                            } else if (value instanceof String) {
                                                map.put(key, new ScoutValue((String) value));
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                        }
                                    }
                                    superScoutDB.updateMatch(map);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            rotateText(CYAN, "Super Data Received");
                            break;
                        case Constants.DRIVE_TEAM_FEEDBACK_HEADER:
                            try {
                                JSONArray jsonArray = new JSONArray(message.substring(1));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, ScoutValue> map = new HashMap<>();
                                    Iterator<String> iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            Object value = jsonObject.get(key);
                                            if (value instanceof Integer) {
                                                map.put(key, new ScoutValue((int) value));
                                            } else if (value instanceof Float) {
                                                map.put(key, new ScoutValue((float) value));
                                            } else if (value instanceof String) {
                                                map.put(key, new ScoutValue((String) value));
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                        }
                                    }
                                    driveTeamFeedbackDB.updateComments(map.get(DriveTeamFeedbackDB.KEY_TEAM_NUMBER).getInt(), map.get(DriveTeamFeedbackDB.KEY_COMMENTS).getString());
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            rotateText(CYAN, "Drive Team Feedback Data Received");
                            break;
                        case Constants.STATS_HEADER:
                            filename = "";
                            try {
                                JSONArray jsonArray = new JSONArray(message.substring(1));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    HashMap<String, ScoutValue> map = new HashMap<>();
                                    Iterator<String> iter = jsonObject.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        try {
                                            Object value = jsonObject.get(key);
                                            if (value instanceof Integer) {
                                                map.put(key, new ScoutValue((int) value));
                                            } else if (value instanceof Float) {
                                                map.put(key, new ScoutValue((float) value));
                                            } else if (value instanceof String) {
                                                map.put(key, new ScoutValue((String) value));
                                            }
                                        } catch (JSONException e) {
                                            // Something went wrong!
                                        }
                                    }
                                    statsDB.updateStats(map);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            rotateText(CYAN, "Stats Data Received");
                            break;
                        case Constants.SCHEDULE_HEADER:
                            filename = "";
                            try {
                                JSONArray jsonArray = new JSONArray(message.substring(1));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    scheduleDB.addMatch(jsonObject.getInt(ScheduleDB.KEY_MATCH_NUMBER),
                                            jsonObject.getInt(ScheduleDB.KEY_BLUE1),
                                            jsonObject.getInt(ScheduleDB.KEY_BLUE2),
                                            jsonObject.getInt(ScheduleDB.KEY_BLUE3),
                                            jsonObject.getInt(ScheduleDB.KEY_RED1),
                                            jsonObject.getInt(ScheduleDB.KEY_RED2),
                                            jsonObject.getInt(ScheduleDB.KEY_RED3));
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                            rotateText(CYAN, "Schedule Received");
                            break;
                        case Constants.FILENAME_HEADER:
                            filename = message.substring(1);
                            break;
                        case Constants.RECEIVE_HEADER:
                            if (message.equals(Constants.RECEIVE_ALL_HEADER)) {
                                filename = "";
                                selectedAddress = bluetoothSync.getConnectedAddress();
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(""));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                rotateText(CYAN, "Match Data Sent");
                                Log.d(TAG, "Match Data Sent");

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(""));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                rotateText(CYAN, "Pit Data Sent");
                                Log.d(TAG, "Pit Data Sent");

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(""));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                rotateText(CYAN, "Super Data Sent");
                                Log.d(TAG, "Super Data Sent");

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(""));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                rotateText(CYAN, "Drive Team Feedback Data Sent");
                                Log.d(TAG, "Drive Team Feedback Data Sent");

                                String statsUpdatedText = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStats());
                                while(!bluetoothSync.write(statsUpdatedText.getBytes()));
                                rotateText(CYAN, "Stats Data Sent");
                                Log.d(TAG, "Stats Data Sent");
                            }
                            else if(message.equals(Constants.RECEIVE_PICTURE_HEADER))
                            {
                                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                                for (int i = 0; i < filenames.size(); i++) {
                                    while(!bluetoothSync.write((Constants.FILENAME_HEADER + filenames.get(i)).getBytes()));
                                    File file = new File(SyncActivity.this.getFilesDir(), filenames.get(i));
                                    while(!bluetoothSync.writeFile(file));
                                    rotateText(BLACK, String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                    Log.d(TAG, String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                }
                            }
                            else if(message.equals(Constants.RECEIVE_SCHEDULE_HEADER))
                            {
                                String scheduleText = Constants.SCHEDULE_HEADER + Utilities.CursorToJsonString(scheduleDB.getSchedule());
                                while (!bluetoothSync.write(scheduleText.getBytes())) ;
                                rotateText(BLACK, "Schedule Sent");
                                Log.d(TAG, "Schedule Sent");
                            }
                            else {
                                filename = "";
                                selectedAddress = bluetoothSync.getConnectedAddress();
                                String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes()));
                                rotateText(BLACK, "Match Data Sent");
                                Log.d(TAG, "Match Data Sent");

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes()));
                                rotateText(BLACK, "Pit Data Sent");
                                Log.d(TAG, "Pit Data Sent");

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                rotateText(BLACK, "Super Data Sent");
                                Log.d(TAG, "Super Data Sent");

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                rotateText(BLACK, "Drive Team Feedback Data Sent");
                                Log.d(TAG, "Drive Team Feedback Data Sent");

                                String statsUpdatedText = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStatsSince(lastUpdated));
                                while(!bluetoothSync.write(statsUpdatedText.getBytes()));
                                rotateText(BLACK, "Stats Data Sent");
                                Log.d(TAG, "Stats Data Sent");
                            }
                            break;
                        case Constants.FILE_HEADER:
                            if (message.startsWith("file:")) {
                                byte[] fileBuffer = Arrays.copyOfRange((byte[]) msg.obj, 5, ((byte[]) msg.obj).length);
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = SyncActivity.this.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
                                    fileOutputStream.write(fileBuffer);
                                    fileOutputStream.close();
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG,e.getMessage());
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                rotateText(CYAN, String.format("File %s Received", filename));
                                Log.d(TAG, String.format("File %s Received", filename));
                            }
                            break;
                        case Constants.PING_HEADER:
                            if(message.equals(Constants.PING))
                            {
                                rotateText(BLACK,"Ping Received, sending Pong");
                                Log.d(TAG, "Ping Received, sending Pong");
                                for (int i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                    if (bluetoothSync.write(Constants.PONG.getBytes())) {
                                        break;
                                    }
                                }
                            }
                            else if(message.equals(Constants.PONG))
                            {
                                rotateText(GREEN, "Pong Received");
                                Log.d(TAG, "Pong Received");
                            }
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        ((Button)findViewById(R.id.back)).setOnClickListener(this);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                ArrayAdapter<String> pairedDevicesArrayAdapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);

                // Find and set up the ListView for paired devices
                pairedSpinner = (Spinner) findViewById(R.id.paired_devices);
                pairedSpinner.setAdapter(pairedDevicesArrayAdapter);
                pairedSpinner.setOnItemSelectedListener(this);

                // Get a set of currently paired devices
                Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
                pairedDevices = pairedDevicesSet.toArray();
                pairedDevicesArrayAdapter.add("None");
                if (pairedDevices.length > 0) {
                    for (int i = 0; i < pairedDevices.length; i++) {
                        BluetoothDevice bluetoothDevice = (BluetoothDevice)pairedDevices[i];
                        pairedDevicesArrayAdapter.add(String.format("%s - %s",bluetoothDevice.getName(),bluetoothDevice.getAddress()));
                    }
                }

                ArrayAdapter<String> actionArrayAdapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
                actionSpinner = (Spinner)findViewById(R.id.action);
                actionSpinner.setAdapter(actionArrayAdapter);
                actionArrayAdapter.addAll("None", "Ping", "Send Update", "Send All", "Send Pictures",
                        "Send Schedule", "Receive Update", "Receive All", "Receive Pictures",
                        "Receive Schedule");
                actionSpinner.setOnItemSelectedListener(this);


                handler = new SyncHandler();
                bluetoothSync = new BluetoothSync(handler, false);

                textView = (TextView) findViewById(R.id.sync_log);

                matchScoutDB = new MatchScoutDB(this, eventID);
                pitScoutDB = new PitScoutDB(this, eventID);
                superScoutDB = new SuperScoutDB(this, eventID);
                driveTeamFeedbackDB = new DriveTeamFeedbackDB(this,eventID);
                scheduleDB = new ScheduleDB(this,eventID);
                statsDB = new StatsDB(this, eventID);
                syncDB = new SyncDB(this, eventID);

                ((Button) findViewById(R.id.execute_action)).setOnClickListener(this);
                ((Button) findViewById(R.id.start_service)).setOnClickListener(this);

                circularBuffer = new CircularBuffer(20);

                bluetoothSync.start();
            } else {
                findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
                findViewById(R.id.paired_devices).setVisibility(View.GONE);
                findViewById(R.id.execute_action).setVisibility(View.GONE);
                findViewById(R.id.action).setVisibility(View.GONE);
                findViewById(R.id.start_service).setVisibility(View.GONE);
                findViewById(R.id.sync_log).setVisibility(View.GONE);
            }
        }
        else
        {
            findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.bluetooth_text)).setText("No Bluetooth on this device");
            findViewById(R.id.paired_devices).setVisibility(View.GONE);
            findViewById(R.id.execute_action).setVisibility(View.GONE);
            findViewById(R.id.action).setVisibility(View.GONE);
            findViewById(R.id.start_service).setVisibility(View.GONE);
            findViewById(R.id.sync_log).setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy(){
        if (bluetoothSync != null){
            bluetoothSync.stop();
        }
        super.onDestroy();
    }

    ArrayList<String> getImageFiles(Cursor cursor)
    {
        ArrayList<String> filenames = new ArrayList<>();
        int i = 0;
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
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.back:
                this.finish();
                break;
            case R.id.execute_action:
                if(pairedSpinner.getSelectedItemPosition() > 0 && actionSpinner.getSelectedItemPosition() > 0)
                {
                    int position = actionSpinner.getSelectedItemPosition();
                    if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                        String text;
                        switch (position) {
                            case 0: //None
                                Log.e(TAG, "This should never happen...");
                                rotateText(RED,"This should never happen...");
                                break;
                            case 1: // Ping
                                Log.d(TAG, "Sending Ping...");
                                rotateText(BLUE,"Sending Ping...");
                                for (int i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                    if (bluetoothSync.write(Constants.PING.getBytes())) {
                                        break;
                                    }
                                }
                                break;
                            case 2: //Send Update
                                Log.d(TAG, "Sending Update");
                                rotateText(BLUE,"Sending Update");
                                String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                                syncDB.updateSync(selectedAddress);

                                text = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Match Data Sent");
                                rotateText(BLUE,"Match Data Sent");

                                text = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Pit Data Sent");
                                rotateText(BLUE,"Pit Data Sent");

                                text = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Super Data Sent");
                                rotateText(BLUE,"Super Data Sent");

                                text = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Drive Team Feedback Data Sent");
                                rotateText(BLUE,"Drive Team Feedback Data Sent");

                                text = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStatsSince(lastUpdated));
                                while(!bluetoothSync.write(text.getBytes()));
                                Log.d(TAG, "Stats Data Sent");
                                rotateText(BLUE,"Stats Data Sent");
                                break;
                            case 3: //Send All
                                Log.d(TAG, "Sending All");
                                rotateText(BLUE,"Sending All");
                                syncDB.updateSync(selectedAddress);

                                text = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(""));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Match Data Sent");
                                rotateText(BLUE,"Match Data Sent");

                                text = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(""));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Pit Data Sent");
                                rotateText(BLUE,"Pit Data Sent");

                                text = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(""));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Super Data Sent");
                                rotateText(BLUE,"Super Data Sent");

                                text = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(""));
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG, "Drive Team Feedback Data Sent");
                                rotateText(BLUE,"Drive Team Feedback Data Sent");

                                text = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStats());
                                while(!bluetoothSync.write(text.getBytes()));
                                Log.d(TAG, "Stats Data Sent");
                                rotateText(BLUE,"Stats Data Sent");
                                break;
                            case 4: //Send Pictures
                                Log.d(TAG,"Sending Pictures");
                                rotateText(BLUE,"Sending Pictures");
                                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                                for (int i = 0; i < filenames.size(); i++) {
                                    while(!bluetoothSync.write((Constants.FILENAME_HEADER + filenames.get(i)).getBytes()));
                                    File file = new File(SyncActivity.this.getFilesDir(), filenames.get(i));
                                    while(!bluetoothSync.writeFile(file));
                                    Log.d(TAG,String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                    rotateText(BLACK,String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                }
                                break;
                            case 5: //Send Schedule
                                Log.d(TAG,"Sending Schedule");
                                rotateText(BLUE,"Sending Schedule");
                                text = Constants.SCHEDULE_HEADER + Utilities.CursorToJsonString(scheduleDB.getSchedule());
                                while (!bluetoothSync.write(text.getBytes())) ;
                                Log.d(TAG,"Schedule Sent");
                                rotateText(BLUE,"Schedule Sent");
                                break;
                            case 6: //Receive Update
                                Log.d(TAG,"Requesting Update");
                                rotateText(BLUE,"Requesting Update");
                                while (!bluetoothSync.write(Constants.RECEIVE_UPDATE_HEADER.getBytes()));
                                break;
                            case 7: //Receive All
                                Log.d(TAG,"Requesting All");
                                rotateText(BLUE,"Requesting All");
                                while (!bluetoothSync.write(Constants.RECEIVE_ALL_HEADER.getBytes()));
                                break;
                            case 8: //Receive Pictures
                                Log.d(TAG, "Requesting Picture");
                                rotateText(BLUE,"Requesting Picture");
                                while (!bluetoothSync.write(Constants.RECEIVE_PICTURE_HEADER.getBytes()));
                                break;
                            case 9: //Receive Schedule
                                Log.d(TAG,"Requesting Schedule");
                                rotateText(BLUE,"Requesting Schedule");
                                while (!bluetoothSync.write(Constants.RECEIVE_SCHEDULE_HEADER.getBytes()));
                                break;
                        }
                    }
                }
                break;
            case R.id.start_service:
                Log.d(TAG, "Start Service");
                rotateText(BLUE,"Start Service");
                Intent intent = new Intent(this, SyncService.class);
                startService(intent);
                break;
        }
    }

    private class ConnectionTask extends AsyncTask<BluetoothDevice,Void,Void>
    {

        @Override
        protected void onPreExecute()
        {
            rotateText(CYAN, "Attempting to connect");
        }

        @Override
        protected Void doInBackground(BluetoothDevice... params) {
            bluetoothSync.connect((params[0]), false);
            long time = System.currentTimeMillis();
            long newTime = time;
            while (newTime < time + Constants.CONNECTION_TIMEOUT && bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                newTime = System.currentTimeMillis();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                rotateText(RED, "Connection failed");
                pairedSpinner.setSelection(0);
                bluetoothSync.start();
            }
            else
            {
                rotateText(GREEN, String.format("Connnected to %s", bluetoothSync.getConnectedName()));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.paired_devices)
        {
            String info = String.valueOf(pairedSpinner.getSelectedItem());
            if(!info.equals("None")) {
                selectedAddress = info.substring(info.length() - 17);
                new ConnectionTask().execute((BluetoothDevice)pairedDevices[position-1]);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void rotateText(String color, String text)
    {
        circularBuffer.insert(String.format("<font color='%s'>%s</font><br>",color,text));
        textView.setText(Html.fromHtml(circularBuffer.toString()));
    }
}
