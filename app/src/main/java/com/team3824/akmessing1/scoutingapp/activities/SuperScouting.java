package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_SuperScout;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothHandler;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Activity that holds all the fragments for super scouting
 */
public class SuperScouting extends AppCompatActivity {

    final private String TAG = "SuperScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_SuperScout adapter;

    private int matchNumber;

    private String eventId;

    private ArrayList<Integer> arrayList;

    private boolean nextMatch = false;
    private boolean practice = false;

    /**
     * Sets up the view page, pager adapter, and tab layout
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_scouting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.super_scouting_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        matchNumber = extras.getInt(Constants.MATCH_NUMBER);
        if (matchNumber > 0) {
            setTitle("Match Number: " + matchNumber);
        } else {
            practice = true;
            setTitle("Practice Match");
        }


        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);

        Cursor cursor = scheduleDB.getMatch(matchNumber);
        arrayList = new ArrayList<Integer>();
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)));
        arrayList.add(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)));

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.super_view_pager);
        adapter = new FPA_SuperScout(getFragmentManager(), arrayList);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.super_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        // Get data from super scout db
        SuperScoutDB superScoutDB = new SuperScoutDB(this, eventId);
        ScoutMap map = superScoutDB.getMatchInfo(matchNumber);
        if (map != null) {
            adapter.setValueMap(map);
        }

        if (scheduleDB.getNumMatches() > matchNumber)
            nextMatch = true;

        Utilities.setupUI(this, findViewById(android.R.id.content));
    }

    /**
     * Creates the overflow menu
     *
     * @param menu The menu to be filled
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_overflow, menu);
        if (matchNumber == 1 || practice) {
            menu.removeItem(R.id.previous);
        }
        if (!nextMatch) {
            menu.removeItem(R.id.next);
        }
        return true;
    }

    /**
     * Implements the actions that happen when a menu item is selected
     *
     * @param item The menu item selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                home_pres();
                break;
            case R.id.back:
                back_press();
                break;
            case R.id.previous:
                previous_press();
                break;
            case R.id.next:
                next_press();
                break;
            default:
                assert false;
        }
        return true;
    }

    /**
     * Brings up a dialog box asking the user if they want to save. If so a task is started to save
     * and send the data to the server tablet. The user is then brought to the home screen.
     */
    private void home_pres() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                ScoutMap data = new ScoutMap();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    if (!practice) {
                        new SaveTask().execute(data);
                    }

