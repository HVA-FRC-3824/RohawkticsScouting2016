package com.team3824.akmessing1.scoutingapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
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

public class SyncActivity extends AppCompatActivity implements View.OnClickListener{
    private static String TAG = "SyncActivity";
    private BluetoothAdapter mBluetoothAdapter = null;
    Object[] pairedDevices;
    private TextView textView;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;
    private String selectedAddress;

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    ScheduleDB scheduleDB;
    SyncDB syncDB;

    private class SyncHandler extends android.os.Handler
    {
        String filename = "";

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case MessageType.COULD_NOT_CONNECT:
                    textView.setText("Could not connect");
                    break;
                case MessageType.DATA_SENT_OK:
                    textView.setText("Data sent ok");
                    break;
                case MessageType.SENDING_DATA:
                    textView.setText("Sending data");
                    break;
                case MessageType.DIGEST_DID_NOT_MATCH:
                    textView.setText("Digest did not match");
                    break;
                case MessageType.INVALID_HEADER:
                    textView.setText("Invalid header");
                    break;
                case MessageType.DATA_RECEIVED:
                    String message = new String((byte[]) msg.obj);
                    Log.d(TAG, "Received: " + message);
                    textView.setText(message);
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
                            Toast.makeText(SyncActivity.this, "Match Data Received", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SyncActivity.this, "Pit Data Received", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SyncActivity.this, "Super Data Received", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Received", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SyncActivity.this, "Schedule Received", Toast.LENGTH_SHORT).show();
                            break;
                        case Constants.FILENAME_HEADER:
                            filename = message.substring(1);
                            break;
                        case Constants.RECEIVE_HEADER:
                            if (message.equals(Constants.RECEIVE_ALL_HEADER)) {
                                filename = "";
                                selectedAddress = bluetoothSync.getConnectedAddress();
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(""));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Match Data Sent", Toast.LENGTH_SHORT).show();

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(""));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(""));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Super Data Sent", Toast.LENGTH_SHORT).show();

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(""));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();

                            }
                            else if(message.equals(Constants.RECEIVE_PICTURE_HEADER))
                            {
                                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                                for (int i = 0; i < filenames.size(); i++) {
                                    while(!bluetoothSync.write((Constants.FILENAME_HEADER + filenames.get(i)).getBytes()));
                                    File file = new File(SyncActivity.this.getFilesDir(), filenames.get(i));
                                    while(!bluetoothSync.writeFile(file));
                                    Toast.makeText(SyncActivity.this, String.format("Picture %d of %d Sent",i+1,filenames.size()), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if(message.equals(Constants.RECEIVE_SCHEDULE_HEADER))
                            {
                                String scheduleText = Constants.SCHEDULE_HEADER + Utilities.CursorToJsonString(scheduleDB.getSchedule());
                                while (!bluetoothSync.write(scheduleText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Schedule Sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                filename = "";
                                selectedAddress = bluetoothSync.getConnectedAddress();
                                String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Match Data Sent", Toast.LENGTH_SHORT).show();

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Super Data Sent", Toast.LENGTH_SHORT).show();

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();
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
                                Log.d(TAG,String.format("File %s Received", filename));
                                Toast.makeText(SyncActivity.this, String.format("File %s Received", filename), Toast.LENGTH_SHORT).show();
                            }
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        ((Button)findViewById(R.id.back)).setOnClickListener(this);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                ArrayAdapter<String> pairedDevicesArrayAdapter =
                        new ArrayAdapter<String>(this, R.layout.list_item_device_name);

                // Find and set up the ListView for paired devices
                ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
                pairedListView.setAdapter(pairedDevicesArrayAdapter);
                pairedListView.setOnItemClickListener(mDeviceClickListener);

                // Get a set of currently paired devices
                Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
                pairedDevices = pairedDevicesSet.toArray();
                if (pairedDevices.length > 0) {
                    for (int i = 0; i < pairedDevices.length; i++) {
                        BluetoothDevice bluetoothDevice = (BluetoothDevice)pairedDevices[i];
                        pairedDevicesArrayAdapter.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                    }
                }
                handler = new SyncHandler();
                bluetoothSync = new BluetoothSync(handler, false);

                textView = (TextView) findViewById(R.id.sync_log);

                matchScoutDB = new MatchScoutDB(this, eventID);
                pitScoutDB = new PitScoutDB(this, eventID);
                superScoutDB = new SuperScoutDB(this, eventID);
                driveTeamFeedbackDB = new DriveTeamFeedbackDB(this,eventID);
                scheduleDB = new ScheduleDB(this,eventID);
                syncDB = new SyncDB(this, eventID);

                ((Button) findViewById(R.id.sync_send_update)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_send_all)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_picture_send)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_schedule_send)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_receive_update)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_receive_all)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_picture_receive)).setOnClickListener(this);
                ((Button) findViewById(R.id.sync_schedule_receive)).setOnClickListener(this);
                bluetoothSync.start();
            } else {
                findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
                findViewById(R.id.paired_devices).setVisibility(View.GONE);
                findViewById(R.id.sync_send_update).setVisibility(View.GONE);
                findViewById(R.id.sync_send_all).setVisibility(View.GONE);
                findViewById(R.id.sync_picture_send).setVisibility(View.GONE);
                findViewById(R.id.sync_schedule_send).setVisibility(View.GONE);
                findViewById(R.id.sync_receive_update).setVisibility(View.GONE);
                findViewById(R.id.sync_receive_all).setVisibility(View.GONE);
                findViewById(R.id.sync_picture_receive).setVisibility(View.GONE);
                findViewById(R.id.sync_schedule_receive).setVisibility(View.GONE);
                findViewById(R.id.sync_log).setVisibility(View.GONE);


            }
        }
        else
        {
            findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.bluetooth_text)).setText("No Bluetooth on this device");
            findViewById(R.id.paired_devices).setVisibility(View.GONE);
            findViewById(R.id.sync_send_update).setVisibility(View.GONE);
            findViewById(R.id.sync_send_all).setVisibility(View.GONE);
            findViewById(R.id.sync_picture_send).setVisibility(View.GONE);
            findViewById(R.id.sync_schedule_send).setVisibility(View.GONE);
            findViewById(R.id.sync_receive_update).setVisibility(View.GONE);
            findViewById(R.id.sync_receive_all).setVisibility(View.GONE);
            findViewById(R.id.sync_picture_receive).setVisibility(View.GONE);
            findViewById(R.id.sync_schedule_receive).setVisibility(View.GONE);
            findViewById(R.id.sync_log).setVisibility(View.GONE);
        }
    }

    /**
     * The on-click listener for all devices in the ListViews
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            selectedAddress = info.substring(info.length() - 17);
            for(int i = 0; i < av.getChildCount(); i++) {
                ((TextView)av.getChildAt(i)).setTextColor(Color.BLACK);
                ((TextView)av.getChildAt(i)).setBackgroundColor(Color.WHITE);
            }
            v.setBackgroundColor(Color.BLUE);
            ((TextView) v).setTextColor(Color.WHITE);
            bluetoothSync.connect(((BluetoothDevice) pairedDevices[position]), false);
            long time = SystemClock.currentThreadTimeMillis();
            while(SystemClock.currentThreadTimeMillis() < time + Constants.CONNECTION_TIMEOUT && bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED);
            if(bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED)
            {
                v.setBackgroundColor(Color.WHITE);
                ((TextView) v).setTextColor(Color.BLACK);
                textView.setText("Connection failed");
                bluetoothSync.start();
            }
        }
    };

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
            case R.id.sync_send_update:
                Log.d(TAG, "Sending");
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                    syncDB.updateSync(selectedAddress);

                    String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                    while (!bluetoothSync.write(matchUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Match Data Sent", Toast.LENGTH_SHORT).show();

                    String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                    while (!bluetoothSync.write(pitUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                    String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                    while (!bluetoothSync.write(superUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Super Data Sent", Toast.LENGTH_SHORT).show();

                    String driverUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                    while (!bluetoothSync.write(driverUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync_send_all:
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    syncDB.updateSync(selectedAddress);

                    String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(""));
                    while (!bluetoothSync.write(matchUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Match Data Sent", Toast.LENGTH_SHORT).show();

                    String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(""));
                    while (!bluetoothSync.write(pitUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                    String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(""));
                    while (!bluetoothSync.write(superUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Super Data Sent", Toast.LENGTH_SHORT).show();

                    String driverUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(""));
                    while (!bluetoothSync.write(driverUpdatedText.getBytes()));
                    Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync_picture_send:
                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                for (int i = 0; i < filenames.size(); i++) {
                    while(!bluetoothSync.write((Constants.FILENAME_HEADER + filenames.get(i)).getBytes()));
                    File file = new File(SyncActivity.this.getFilesDir(), filenames.get(i));
                    while(!bluetoothSync.writeFile(file));
                    Toast.makeText(SyncActivity.this, String.format("Picture %d of %d Sent",i+1,filenames.size()), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync_schedule_send:
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    String scheduleText = Constants.SCHEDULE_HEADER + Utilities.CursorToJsonString(scheduleDB.getSchedule());
                    while (!bluetoothSync.write(scheduleText.getBytes())) ;
                    Toast.makeText(SyncActivity.this, "Schedule Sent", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync_receive_update:
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    while (!bluetoothSync.write(Constants.RECEIVE_UPDATE_HEADER.getBytes()));
                }
                break;
            case R.id.sync_receive_all:
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    while (!bluetoothSync.write(Constants.RECEIVE_ALL_HEADER.getBytes()));
                }
                break;
            case R.id.sync_picture_receive:
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    while (!bluetoothSync.write(Constants.RECEIVE_SCHEDULE_HEADER.getBytes()));
                }
                break;
            case R.id.sync_schedule_receive:
                break;
        }
    }
}
