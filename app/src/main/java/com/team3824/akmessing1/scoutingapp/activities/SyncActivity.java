package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.utilities.CircularBuffer;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothHandler;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothSync;

import java.util.ArrayList;
import java.util.Set;

/**
 *  Admin Activity to control sending and requesting data to and from the other tablets
 */
public class SyncActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static String TAG = "SyncActivity";

    private final String RED = "red";
    private final String GREEN = "green";
    private final String BLUE = "blue";
    private final String CYAN = "cyan";
    private final String YELLOW = "#DAA520";
    private final String BLACK = "black";

    Object[] pairedDevices;
    Spinner pairedSpinner, actionSpinner;

    MatchScoutDB matchScoutDB;
    PitScoutDB pitScoutDB;
    SuperScoutDB superScoutDB;
    DriveTeamFeedbackDB driveTeamFeedbackDB;
    ScheduleDB scheduleDB;
    StatsDB statsDB;
    SyncDB syncDB;

    CircularBuffer circularBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private TextView textView;
    private BluetoothSync bluetoothSync;
    private SyncHandler handler;
    private String selectedAddress;

    /**
     *  Checks if this device supports bluetooth and if bluetooth enabled. Also sets up the paired
     *  devices dropdown and the actions dropdown.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");

        ((Button) findViewById(R.id.back)).setOnClickListener(this);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
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
                        BluetoothDevice bluetoothDevice = (BluetoothDevice) pairedDevices[i];
                        pairedDevicesArrayAdapter.add(String.format("%s - %s", bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                    }
                }

                ArrayAdapter<String> actionArrayAdapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
                actionSpinner = (Spinner) findViewById(R.id.action);
                actionSpinner.setAdapter(actionArrayAdapter);
                actionArrayAdapter.addAll(Constants.SYNC_ACTIONS);
                actionSpinner.setOnItemSelectedListener(this);


                handler = new SyncHandler();
                bluetoothSync = new BluetoothSync(handler, false);

                textView = (TextView) findViewById(R.id.sync_log);
                textView.setMovementMethod(ScrollingMovementMethod.getInstance());

                matchScoutDB = new MatchScoutDB(this, eventID);
                pitScoutDB = new PitScoutDB(this, eventID);
                superScoutDB = new SuperScoutDB(this, eventID);
                driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventID);
                scheduleDB = new ScheduleDB(this, eventID);
                statsDB = new StatsDB(this, eventID);
                syncDB = new SyncDB(this, eventID);

                ((Button) findViewById(R.id.execute_action)).setOnClickListener(this);

                circularBuffer = new CircularBuffer(20);

                bluetoothSync.start();
            } else {
                findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
                findViewById(R.id.paired_devices).setVisibility(View.GONE);
                findViewById(R.id.execute_action).setVisibility(View.GONE);
                findViewById(R.id.action).setVisibility(View.GONE);
                findViewById(R.id.sync_log).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.bluetooth_text).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.bluetooth_text)).setText("No Bluetooth on this device");
            findViewById(R.id.paired_devices).setVisibility(View.GONE);
            findViewById(R.id.execute_action).setVisibility(View.GONE);
            findViewById(R.id.action).setVisibility(View.GONE);
            findViewById(R.id.sync_log).setVisibility(View.GONE);
        }
    }

    /**
     *  Kills the bluetooth threads
     */
    @Override
    public void onDestroy() {
        if (bluetoothSync != null) {
            bluetoothSync.stop();
        }
        super.onDestroy();
    }

    /**
     *  Gets the file names for the robot pictures for the current event
     * @param cursor The response from the database query
     * @return
     */
    ArrayList<String> getImageFiles(Cursor cursor) {
        ArrayList<String> filenames = new ArrayList<>();
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.getColumnIndex(Constants.PIT_ROBOT_PICTURE) != -1) {
                if (cursor.getType(cursor.getColumnIndex(Constants.PIT_ROBOT_PICTURE)) == Cursor.FIELD_TYPE_STRING) {
                    String filename = cursor.getString(cursor.getColumnIndex(Constants.PIT_ROBOT_PICTURE));
                    if (!filename.equals("")) {
                        Log.d(TAG, filename);
                        filenames.add(filename);
                    }
                }
            }
        }
        return filenames;
    }

    /**
     *  Handler for clicking the buttons
     * @param v The view that was clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.execute_action:
                if (pairedSpinner.getSelectedItemPosition() > 0 && actionSpinner.getSelectedItemPosition() > 0) {
                    int position = actionSpinner.getSelectedItemPosition();
                    if (bluetoothSync.getState() == BluetoothSync.STATE_CONNECTED) {
                        String text;
                        switch (position) {

                            case Constants.SYNC_NONE_INDEX: //None
                                Log.e(TAG, "This should never happen...");
                                rotateText(RED, "This should never happen...");
                                break;
                            case Constants.SYNC_PING_INDEX: // Ping
                                Log.d(TAG, "Sending Ping...");
                                rotateText(BLUE, "Sending Ping...");
                                for (int i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                                    if (bluetoothSync.write(Constants.PING.getBytes())) {
                                        break;
                                    }
                                }
                                break;
                            case Constants.SYNC_SEND_MATCH_INDEX:
                                break;
                            case Constants.SYNC_SEND_PIT_INDEX:
                                break;
                            case Constants.SYNC_SEND_SUPER_INDEX:
                                break;
                            case Constants.SYNC_SEND_FEEDBACK_INDEX:
                                break;
                            case Constants.SYNC_SEND_STATS_INDEX:
                                break;
                            case Constants.SYNC_SEND_ALL_INDEX:
                                break;
                            case Constants.SYNC_SEND_SCHEDULE_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_MATCH_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_PIT_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_SUPER_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_FEEDBACK_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_STATS_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_ALL_INDEX:
                                break;
                            case Constants.SYNC_RECEIVE_SCHEDULE_INDEX:
                                break;
                        }
                    }
                }
                break;
        }
    }

    /**
     * Starts the connection task when a tablet is selected from the dropdown
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.paired_devices) {
            String info = String.valueOf(pairedSpinner.getSelectedItem());
            if (!info.equals("None")) {
                selectedAddress = info.substring(info.length() - 17);
                new ConnectionTask().execute((BluetoothDevice) pairedDevices[position - 1]);
            }
        }
    }

    /**
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     *  Rotates the circular buffer with the new text and displays it in the log text view in the
     *  specified color
     *
     * @param color The color this particular line of text will be displayed as
     * @param text The text that is added
     */
    public void rotateText(String color, String text) {
        circularBuffer.insert(String.format("<font color='%s'>%s</font><br>", color, text));
        textView.setText(Html.fromHtml(circularBuffer.toString()));
    }

    /**
     *  Bluetooth message handler with custom display text that adds to the log text view
     */
    private class SyncHandler extends BluetoothHandler {
        @Override
        public void displayText(String text) {
            rotateText(BLACK, text);
        }
    }

    /**
     *  Asynchronous Task that attempts to connect to the selected tablet
     */
    private class ConnectionTask extends AsyncTask<BluetoothDevice, Void, Void> {

        /**
         * Lets user know the connection attempt is starting
         */
        @Override
        protected void onPreExecute() {
            rotateText(CYAN, "Attempting to connect");
        }

        /**
         * Attempts to connect
         * @param params The Bluetooth Device to connect to
         * @return nothing
         */
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

        /**
         * Displays if connection was successful
         * @param result nothing
         */
        @Override
        protected void onPostExecute(Void result) {
            if (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                rotateText(RED, "Connection failed");
                pairedSpinner.setSelection(0);
                bluetoothSync.start();
            } else {
                rotateText(GREEN, String.format("Connnected to %s", bluetoothSync.getConnectedName()));
            }
        }
    }
}
