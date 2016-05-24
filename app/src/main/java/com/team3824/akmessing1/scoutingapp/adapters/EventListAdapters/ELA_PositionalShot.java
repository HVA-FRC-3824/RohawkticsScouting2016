package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_PositionalShot;

import java.util.ArrayList;

/**
 * @author Andrew Messing
 * @version
 */
public class ELA_PositionalShot extends ArrayAdapter<ELI_PositionalShot>{

    private final String TAG = "ELA_PositionalShot";

    private ArrayList<ELI_PositionalShot> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_PositionalShot(Context context, ArrayList<ELI_PositionalShot> teams) {
        super(context, R.layout.list_item_event_positional_shot, teams);
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
            convertView = inflater.inflate(R.layout.list_item_event_positional_shot, null);
        }

        ELI_PositionalShot team = mTeams.get(position);

        team.mRank = position;
        TextView textView;

        //Header row
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_made);
            textView.setText("Made");
            textView = (TextView) convertView.findViewById(R.id.event_taken);
            textView.setText("Taken");
            textView = (TextView) convertView.findViewById(R.id.event_percentage);
            textView.setText("Percentage");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(team.mRank));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_made);
            textView.setText(String.valueOf(team.mMade));
            textView = (TextView) convertView.findViewById(R.id.event_taken);
            textView.setText(String.valueOf(team.mTotal));
            textView = (TextView) convertView.findViewById(R.id.event_percentage);
            textView.setText(String.format("%.1f%%",team.mPercent));
        }

        return convertView;
    }

}
