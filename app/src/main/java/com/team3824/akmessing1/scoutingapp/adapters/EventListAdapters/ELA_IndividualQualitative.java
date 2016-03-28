package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Qualitative;

import java.util.ArrayList;

/**
 * @author Andrew Messing
 */
public class ELA_IndividualQualitative extends ArrayAdapter<ELI_Qualitative> {

    private final String TAG = "ELA_IndividualQualitative";


    private ArrayList<ELI_Qualitative> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_IndividualQualitative(Context context, ArrayList<ELI_Qualitative> teams) {
        super(context, R.layout.list_item_event_qualitative, teams);
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
            convertView = inflater.inflate(R.layout.list_item_event_qualitative, null);
        }

        ELI_Qualitative team = mTeams.get(position);

        convertView.findViewById(R.id.event_evasiveness).setVisibility(View.GONE);
        convertView.findViewById(R.id.event_blocking).setVisibility(View.GONE);
        convertView.findViewById(R.id.event_speed).setVisibility(View.GONE);
        convertView.findViewById(R.id.event_pushing).setVisibility(View.GONE);
        convertView.findViewById(R.id.event_driver_control).setVisibility(View.GONE);
        TextView textView;
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(team.mRank[0]);
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
        }

        return convertView;
    }
}
