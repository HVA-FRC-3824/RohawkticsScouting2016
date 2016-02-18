package com.team3824.akmessing1.scoutingapp.services;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
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

public class SyncService extends NonStopIntentService{
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;

    private String TAG = "SyncService";

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    SyncDB syncDB;

    boolean received = false;

    private class SyncHandler extends android.os.Handler
    {
        String filename = "";

        @Override
        public void handleMessage(Message msg)
        {
            Context context = getApplicationContext();

            switch (msg.what) {
                case MessageType.DATA_RECEIVED:
                    String message = new String((byte[]) msg.obj);
                    Log.d(TAG, "Received: " + message);
                    if (message.length() == 0)
                        return;
                    switch (message.charAt(0)) {
                        case 'M':
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
                            Toast.makeText(context, "Match Data Received", Toast.LENGTH_SHORT).show();
                            break;
                        case 'P':
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
                            Toast.makeText(context, "Pit Data Received", Toast.LENGTH_SHORT).show();
                            break;
                        case 'S':
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
                            Toast.makeText(context, "Super Data Received", Toast.LENGTH_SHORT).show();
                            break;
                        case 'D':
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
                            Toast.makeText(context, "Drive Team Feedback Data Received", Toast.LENGTH_SHORT).show();
                            break;
                        case 'F':
                            filename = message.substring(1);
                            break;
                        case 'r':
                            if(message.equals("received"))
                            {
                                received = true;
                            }
                            break;
                        case 'R':
                            if (message.equals("RA")) {
                                filename = "";
                                String selectedAddress = bluetoothSync.getConnectedAddress();
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = "M" + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(""));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Match Data Sent", Toast.LENGTH_SHORT).show();

                                String pitUpdatedText = "P" + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(""));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                                String superUpdatedText = "S" + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(""));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Super Data Sent", Toast.LENGTH_SHORT).show();

                                String driveUpdatedText = "D" + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(""));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();

                                while(!bluetoothSync.write("received".getBytes()));
                            }
                            else if(message.equals("RP"))
                            {
                                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                                for (int i = 0; i < filenames.size(); i++) {
                                    while(!bluetoothSync.write(("F" + filenames.get(i)).getBytes()));
                                    File file = new File(context.getFilesDir(), filenames.get(i));
                                    while(!bluetoothSync.writeFile(file));
                                    Toast.makeText(context, String.format("Picture %d of %d Sent",i+1,filenames.size()), Toast.LENGTH_SHORT).show();
                                }

                                while(!bluetoothSync.write("received".getBytes()));
                            }
                            else {
                                filename = "";
                                String selectedAddress = bluetoothSync.getConnectedAddress();
                                String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = "M" + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Match Data Sent", Toast.LENGTH_SHORT).show();

                                String pitUpdatedText = "P" + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                                String superUpdatedText = "S" + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Super Data Sent", Toast.LENGTH_SHORT).show();

                                String driveUpdatedText = "D" + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                Toast.makeText(context, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();

                                while(!bluetoothSync.write("received".getBytes()));
                            }
                            break;
                        case 'f':
                            if (message.startsWith("file:")) {
                                byte[] fileBuffer = Arrays.copyOfRange((byte[]) msg.obj, 5, ((byte[]) msg.obj).length);
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = context.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
                                    fileOutputStream.write(fileBuffer);
                                    fileOutputStream.close();
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG,e.getMessage());
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                Log.d(TAG,String.format("File %s Received", filename));
                                Toast.makeText(context, String.format("File %s Received", filename), Toast.LENGTH_SHORT).show();
                            }
                            break;
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
        Context context = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
        {
            Toast.makeText(context,"Bluetooth is not on",Toast.LENGTH_SHORT).show();
        }
        else {

            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
            String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
            String userType = sharedPreferences.getString(Constants.USER_TYPE, "");
            matchScoutDB = new MatchScoutDB(this, eventID);
            pitScoutDB = new PitScoutDB(this, eventID);
            superScoutDB = new SuperScoutDB(this, eventID);
            driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventID);
            syncDB = new SyncDB(this, eventID);

