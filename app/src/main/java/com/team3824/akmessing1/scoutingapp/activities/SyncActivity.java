package com.team3824.akmessing1.scoutingapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.Utilities;
import com.team3824.akmessing1.scoutingapp.bluetooth.BluetoothSync;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    SyncDB syncDB;

    private class SyncHandler extends android.os.Handler
    {
        String filename = "";

        @Override
        public void handleMessage(Message msg)
        {
            String message = new String((byte[])msg.obj);
            Log.d(TAG, "Received: " + message);
            textView.setText(message);
            if(message.length() == 0)
                return;
            switch(message.charAt(0))
            {
                case 'M':
                    filename = "";
                    try {
                        JSONArray jsonArray = new JSONArray(message.substring(1));
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, ScoutValue> map = new HashMap<>();
                            Iterator<String> iter = jsonObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    Object value = jsonObject.get(key);
                                    if(value instanceof Integer)
                                    {
                                        map.put(key,new ScoutValue((int)value));
                                    }
                                    else if(value instanceof Float)
                                    {
                                        map.put(key,new ScoutValue((float)value));
                                    }
                                    else if(value instanceof String)
                                    {
                                        map.put(key,new ScoutValue((String)value));
                                    }
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                            SyncActivity.this.matchScoutDB.updateMatch(map);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(SyncActivity.this,"Match Data Received",Toast.LENGTH_SHORT).show();
                    break;
                case 'P':
                    filename = "";
                    try{
                        JSONArray jsonArray = new JSONArray(message.substring(1));
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, ScoutValue> map = new HashMap<>();
                            Iterator<String> iter = jsonObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    Object value = jsonObject.get(key);
                                    if(value instanceof Integer)
                                    {
                                        map.put(key,new ScoutValue((int)value));
                                    }
                                    else if(value instanceof Float)
                                    {
                                        map.put(key,new ScoutValue((float)value));
                                    }
                                    else if(value instanceof String)
                                    {
                                        map.put(key,new ScoutValue((String)value));
                                    }
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                            SyncActivity.this.pitScoutDB.updatePit(map);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(SyncActivity.this,"Pit Data Received",Toast.LENGTH_SHORT).show();
                    break;
                case 'S':
                    filename = "";
                    try{
                        JSONArray jsonArray = new JSONArray(message.substring(1));
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, ScoutValue> map = new HashMap<>();
                            Iterator<String> iter = jsonObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    Object value = jsonObject.get(key);
                                    if(value instanceof Integer)
                                    {
                                        map.put(key,new ScoutValue((int)value));
                                    }
                                    else if(value instanceof Float)
                                    {
                                        map.put(key,new ScoutValue((float)value));
                                    }
                                    else if(value instanceof String)
                                    {
                                        map.put(key,new ScoutValue((String)value));
                                    }
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                            SyncActivity.this.superScoutDB.updateMatch(map);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(SyncActivity.this, "Super Data Received", Toast.LENGTH_SHORT).show();
                    break;
                case 'D':
                    try{
                        JSONArray jsonArray = new JSONArray(message.substring(1));
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            HashMap<String, ScoutValue> map = new HashMap<>();
                            Iterator<String> iter = jsonObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    Object value = jsonObject.get(key);
                                    if(value instanceof Integer)
                                    {
                                        map.put(key,new ScoutValue((int)value));
                                    }
                                    else if(value instanceof Float)
                                    {
                                        map.put(key,new ScoutValue((float)value));
                                    }
                                    else if(value instanceof String)
                                    {
                                        map.put(key,new ScoutValue((String)value));
                                    }
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }
                            SyncActivity.this.driveTeamFeedbackDB.updateComments(map.get(DriveTeamFeedbackDB.KEY_TEAM_NUMBER).getInt(),map.get(DriveTeamFeedbackDB.KEY_COMMENTS).getString());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Received", Toast.LENGTH_SHORT).show();
                    break;
                case 'F':
                    filename = message.substring(1);
                    break;
                case 'R':
                    filename = "";
                    selectedAddress = bluetoothSync.getConnectedAddress();
                    String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                    syncDB.updateSync(selectedAddress);
                    String matchUpdatedText = "M" + Utilities.CursorToJsonString(SyncActivity.this.matchScoutDB.getInfoSince(lastUpdated));
                    bluetoothSync.write(matchUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this,"Match Data Sent",Toast.LENGTH_SHORT).show();
                    String pitUpdatedText = "P" + Utilities.CursorToJsonString(SyncActivity.this.pitScoutDB.getAllTeamInfoSince(lastUpdated));
                    bluetoothSync.write(pitUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this,"Pit Data Sent",Toast.LENGTH_SHORT).show();
                    String superUpdatedText = "S" + Utilities.CursorToJsonString(SyncActivity.this.superScoutDB.getAllMatchesSince(lastUpdated));
                    bluetoothSync.write(superUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this,"Super Data Sent",Toast.LENGTH_SHORT).show();
                    break;
                case 'f':
                    if(message.startsWith("file:") && message.endsWith(":end")) {
                        String messageWOPrefix = message.substring(5);
                        String messageWOSuffix = message.substring(0,messageWOPrefix.length()-4);
                        File f = new File(SyncActivity.this.getFilesDir(), filename);
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(f);
                            fileOutputStream.write(messageWOSuffix.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Toast.makeText(SyncActivity.this,"File " + filename + " Received",Toast.LENGTH_SHORT).show();
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
                        pairedDevicesArrayAdapter.add(((BluetoothDevice) pairedDevices[i]).getName() + "\n" + ((BluetoothDevice) pairedDevices[i]).getAddress());
                    }
                }
                handler = new SyncHandler();
                bluetoothSync = new BluetoothSync(handler, false);

                textView = (TextView) findViewById(R.id.sync_log);

                matchScoutDB = new MatchScoutDB(this, eventID);
                pitScoutDB = new PitScoutDB(this, eventID);
                superScoutDB = new SuperScoutDB(this, eventID);
                syncDB = new SyncDB(this, eventID);

                ((Button) findViewById(R.id.sync_send)).setOnClickListener(this);

                ((Button) findViewById(R.id.sync_picture_send)).setOnClickListener(this);

                Button sync_receive = (Button) findViewById(R.id.sync_receive);
                sync_receive.setOnClickListener(this);

                bluetoothSync.start();
            } else {
                findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
                findViewById(R.id.paired_devices).setVisibility(View.GONE);
                findViewById(R.id.sync_send).setVisibility(View.GONE);
                findViewById(R.id.sync_picture_send).setVisibility(View.GONE);
                findViewById(R.id.sync_receive).setVisibility(View.GONE);
                findViewById(R.id.sync_log).setVisibility(View.GONE);


            }
        }
        else
        {
            findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.bluetooth_text)).setText("No Bluetooth on this device");
            findViewById(R.id.paired_devices).setVisibility(View.GONE);
            findViewById(R.id.sync_send).setVisibility(View.GONE);
            findViewById(R.id.sync_picture_send).setVisibility(View.GONE);
            findViewById(R.id.sync_receive).setVisibility(View.GONE);
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
            bluetoothSync.connect(((BluetoothDevice)pairedDevices[position]), false);
        }
    };

    ArrayList<String> getImageFiles(Cursor cursor)
    {
        ArrayList<String> filenames = new ArrayList<>();
        int i = 0;
        while(!cursor.isAfterLast())
        {
            if(cursor.getColumnIndex("robotPicture") != -1) {
                if(cursor.getType(cursor.getColumnIndex("robotPicture")) == Cursor.FIELD_TYPE_STRING) {
                    String filename = cursor.getString(cursor.getColumnIndex("robotPicture"));
                    Log.d(TAG, filename);
                    filenames.add(filename);
                }
            }
            cursor.moveToNext();
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
            case R.id.sync_send:
                Log.d(TAG, "Sending");
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    Log.d(TAG, "Connected");
                    String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                    syncDB.updateSync(selectedAddress);
                    String matchUpdatedText = "M" + Utilities.CursorToJsonString(SyncActivity.this.matchScoutDB.getInfoSince(lastUpdated));
                    bluetoothSync.write(matchUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this, "Match Data Sent", Toast.LENGTH_SHORT).show();
                    String pitUpdatedText = "P" + Utilities.CursorToJsonString(SyncActivity.this.pitScoutDB.getAllTeamInfoSince(lastUpdated));
                    bluetoothSync.write(pitUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this, "Pit Data Sent", Toast.LENGTH_SHORT).show();
                    String superUpdatedText = "S" + Utilities.CursorToJsonString(SyncActivity.this.superScoutDB.getAllMatchesSince(lastUpdated));
                    bluetoothSync.write(superUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this, "Super Data Sent", Toast.LENGTH_SHORT).show();
                    String driverUpdatedText = "D" + Utilities.CursorToJsonString(SyncActivity.this.driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                    bluetoothSync.write(driverUpdatedText.getBytes());
                    Toast.makeText(SyncActivity.this, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync_picture_send:
                ArrayList<String> filenames = getImageFiles(SyncActivity.this.pitScoutDB.getAllTeamInfo());
                for (int i = 0; i < filenames.size(); i++) {
                    bluetoothSync.write(("F" + filenames.get(i)).getBytes());
                    File file = new File(SyncActivity.this.getFilesDir(), filenames.get(i));
                    bluetoothSync.writeFile(file);
                    Toast.makeText(SyncActivity.this, "Picture " + String.valueOf(i + 1) + " of " + String.valueOf(filenames.size()) + " Sent", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.sync_receive:
                if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                    bluetoothSync.write(("R").getBytes());
                }
                break;
        }
    }


}
