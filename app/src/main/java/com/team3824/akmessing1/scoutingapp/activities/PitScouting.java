package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_PitScout;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Activity that holds the fragments for pit scouting
 */
public class PitScouting extends Activity {

    final private String TAG = "PitScouting";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_PitScout adapter;

    private int teamNumber;

    private String eventId;
    private String userType;
    private int prevTeamNumber = -1;
    private int nextTeamNumber = -1;

    /**
     * Sets up the view pager, pager adapter, and tab layout
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        Toolbar toolbar = (Toolbar) findViewById(R.id.pit_scouting_toolbar);
        setActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        teamNumber = extras.getInt(Constants.TEAM_NUMBER);
        setTitle("Team Number: " + teamNumber);

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.pit_view_pager);
        adapter = new FPA_PitScout(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = (TabLayout) findViewById(R.id.pit_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        userType = sharedPreferences.getString(Constants.USER_TYPE, "");
        PitScoutDB pitScoutDB = new PitScoutDB(this, eventId);
        Map<String, ScoutValue> map = pitScoutDB.getTeamMap(teamNumber);
        if (map.get(PitScoutDB.KEY_COMPLETE).getInt() > 0) {
            adapter.setValueMap(map);
        }

        prevTeamNumber = pitScoutDB.getPreviousTeamNumber(teamNumber);
        nextTeamNumber = pitScoutDB.getNextTeamNumber(teamNumber);

        Utilities.setupUI(this, findViewById(android.R.id.content));

    }

    /**
     * Creates the overflow menu for the toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.team_overflow, menu);
        if (prevTeamNumber == -1) {
            menu.removeItem(R.id.previous);
        }
        if (nextTeamNumber == -1) {
            menu.removeItem(R.id.next);
        }
        if (!userType.equals(Constants.ADMIN)) {
            menu.removeItem(R.id.reset);
        }
        return true;
    }

    /**
     * Implements the actions that happen when a option is selected from the overflow menu
     *
     * @param item The item that was selected from the overflow menu
     * @return
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
            case R.id.reset:
                reset();
                // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     *  The action that happens when the home button is pressed. Brings up dialog with options to save
     *  and takes user to the home screen.
     */
    private void home_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
        builder.setTitle("Save pit data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                Map<String, ScoutValue> data = new HashMap<>();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    Intent intent = new Intent(PitScouting.this, HomeScreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(PitScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(PitScouting.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     *  The action that happens when the back button is pressed. Brings up dialog with options to save
     *  and takes user to the team list.
     */
    private void back_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
        builder.setTitle("Save pit data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                Map<String, ScoutValue> data = new HashMap<>();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    // Add the team and match numbers
                    new SaveTask().execute(data);

                    Intent intent = new Intent(PitScouting.this, PitList.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(PitScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
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
                Intent intent2 = new Intent(PitScouting.this, PitList.class);
                startActivity(intent2);
            }
        });
        builder.show();
    }

    /**
     *  The action that happens when the previous team button is pressed. Brings up dialog with options to save
     *  and takes user to pit scout the previous team.
     */
    private void previous_press() {
        Log.d(TAG, "previous team pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
        builder.setTitle("Save pit data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                Map<String, ScoutValue> data = new HashMap<>();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    // Go to the next match
                    Intent intent = new Intent(PitScouting.this, PitScouting.class);
                    intent.putExtra(Constants.TEAM_NUMBER, prevTeamNumber);
                    startActivity(intent);
                } else {
                    Toast.makeText(PitScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(PitScouting.this, PitScouting.class);
                intent.putExtra(Constants.TEAM_NUMBER, prevTeamNumber);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     *  The action that happens when the next team button is pressed. Brings up dialog with options to save
     *  and takes user to pit scout the next team.
     */
    private void next_press() {
        Log.d(TAG, "next team pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
        builder.setTitle("Save pit data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Collect values from all the custom elements
                List<ScoutFragment> fragmentList = adapter.getAllFragments();
                Map<String, ScoutValue> data = new HashMap<>();
                String error = "";
                for (ScoutFragment fragment : fragmentList) {
                    error += fragment.writeContentsToMap(data);
                }

                if (error.equals("")) {
                    Log.d(TAG, "Saving values");
                    new SaveTask().execute(data);

                    // Go to the next match
                    Intent intent = new Intent(PitScouting.this, PitScouting.class);
                    intent.putExtra(Constants.TEAM_NUMBER, nextTeamNumber);
                    startActivity(intent);
                } else {
                    Toast.makeText(PitScouting.this, String.format("Error: %s", error), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Cancel Option
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dialog box goes away
            }
        });

        // Continue w/o Saving Option
        builder.setNegativeButton("Continue w/o Saving", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Go to the next match
                Intent intent = new Intent(PitScouting.this, PitScouting.class);
                intent.putExtra(Constants.TEAM_NUMBER, nextTeamNumber);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     *  Resets the page and the entry in the database.
     */
    //TODO: Shouldn't be needed any more because of the pit group system, so remove
    private void reset() {
        Log.d(TAG, "team delete pressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(PitScouting.this);
        builder.setTitle("Reset pit data?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PitScoutDB pitScoutDB = new PitScoutDB(PitScouting.this, eventId);
                pitScoutDB.resetTeam(teamNumber);
                Map<String, ScoutValue> map = new Hashtable<String, ScoutValue>();
                Intent intent = new Intent(PitScouting.this, PitScouting.class);
                intent.putExtra(Constants.TEAM_NUMBER, teamNumber);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     * Asynchronous Task to save the data to the database.
     */
    private class SaveTask extends AsyncTask<Map<String, ScoutValue>, Void, Void> {

        @Override
        protected Void doInBackground(Map<String, ScoutValue>... maps) {
            Map<String, ScoutValue> data = maps[0];

            // Change picture filename to use event id and team number
            String picture_filename = data.get(Constants.PIT_ROBOT_PICTURE).getString();
            File picture = new File(getFilesDir(), picture_filename);
            if (picture.exists()) {
                String newPathName = String.format("%s_%d.jpg", eventId, teamNumber);
                File newPath = new File(getFilesDir(), newPathName);
                picture.renameTo(newPath);
                data.remove(Constants.PIT_ROBOT_PICTURE);
                data.put(Constants.PIT_ROBOT_PICTURE, new ScoutValue(newPathName));
            }

            PitScoutDB pitScoutDB = new PitScoutDB(PitScouting.this, eventId);
            // Add the team and match numbers
            data.put(PitScoutDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
            // Store values to the database
            pitScoutDB.updatePit(data);
            return null;
        }
    }

}
