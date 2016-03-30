package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.utilities.AggregateStats;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothHandler;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothSync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * The home screen for the app. Different buttons appear based on the user type in settings.
 *
 * @author Andrew Messing
 */
public class HomeScreen extends Activity implements View.OnClickListener {
    private final String TAG = "HomeScreen";
    private SyncHandler handler;
    private BluetoothSync bluetoothSync;

    private MatchScoutDB matchScoutDB;
    private PitScoutDB pitScoutDB;
    private SuperScoutDB superScoutDB;
    private DriveTeamFeedbackDB driveTeamFeedbackDB;
    private StatsDB statsDB;
    private ScheduleDB scheduleDB;
    private SyncDB syncDB;

    private String eventID;

    private BluetoothAdapter bluetoothAdapter = null;

    /**
     * Sets up the buttons that are to appear. Different buttons become visible based on the user
     * type.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        ((TextView) findViewById(R.id.version)).setText(Constants.VERSION);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String type = sharedPreferences.getString(Constants.Settings.USER_TYPE, "");
        eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!eventID.equals("")) {
            pitScoutDB = new PitScoutDB(this, eventID);
            matchScoutDB = new MatchScoutDB(this, eventID);
            superScoutDB = new SuperScoutDB(this, eventID);
            statsDB = new StatsDB(this, eventID);
            driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventID);
            scheduleDB = new ScheduleDB(this, eventID);
            syncDB = new SyncDB(this, eventID);
        }

        // If settings have been saved then set up the database helpers and the sync handler
        if (!eventID.equals("") && bluetoothAdapter != null) {
            handler = new SyncHandler();
            bluetoothSync = new BluetoothSync(handler, false);
            handler.setDatabaseHelpers(matchScoutDB, pitScoutDB, superScoutDB, driveTeamFeedbackDB, statsDB, syncDB, scheduleDB);
            handler.setContext(this);
        }

        setupButton(R.id.settings_button);

        // Depending on the user type set up the buttons
        switch (type) {
            case Constants.User_Types.MATCH_SCOUT: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.scoutMatch_button);
                if (bluetoothAdapter != null) {
                    setupButton(R.id.syncMatch_button);
                }
                break;
            }
            case Constants.User_Types.PIT_SCOUT: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.scoutPit_button);
                if (bluetoothAdapter != null) {
                    setupButton(R.id.syncPit_button);
                    setupButton(R.id.picture_transer_button);
                }
                break;
            }
            case Constants.User_Types.SUPER_SCOUT: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.superScout_button);
                if (bluetoothAdapter != null) {
                    setupButton(R.id.syncSuper_button);
                }
                break;
            }
            case Constants.User_Types.DRIVE_TEAM: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.matchPlanning_button);
                setupButton(R.id.viewTeam_button);
                setupButton(R.id.viewMatch_button);
                setupButton(R.id.eliminationMatches_button);
                setupButton(R.id.viewEvent_button);
                setupButton(R.id.viewNotes_button);
                setupButton(R.id.feedback_button);
                if (bluetoothAdapter != null) {
                    setupButton(R.id.syncDriveTeam_button);
                    setupButton(R.id.picture_transer_button);
                    setupButton(R.id.bluetooth_button);
                    if (bluetoothAdapter.isEnabled()) {
                        findViewById(R.id.bluetooth_button).setBackgroundColor(Color.GREEN);
                    } else {
                        findViewById(R.id.bluetooth_button).setBackgroundColor(Color.RED);
                    }
                }
                break;
            }
            case Constants.User_Types.STRATEGY: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.matchPlanning_button);
                setupButton(R.id.viewTeam_button);
                setupButton(R.id.viewMatch_button);
                setupButton(R.id.eliminationMatches_button);
                setupButton(R.id.viewEvent_button);
                setupButton(R.id.viewNotes_button);
                setupButton(R.id.viewPickList_button);
                if(bluetoothAdapter != null) {
                    setupButton(R.id.syncStrategy_button);
                    setupButton(R.id.picture_transer_button);

                }
                break;
            }
            case Constants.User_Types.SERVER: {
                //setupButton(R.id.upload_download_button);
                if(bluetoothAdapter != null) {
                    setupButton(R.id.server_button);
                    setupButton(R.id.picture_transer_button);
                }
                break;
            }
            case Constants.User_Types.ADMIN: {
                setupButton(R.id.matchSchedule_button);

                setupButton(R.id.scoutMatch_button);
                setupButton(R.id.scoutPit_button);
                setupButton(R.id.superScout_button);
                setupButton(R.id.feedback_button);

                setupButton(R.id.matchPlanning_button);

                setupButton(R.id.viewTeam_button);
                setupButton(R.id.viewMatch_button);
                setupButton(R.id.eliminationMatches_button);
                setupButton(R.id.viewEvent_button);
                setupButton(R.id.viewNotes_button);
                setupButton(R.id.viewPickList_button);

                setupButton(R.id.sync_button);
                setupButton(R.id.aggregate_button);

                setupButton(R.id.database_button);
                setupButton(R.id.file_button);
                //setupButton(R.id.upload_download_button);
                setupButton(R.id.teamlist_button);

                if (bluetoothAdapter != null) {
                    setupButton(R.id.syncMatch_button);
                    setupButton(R.id.syncPit_button);
                    setupButton(R.id.syncSuper_button);
                    setupButton(R.id.syncDriveTeam_button);
                    setupButton(R.id.syncStrategy_button);
                    setupButton(R.id.picture_transer_button);
                    setupButton(R.id.bluetooth_button);
                    if (bluetoothAdapter.isEnabled()) {
                        findViewById(R.id.bluetooth_button).setBackgroundColor(Color.GREEN);
                    } else {
                        findViewById(R.id.bluetooth_button).setBackgroundColor(Color.RED);
                    }
                }
                break;
            }
        }
    }

    /**
     * Makes the button visible and attaches the on click listener
     *
     * @param btn id for the button to be set up
     */
    private void setupButton(int btn) {
        Button button = (Button) findViewById(btn);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);
    }

    /**
     * Provides the actions for each button when clicked
     *
     * @param v The view clicked (all buttons in this case)
     */
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.settings_button:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.matchSchedule_button:
                intent = new Intent(this, MatchSchedule.class);
                startActivity(intent);
                break;
            case R.id.scoutMatch_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.MATCH_SCOUTING);
                startActivity(intent);
                break;
            case R.id.syncMatch_button:
                new SyncMatches().execute();
                break;
            case R.id.scoutPit_button:
                intent = new Intent(this, TeamList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.PIT_SCOUTING);
                startActivity(intent);
                break;
            case R.id.syncPit_button:
                new SyncPit().execute();
                break;
            case R.id.superScout_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.SUPER_SCOUTING);
                startActivity(intent);
                break;
            case R.id.syncSuper_button:
                new SyncSuper().execute();
                break;
            case R.id.feedback_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.DRIVE_TEAM_FEEDBACK);
                startActivity(intent);
                break;
            case R.id.syncDriveTeam_button:
                new SyncDriveTeam().execute();
                break;
            case R.id.syncStrategy_button:
                new SyncStrategy().execute();
                break;
            case R.id.matchPlanning_button:
                intent = new Intent(this, MatchPlanning.class);
                startActivity(intent);
                break;
            case R.id.viewTeam_button:
                intent = new Intent(this, TeamList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.TEAM_VIEWING);
                startActivity(intent);
                break;
            case R.id.viewMatch_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.MATCH_VIEWING);
                startActivity(intent);
                break;
            case R.id.eliminationMatches_button:
                intent = new Intent(this, EliminationMatchList.class);
                startActivity(intent);
                break;
            case R.id.viewEvent_button:
                intent = new Intent(this, EventView.class);
                startActivity(intent);
                break;
            case R.id.viewNotes_button:
                intent = new Intent(this, Notes.class);
                startActivity(intent);
                break;
            case R.id.viewPickList_button:
                intent = new Intent(this, PickList.class);
                startActivity(intent);
                break;
            case R.id.sync_button:
                intent = new Intent(this, SyncActivity.class);
                startActivity(intent);
                break;
            case R.id.aggregate_button:
                ArrayList<Integer> matches = superScoutDB.getMatchNumbers();
                if (matches != null) {
                    new AggregateTask().execute(matches);
                }
                break;
            case R.id.database_button:
                intent = new Intent(this, DatabaseManagement.class);
                startActivity(intent);
                break;
            case R.id.file_button:
                intent = new Intent(this, FileView.class);
                startActivity(intent);
                break;
            case R.id.upload_download_button:
                //TODO: Fill in
                break;
            case R.id.picture_transer_button:
                intent = new Intent(this, PictureTransfer.class);
                startActivity(intent);
                break;
            case R.id.teamlist_button:
                intent = new Intent(this, TeamListBuilder.class);
                startActivity(intent);
                break;
            case R.id.server_button:
                intent = new Intent(this, Server.class);
                startActivity(intent);
                break;
            case R.id.bluetooth_button:
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.disable();
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.RED);
                } else {
                    bluetoothAdapter.enable();
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.GREEN);
                }
                break;
        }
    }

    /**
     * Time out function for connecting to another tablet via bluetooth
     *
     * @return if a connection happens before the timeout then return true otherwise false
     */
    private boolean timeout() {
        long time = SystemClock.currentThreadTimeMillis();
        while (!bluetoothSync.isConnected()) {
            if (SystemClock.currentThreadTimeMillis() > time + Constants.Bluetooth.CONNECTION_TIMEOUT) {
                return false;
            }
        }
        return true;
    }

    /**
     * Bluetooth Handler with specific display text for log and toast
     */
    private class SyncHandler extends BluetoothHandler {
        /**
         * Displays the log text received from the Bluetooth Handler as a Toast Message
         *
         * @param text Text to display on the screen
         */
        @Override
        public void displayText(String text) {
            Log.d(TAG, text);
            Toast.makeText(HomeScreen.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Asynchronous Task that syncs the match data with the server tablet
     */
    private class SyncMatches extends AsyncTask<Void, String, Void> {
        /**
         * Connects to the server tablet and sends the match data from the database table for
         * the current event
         *
         * @param params nothing
         * @return nothing
         */
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Connecting to Server");
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice server = null;
            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                if (connectedName.equals(Constants.Bluetooth.SERVER_NAME)) {
                    server = device;
                    break;
                }
            }

            if (server == null) {
                publishProgress("Cannot find Server in Paired Devices List");
                return null;
            }

            int i;
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                bluetoothSync.connect(server, false);
                if (timeout()) {
                    break;
                }
            }
            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                publishProgress("Connection to Server Failed");
                return null;
            }

            String connectedAddress = bluetoothSync.getConnectedAddress();
            String lastUpdated = syncDB.getMatchLastUpdated(connectedAddress);
            syncDB.updateMatchSync(connectedAddress);

            String matchUpdatedText = Constants.Bluetooth.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfo());
            if (!matchUpdatedText.equals(String.format("%c[]", Constants.Bluetooth.MATCH_HEADER))) {
                for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                    if (bluetoothSync.write(matchUpdatedText.getBytes())) {
                        break;
                    } else {
                        publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                    }
                }
                if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                    Utilities.JsonToMatchDB(matchScoutDB, matchUpdatedText);
                    publishProgress("Match Data Requeued");
                } else {
                    publishProgress("Match Data Sent");
                }
            } else {
                publishProgress("No new Match Data to send");
            }

            bluetoothSync.stop();

            return null;
        }

        /**
         * Displays text to the screen through Toast
         *
         * @param values First element is the text to be displayed to the screen
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(HomeScreen.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Asynchronous Task that syncs the pit data with the server tablet
     */
    private class SyncPit extends AsyncTask<Void, String, Void> {
        /**
         * Connects to the server tablet and sends the pit data from the database table for
         * the current event
         *
         * @param params nothing
         * @return nothing
         */
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Connecting to Server");
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice server = null;
            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                if (connectedName.equals(Constants.Bluetooth.SERVER_NAME)) {
                    server = device;
                    break;
                }
            }

            if (server == null) {
                publishProgress("Cannot find Server in Paired Devices List");
                return null;
            }

            int i;
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                bluetoothSync.connect(server, false);
                if (timeout()) {
                    break;
                }
            }
            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                publishProgress("Connection to Server Failed");
                return null;
            }

            String connectedAddress = bluetoothSync.getConnectedAddress();
            String lastUpdated = syncDB.getPitLastUpdated(connectedAddress);
            syncDB.updatePitSync(connectedAddress);

            String pitUpdatedText = Constants.Bluetooth.PIT_HEADER + Utilities.CursorToJsonString(pitScoutDB.getAllTeamsInfo());
            if (!pitUpdatedText.equals(String.format("%c[]", Constants.Bluetooth.PIT_HEADER))) {
                for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                    if (bluetoothSync.write(pitUpdatedText.getBytes())) {
                        break;
                    } else {
                        publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                    }
                }
                if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                    Utilities.JsonToPitDB(pitScoutDB, pitUpdatedText);
                    publishProgress("Pit Data Requeued");
                } else {
                    publishProgress("Pit Data Sent");
                }
            } else {
                publishProgress("No new Pit Data to send");
            }

            bluetoothSync.stop();

            return null;
        }

        /**
         * Displays text to the screen through Toast
         *
         * @param values First element is the text to be displayed to the screen
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(HomeScreen.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Asynchronous Task that syncs the super data with the server tablet
     */
    private class SyncSuper extends AsyncTask<Void, String, Void> {
        /**
         * Connects to the server tablet and sends the super data from the database table for
         * the current event
         *
         * @param params nothing
         * @return nothing
         */
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Connecting to Server");
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice server = null;
            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                if (connectedName.equals(Constants.Bluetooth.SERVER_NAME)) {
                    server = device;
                    break;
                }
            }

            if (server == null) {
                publishProgress("Cannot find Server in Paired Devices List");
                return null;
            }

            int i;
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                bluetoothSync.connect(server, false);
                if (timeout()) {
                    break;
                }
            }
            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                publishProgress("Connection to Server Failed");
                return null;
            }

            String connectedAddress = bluetoothSync.getConnectedAddress();
            String lastUpdated = syncDB.getSuperLastUpdated(connectedAddress);
            syncDB.updateSuperSync(connectedAddress);

            String superUpdatedText = Constants.Bluetooth.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatches());
            if (!superUpdatedText.equals(String.format("%c[]", Constants.Bluetooth.SUPER_HEADER))) {
                for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                    if (bluetoothSync.write(superUpdatedText.getBytes())) {
                        break;
                    } else {
                        publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                    }
                }
                if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                    Utilities.JsonToSuperDB(superScoutDB, superUpdatedText);
                    publishProgress("Super Data Requeued");
                } else {
                    publishProgress("Super Data Sent");
                }
            } else {
                publishProgress("No new Super Data to send");
            }

            bluetoothSync.stop();

            return null;
        }

        /**
         * Displays text to the screen through Toast
         *
         * @param values First element is the text to be displayed to the screen
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(HomeScreen.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Asynchronous Task that syncs the drive team feedback with the server tablet
     */
    private class SyncDriveTeam extends AsyncTask<Void, String, Void> {
        /**
         * Connects to the server tablet and sends the drive team feedback from the database table for
         * the current event
         *
         * @param params nothing
         * @return nothing
         */
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Connecting to Server");
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice server = null;
            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                if (connectedName.equals(Constants.Bluetooth.SERVER_NAME)) {
                    server = device;
                    break;
                }
            }

            if (server == null) {
                publishProgress("Cannot find Server in Paired Devices List");
                return null;
            }

            int i;
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                bluetoothSync.connect(server, false);
                if (timeout()) {
                    break;
                }
            }
            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                publishProgress("Connection to Server Failed");
                return null;
            }

            String connectedAddress = bluetoothSync.getConnectedAddress();
            String lastUpdated = syncDB.getDriveTeamLastUpdated(connectedAddress);
            syncDB.updateDriveTeamSync(connectedAddress);

            String driveTeamUpdatedText = Constants.Bluetooth.DRIVE_TEAM_FEEDBACK_HEADER + Utilities.CursorToJsonString(driveTeamFeedbackDB.getAllComments());
            if (!driveTeamUpdatedText.equals(String.format("%c[]", Constants.Bluetooth.DRIVE_TEAM_FEEDBACK_HEADER))) {
                for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                    if (bluetoothSync.write(driveTeamUpdatedText.getBytes())) {
                        break;
                    } else {
                        publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                    }
                }
                if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                    Utilities.JsonToDriveTeamDB(driveTeamFeedbackDB, driveTeamUpdatedText);
                    publishProgress("Drive Team Feedback Requeued");
                    bluetoothSync.stop();
                    return null;
                } else {
                    publishProgress("Drive Team Feedback Sent");
                }
            } else {
                publishProgress("No new Drive Team Feedback to send");
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_MATCH.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Match Data Sent");
                while (!handler.getReceived()) {
                    Log.d(TAG, String.format("Received: %d", handler.getReceived() ? 1 : 0));
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                    SystemClock.sleep(500);
                }
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_PIT.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Pit Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_SUPER.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Super Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_STATS.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Stats Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            bluetoothSync.stop();
            return null;
        }

        /**
         * Displays text to the screen through Toast
         *
         * @param values First element is the text to be displayed to the screen
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(HomeScreen.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Asynchronous Task that requests data from the server tablet
     */
    private class SyncStrategy extends AsyncTask<Void, String, Void> {
        /**
         * Connects to the server tablet and sends the drive team feedback from the database table for
         * the current event
         *
         * @param params nothing
         * @return nothing
         */
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Connecting to Server");
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
            BluetoothDevice server = null;
            for (BluetoothDevice device : devices) {
                String connectedName = device.getName();
                if (connectedName.equals(Constants.Bluetooth.SERVER_NAME)) {
                    server = device;
                    break;
                }
            }

            if (server == null) {
                publishProgress("Cannot find Server in Paired Devices List");
                return null;
            }

            int i;
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                bluetoothSync.connect(server, false);
                if (timeout()) {
                    break;
                }
            }
            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                publishProgress("Connection to Server Failed");
                return null;
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_MATCH.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Match Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_PIT.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Pit Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_SUPER.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Super Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            handler.setReceived(false);
            for (i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                if (bluetoothSync.write(Constants.Bluetooth.REQUEST_STATS.getBytes())) {
                    break;
                } else {
                    publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.Bluetooth.NUM_ATTEMPTS));
                }
            }

            if (i == Constants.Bluetooth.NUM_ATTEMPTS) {
                bluetoothSync.stop();
                return null;
            } else {
                publishProgress("Request for Stats Data Sent");
                while (!handler.getReceived()) {
                    if (!bluetoothSync.isConnected()) {
                        publishProgress("Connection lost during request");
                        bluetoothSync.stop();
                        return null;
                    }
                }
            }

            bluetoothSync.stop();
            return null;
        }

        /**
         * Displays text to the screen through Toast
         *
         * @param values First element is the text to be displayed to the screen
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(HomeScreen.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    private class AggregateTask extends AsyncTask<ArrayList<Integer>, Void, Void> {
        @Override
        public void onPreExecute() {
            Toast.makeText(HomeScreen.this, "Beginning Calculations...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(ArrayList<Integer>... params) {
            ArrayList<Integer> matches = params[0];
            AggregateStats.updateSuper(new HashSet<Integer>(matches), matchScoutDB, superScoutDB, statsDB, eventID, HomeScreen.this);
            return null;
        }

        @Override
        public void onPostExecute(Void Result) {
            Toast.makeText(HomeScreen.this, "Data Aggregated", Toast.LENGTH_SHORT).show();
        }
    }
}
