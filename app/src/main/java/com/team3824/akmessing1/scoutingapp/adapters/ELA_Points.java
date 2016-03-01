package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Points;

import java.util.ArrayList;

/**
 * Adapter for the Event List when points are used to compare teams
 */
public class ELA_Points extends ArrayAdapter<ELI_Points> {

    ArrayList<ELI_Points> mTeams;
    Context mContext;

    /**
     * @param context
     * @param textViewResourceId
     * @param teams
     */
    public ELA_Points(Context context, int textViewResourceId, ArrayList<ELI_Points> teams) {
        super(context, textViewResourceId, teams);
        mTeams = teams;
        mContext = context;
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
            convertView = inflater.inflate(R.layout.list_item_event_points, null);
        }

        ELI_Points team = mTeams.get(position);

        team.mRank = position;
        TextView textView;

        //Header row
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_defense_points);
            textView.setText("Defense Points");
            textView = (TextView) convertView.findViewById(R.id.event_high_points);
            textView.setText("High Goal Points");
            textView = (TextView) convertView.findViewById(R.id.event_low_points);
            textView.setText("Low Goal Points");
            textView = (TextView) convertView.findViewById(R.id.event_auto_points);
            textView.setText("Auto Points");
            textView = (TextView) convertView.findViewById(R.id.event_teleop_points);
            textView.setText("Teleop Points");
            textView = (TextView) convertView.findViewById(R.id.event_endgame_points);
            textView.setText("Endgame Points");
            textView = (TextView) convertView.findViewById(R.id.event_foul_points);
            textView.setText("Foul Points");
            textView = (TextView) convertView.findViewById(R.id.event_points);
            textView.setText("Total Points");
            textView = (TextView) convertView.findViewById(R.id.event_avg_points);
            textView.setText("Avg Points");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(team.mRank));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_defense_points);
            textView.setText(String.valueOf(team.mDefensePoints));
            textView = (TextView) convertView.findViewById(R.id.event_high_points);
            textView.setText(String.valueOf(team.mHighPoints));
            textView = (TextView) convertView.findViewById(R.id.event_low_points);
            textView.setText(String.valueOf(team.mLowPoints));
            textView = (TextView) convertView.findViewById(R.id.event_auto_points);
            textView.setText(String.valueOf(team.mAutoPoints));
            textView = (TextView) convertView.findViewById(R.id.event_teleop_points);
            textView.setText(String.valueOf(team.mTeleopPoints));
            textView = (TextView) convertView.findViewById(R.id.event_endgame_points);
            textView.setText(String.valueOf(team.mEndgamePoints));
            textView = (TextView) convertView.findViewById(R.id.event_foul_points);
            textView.setText(String.valueOf(team.mFoulPoints));
            textView = (TextView) convertView.findViewById(R.id.event_points);
            textView.setText(String.valueOf(team.mTotalPoints));
            textView = (TextView) convertView.findViewById(R.id.event_avg_points);
            textView.setText(String.valueOf(team.mAvgPoints));
        }

        return convertView;
    }

}
