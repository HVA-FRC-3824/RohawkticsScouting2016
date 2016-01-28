package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.event_list_items.ELI_Auto;

import java.util.ArrayList;

public class ELA_Auto extends ArrayAdapter<ELI_Auto> {
    ArrayList<ELI_Auto> mTeams;
    Context mContext;

    public ELA_Auto(Context context, int textViewResourceId, ArrayList<ELI_Auto> teams)
    {
        super(context,textViewResourceId,teams);
        mTeams = teams;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_event_auto, null);
        }

        ELI_Auto team = mTeams.get(position);

        team.mRank = position;
        TextView textView;
        if(team.mTeamNumber == -1)
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView)convertView.findViewById(R.id.event_Portcullis);
            textView.setText("Portcullis");
            textView = (TextView)convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText("Cheval de Frise");
            textView = (TextView)convertView.findViewById(R.id.event_Moat);
            textView.setText("Moat");
            textView = (TextView)convertView.findViewById(R.id.event_Ramparts);
            textView.setText("Ramparts");
            textView = (TextView)convertView.findViewById(R.id.event_Drawbridge);
            textView.setText("Drawbridge");
            textView = (TextView)convertView.findViewById(R.id.event_Sally_Port);
            textView.setText("Sally Port");
            textView = (TextView)convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText("Rough Terrain");
            textView = (TextView)convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText("Rock Wall");
            textView = (TextView)convertView.findViewById(R.id.event_Low_Bar);
            textView.setText("Low Bar");
            textView = (TextView)convertView.findViewById(R.id.event_High);
            textView.setText("High Goal");
            textView = (TextView)convertView.findViewById(R.id.event_Low);
            textView.setText("Low Goal");
        }
        else
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position));
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView)convertView.findViewById(R.id.event_Portcullis);
            textView.setText(create_text(team.mDefenses.cPortcullis, team.mDefenses.sPortcullis));
            textView = (TextView)convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText(create_text(team.mDefenses.cChevalDeFrise,team.mDefenses.sChevalDeFrise));
            textView = (TextView)convertView.findViewById(R.id.event_Moat);
            textView.setText(create_text(team.mDefenses.cMoat,team.mDefenses.sMoat));
            textView = (TextView)convertView.findViewById(R.id.event_Ramparts);
            textView.setText(create_text(team.mDefenses.cRamparts,team.mDefenses.sRamparts));
            textView = (TextView)convertView.findViewById(R.id.event_Drawbridge);
            textView.setText(create_text(team.mDefenses.cDrawbridge,team.mDefenses.sDrawbridge));
            textView = (TextView)convertView.findViewById(R.id.event_Sally_Port);
            textView.setText(create_text(team.mDefenses.cSallyPort,team.mDefenses.sSallyPort));
            textView = (TextView)convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText(create_text(team.mDefenses.cRoughTerrain,team.mDefenses.sRoughTerrain));
            textView = (TextView)convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText(create_text(team.mDefenses.cRockWall,team.mDefenses.sRockWall));
            textView = (TextView)convertView.findViewById(R.id.event_Low_Bar);
            textView.setText(create_text(team.mDefenses.cLowBar,team.mDefenses.sLowBar));
            textView = (TextView)convertView.findViewById(R.id.event_High);
            textView.setText(create_text(team.mHigh.mAutoMade,team.mHigh.mAutoTaken));
            textView = (TextView)convertView.findViewById(R.id.event_Low);
            textView.setText(create_text(team.mLow.mAutoTaken,team.mLow.mAutoTaken));
        }

        return convertView;
    }

    String create_text(int cross, int seen)
    {
        String text = String.valueOf(cross) + " ("+String.valueOf(seen)+") : ";
        if(seen == 0)
            text += "0";
        else
            text += String.valueOf((float)cross/(float)seen);
        return text;
    }
}
