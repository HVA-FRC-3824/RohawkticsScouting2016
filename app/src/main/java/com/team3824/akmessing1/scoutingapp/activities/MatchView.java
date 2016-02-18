package com.team3824.akmessing1.scoutingapp.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.fragments.MatchTeamFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;

public class MatchView extends AppCompatActivity {

    private static final String TAG = "MatchView";

    private boolean prevMatch, nextMatch;
    private int matchNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        Bundle extras = getIntent().getExtras();
        matchNumber = extras.getInt(Constants.MATCH_NUMBER);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.match_view_toolbar);
        setSupportActionBar(toolbar);

        setTitle("Match Number: " + matchNumber);

        TextView tv = (TextView)findViewById(R.id.auto_high_goal_blue);
        tv.setText("\t\tHigh goal"+ Html.fromHtml("<sup>*</sup")+":");
        tv = (TextView)findViewById(R.id.auto_low_goal_blue);
        tv.setText("\t\tLow goal"+ Html.fromHtml("<sup>*</sup")+":");
        tv = (TextView)findViewById(R.id.teleop_high_goal_blue);
        tv.setText("\t\tHigh goal"+ Html.fromHtml("<sup>*</sup")+":");
        tv = (TextView)findViewById(R.id.teleop_low_goal_blue);
        tv.setText("\t\tLow goal"+ Html.fromHtml("<sup>*</sup")+":");

        tv = (TextView)findViewById(R.id.auto_high_goal_red);
        tv.setText("\t\tHigh goal"+ Html.fromHtml("<sup>*</sup")+":");
        tv = (TextView)findViewById(R.id.auto_low_goal_red);
        tv.setText("\t\tLow goal"+ Html.fromHtml("<sup>*</sup")+":");
        tv = (TextView)findViewById(R.id.teleop_high_goal_red);
        tv.setText("\t\tHigh goal"+ Html.fromHtml("<sup>*</sup")+":");
        tv = (TextView)findViewById(R.id.teleop_low_goal_red);
        tv.setText("\t\tLow goal"+ Html.fromHtml("<sup>*</sup")+":");

        tv = (TextView)findViewById(R.id.best_defenses_blue);
        String text = "Best Defenses:";
        for(int i = 0; i < Constants.NUM_BEST-1; i++)
            text += "\n";
        tv.setText(text);
        tv = (TextView)findViewById(R.id.best_defenses_red);
        text = "Best Defenses:";
        for(int i = 0; i < Constants.NUM_BEST-1; i++)
            text += "\n";
        tv.setText(text);
        tv = (TextView)findViewById(R.id.worst_defenses_blue);
        text = "Worst Defenses:";
        for(int i = 0; i < Constants.NUM_WORST-1; i++)
            text += "\n";
        tv.setText(text);
        tv = (TextView)findViewById(R.id.worst_defenses_red);
        text = "Worst Defenses:";
        for(int i = 0; i < Constants.NUM_WORST-1; i++)
            text += "\n";
        tv.setText(text);

        ScheduleDB scheduleDB = new ScheduleDB(this,eventId);
        Cursor cursor = scheduleDB.getMatch(matchNumber);
        FragmentManager fm = getFragmentManager();
        MatchTeamFragment blue1 = (MatchTeamFragment) fm.findFragmentById(R.id.blue1);
        blue1.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE1)),this);
        MatchTeamFragment blue2 = (MatchTeamFragment) fm.findFragmentById(R.id.blue2);
        blue2.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE2)),this);
        MatchTeamFragment blue3 = (MatchTeamFragment) fm.findFragmentById(R.id.blue3);
        blue3.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_BLUE3)),this);
        MatchTeamFragment red1 = (MatchTeamFragment) fm.findFragmentById(R.id.red1);
        red1.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED1)),this);
        MatchTeamFragment red2 = (MatchTeamFragment) fm.findFragmentById(R.id.red2);
        red2.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED2)),this);
        MatchTeamFragment red3 = (MatchTeamFragment) fm.findFragmentById(R.id.red3);
        red3.setTeamNumber(cursor.getInt(cursor.getColumnIndex(ScheduleDB.KEY_RED3)),this);

        prevMatch = matchNumber != 1;
        Cursor nextCursor = scheduleDB.getMatch(matchNumber + 1);
        nextMatch = nextCursor != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.match_overflow, menu);
        if(!prevMatch) {
            menu.removeItem(R.id.previous);
        }
        if(!nextMatch) {
            menu.removeItem(R.id.next);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.home:
                intent = new Intent(this,StartScreen.class);
                startActivity(intent);
                break;
            case R.id.back:
                intent = new Intent(this,MatchList.class);
                intent.putExtra(Constants.NEXT_PAGE,Constants.MATCH_VIEWING);
                startActivity(intent);
                break;
            case R.id.previous:
                intent = new Intent(this,MatchView.class);
                intent.putExtra(Constants.MATCH_NUMBER,matchNumber-1);
                startActivity(intent);
                break;
            case R.id.next:
                intent = new Intent(this,MatchView.class);
                intent.putExtra(Constants.MATCH_NUMBER,matchNumber+1);
                startActivity(intent);
                break;
            // Shouldn't be one
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }
}
