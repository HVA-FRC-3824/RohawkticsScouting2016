package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Shots;

import java.util.ArrayList;

/**
 * Adapter for the Event List when shooting is used to compare teams
 */
public class ELA_Shot extends ArrayAdapter<ELI_Shots> {

    ArrayList<ELI_Shots> mTeams;
    Context mContext;

    /**
     * @param context
     * @param textViewResourceId
     * @param teams
     */
    public ELA_Shot(Context context, int textViewResourceId, ArrayList<ELI_Shots> teams) {
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
            convertView = inflater.inflate(R.layout.list_item_event_shot, null);
        }

        ELI_Shots team = mTeams.get(position);

        team.mRank = position;
        TextView textView;

        //Header row
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_auto_made);
            textView.setText("Auto Made");
            textView = (TextView) convertView.findViewById(R.id.event_auto_taken);
            textView.setText("Auto Taken");
            textView = (TextView) convertView.findViewById(R.id.event_auto_percentage);
            textView.setText("Auto Percentage");
            textView = (TextView) convertView.findViewById(R.id.event_teleop_made);
            textView.setText("Teleop Made");
            textView = (TextView) convertView.findViewById(R.id.event_teleop_taken);
            textView.setText("Teleop Taken");
            textView = (TextView) convertView.findViewById(R.id.event_teleop_percentage);
            textView.setText("Teleop Percentage");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(team.mRank));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_auto_made);
            textView.setText(String.valueOf(team.mAutoMade));
            textView = (TextView) convertView.findViewById(R.id.event_auto_taken);
            textView.setText(String.valueOf(team.mAutoTaken));
            textView = (TextView) convertView.findViewById(R.id.event_auto_percentage);
            textView.setText(String.valueOf(team.mAutoPercentage) + "%");
            textView = (TextView) convertView.findViewById(R.id.event_teleop_made);
            textView.setText(String.valueOf(team.mTeleopMade));
            textView = (TextView) convertView.findViewById(R.id.event_teleop_taken);
            textView.setText(String.valueOf(team.mTeleopTaken));
            textView = (TextView) convertView.findViewById(R.id.event_teleop_percentage);
            textView.setText(String.valueOf(team.mTeleopPercentage) + "%");
        }

        return convertView;
    }

}
