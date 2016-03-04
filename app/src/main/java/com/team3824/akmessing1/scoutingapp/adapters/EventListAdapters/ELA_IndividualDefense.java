package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_IndividualDefense;

import java.util.ArrayList;

/**
 * Adapter for the Event List when an individual defense is used to compare teams
 */
public class ELA_IndividualDefense extends ArrayAdapter<ELI_IndividualDefense> {

    private final String TAG = "ELA_IndividualDefense";

    private ArrayList<ELI_IndividualDefense> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_IndividualDefense(Context context, ArrayList<ELI_IndividualDefense> teams) {
        super(context, R.layout.list_item_event_individual_defense, teams);
        mTeams = teams;
    }

    /**
     * @param position
     * @param convertView
     * @param parentView
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_event_individual_defense, null);
        }

        ELI_IndividualDefense team = mTeams.get(position);

        team.mRank = position;
        TextView textView;

        //Header row
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_cross);
            textView.setText("Auto Cross");
            textView = (TextView) convertView.findViewById(R.id.event_reach);
            textView.setText("Auto Reach");
            textView = (TextView) convertView.findViewById(R.id.event_seen);
            textView.setText("Seen");
            textView = (TextView) convertView.findViewById(R.id.event_teleop_cross);
            textView.setText("Teleop Cross");
            textView = (TextView) convertView.findViewById(R.id.event_time);
            textView.setText("Time (s)");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_cross);
            textView.setText(String.valueOf(team.mAutoCross));
            textView = (TextView) convertView.findViewById(R.id.event_reach);
            textView.setText(String.valueOf(team.mAutoReach));
            textView = (TextView) convertView.findViewById(R.id.event_seen);
            textView.setText(String.valueOf(team.mSeen));
            textView = (TextView) convertView.findViewById(R.id.event_teleop_cross);
            textView.setText(String.valueOf(team.mTeleopCross));
            textView = (TextView) convertView.findViewById(R.id.event_time);
            textView.setText(String.valueOf(team.mTime));
        }

        return convertView;
    }

}
