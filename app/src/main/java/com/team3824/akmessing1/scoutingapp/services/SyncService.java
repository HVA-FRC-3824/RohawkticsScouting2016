package com.team3824.akmessing1.scoutingapp.services;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
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

public class SyncService extends IntentService{
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;
    private Handler toastHandler;

    private String TAG = "SyncService";

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    StatsDB statsDB;
    SyncDB syncDB;

    boolean first = true;

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
                            writeToast("Match Data Received");
                            Log.d(TAG,"Match Data Received");
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
                            writeToast("Pit Data Received");
                            Log.d(TAG,"Pit Data Received");
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
                            writeToast("Super Data Received");
                            Log.d(TAG,"Super Data Received");
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
                            writeToast("Drive Team Feedback Data Received");
                            Log.d(TAG,"Drive Team Feedback Data Received");
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
                            writeToast("Stats Data Received");
                            Log.d(TAG,"Stats Data Received");
                            break;
                        case Constants.FILENAME_HEADER:
                            filename = message.substring(1);
                            break;
                        case 'r':
                            if(message.equals("received"))
                            {
                                received = true;
                            }
                            break;
                        case Constants.RECEIVE_HEADER:
                            if (message.equals(Constants.RECEIVE_ALL_HEADER)) {
                                filename = "";
                                String selectedAddress = bluetoothSync.getConnectedAddress();
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfo());
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                writeToast("Match Data Sent");
                                Log.d(TAG, "Match Data Sent");

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfo());
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                writeToast("Pit Data Sent");
                                Log.d(TAG, "Pit Data Sent");

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatches());
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                writeToast("Super Data Sent");
                                Log.d(TAG, "Super Data Sent");

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllComments());
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                writeToast("Drive Team Feedback Data Sent");
                                Log.d(TAG, "Drive Team Feedback Data Sent");

                                String statsUpdatedText = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStats());
                                while(!bluetoothSync.write(statsUpdatedText.getBytes()));
                                writeToast("Stats Data Sent");
                                Log.d(TAG, "Stats Data Sent");

                                while(!bluetoothSync.write("received".getBytes()));
                            }
                            else if(message.equals(Constants.RECEIVE_PICTURE_HEADER))
                            {
                                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                                for (int i = 0; i < filenames.size(); i++) {
                                    while(!bluetoothSync.write((Constants.FILENAME_HEADER + filenames.get(i)).getBytes()));
                                    File file = new File(context.getFilesDir(), filenames.get(i));
                                    while(!bluetoothSync.writeFile(file));
                                    writeToast(String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                    Log.d(TAG,String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                }

                                while(!bluetoothSync.write("received".getBytes()));
                            }
                            else {
                                filename = "";
                                String selectedAddress = bluetoothSync.getConnectedAddress();
                                String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
                                while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                                writeToast("Match Data Sent");
                                Log.d(TAG, "Match Data Sent");

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                                writeToast("Pit Data Sent");
                                Log.d(TAG,"Pit Data Sent");

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                                writeToast("Super Data Sent");
                                Log.d(TAG,"Super Data Sent");

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                                writeToast("Drive Team Feedback Data Sent");
                                Log.d(TAG, "Drive Team Feedback Data Sent");

                                String statsUpdatedText = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStatsSince(lastUpdated));
                                while(!bluetoothSync.write(statsUpdatedText.getBytes()));
                                writeToast("Stats Data Sent");
                                Log.d(TAG, "Stats Data Sent");

                                while(!bluetoothSync.write("received".getBytes()));
                            }
                            break;
                        case Constants.FILE_HEADER:
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
                                writeToast(String.format("File %s Received", filename));
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
        toastHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled())
        {
            writeToast("Bluetooth is not on");
            Log.d(TAG,"Bluetooth is not on");
        }
        else {
            writeToast("BEGIN Syncing");
            Log.d(TAG,"BEGIN Syncing");
            Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
            String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
            String userType = sharedPreferences.getString(Constants.USER_TYPE, "");
            matchScoutDB = new MatchScoutDB(this, eventID);
            pitScoutDB = new PitScoutDB(this, eventID);
            superScoutDB = new SuperScoutDB(this, eventID);
            driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventID);
            statsDB = new StatsDB(this,eventID);
            syncDB = new SyncDB(this, eventID);

            if (!userType.equals(Constants.SERVER)) {
                while (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED
                        || bluetoothSync.getState() == BluetoothSync.STATE_CONNECTING) ;

                for (BluetoothDevice device : devices) {
                    String connectedName = device.getName();
                    if (!connectedName.equals("3824_Server")) {
                        continue;
                    }
                    Log.d(TAG, String.format("Connecting to %s", connectedName));

                    while(true) {
                        bluetoothSync.connect(device, false);
                        if (timeout()) {
                            break;
                        }
                    }

                    String connectedAddress = bluetoothSync.getConnectedAddress();
                    String lastUpdated = syncDB.getLastUpdated(connectedAddress);
                    syncDB.updateSync(connectedAddress);

                    switch (userType) {
                        case Constants.MATCH_SCOUT:
                            String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
                            while (!bluetoothSync.write(matchUpdatedText.getBytes())) ;
                            writeToast("Match Data Sent");
                            break;
                        case Constants.PIT_SCOUT:
                            String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                            while (!bluetoothSync.write(pitUpdatedText.getBytes())) ;
                            writeToast("Pit Data Sent");
                            break;
                        case Constants.SUPER_SCOUT:
                            String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                            while (!bluetoothSync.write(superUpdatedText.getBytes())) ;
                            writeToast("Super Data Sent");
                            break;
                        case Constants.DRIVE_TEAM:
                            String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                            while (!bluetoothSync.write(driveUpdatedText.getBytes())) ;
                            writeToast("Drive Team Feedback Data Sent");

                            received = false;
                            while (!bluetoothSync.write(Constants.RECEIVE_UPDATE_HEADER.getBytes())) ;
                            while (!received) {
                                SystemClock.sleep(250);
                            }
                            writeToast("Data Received");
                            break;
                        case Constants.STRATEGY:
                        case Constants.ADMIN:
                            received = false;
                            while (!bluetoothSync.write(Constants.RECEIVE_UPDATE_HEADER.getBytes())) ;
                            while (!received) {
                                SystemClock.sleep(250);
                            }

                            writeToast("Data Received");
                            break;
                    }
                    bluetoothSync.stop();
                }
            }
            writeToast("END Syncing");
            Log.d(TAG,"END Syncing");
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

    private void writeToast(final String message)
    {
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SyncService.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
