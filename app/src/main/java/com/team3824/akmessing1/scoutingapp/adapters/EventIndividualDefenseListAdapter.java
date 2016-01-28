package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.event_list_items.EventListItemIndividualDefense;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EventIndividualDefenseListAdapter extends ArrayAdapter<EventListItemIndividualDefense>{

    ArrayList<EventListItemIndividualDefense> mTeams;
    Context mContext;

    public EventIndividualDefenseListAdapter(Context context, int textViewResourceId, ArrayList<EventListItemIndividualDefense> teams)
    {
        super(context, textViewResourceId,teams);
        mTeams = teams;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_event_individual_defense, null);
        }

        EventListItemIndividualDefense team = mTeams.get(position);

        team.mRank = position;
        TextView textView;
        if(team.mTeamNumber == -1)
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView)convertView.findViewById(R.id.event_cross);
            textView.setText("Auto Cross");
            textView = (TextView)convertView.findViewById(R.id.event_reach);
            textView.setText("Auto Reach");
            textView = (TextView)convertView.findViewById(R.id.event_seen);
            textView.setText("Seen");
            textView = (TextView)convertView.findViewById(R.id.event_teleop_cross);
            textView.setText("Teleop Cross");
            textView = (TextView)convertView.findViewById(R.id.event_avg);
            textView.setText("Avg");
        }
        else
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position));
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView)convertView.findViewById(R.id.event_cross);
            textView.setText(String.valueOf(team.mAutoCross));
            textView = (TextView)convertView.findViewById(R.id.event_reach);
            textView.setText(String.valueOf(team.mAutoReach));
            textView = (TextView)convertView.findViewById(R.id.event_seen);
            textView.setText(String.valueOf(team.mSeen));
            textView = (TextView)convertView.findViewById(R.id.event_teleop_cross);
            textView.setText(String.valueOf(team.mTeleopCross));
            textView = (TextView)convertView.findViewById(R.id.event_avg);
            textView.setText(String.valueOf(team.mAvg));
        }

        return convertView;
    }

}
