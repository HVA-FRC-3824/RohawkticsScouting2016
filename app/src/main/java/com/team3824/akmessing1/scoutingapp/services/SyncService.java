package com.team3824.akmessing1.scoutingapp.services;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.Utilities;
import com.team3824.akmessing1.scoutingapp.bluetooth.BluetoothSync;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SyncService extends NonStopIntentService{
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;

    private String TAG = "SyncService";

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    SyncDB syncDB;

    boolean recieved = false;

    private class SyncHandler extends android.os.Handler
    {
        String filename = "";

        @Override
        public void handleMessage(Message msg)
        {
            Context context = getApplicationContext();
            String message = new String((byte[])msg.obj);
            Log.d(TAG, "Received: " + message);
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
                            SyncService.this.matchScoutDB.updateMatch(map);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(context, "Match Data Received", Toast.LENGTH_SHORT).show();
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
                            SyncService.this.pitScoutDB.updatePit(map);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(context,"Pit Data Received",Toast.LENGTH_SHORT).show();
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
                            SyncService.this.superScoutDB.updateMatch(map);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                    Toast.makeText(getApplicationContext(), "Super Data Received", Toast.LENGTH_SHORT).show();
                    break;
                case 'F':
                    filename = message.substring(1);
                    break;
                case 'R':

                    filename = "";
                    String selectedAddress = bluetoothSync.getConnectedAddress();
                    String lastUpdated = SyncService.this.syncDB.getLastUpdated(selectedAddress);
                    syncDB.updateSync(selectedAddress);
                    String matchUpdatedText = "M" + Utilities.CursorToJsonString(SyncService.this.matchScoutDB.getInfoSince(lastUpdated));
                    bluetoothSync.write(matchUpdatedText.getBytes());
                    Toast.makeText(context,"Match Data Sent",Toast.LENGTH_SHORT).show();
                    String pitUpdatedText = "P" + Utilities.CursorToJsonString(SyncService.this.pitScoutDB.getAllTeamInfoSince(lastUpdated));
                    bluetoothSync.write(pitUpdatedText.getBytes());
                    Toast.makeText(context,"Pit Data Sent",Toast.LENGTH_SHORT).show();
                    String superUpdatedText = "S" + Utilities.CursorToJsonString(SyncService.this.superScoutDB.getAllMatchesSince(lastUpdated));
                    bluetoothSync.write(superUpdatedText.getBytes());
                    Toast.makeText(context,"Super Data Sent",Toast.LENGTH_SHORT).show();
                    bluetoothSync.write(("received").getBytes());
                    break;
                case 'r':
                    if(message.equals("recieved"))
                    {
                        recieved = true;
                    }
                case 'f':
                    if(message.startsWith("file:") && message.endsWith(":end")) {
                        String messageWOPrefix = message.substring(5);
                        String messageWOSuffix = message.substring(0,messageWOPrefix.length()-4);
                        File f = new File(context.getFilesDir(), filename);
                        FileOutputStream fileOutputStream = null;
                        try {
                            fileOutputStream = new FileOutputStream(f);
                            fileOutputStream.write(messageWOSuffix.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        Toast.makeText(context,"File Received",Toast.LENGTH_SHORT).show();

                    }
                    break;
            }
        }
    }

    public SyncService()
    {
        super("SyncService");
        handler = new SyncHandler();
        bluetoothSync = new BluetoothSync(handler,false);
        if(mBluetoothAdapter.getDefaultAdapter().isEnabled()) {
            bluetoothSync.start();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
        {
            Toast.makeText(this,"Bluetooth is not on",Toast.LENGTH_SHORT).show();
        }
        else {
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
            String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
            String userType = sharedPreferences.getString(Constants.USER_TYPE, "");
            matchScoutDB = new MatchScoutDB(this, eventID);
            pitScoutDB = new PitScoutDB(this, eventID);
            superScoutDB = new SuperScoutDB(this, eventID);
            syncDB = new SyncDB(this, eventID);

            while (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED
                    || bluetoothSync.getState() == BluetoothSync.STATE_CONNECTING) ;

            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                // TODO: get actual names
                switch (userType) {
                    case Constants.MATCH_SCOUT:
                        if (connectedName.equals("3824_SuperScout")) {
                            bluetoothSync.connect(device, false);
                            while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) ;
                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);
                            String matchUpdatedText = "M" + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                            bluetoothSync.write(matchUpdatedText.getBytes());
                            Toast.makeText(this, "Match Data Sent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.PIT_SCOUT:
                        if (connectedName.equals("3824_SuperScout")) {
                            bluetoothSync.connect(device, false);
                            while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) ;
                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);
                            String pitUpdatedText = "P" + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                            bluetoothSync.write(pitUpdatedText.getBytes());
                            Toast.makeText(this, "Pit Data Sent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.SUPER_SCOUT:
                        if (connectedName.equals("3824_DriveTeam") || connectedName.equals("3824_Strategy") || connectedName.equals("3824_Admin")) {
                            bluetoothSync.connect(device, false);
                            while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) ;
                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);
                            String matchUpdatedText = "M" + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                            bluetoothSync.write(matchUpdatedText.getBytes());
                            Toast.makeText(this, "Match Data Sent", Toast.LENGTH_SHORT).show();
                            String pitUpdatedText = "P" + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                            bluetoothSync.write(pitUpdatedText.getBytes());
                            Toast.makeText(this, "Pit Data Sent", Toast.LENGTH_SHORT).show();
                            String superUpdatedText = "S" + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                            bluetoothSync.write(superUpdatedText.getBytes());
                            Toast.makeText(this, "Super Data Sent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.DRIVE_TEAM:
                    case Constants.STRATEGY:
                    case Constants.ADMIN:
                        if (connectedName.equals("3824_SuperScout")) {
                            bluetoothSync.connect(device, false);
                            while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) ;
                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);
                            bluetoothSync.write(("R").getBytes());
                            recieved = false;
                            while (!recieved) ;
                            Toast.makeText(this, "Data Recieved", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
            bluetoothSync.start();
        }
    }
}
