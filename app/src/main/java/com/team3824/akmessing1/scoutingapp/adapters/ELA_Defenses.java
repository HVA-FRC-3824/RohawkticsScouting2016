package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Defenses;

import java.util.ArrayList;

public class ELA_Defenses extends ArrayAdapter<ELI_Defenses>{

    ArrayList<ELI_Defenses> mTeams;
    Context mContext;

    public ELA_Defenses(Context context, int textViewResourceId, ArrayList<ELI_Defenses> teams)
    {
        super(context, textViewResourceId,teams);
        mTeams = teams;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_event_defenses, null);
        }

        ELI_Defenses team = mTeams.get(position);

        team.mRank = position;
        TextView textView;
        if(team.mTeamNumber == -1)
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView)convertView.findViewById(R.id.event_Portcullis);
            textView.setText("Portcullis"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText("Cheval de Frise"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Moat);
            textView.setText("Moat");
            textView = (TextView)convertView.findViewById(R.id.event_Ramparts);
            textView.setText("Ramparts"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Drawbridge);
            textView.setText("Drawbridge"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Sally_Port);
            textView.setText("Sally Port"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText("Rough Terrain"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText("Rock Wall"+ Html.fromHtml("<sup>*</sup"));
            textView = (TextView)convertView.findViewById(R.id.event_Low_Bar);
            textView.setText("Low Bar"+ Html.fromHtml("<sup>*</sup"));
        }
        else
        {
            textView = (TextView)convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position));
            textView = (TextView)convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));

            textView = (TextView)convertView.findViewById(R.id.event_Portcullis);
            textView.setText(create_text(team.cPortcullis,team.sPortcullis,team.tPortcullis));

            textView = (TextView)convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText(create_text(team.cChevalDeFrise,team.sChevalDeFrise,team.tChevalDeFrise));

            textView = (TextView)convertView.findViewById(R.id.event_Moat);
            textView.setText(create_text(team.cMoat,team.sMoat,team.tMoat));

            textView = (TextView)convertView.findViewById(R.id.event_Ramparts);
            textView.setText(create_text(team.cRamparts,team.sRamparts, team.tRamparts));

            textView = (TextView)convertView.findViewById(R.id.event_Drawbridge);
            textView.setText(create_text(team.cDrawbridge,team.sDrawbridge,team.tDrawbridge));

            textView = (TextView)convertView.findViewById(R.id.event_Sally_Port);
            textView.setText(create_text(team.cSallyPort,team.sSallyPort,team.tSallyPort));

            textView = (TextView)convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText(create_text(team.cRoughTerrain,team.sRoughTerrain,team.tRoughTerrain));

            textView = (TextView)convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText(create_text(team.cRockWall,team.sRockWall,team.tRockWall));

            textView = (TextView)convertView.findViewById(R.id.event_Low_Bar);
            textView.setText(create_text(team.cLowBar,team.sLowBar,team.tLowBar));
        }

        return convertView;
    }

    String create_text(int cross, int seen, float speed)
    {
        String text = String.format("%d (%d) : %f",cross,seen,speed);
        return text;
    }
}
