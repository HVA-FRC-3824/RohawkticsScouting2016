package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Endgame;

import java.util.ArrayList;

public class ELA_Endgame extends ArrayAdapter<ELI_Endgame> {

    ArrayList<ELI_Endgame> mTeams;
    Context mContext;

    public ELA_Endgame(Context context, int textViewResourceId, ArrayList<ELI_Endgame> teams)
    {
        super(context, textViewResourceId,teams);
        mTeams = teams;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_event_endgame, null);
        }

        ELI_Endgame team = mTeams.get(position);

        team.mRank = position;
        TextView textView;
        if(team.mTeamNumber == -1)
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView)convertView.findViewById(R.id.event_challenge);
            textView.setText("Challenge");
            textView = (TextView)convertView.findViewById(R.id.event_scale);
            textView.setText("Scale");
        }
        else
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(team.mRank));
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView)convertView.findViewById(R.id.event_challenge);
            textView.setText(String.valueOf(team.mChallenge));
            textView = (TextView)convertView.findViewById(R.id.event_scale);
            textView.setText(String.valueOf(team.mScale));
        }

        return convertView;
    }

}
