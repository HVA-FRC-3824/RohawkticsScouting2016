package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.views.CustomEdittext;

/**
 * Activity for the Drive Team to input comments about alliance partners during qualification matches.
 *
 * @author Andrew Messing
 *
 */
public class DTFeedback extends Activity {
    private String TAG = "DTFeedback";
    private DriveTeamFeedbackDB driveTeamFeedbackDB;
    private int team1 = -1, team2 = -1;
    private EditText commentEditText1, commentEditText2;

    /**
     * Sets up the toolbar, collects the two alliance partners for the given match from the
     * schedule database table, and fills in the comments section if a previous comment is in the
     * drive team feedback database table.
     *
     * @param savedInstanceState The Bundle containing anything saved from the last time this activity
     *                           was used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveteam_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.driveteam_feedback_toolbar);
        setActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        int matchNumber = extras.getInt(Constants.Intent_Extras.MATCH_NUMBER);
        String position = extras.getString("position");
        setTitle("Match Number: " + matchNumber);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        // Gets the two alliance partners from the schedule
        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
        Cursor cursor = scheduleDB.getMatch(matchNumber);

        // Sets up the other two teams based on which position we are
        if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)) == Constants.OUR_TEAM_NUMBER) {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3));
        } else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)) == Constants.OUR_TEAM_NUMBER) {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3));
        } else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)) == Constants.OUR_TEAM_NUMBER) {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2));
        } else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)) == Constants.OUR_TEAM_NUMBER) {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3));
        } else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)) == Constants.OUR_TEAM_NUMBER) {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3));
        } else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)) == Constants.OUR_TEAM_NUMBER) {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2));
        }

        if (team1 == -1) {
            Log.d(TAG, "3824 is not in match");
            Toast.makeText(this, "Error: wrong match", Toast.LENGTH_SHORT).show();
        } else {
            driveTeamFeedbackDB = new DriveTeamFeedbackDB(this, eventId);

            /*
                Sets the label for the first alliance partner with their team number and fills in
                the comment if their is a previous comment in the database.
            */
            TextView textview = (TextView) findViewById(R.id.team_number_1);
            textview.setText(String.valueOf(team1));
            String comment = driveTeamFeedbackDB.getComments(team1);
            CustomEdittext customEdittext = (CustomEdittext) findViewById(R.id.driveteam_feedback_1);
            commentEditText1 = (EditText) customEdittext.findViewById(R.id.edittext);
            commentEditText1.setText(comment);

            /*
                Sets the label for the second alliance partner with their team number and fills in
                the comment if their is a previous comment in the database.
            */
            textview = (TextView) findViewById(R.id.team_number_2);
            textview.setText(String.valueOf(team2));
            comment = driveTeamFeedbackDB.getComments(team2);
            customEdittext = (CustomEdittext) findViewById(R.id.driveteam_feedback_2);
            commentEditText2 = (EditText) customEdittext.findViewById(R.id.edittext);
            commentEditText2.setText(comment);
        }

        Utilities.setupUI(this, findViewById(android.R.id.content));

    }

    /**
     * Sets up the overflow menu for the toolbar.
     *
     * @param menu The menu to be filled
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driveteam_feedback_overflow, menu);
        return true;
    }

    /**
     * Event handler for when an option is selected from the toolbar overflow menu.
     *
     * @param item The menu item that is selected from the toolbar overflow menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.driveteam_feedback_home:
                home_press();
                break;
            case R.id.driveteam_feedback_back:
                back_press();
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    /**
     * Action for when the home button is pressed. Brings up a dialog box with the options for Save,
     * Continue w/o Saving, or cancel. If Save or Continue w/o Saving is selected then the Home Screen
     * Activity is brought up.
     */
    private void home_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DTFeedback.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Saving values");

                // Store values to the database
                driveTeamFeedbackDB.updateComments(team1, String.valueOf(commentEditText1.getText()));
                driveTeamFeedbackDB.updateComments(team2, String.valueOf(commentEditText2.getText()));

                Intent intent = new Intent(DTFeedback.this, HomeScreen.class);
                startActivity(intent);
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
                Intent intent = new Intent(DTFeedback.this, HomeScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    /**
     * Action for when the back button is pressed. Brings up a dialog box with the options for Save,
     * Continue w/o Saving, or cancel. If Save or Continue w/o Saving is selected then the Our Match
     * List Activity is brought up.
     */
    private void back_press() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DTFeedback.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "Saving values");
                // Store values to the database
                driveTeamFeedbackDB.updateComments(team1, String.valueOf(commentEditText1.getText()));
                driveTeamFeedbackDB.updateComments(team2, String.valueOf(commentEditText2.getText()));

                Intent intent = new Intent(DTFeedback.this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE,Constants.Intent_Extras.DRIVE_TEAM_FEEDBACK);
                startActivity(intent);
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
                Intent intent = new Intent(DTFeedback.this, MatchList.class);
                intent.putExtra(Constants.Intent_Extras.NEXT_PAGE,Constants.Intent_Extras.DRIVE_TEAM_FEEDBACK);
                startActivity(intent);
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed()
    {
        back_press();
    }
}
