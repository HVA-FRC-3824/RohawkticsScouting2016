package com.team3824.akmessing1.scoutingapp.bluetooth;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.MessageType;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BluetoothHandler extends Handler {

    String filename = "";
    BluetoothSync bluetoothSync;
    Context context;
    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    StatsDB statsDB;
    SyncDB syncDB;
    ScheduleDB scheduleDB;

    boolean received = false;

    public void setBluetoothSync(BluetoothSync b)
    {
        bluetoothSync = b;
    }

    public void setContext(Context c)
    {
        context = c;
    }

    public void setDatabaseHelpers(MatchScoutDB m, PitScoutDB p, SuperScoutDB s, DriveTeamFeedbackDB d, StatsDB st, SyncDB sy, ScheduleDB sc)
    {
        matchScoutDB = m;
        pitScoutDB = p;
        superScoutDB = s;
        driveTeamFeedbackDB = d;
        statsDB = st;
        syncDB = sy;
        scheduleDB = sc;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (msg.what) {
            case MessageType.COULD_NOT_CONNECT:
                displayText("Could not connect");
                break;
            case MessageType.DATA_SENT_OK:
                displayText("Data sent ok");
                break;
            case MessageType.SENDING_DATA:
                displayText("Sending data");
                break;
            case MessageType.DIGEST_DID_NOT_MATCH:
                displayText("Digest did not match");
                break;
            case MessageType.INVALID_HEADER:
                displayText("Invalid header");
                break;
            case MessageType.CONNECTION_LOST:
                displayText("Connection Lost");
                break;
            case MessageType.DATA_RECEIVED:
                String message = new String((byte[]) msg.obj);
                if(message.length() > 30)
                {
                    displayText(String.format("Received: %s ... %s", message.substring(0, 15), message.substring(message.length() - 15)));
                }
                else {
                    displayText(String.format("Received: %s", message));
                }

                if (message.length() == 0)
                    return;

                switch (message.charAt(0)) {
                    case Constants.MATCH_HEADER:
                        filename = "";
                        Utilities.JsonToMatchDB(matchScoutDB, message);
                        receivedData(message);
                        displayText("Match Data Received");
                        break;
                    case Constants.PIT_HEADER:
                        filename = "";
                        Utilities.JsonToPitDB(pitScoutDB, message);
                        displayText("Pit Data Received");
                        break;
                    case Constants.SUPER_HEADER:
                        filename = "";
                        Utilities.JsonToSuperDB(superScoutDB,message);
                        receivedData(message);
                        displayText("Super Data Received");
                        break;
                    case Constants.DRIVE_TEAM_FEEDBACK_HEADER:
                        filename = "";
                        Utilities.JsonToDriveTeamDB(driveTeamFeedbackDB, message);
                        displayText("Drive Team Feedback Data Received");
                        break;
                    case Constants.STATS_HEADER:
                        filename = "";
                        Utilities.JsonToStatsDB(statsDB,message);
                        displayText("Stats Data Received");
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
                        }
                        displayText("Schedule Received");
                        break;
                    case Constants.FILENAME_HEADER:
                        filename = message.substring(1);
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
                            } catch (IOException e) {
                            }
                            displayText(String.format("File %s Received", filename));
                        }
                        break;
                    case Constants.PING_HEADER:
                        if(message.equals(Constants.PING))
                        {
                            displayText("Ping Received, sending Pong");
                            for (int i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                if (bluetoothSync.write(Constants.PONG.getBytes())) {
                                    break;
                                }
                            }
                        }
                        else if(message.equals(Constants.PONG))
                        {
                            displayText("Pong Received");
                        }
                    case Constants.REQUEST_HEADER:
                        String lastUpdated;
                        switch (message.charAt(1)) {
                            case Constants.MATCH_HEADER:
                                lastUpdated = syncDB.getMatchLastUpdated(bluetoothSync.getConnectedAddress());
                                syncDB.updateMatchSync(bluetoothSync.getConnectedAddress());
                                String matchText = Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
                                while(!bluetoothSync.write(matchText.getBytes()));
                                displayText("Responded with Match Data");
                                break;
                            case Constants.PIT_HEADER:
                                lastUpdated = syncDB.getMatchLastUpdated(bluetoothSync.getConnectedAddress());
                                syncDB.updatePitSync(bluetoothSync.getConnectedAddress());
                                String pitText = Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                while(!bluetoothSync.write(pitText.getBytes()));
                                displayText("Responded with Pit Data");
                                break;
                            case Constants.SUPER_HEADER:
                                lastUpdated = syncDB.getSuperLastUpdated(bluetoothSync.getConnectedAddress());
                                syncDB.updateSuperSync(bluetoothSync.getConnectedAddress());
                                String superText = Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                while(!bluetoothSync.write(superText.getBytes()));
                                displayText("Responded with Super Data");
                                break;
                            case Constants.DRIVE_TEAM_FEEDBACK_HEADER:
                                lastUpdated = syncDB.getDriveTeamLastUpdated(bluetoothSync.getConnectedAddress());
                                syncDB.updateDriveTeamSync(bluetoothSync.getConnectedAddress());
                                String driveTeamText = Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                while(!bluetoothSync.write(driveTeamText.getBytes()));
                                displayText("Responded with Drive Team Feedback");
                                break;
                            case Constants.STATS_HEADER:
                                lastUpdated = syncDB.getStatsLastUpdated(bluetoothSync.getConnectedAddress());
                                syncDB.updateStatsSync(bluetoothSync.getConnectedAddress());
                                String statsText = Utilities.CursorToJsonString(statsDB.getStatsSince(lastUpdated));
                                while(!bluetoothSync.write(statsText.getBytes()));
                                displayText("Responded with Stats");
                                break;
                            case Constants.SCHEDULE_HEADER:
                                String scheduleText = Utilities.CursorToJsonString(scheduleDB.getSchedule());
                                while(!bluetoothSync.write(scheduleText.getBytes()));
                                displayText("Responded with Schedule");
                                break;
                        }
                        while (!bluetoothSync.write("r".getBytes()))
                        break;
                    case Constants.RECEIVE_HEADER:
                        received = true;
                        break;
                }
                break;
        }
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
                        displayText(filename);
                        filenames.add(filename);
                    }
                }
            }
        }
        return filenames;
    }

    public void displayText(String text)
    {

    }

    public void receivedData(String data)
    {

    }
}
