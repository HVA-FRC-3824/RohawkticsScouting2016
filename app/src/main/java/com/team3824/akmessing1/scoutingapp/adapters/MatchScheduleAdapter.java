package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.Match;

import java.util.ArrayList;

public class MatchScheduleAdapter extends ArrayAdapter<Match>{
    private String TAG = "MatchScheduleAdapter";

    ArrayList<Match> matches;

    public MatchScheduleAdapter(Context context, int textViewResourceId, ArrayList<Match> matches)
    {
        super(context,textViewResourceId,matches);
        this.matches = matches;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_schedule_match, null);
        }

        Match match = matches.get(position);

        TextView textView = (TextView)convertView.findViewById(R.id.schedule_matchNum);
        textView.setText(String.valueOf(match.matchNumber));
        if(match.teams[Constants.BLUE1_INDEX] == 3824 || match.teams[Constants.BLUE2_INDEX] == 3824 ||
        match.teams[Constants.BLUE3_INDEX] == 3824 || match.teams[Constants.RED1_INDEX] == 3824 ||
        match.teams[Constants.RED2_INDEX] == 3824 || match.teams[Constants.RED3_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView)convertView.findViewById(R.id.schedule_blue1);
        textView.setText(String.valueOf(match.teams[Constants.BLUE1_INDEX]));
        if(match.teams[Constants.BLUE1_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else if(match.futureAlly[Constants.BLUE1_INDEX] && match.futureOpponent[Constants.BLUE1_INDEX])
        {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureAlly[Constants.BLUE1_INDEX])
        {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureOpponent[Constants.BLUE1_INDEX])
        {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView)convertView.findViewById(R.id.schedule_blue2);
        textView.setText(String.valueOf(match.teams[Constants.BLUE2_INDEX]));
        if(match.teams[Constants.BLUE2_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else if(match.futureAlly[Constants.BLUE2_INDEX] && match.futureOpponent[Constants.BLUE2_INDEX])
        {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureAlly[Constants.BLUE2_INDEX])
        {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureOpponent[Constants.BLUE2_INDEX])
        {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView)convertView.findViewById(R.id.schedule_blue3);
        textView.setText(String.valueOf(match.teams[Constants.BLUE3_INDEX]));
        if(match.teams[Constants.BLUE3_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else if(match.futureAlly[Constants.BLUE3_INDEX] && match.futureOpponent[Constants.BLUE3_INDEX])
        {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureAlly[Constants.BLUE3_INDEX])
        {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureOpponent[Constants.BLUE3_INDEX])
        {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView)convertView.findViewById(R.id.schedule_red1);
        textView.setText(String.valueOf(match.teams[Constants.RED1_INDEX]));
        if(match.teams[Constants.RED1_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else if(match.futureAlly[Constants.RED1_INDEX] && match.futureOpponent[Constants.RED1_INDEX])
        {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureAlly[Constants.RED1_INDEX])
        {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureOpponent[Constants.RED1_INDEX])
        {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView)convertView.findViewById(R.id.schedule_red2);
        textView.setText(String.valueOf(match.teams[Constants.RED2_INDEX]));
        if(match.teams[Constants.RED2_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else if(match.futureAlly[Constants.RED2_INDEX] && match.futureOpponent[Constants.RED2_INDEX])
        {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureAlly[Constants.RED2_INDEX])
        {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureOpponent[Constants.RED2_INDEX])
        {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView)convertView.findViewById(R.id.schedule_red3);
        textView.setText(String.valueOf(match.teams[Constants.RED3_INDEX]));
        if(match.teams[Constants.RED3_INDEX] == 3824)
        {
            textView.setBackgroundColor(Color.BLUE);
            textView.setTextColor(Color.WHITE);
        }
        else if(match.futureAlly[Constants.RED3_INDEX] && match.futureOpponent[Constants.RED3_INDEX])
        {
            textView.setBackgroundColor(Color.YELLOW);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureAlly[Constants.RED3_INDEX])
        {
            textView.setBackgroundColor(Color.GREEN);
            textView.setTextColor(Color.BLACK);
        }
        else if(match.futureOpponent[Constants.RED3_INDEX])
        {
            textView.setBackgroundColor(Color.RED);
            textView.setTextColor(Color.WHITE);
        }
        // Fixes weird bug...
        else
        {
            textView.setBackgroundColor(Color.WHITE);
            textView.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}
