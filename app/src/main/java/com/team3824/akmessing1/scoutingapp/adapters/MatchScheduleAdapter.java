package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.Match;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import java.util.ArrayList;

/**
 * Adapter for the match schedule. Highlight teams based on if we will play with them, against them,
 * or both.
 *
 * @author Andrew Messing
 * @version
 */
public class MatchScheduleAdapter extends ArrayAdapter<Match> {

    private final String TAG = "MatchScheduleAdapter";

    private ArrayList<Match> matches;

    /**
     * @param context
     * @param matches
     */
    public MatchScheduleAdapter(Context context, ArrayList<Match> matches) {
        super(context, R.layout.list_item_schedule_match, matches);
        this.matches = matches;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_schedule_match, null);
        }

        Match match = matches.get(position);

        TextView textView = (TextView) convertView.findViewById(R.id.schedule_matchNum);
        textView.setText(String.valueOf(match.matchNumber));

        // Sets the match number textview to blue if we are in the match, white otherwise.
        if (match.teams[Constants.Match_Schedule.BLUE1_INDEX] == Constants.OUR_TEAM_NUMBER || match.teams[Constants.Match_Schedule.BLUE2_INDEX] == Constants.OUR_TEAM_NUMBER ||
                match.teams[Constants.Match_Schedule.BLUE3_INDEX] == Constants.OUR_TEAM_NUMBER || match.teams[Constants.Match_Schedule.RED1_INDEX] == Constants.OUR_TEAM_NUMBER ||
                match.teams[Constants.Match_Schedule.RED2_INDEX] == Constants.OUR_TEAM_NUMBER || match.teams[Constants.Match_Schedule.RED3_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        // Highlights teams based on if they are a future ally, future opponent, both, or us.
        textView = (TextView) convertView.findViewById(R.id.schedule_blue1);
        textView.setText(String.valueOf(match.teams[Constants.Match_Schedule.BLUE1_INDEX]));
        if (match.teams[Constants.Match_Schedule.BLUE1_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else if (match.futureAlly[Constants.Match_Schedule.BLUE1_INDEX] && match.futureOpponent[Constants.Match_Schedule.BLUE1_INDEX]) {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureAlly[Constants.Match_Schedule.BLUE1_INDEX]) {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureOpponent[Constants.Match_Schedule.BLUE1_INDEX]) {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.schedule_blue2);
        textView.setText(String.valueOf(match.teams[Constants.Match_Schedule.BLUE2_INDEX]));
        if (match.teams[Constants.Match_Schedule.BLUE2_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else if (match.futureAlly[Constants.Match_Schedule.BLUE2_INDEX] && match.futureOpponent[Constants.Match_Schedule.BLUE2_INDEX]) {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureAlly[Constants.Match_Schedule.BLUE2_INDEX]) {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureOpponent[Constants.Match_Schedule.BLUE2_INDEX]) {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.schedule_blue3);
        textView.setText(String.valueOf(match.teams[Constants.Match_Schedule.BLUE3_INDEX]));
        if (match.teams[Constants.Match_Schedule.BLUE3_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else if (match.futureAlly[Constants.Match_Schedule.BLUE3_INDEX] && match.futureOpponent[Constants.Match_Schedule.BLUE3_INDEX]) {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureAlly[Constants.Match_Schedule.BLUE3_INDEX]) {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureOpponent[Constants.Match_Schedule.BLUE3_INDEX]) {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.schedule_red1);
        textView.setText(String.valueOf(match.teams[Constants.Match_Schedule.RED1_INDEX]));
        if (match.teams[Constants.Match_Schedule.RED1_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else if (match.futureAlly[Constants.Match_Schedule.RED1_INDEX] && match.futureOpponent[Constants.Match_Schedule.RED1_INDEX]) {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureAlly[Constants.Match_Schedule.RED1_INDEX]) {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureOpponent[Constants.Match_Schedule.RED1_INDEX]) {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.schedule_red2);
        textView.setText(String.valueOf(match.teams[Constants.Match_Schedule.RED2_INDEX]));
        if (match.teams[Constants.Match_Schedule.RED2_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else if (match.futureAlly[Constants.Match_Schedule.RED2_INDEX] && match.futureOpponent[Constants.Match_Schedule.RED2_INDEX]) {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureAlly[Constants.Match_Schedule.RED2_INDEX]) {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureOpponent[Constants.Match_Schedule.RED2_INDEX]) {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.schedule_red3);
        textView.setText(String.valueOf(match.teams[Constants.Match_Schedule.RED3_INDEX]));
        if (match.teams[Constants.Match_Schedule.RED3_INDEX] == Constants.OUR_TEAM_NUMBER) {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        } else if (match.futureAlly[Constants.Match_Schedule.RED3_INDEX] && match.futureOpponent[Constants.Match_Schedule.RED3_INDEX]) {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureAlly[Constants.Match_Schedule.RED3_INDEX]) {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        } else if (match.futureOpponent[Constants.Match_Schedule.RED3_INDEX]) {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
