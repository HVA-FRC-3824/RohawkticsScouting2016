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
 * Admin Activity to control sending and requesting data to and from the other tablets
 *
 * @author Andrew Messing
 * @version
 */
public class SyncActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static String TAG = "SyncActivity";

    private final String YELLOW = "#DAA520";

    private Object[] pairedDevices;
    private Spinner pairedSpinner;
    private Spinner actionSpinner;

    private CircularBuffer circularBuffer;
    private TextView textView;
    private BluetoothSync bluetoothSync;

    /**
     * Checks if this device supports bluetooth and if bluetooth enabled. Also sets up the paired
     * devices dropdown and the actions dropdown.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        findViewById(R.id.back).setOnClickListener(this);

        // Get local Bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                actionArrayAdapter.addAll(Constants.Sync_Activity.SYNC_ACTIONS);
                actionSpinner.setOnItemSelectedListener(this);


                SyncHandler handler = new SyncHandler();
                bluetoothSync = new BluetoothSync(handler, false);

                textView = (TextView) findViewById(R.id.sync_log);
                textView.setMovementMethod(ScrollingMovementMethod.getInstance());

                MatchScoutDB matchScoutDB = new MatchScoutDB(this, eventID);
                PitScoutDB pitScoutDB = new PitScoutDB(this, eventID);
                SuperScoutDB superScoutDB = new SuperScoutDB(this, eventID);
                DriveTeamFeedbackDB driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventID);
                ScheduleDB scheduleDB = new ScheduleDB(this, eventID);
                StatsDB statsDB = new StatsDB(this, eventID);
                SyncDB syncDB = new SyncDB(this, eventID);

                findViewById(R.id.execute_action).setOnClickListener(this);

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
     * Kills the bluetooth threads
     */
    @Override
    public void onDestroy() {
        if (bluetoothSync != null) {
            bluetoothSync.stop();
        }
        super.onDestroy();
    }

    /**
     * Gets the file names for the robot pictures for the current event
     *
     * @param cursor The response from the database query
     * @return
     */
    ArrayList<String> getImageFiles(Cursor cursor) {
        ArrayList<String> filenames = new ArrayList<>();
        int i = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            if (cursor.getColumnIndex(Constants.Pit_Inputs.PIT_ROBOT_PICTURE) != -1) {
                if (cursor.getType(cursor.getColumnIndex(Constants.Pit_Inputs.PIT_ROBOT_PICTURE)) == Cursor.FIELD_TYPE_STRING) {
                    String filename = cursor.getString(cursor.getColumnIndex(Constants.Pit_Inputs.PIT_ROBOT_PICTURE));
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
     * Handler for clicking the buttons
     *
     * @param v The view that was clicked
     */
    //TODO: Finish
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

                            case Constants.Sync_Activity.SYNC_NONE_INDEX: //None
                                assert false;
                                break;
                            case Constants.Sync_Activity.SYNC_PING_INDEX: // Ping
                                Log.d(TAG, "Sending Ping...");
                                String BLUE = "blue";
                                rotateText(BLUE, "Sending Ping...");
                                for (int i = 0; i < Constants.Bluetooth.NUM_ATTEMPTS; i++) {
                                    if (bluetoothSync.write(Constants.Bluetooth.PING.getBytes())) {
                                        break;
                                    }
                                }
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_MATCH_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_PIT_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_SUPER_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_FEEDBACK_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_STATS_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_ALL_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_SEND_SCHEDULE_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_MATCH_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_PIT_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_SUPER_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_FEEDBACK_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_STATS_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_ALL_INDEX:
                                break;
                            case Constants.Sync_Activity.SYNC_RECEIVE_SCHEDULE_INDEX:
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
                String selectedAddress = info.substring(info.length() - 17);
                new ConnectionTask().execute((BluetoothDevice) pairedDevices[position - 1]);
            }
        }
    }

    /**
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Rotates the circular buffer with the new text and displays it in the log text view in the
     * specified color
     *
     * @param color The color this particular line of text will be displayed as
     * @param text  The text that is added
     */
    private void rotateText(String color, String text) {
        circularBuffer.insert(String.format("<font color='%s'>%s</font><br>", color, text));
        textView.setText(Html.fromHtml(circularBuffer.toString()));
    }

    /**
     * Bluetooth message handler with custom display text that adds to the log text view
     */
    private class SyncHandler extends BluetoothHandler {
        @Override
        public void displayText(String text) {
            String BLACK = "black";
            rotateText(BLACK, text);
        }
    }

    /**
     * Asynchronous Task that attempts to connect to the selected tablet
     */
    private class ConnectionTask extends AsyncTask<BluetoothDevice, Void, Void> {

        /**
         * Lets user know the connection attempt is starting
         */
        @Override
        protected void onPreExecute() {
            String CYAN = "cyan";
            rotateText(CYAN, "Attempting to connect");
        }

        /**
         * Attempts to connect
         *
         * @param params The Bluetooth Device to connect to
         * @return nothing
         */
        @Override
        protected Void doInBackground(BluetoothDevice... params) {
            bluetoothSync.connect((params[0]), false);
            long time = System.currentTimeMillis();
            long newTime = time;
            while (newTime < time + Constants.Bluetooth.CONNECTION_TIMEOUT && bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                newTime = System.currentTimeMillis();
            }
            return null;
        }

        /**
         * Displays if connection was successful
         *
         * @param result nothing
         */
        @Override
        protected void onPostExecute(Void result) {
            if (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                String RED = "red";
                rotateText(RED, "Connection failed");
                pairedSpinner.setSelection(0);
                bluetoothSync.start();
            } else {
                String GREEN = "green";
                rotateText(GREEN, String.format("Connnected to %s", bluetoothSync.getConnectedName()));
            }
        }
    }
}
