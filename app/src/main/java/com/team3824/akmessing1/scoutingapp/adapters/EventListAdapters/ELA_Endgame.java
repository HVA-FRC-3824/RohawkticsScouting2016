package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Endgame;

import java.util.ArrayList;

/**
 * Adapter for the Event List when endgame is used to compare teams
 */
@SuppressWarnings("ALL")
public class ELA_Endgame extends ArrayAdapter<ELI_Endgame> {

    private final String TAG = "ELA_Endgame";

    private ArrayList<ELI_Endgame> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_Endgame(Context context, ArrayList<ELI_Endgame> teams) {
        super(context, R.layout.list_item_event_endgame, teams);
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
            convertView = inflater.inflate(R.layout.list_item_event_endgame, null);
        }

        ELI_Endgame team = mTeams.get(position);

        team.mRank = position;
        TextView textView;

        //Header row
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_challenge);
            textView.setText("Challenge");
            textView = (TextView) convertView.findViewById(R.id.event_scale);
            textView.setText("Scale");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(team.mRank));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_challenge);
            textView.setText(String.valueOf(team.mChallenge));
            textView = (TextView) convertView.findViewById(R.id.event_scale);
            textView.setText(String.valueOf(team.mScale));
        }

        return convertView;
    }

}
