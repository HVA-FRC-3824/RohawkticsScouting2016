package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
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
import android.os.Looper;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters.FPA_MatchScout;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SyncDB;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothHandler;
import com.team3824.akmessing1.scoutingapp.utilities.bluetooth.BluetoothSync;

import java.util.List;
import java.util.Set;

/**
 * Activity that holds the fragments for scouting matches.
 */
public class MatchScouting extends Activity {

    private static String TAG = "MatchScouting";

    private FPA_MatchScout adapter;

    private int teamNumber;
    private int matchNumber;

    private int prevTeamNumber = -1;
    private int nextTeamNumber = -1;
    private String eventId;

    private boolean practice = false;

    /**
     * Sets up the view pager, pager adapter, and tab layout.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_scouting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.match_scouting_toolbar);
        setActionBar(toolbar);

        // Get Match Number and Team Number from the intent
        Bundle extras = getIntent().getExtras();
        matchNumber = extras.getInt(Constants.Intent_Extras.MATCH_NUMBER);
        if (matchNumber > 0) {
            teamNumber = extras.getInt(Constants.Intent_Extras.TEAM_NUMBER);
            setTitle("Match Number: " + matchNumber + " Team Number: " + teamNumber);
        } else {
            practice = true;
            setTitle("Practice Match");
        }

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String allianceColor = sharedPreferences.getString(Constants.Settings.ALLIANCE_COLOR, "");

        // Set up tabs and pages for different fragments of a match
        findViewById(android.R.id.content).setKeepScreenOn(true);
        ViewPager viewPager = (ViewPager) findViewById(R.id.match_view_pager);
        adapter = new FPA_MatchScout(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.match_tab_layout);
        if (allianceColor.equals(Constants.Alliance_Colors.BLUE)) {
            tabLayout.setBackgroundColor(Color.BLUE);
        } else {
            tabLayout.setBackgroundColor(Color.RED);
        }
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        // Restore any values from the database if this team/match combo has been scouted before
        // (basically if updating)
        eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        MatchScoutDB matchScoutDB = new MatchScoutDB(this, eventId);
        ScoutMap map = matchScoutDB.getTeamMatchInfo(teamNumber, matchNumber);
        if (map != null) {
            adapter.setValueMap(map);
        }

        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
        // First match doesn't need a previous menu option
        if (!practice && matchNumber != 1) {
            Cursor prevCursor = scheduleDB.getMatch(matchNumber - 1);
            int allianceNum = sharedPreferences.getInt(Constants.Settings.ALLIANCE_NUMBER, 0);
            prevTeamNumber = prevCursor.getInt(prevCursor.getColumnIndex(allianceColor.toLowerCase() + allianceNum));
            Log.d(TAG, "Prev Team: " + prevTeamNumber);
        } else {
            Log.d(TAG, "No previous team");
        }

        // Determine if the last match or not
        Cursor nextCursor = null;
        if (!practice) {
            nextCursor = scheduleDB.getMatch(matchNumber + 1);
        }

        //Last match doesn't need a next button
        if (practice) {
            nextTeamNumber = 0;
        } else if (nextCursor != null && nextCursor.getCount() > 0) {
            int allianceNum = sharedPreferences.getInt(Constants.Settings.ALLIANCE_NUMBER, 0);
            nextTeamNumber = nextCursor.getInt(nextCursor.getColumnIndex(allianceColor.toLowerCase() + allianceNum));
            Log.d(TAG, "Next Team: " + nextTeamNumber);
        } else {
            Log.d(TAG, "No next team");
        }

        Utilities.setupUI(this, findViewById(android.R.id.content));

    }

    /**
     * Creates the overflow menu for the toolbar. Removes previous match or next match options if
     * they do not exist.
     *
     * @param menu The menu that is filled with the overflow menu.
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_overflow, menu);
        if (prevTeamNumber == -1) {
            menu.removeItem(R.id.previous);
        }
        if (nextTeamNumber == -1) {
            menu.removeItem(R.id.next);
        }
        return true;
    }

    /**
     * Implements the actions for the overflow menu
     *
     * @param item Menu item that is selected from the overflow menu
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                home_press();
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
     * The action that happens when the home button is pressed. Brings up dialog with options to save
     * and takes user to the home screen.
     */
    private void home_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
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
                    Intent intent = new Intent(MatchScouting.this, HomeScreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();

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
                Intent intent = new Intent(MatchScouting.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * The action that happens when the back button is pressed. Brings up dialog with options to save
     * and takes user to the match list.
     */
    private void back_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
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
                    Intent intent = new Intent(MatchScouting.this, MatchList.class);
                    intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.MATCH_SCOUTING);
                    startActivity(intent);
                } else {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(MatchScouting.this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE, Constants.Intent_Extras.MATCH_SCOUTING);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * The action that happens when the previous match button is pressed. Brings up dialog with options to save
     * and takes user to match scout the previous match.
     */
    private void previous_press() {
        Log.d(TAG, "previous match pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
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
                    Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                    if (practice) {
                        intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, -1);
                    } else {
                        intent.putExtra(Constants.Intent_Extras.TEAM_NUMBER, prevTeamNumber);
                        intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, matchNumber - 1);
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                if (practice) {
                    intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, -1);
                } else {
                    intent.putExtra(Constants.Intent_Extras.TEAM_NUMBER, prevTeamNumber);
                    intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, matchNumber - 1);
                }
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * The action that happens when the next match button is pressed. Brings up dialog with options to save
     * and takes user to match scout the next match.
     */
    private void next_press() {
        Log.d(TAG, "next match pressed");

        AlertDialog.Builder builder = new AlertDialog.Builder(MatchScouting.this);
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
                    Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                    if (practice) {
                        intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, -1);
                    } else {
                        intent.putExtra(Constants.Intent_Extras.TEAM_NUMBER, nextTeamNumber);
                        intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, matchNumber + 1);
                    }
                    startActivity(intent);
                } else {
                    Toast.makeText(MatchScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(MatchScouting.this, MatchScouting.class);
                if (practice) {
                    intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, -1);
                } else {
                    intent.putExtra(Constants.Intent_Extras.TEAM_NUMBER, nextTeamNumber);
                    intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, matchNumber + 1);
                }
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * Asynchronous Task to save the data to the database and attempt to send to the server.
     */
    private class SaveTask extends AsyncTask<ScoutMap, String, Void> {

        BluetoothSync bluetoothSync;

        @Override
        protected Void doInBackground(ScoutMap... maps) {
            ScoutMap data = maps[0];

            MatchScoutDB matchScoutDB = new MatchScoutDB(MatchScouting.this, eventId);
            data.put(MatchScoutDB.KEY_MATCH_NUMBER, matchNumber);
            data.put(MatchScoutDB.KEY_TEAM_NUMBER, teamNumber);
            data.put(MatchScoutDB.KEY_ID, String.format("%d_%d", matchNumber, teamNumber));
            // Store values to the database
            matchScoutDB.updateMatch(data);

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                if(Looper.myLooper() == null) {
                    Looper.prepare();
                }
                bluetoothSync = new BluetoothSync(new BluetoothHandler(), false);
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

                SyncDB syncDB = new SyncDB(MatchScouting.this, eventId);
                String connectedAddress = bluetoothSync.getConnectedAddress();
                String lastUpdated = syncDB.getMatchLastUpdated(connectedAddress);
                syncDB.updateMatchSync(connectedAddress);

                String matchUpdatedText = Constants.Bluetooth.MATCH_HEADER + Utilities.CursorToJsonString(matchScoutDB.getAllInfoSince(lastUpdated));
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
            }
            return null;
        }

        private boolean timeout() {
            long time = SystemClock.currentThreadTimeMillis();
            while (bluetoothSync.getState() != BluetoothSync.STATE_CONNECTED) {
                if (SystemClock.currentThreadTimeMillis() > time + Constants.Bluetooth.CONNECTION_TIMEOUT) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String text = values[0];
            Log.d(TAG, text);
            Toast.makeText(MatchScouting.this, text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        back_press();
    }
}
