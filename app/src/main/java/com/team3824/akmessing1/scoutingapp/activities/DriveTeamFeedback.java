package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.views.CustomEdittext;

/**
 * Created by Gretel on 2/2/16.
 */
public class DriveTeamFeedback extends AppCompatActivity{
    private String TAG = "DriveTeamFeedback";
    private String position;
    private DriveTeamFeedbackDB driveTeamFeedbackDB;
    private int team1 = -1, team2 = -1;
    private EditText commentEditText1, commentEditText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveteam_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.driveteam_feedback_toolbar);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        int matchNumber = extras.getInt(Constants.MATCH_NUMBER);
        position = extras.getString("position");
        setTitle("Match Number: " + matchNumber);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
        Cursor cursor = scheduleDB.getMatch(matchNumber);

        if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3));}

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3));
        }

        else if (cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)) == 3824)
        {
            team1 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1));
            team2 = cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2));
        }

        if(team1 == -1)
        {
            Log.d(TAG,"3824 is not in match");
            Toast.makeText(this,"Error: wrong match",Toast.LENGTH_SHORT).show();
        }
        else {
            driveTeamFeedbackDB = new DriveTeamFeedbackDB(this,eventId);

            TextView textview = (TextView) findViewById(R.id.team_number_1);
            textview.setText(String.valueOf(team1));
            String comment = driveTeamFeedbackDB.getComments(team1);
            CustomEdittext customEdittext = (CustomEdittext)findViewById(R.id.driveteam_feedback_1);
            commentEditText1 = (EditText)customEdittext.findViewById(R.id.edittext);
            commentEditText1.setText(comment);

            textview = (TextView) findViewById(R.id.team_number_2);
            textview.setText(String.valueOf(team2));
            comment = driveTeamFeedbackDB.getComments(team2);
            customEdittext = (CustomEdittext)findViewById(R.id.driveteam_feedback_2);
            commentEditText2 = (EditText)customEdittext.findViewById(R.id.edittext);
            commentEditText2.setText(comment);
        }

        Utilities.setupUI(this, findViewById(android.R.id.content));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driveteam_feedback_overflow, menu);
        return true;
    }

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
    private void home_press()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriveTeamFeedback.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Saving values");

                // Store values to the database
                driveTeamFeedbackDB.updateComments(team1, String.valueOf(commentEditText1.getText()));
                driveTeamFeedbackDB.updateComments(team2, String.valueOf(commentEditText2.getText()));
                // Go to the next match
                Intent intent = new Intent(DriveTeamFeedback.this, StartScreen.class);
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
                Intent intent = new Intent(DriveTeamFeedback.this, StartScreen.class);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void back_press()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(DriveTeamFeedback.this);
        builder.setTitle("Save match data?");

        // Save option
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.d(TAG, "Saving values");
                // Store values to the database
                driveTeamFeedbackDB.updateComments(team1, String.valueOf(commentEditText1.getText()));
                driveTeamFeedbackDB.updateComments(team2, String.valueOf(commentEditText2.getText()));

                // Go to the next match
                Intent intent = new Intent(DriveTeamFeedback.this, OurMatchList.class);
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
                Intent intent = new Intent(DriveTeamFeedback.this, OurMatchList.class);
                startActivity(intent);
            }
        });
        builder.show();
    }
}
