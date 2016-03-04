package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Qualitative;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import java.util.ArrayList;

/**
 * Adapter for the Event List when an ability or qualitative metric is used to compare teams
 *
 * @author Andrew Messing
 * @version
 */
public class ELA_Qualitative extends ArrayAdapter<ELI_Qualitative> {

    private final String TAG = "ELA_Qualitative";

    private ArrayList<ELI_Qualitative> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_Qualitative(Context context, ArrayList<ELI_Qualitative> teams) {
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

        TextView textView;
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_evasiveness);
            textView.setText("Evasiveness");
            textView = (TextView) convertView.findViewById(R.id.event_blocking);
            textView.setText("Blocking");
            textView = (TextView) convertView.findViewById(R.id.event_speed);
            textView.setText("Speed");
            textView = (TextView) convertView.findViewById(R.id.event_pushing);
            textView.setText("Pushing");
            textView = (TextView) convertView.findViewById(R.id.event_driver_control);
            textView.setText("Driver Control");
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position+1));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_evasiveness);
            textView.setText(team.mRank[Constants.Qualitative_Rankings.EVASION_ABILITY_INDEX]);
            textView = (TextView) convertView.findViewById(R.id.event_blocking);
            textView.setText(team.mRank[Constants.Qualitative_Rankings.BLOCKING_ABILITY_INDEX]);
            textView = (TextView) convertView.findViewById(R.id.event_speed);
            textView.setText(team.mRank[Constants.Qualitative_Rankings.SPEED_INDEX]);
            textView = (TextView) convertView.findViewById(R.id.event_pushing);
            textView.setText(team.mRank[Constants.Qualitative_Rankings.PUSHING_ABILITY_INDEX]);
            textView = (TextView) convertView.findViewById(R.id.event_driver_control);
            textView.setText(team.mRank[Constants.Qualitative_Rankings.DRIVER_CONTROL_INDEX]);
        }

        return convertView;
    }
}