                    // Go to the next match
                    Intent intent = new Intent(SuperScouting.this, HomeScreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SuperScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Cancel Option
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dialogbox goes away
            }
        });

        // Continue w/o Saving Option
        builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * Brings up a dialog box asking the user if they want to save. If so a task is started to save
     * and send the data to the server tablet. The user is then brought to the match list screen.
     */
    private void back_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                ScoutMap data = new ScoutMap();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    if (!practice) {
                        new SaveTask().execute(data);
                    }

                    // Go to the next match
                    Intent intent = new Intent(SuperScouting.this, MatchList.class);
                    intent.putExtra(Constants.NEXT_PAGE, Constants.SUPER_SCOUTING);
                    startActivity(intent);
                } else {
                    Toast.makeText(SuperScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Cancel Option
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dialogbox goes away
            }
        });

        // Continue w/o Saving Option
        builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, MatchList.class);
                intent.putExtra(Constants.NEXT_PAGE, Constants.SUPER_SCOUTING);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * Brings up a dialog box asking the user if they want to save. If so a task is started to save
     * and send the data to the server tablet. The previous match super scouting is then brought up.
     */
    private void previous_press() {
        Log.d(TAG, "previous match pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                ScoutMap data = new ScoutMap();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    if (!practice) {
                        new SaveTask().execute(data);
                    }

                    // Go to the next match
                    Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
                    if (practice) {
                        intent.putExtra(Constants.MATCH_NUMBER, -1);
                    } else {
                        intent.putExtra(Constants.MATCH_NUMBER, matchNumber - 1);
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(SuperScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Cancel Option
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dialogbox goes away
            }
        });

        // Continue w/o Saving Option
        builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go to the next match
                Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
                if (practice) {
                    intent.putExtra(Constants.MATCH_NUMBER, -1);
                } else {
                    intent.putExtra(Constants.MATCH_NUMBER, matchNumber - 1);
                }
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * Brings up a dialog box asking the user if they want to save. If so a task is started to save
     * and send the data to the server tablet. The next match super scouting is then brought up.
     */
    private void next_press() {
        Log.d(TAG, "next match pressed");

        AlertDialog.Builder builder = new AlertDialog.Builder(SuperScouting.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Collect values from all the custom elements
                        List<ScoutFragment> fragmentList = adapter.getAllFragments();
                        ScoutMap data = new ScoutMap();
                        String error = "";
                        for (ScoutFragment fragment : fragmentList) {
                            error += fragment.writeContentsToMap(data);
                        }

                        if (error.equals("")) {
                            Log.d(TAG, "Saving values");
                            if (!practice) {
                                new SaveTask().execute(data);
                            }

                            // Go to the next match
                            Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
                            if (practice) {
                                intent.putExtra(Constants.MATCH_NUMBER, -1);
                            } else {
                                intent.putExtra(Constants.MATCH_NUMBER, matchNumber + 1);
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(SuperScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                        }
                    }
                }

        );

        // Cancel Option
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dialogbox goes away
                    }
                }

        );

        // Continue w/o Saving Option
        builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Go to the next match
                        Intent intent = new Intent(SuperScouting.this, SuperScouting.class);
                        if (practice) {
                            intent.putExtra(Constants.MATCH_NUMBER, -1);
                        } else {
                            intent.putExtra(Constants.MATCH_NUMBER, matchNumber + 1);
                        }
                        startActivity(intent);
                    }
                }

        );
        builder.show();
    }

    /**
     * Asynchronous Task that saves the data in the database and attempts to send to the server
     * tablet
     */
    private class SaveTask extends AsyncTask<ScoutMap, String, Void> {

        BluetoothSync bluetoothSync;

        /**
         * Saves the data to the database and attempts to send to the server tablet
         *
         * @param maps The data
         * @return nothing
         */
        @Override
        protected Void doInBackground(ScoutMap... maps) {
            ScoutMap data = maps[0];

            // Add the team and match numbers
            SuperScoutDB superScoutDB = new SuperScoutDB(SuperScouting.this, eventId);
            data.put(SuperScoutDB.KEY_MATCH_NUMBER, matchNumber);
            data.put(SuperScoutDB.KEY_BLUE1, arrayList.get(0));
            data.put(SuperScoutDB.KEY_BLUE2, arrayList.get(1));
            data.put(SuperScoutDB.KEY_BLUE3, arrayList.get(2));
            data.put(SuperScoutDB.KEY_RED1, arrayList.get(3));
            data.put(SuperScoutDB.KEY_RED2, arrayList.get(4));
            data.put(SuperScoutDB.KEY_RED3, arrayList.get(5));
            // Store values to the database
            superScoutDB.updateMatch(data);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                bluetoothSync = new BluetoothSync(new BluetoothHandler(), false);
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                BluetoothDevice server = null;
                for (BluetoothDevice device : devices) {
                    String connectedName = device.getName();
                    if (connectedName.equals(Constants.SERVER_NAME)) {
                        server = device;
                        break;
                    }
                }

                if (server == null) {
                    publishProgress("Cannot find Server in Paired Devices List");
                    return null;
                }

                int i;
                for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                    bluetoothSync.connect(server, false);
                    if (timeout()) {
                        break;
                    }
                }
                if (i == Constants.NUM_ATTEMPTS) {
                    bluetoothSync.stop();
                    publishProgress("Connection to Server Failed");
                    return null;
                }

                SyncDB syncDB = new SyncDB(SuperScouting.this, eventId);
                String connectedAddress = bluetoothSync.getConnectedAddress();
                String lastUpdated = syncDB.getSuperLastUpdated(connectedAddress);
                syncDB.updateSuperSync(connectedAddress);

                String superUpdatedText = Constants.SUPER_HEADER + Utilities.CursorToJsonString(superScoutDB.getAllMatchesSince(lastUpdated));
                if (!superUpdatedText.equals(String.format("%c[]", Constants.SUPER_HEADER))) {
                    for (i = 0; i < Constants.NUM_ATTEMPTS; i++) {
                        if (bluetoothSync.write(superUpdatedText.getBytes())) {
                            break;
                        } else {
                            publishProgress(String.format("Attempt %d of %d failed", i + 1, Constants.NUM_ATTEMPTS));
                        }
                    }
                    if (i == Constants.NUM_ATTEMPTS) {
                        Utilities.JsonToSuperDB(superScoutDB, superUpdatedText);
                        publishProgress("Super Data Requeued");
                    } else {
                        publishProgress("Super Data Sent");
                    }
                } else {
                    publishProgress("No new Super Data to send");
                }

                bluetoothSync.stop();
            }
            return null;
        }

        /**
         * Timeout for connecting to the Server
         *
         * @return Whether the connection timed out or not
         */
        private boolean timeout() {
            long time = SystemClock.currentThreadTimeMillis();
            while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                if (SystemClock.currentThreadTimeMillis() > time + Constants.CONNECTION_TIMEOUT) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Displays the progress
         *
         * @param values The text to display
         */
        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(SuperScouting.this, text, Toast.LENGTH_SHORT).show();
        }
    }


}
