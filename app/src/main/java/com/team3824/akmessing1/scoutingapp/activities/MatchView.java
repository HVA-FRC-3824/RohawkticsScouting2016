package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.fragments.MatchTeamFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

/**
 * @author Andrew Messing
 * @version 1
 *
 *  Activity to view highlights of all six teams competing in a given match
 */
public class MatchView extends Activity {

    private static final String TAG = "MatchView";

    private boolean prevMatch, nextMatch;
    private int matchNumber;

    /**
     * Sets up each of the team fragments and fixes some of the labels
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        Bundle extras = getIntent().getExtras();
        matchNumber = extras.getInt(Constants.Intent_Extras.MATCH_NUMBER,-1);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.match_view_toolbar);
        setActionBar(toolbar);

        if(matchNumber > 0) {
            setTitle("Match Number: " + matchNumber);
        }
        else
        {
            setTitle(extras.getString(Constants.Alliance_Selection.MATCH_TYPE));
        }

        TextView tv = (TextView) findViewById(R.id.auto_high_goal_blue);
        tv.setText("\t\tHigh goal" + Html.fromHtml("<sup>*</sup") + ":");
        tv = (TextView) findViewById(R.id.auto_low_goal_blue);
        tv.setText("\t\tLow goal" + Html.fromHtml("<sup>*</sup") + ":");
        tv = (TextView) findViewById(R.id.teleop_high_goal_blue);
        tv.setText("\t\tHigh goal" + Html.fromHtml("<sup>*</sup") + ":");
        tv = (TextView) findViewById(R.id.teleop_low_goal_blue);
        tv.setText("\t\tLow goal" + Html.fromHtml("<sup>*</sup") + ":");

        tv = (TextView) findViewById(R.id.auto_high_goal_red);
        tv.setText("\t\tHigh goal" + Html.fromHtml("<sup>*</sup") + ":");
        tv = (TextView) findViewById(R.id.auto_low_goal_red);
        tv.setText("\t\tLow goal" + Html.fromHtml("<sup>*</sup") + ":");
        tv = (TextView) findViewById(R.id.teleop_high_goal_red);
        tv.setText("\t\tHigh goal" + Html.fromHtml("<sup>*</sup") + ":");
        tv = (TextView) findViewById(R.id.teleop_low_goal_red);
        tv.setText("\t\tLow goal" + Html.fromHtml("<sup>*</sup") + ":");

        tv = (TextView) findViewById(R.id.defenses_blue);
        String text = "Defenses Cross Ability:";
        for (int i = 0; i < 8; i++)
            text += "\n";
        tv.setText(text);
        tv = (TextView) findViewById(R.id.defenses_red);
        text = "Defenses Cross Ability:";
        for (int i = 0; i < 8; i++)
            text += "\n";
        tv.setText(text);

        Cursor nextCursor = null;

        if(matchNumber > 0) {
            ScheduleDB scheduleDB = new ScheduleDB(this, eventId);
            Cursor cursor = scheduleDB.getMatch(matchNumber);
            FragmentManager fm = getFragmentManager();
            MatchTeamFragment blue1 = (MatchTeamFragment) fm.findFragmentById(R.id.blue1);
            blue1.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)), this);
            MatchTeamFragment blue2 = (MatchTeamFragment) fm.findFragmentById(R.id.blue2);
            blue2.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)), this);
            MatchTeamFragment blue3 = (MatchTeamFragment) fm.findFragmentById(R.id.blue3);
            blue3.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)), this);
            MatchTeamFragment red1 = (MatchTeamFragment) fm.findFragmentById(R.id.red1);
            red1.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)), this);
            MatchTeamFragment red2 = (MatchTeamFragment) fm.findFragmentById(R.id.red2);
            red2.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)), this);
            MatchTeamFragment red3 = (MatchTeamFragment) fm.findFragmentById(R.id.red3);
            red3.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)), this);

             nextCursor = scheduleDB.getMatch(matchNumber + 1);
        }
        else
        {
            FragmentManager fm = getFragmentManager();
            MatchTeamFragment blue1 = (MatchTeamFragment) fm.findFragmentById(R.id.blue1);
            blue1.setTeamNumber(extras.getInt(Constants.Alliance_Selection.BLUE1), this);
            MatchTeamFragment blue2 = (MatchTeamFragment) fm.findFragmentById(R.id.blue2);
            blue2.setTeamNumber(extras.getInt(Constants.Alliance_Selection.BLUE2), this);
            MatchTeamFragment blue3 = (MatchTeamFragment) fm.findFragmentById(R.id.blue3);
            blue3.setTeamNumber(extras.getInt(Constants.Alliance_Selection.BLUE3), this);
            MatchTeamFragment red1 = (MatchTeamFragment) fm.findFragmentById(R.id.red1);
            red1.setTeamNumber(extras.getInt(Constants.Alliance_Selection.RED1), this);
            MatchTeamFragment red2 = (MatchTeamFragment) fm.findFragmentById(R.id.red2);
            red2.setTeamNumber(extras.getInt(Constants.Alliance_Selection.RED2), this);
            MatchTeamFragment red3 = (MatchTeamFragment) fm.findFragmentById(R.id.red3);
            red3.setTeamNumber(extras.getInt(Constants.Alliance_Selection.RED3), this);
        }

        prevMatch = matchNumber > 1;
        nextMatch = nextCursor != null;
    }

    /**
     *  Creates the overflow menu for the toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_overflow, menu);
        if (!prevMatch) {
            menu.removeItem(R.id.previous);
        }
        if (!nextMatch) {
            menu.removeItem(R.id.next);
        }
        return true;
    }

    /**
     *  Implements the overflow menu actions
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(this, HomeScreen.class);
                startActivity(intent);
                break;
            case R.id.back:
                this.finish();
                break;
            case R.id.previous:
                intent = new Intent(this, MatchView.class);
                intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, matchNumber - 1);
                startActivity(intent);
                break;
            case R.id.next:
                intent = new Intent(this, MatchView.class);
                intent.putExtra(Constants.Intent_Extras.MATCH_NUMBER, matchNumber + 1);
                startActivity(intent);
                break;
            default:
                assert false;
        }
        return true;
    }
}
