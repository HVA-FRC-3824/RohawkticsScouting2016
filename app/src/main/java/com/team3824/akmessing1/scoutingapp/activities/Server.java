package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.bluetooth.BluetoothSync;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.services.AggregateService;
import com.team3824.akmessing1.scoutingapp.utilities.CircularBuffer;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.MessageType;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server extends AppCompatActivity {

    private String TAG = "Server";
    TextView logView;

    BluetoothSync bluetoothSync;
    SyncHandler handler;

    LogCatTask logCatTask;

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    StatsDB statsDB;
    ScheduleDB scheduleDB;
    SyncDB syncDB;

    private class SyncHandler extends android.os.Handler
    {
        String filename = "";

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case MessageType.DATA_RECEIVED:
                    String message = new String((byte[]) msg.obj);
                    if(message.length() > 30)
                    {
                        Log.d(TAG, String.format("Received: %s ... %s",message.substring(0,15),message.substring(message.length()-15)));
                    }
                    else {
                        Log.d(TAG, String.format("Received: %s",message));
                    }
                    if (message.length() == 0)
                        return;
                    switch (message.charAt(0)) {
                        case Constants.MATCH_HEADER:
                            filename = "";
                            if(!message.equals(String.format("%c[]",Constants.MATCH_HEADER))){
                                Utilities.JsonToMatchDB(matchScoutDB, message);
                            }
                            Log.d(TAG, "Match Data Received");
                            break;
                        case Constants.PIT_HEADER:
                            filename = "";
                            if(!message.equals(String.format("%c[]",Constants.PIT_HEADER))) {
                                Utilities.JsonToPitDB(pitScoutDB, message);
                            }
                            Log.d(TAG, "Pit Data Received");
                            break;
                        case Constants.SUPER_HEADER:
                            filename = "";
                            if(!message.equals(String.format("%c[]",Constants.SUPER_HEADER))) {
                                Utilities.JsonToSuperDB(superScoutDB, message);
                            }
                            Log.d(TAG,"Super Data  Received");
                            break;
                        case Constants.DRIVE_TEAM_FEEDBACK_HEADER:
                            if(!message.equals(String.format("%c[]",Constants.DRIVE_TEAM_FEEDBACK_HEADER))) {
                                Utilities.JsonToDriveTeamDB(driveTeamFeedbackDB, message);
                            }
                            Log.d(TAG,"Drive Team Feedback Data Received");
                            break;
                        case Constants.STATS_HEADER:
                            filename = "";
                            if(!message.equals(String.format("%c[]",Constants.STATS_HEADER))) {
                                Utilities.JsonToStatsDB(statsDB, message);
                            }
                            Log.d(TAG, "Stats Data Received");
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
                            Log.d(TAG, "Schedule Received");
                            break;
                        case Constants.FILENAME_HEADER:
                            filename = message.substring(1);
                            break;
                        case Constants.RECEIVE_HEADER:
                            if (message.equals(Constants.RECEIVE_ALL_HEADER)) {
                                Log.d(TAG,"Full Request Received");
                                filename = "";
                                String selectedAddress = bluetoothSync.getConnectedAddress();
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfo());
                                int i;
                                if(!matchUpdatedText.equals(String.format("%c[]",Constants.MATCH_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(matchUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToMatchDB(matchScoutDB, matchUpdatedText);
                                        Log.d(TAG, "Match Data Requeued");
                                    } else {
                                        Log.d(TAG, "Match Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Match Data to send");
                                }

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfo());
                                if(!pitUpdatedText.equals(String.format("%c[]",Constants.PIT_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(pitUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToPitDB(pitScoutDB, pitUpdatedText);
                                        Log.d(TAG, "Pit Data Requeued");
                                    } else {
                                        Log.d(TAG, "Pit Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG,"No new Pit Data to send");
                                }

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatches());
                                if(!superUpdatedText.equals(String.format("%c[]",Constants.SUPER_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(superUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToSuperDB(superScoutDB, superUpdatedText);
                                        Log.d(TAG, "Super Data Requeued");
                                    } else {
                                        Log.d(TAG, "Super Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Super Data to Send");
                                }

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllComments());
                                if(!driveUpdatedText.equals(String.format("%c[]",Constants.DRIVE_TEAM_FEEDBACK_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(driveUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToDriveTeamDB(driveTeamFeedbackDB, driveUpdatedText);
                                        Log.d(TAG, "Drive Team Feedback Data Requeued");
                                    } else {
                                        Log.d(TAG, "Drive Team Feedback Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Drive Team Feedback Data to send");
                                }

                                String statsUpdatedText = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStats());
                                if(!statsUpdatedText.equals(String.format("%c[]",Constants.STATS_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(statsUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToStatsDB(statsDB, statsUpdatedText);
                                        Log.d(TAG, "Stats Data Requeued");
                                    } else {
                                        Log.d(TAG, "Stats Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Stats Data to send");
                                }

                                while (!bluetoothSync.write("received".getBytes()));
                            }
                            else if(message.equals(Constants.RECEIVE_PICTURE_HEADER))
                            {
                                Log.d(TAG, "Picture Request Received");

                                ArrayList<String> filenames = getImageFiles(pitScoutDB.getAllTeamInfo());
                                for (int i = 0; i < filenames.size(); i++) {
                                    while(!bluetoothSync.write((Constants.FILENAME_HEADER + filenames.get(i)).getBytes()));
                                    File file = new File(Server.this.getFilesDir(), filenames.get(i));
                                    while(!bluetoothSync.writeFile(file));
                                    Log.d(TAG,String.format("Picture %d of %d Sent",i+1,filenames.size()));
                                }
                                if(filenames.size() == 0)
                                {
                                    Log.d(TAG,"No Pictures to send");
                                }
                            }
                            else if(message.equals(Constants.RECEIVE_SCHEDULE_HEADER))
                            {
                                Log.d(TAG, "Schedule Request Received");

                                String scheduleText = Constants.SCHEDULE_HEADER + Utilities.CursorToJsonString(scheduleDB.getSchedule());
                                while (!bluetoothSync.write(scheduleText.getBytes())) ;
                                Log.d(TAG, "Schedule Sent");
                            }
                            else {
                                Log.d(TAG,"Update Request Received");
                                filename = "";
                                String selectedAddress = bluetoothSync.getConnectedAddress();
                                String lastUpdated = syncDB.getLastUpdated(selectedAddress);
                                syncDB.updateSync(selectedAddress);

                                String matchUpdatedText = Constants.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
                                int i;
                                if(!matchUpdatedText.equals(String.format("%c[]",Constants.MATCH_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(matchUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToMatchDB(matchScoutDB, matchUpdatedText);
                                        Log.d(TAG, "Match Data Requeued");
                                    } else {
                                        Log.d(TAG, "Match Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Match Data to send");
                                }

                                String pitUpdatedText = Constants.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamInfoSince(lastUpdated));
                                if(!pitUpdatedText.equals(String.format("%c[]",Constants.PIT_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(pitUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToPitDB(pitScoutDB, pitUpdatedText);
                                        Log.d(TAG, "Pit Data Requeued");
                                    } else {
                                        Log.d(TAG, "Pit Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG,"No new Pit Data to send");
                                }

                                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                                if(!superUpdatedText.equals(String.format("%c[]",Constants.SUPER_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(superUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToSuperDB(superScoutDB, superUpdatedText);
                                        Log.d(TAG, "Super Data Requeued");
                                    } else {
                                        Log.d(TAG, "Super Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Super Data to Send");
                                }

                                String driveUpdatedText = Constants.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllCommentsSince(lastUpdated));
                                if(!driveUpdatedText.equals(String.format("%c[]",Constants.DRIVE_TEAM_FEEDBACK_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(driveUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToDriveTeamDB(driveTeamFeedbackDB, driveUpdatedText);
                                        Log.d(TAG, "Drive Team Feedback Data Requeued");
                                    } else {
                                        Log.d(TAG, "Drive Team Feedback Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Drive Team Feedback Data to send");
                                }

                                String statsUpdatedText = Constants.STATS_HEADER + Utilities.CursorToJsonString(statsDB.getStatsSince(lastUpdated));
                                if(!statsUpdatedText.equals(String.format("%c[]",Constants.STATS_HEADER))) {
                                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                        if (bluetoothSync.write(statsUpdatedText.getBytes())) {
                                            break;
                                        }
                                    }
                                    if (i == Constants.NUM_ATTEMPTS) {
                                        Utilities.JsonToStatsDB(statsDB, statsUpdatedText);
                                        Log.d(TAG, "Stats Data Requeued");
                                    } else {
                                        Log.d(TAG, "Stats Data Sent");
                                    }
                                }
                                else
                                {
                                    Log.d(TAG, "No new Stats Data to send");
                                }

                                while (!bluetoothSync.write("received".getBytes()));
                            }
                            break;
                        case Constants.FILE_HEADER:
                            if (message.startsWith("file:")) {
                                byte[] fileBuffer = Arrays.copyOfRange((byte[]) msg.obj, 5, ((byte[]) msg.obj).length);
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = Server.this.openFileOutput(filename, Context.MODE_WORLD_WRITEABLE);
                                    fileOutputStream.write(fileBuffer);
                                    fileOutputStream.close();
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG,e.getMessage());
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                Log.d(TAG, String.format("File %s Received", filename));
                            }
                            break;
                        case Constants.PING_HEADER:
                            if(message.equals(Constants.PING))
                            {
                                Log.d(TAG, "Ping Received, sending Pong");
                                for (int i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                    if (bluetoothSync.write(Constants.PONG.getBytes())) {
                                        break;
                                    }
                                }
                            }
                            else if(message.equals(Constants.PONG))
                            {
                                Log.d(TAG, "Pong Received");
                            }
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

    public class LogCatTask extends AsyncTask<Void, String, Void> {
        Process process;
        private String TAG = "LogCatTask";
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                CircularBuffer buf = new CircularBuffer(50);
                buf.insert("Log Started...<br>");
                publishProgress(buf.toString());
                String line = "";
                while (!isCancelled())
                {
                    line = bufferedReader.readLine();
                    if (line != null && (line.contains("BluetoothSync") || line.contains("Server") || line.contains("AggregateService")))
                    {

                        char logLevel = ' ';
                        if (line.length() > 32) {
                            logLevel = line.charAt(31);
                        }

                        switch (logLevel) {
                            case 'V':
                                line = "<font color='black'>" + line;
                                break;
                            case 'D':
                                line = "<font color='blue'>" + line;
                                break;
                            case 'I':
                                line = "<font color='grey'>" + line;
                                break;
                            case 'W':
                                line = "<font color='#DAA520'>" + line;
                                break;
                            case 'E':
                                line = "<font color='red'>" + line;
                                break;
                            case 'A':
                                line = "<font color='blue'>" + line;
                                break;
                            default:
                                line = "<font color='black'>" + line;
                                break;
                        }

                        line += "</font><br>";
                        buf.insert(line);
                        publishProgress(buf.toString());
                    }
                    line = null;
                    //Thread.sleep(10);
                }
            }
            catch (Exception ex)
            {
                Log.d(TAG, ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            logView.setText(Html.fromHtml(values[0]));

            int scrollAmount = logView.getLayout().getLineTop(logView.getLineCount()) - logView.getHeight();
            if (scrollAmount > 0)
                logView.scrollTo(0, scrollAmount);
            else
                logView.scrollTo(0, 0);

            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG,"Canceling Log Task");
            if(process != null) {
                Log.d(TAG,"Destroying process");
                process.destroy();
            }
            super.onCancelled();
        }

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

        Button button = (Button)findViewById(R.id.aggregate_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Server.this, AggregateService.class);
                intent.putExtra(Constants.UPDATE,false);
                startService(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        handler = new SyncHandler();
        bluetoothSync = new BluetoothSync(handler, false);

        matchScoutDB = new MatchScoutDB(this, eventID);
        pitScoutDB = new PitScoutDB(this, eventID);
        superScoutDB = new SuperScoutDB(this, eventID);
        driveTeamFeedbackDB = new DriveTeamFeedbackDB(this,eventID);
        scheduleDB = new ScheduleDB(this,eventID);
        statsDB = new StatsDB(this, eventID);
        syncDB = new SyncDB(this, eventID);

        logCatTask = new LogCatTask();
        logCatTask.execute();
        bluetoothSync.start();
    }

    @Override
    public void onDestroy(){
        if (bluetoothSync != null){
            bluetoothSync.stop();
        }
        if(logCatTask != null)
        {
            logCatTask.cancel(true);
        }
        super.onDestroy();
    }

}