            while (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED
                    || bluetoothSync.getState() == BluetoothSync.STATE_CONNECTING) ;

            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                switch (userType) {
                    case Constants.MATCH_SCOUT:
                        if (connectedName.equals("3824_Super_Scout")) {
                            bluetoothSync.connect(device, false);
                            if(!timeout())
                                continue;

                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);

                            String matchUpdatedText = "M" + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                            while (!bluetoothSync.write(matchUpdatedText.getBytes()));
                            Toast.makeText(context, "Match Data Sent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.PIT_SCOUT:
                        if (connectedName.equals("3824_Super_Scout")) {
                            bluetoothSync.connect(device, false);
                            if(!timeout())
                                continue;

                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);

                            String pitUpdatedText = "P" + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                            while (!bluetoothSync.write(pitUpdatedText.getBytes()));
                            Toast.makeText(context, "Pit Data Sent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.SUPER_SCOUT:
                        if (connectedName.equals("3824_Drive_Team") || connectedName.equals("3824_Strategy")) {
                            bluetoothSync.connect(device, false);
                            if(!timeout())
                                continue;

                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);

                            String matchUpdatedText = "M" + Utilities.CursorToJsonString(matchScoutDB.getInfoSince(lastUpdated));
                            while (!bluetoothSync.write(matchUpdatedText.getBytes()));
                            Toast.makeText(context, "Match Data Sent", Toast.LENGTH_SHORT).show();

                            String pitUpdatedText = "P" + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                            while (!bluetoothSync.write(pitUpdatedText.getBytes()));
                            Toast.makeText(context, "Pit Data Sent", Toast.LENGTH_SHORT).show();

                            String superUpdatedText = "S" + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                            while (!bluetoothSync.write(superUpdatedText.getBytes()));
                            Toast.makeText(context, "Super Data Sent", Toast.LENGTH_SHORT).show();

                            String driveUpdatedText = "D" + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                            while (!bluetoothSync.write(driveUpdatedText.getBytes()));
                            Toast.makeText(context, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case Constants.DRIVE_TEAM:
                        if(connectedName.equals("3824_Super_Scout"))
                        {
                            bluetoothSync.connect(device, false);
                            if(!timeout())
                                continue;

                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);

                            String driveUpdatedText = "D" + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                            while (!bluetoothSync.write(driveUpdatedText.getBytes()));
                            Toast.makeText(context, "Drive Team Feedback Data Sent", Toast.LENGTH_SHORT).show();

                            received = false;
                            while (!bluetoothSync.write("R".getBytes()));
                            while (!received) {
                                SystemClock.sleep(250);
                            };
                            Toast.makeText(context, "Data Recieved", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    case Constants.STRATEGY:
                    case Constants.ADMIN:
                        if (connectedName.equals("3824_Super_Scout")) {
                            bluetoothSync.connect(device, false);
                            if(!timeout())
                                continue;

                            String connectedAddress = bluetoothSync.getConnectedAddress();
                            String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                            syncDB.updateSync(connectedAddress);

                            received = false;
                            while(!bluetoothSync.write(("R").getBytes()));
                            while (!received)
                            {
                                SystemClock.sleep(250);
                            }

                            Toast.makeText(context, "Data Recieved", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }

            }
            Toast.makeText(context,"Finished syncing",Toast.LENGTH_SHORT).show();
            bluetoothSync.start();
        }
    }

    private boolean timeout()
    {
        long time = SystemClock.currentThreadTimeMillis();
        while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED)
        {
            if(SystemClock.currentThreadTimeMillis() > time + Constants.CONNECTION_TIMEOUT)
            {
                return false;
            }
        }
        return true;
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

}
