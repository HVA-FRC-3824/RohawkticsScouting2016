package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Ability;

import java.util.ArrayList;

/**
 * Adapter for the Event List when an ability or qualitative metric is used to compare teams
 */
public class ELA_Ability extends ArrayAdapter<ELI_Ability> {

    ArrayList<ELI_Ability> mTeams;
    Context mContext;

    /**
     * @param context
     * @param textViewResourceId
     * @param teams
     */
    public ELA_Ability(Context context, int textViewResourceId, ArrayList<ELI_Ability> teams) {
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
            convertView = inflater.inflate(R.layout.list_item_event_ability, null);
        }

        ELI_Ability team = mTeams.get(position);

        TextView textView;
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(team.mRank);
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
        }

        return convertView;
    }
}
