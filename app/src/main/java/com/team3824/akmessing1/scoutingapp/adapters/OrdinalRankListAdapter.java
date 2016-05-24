package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.views.CustomOrdinalRank;

import java.util.ArrayList;

/**
 * Adapter for the Ordinal rank view (Used for qualitative rankings)
 *
 * @author Andrew Messing
 * @version
 */
public class OrdinalRankListAdapter extends ArrayAdapter<Team> implements View.OnClickListener{

    private final String TAG = "OrdinalRankListAdapter";

    private ArrayList<Team> teams;

    /**
     * @param context
     * @param teams
     */
    public OrdinalRankListAdapter(Context context, ArrayList<Team> teams) {
        super(context, R.layout.list_item_ordinal_rank, teams);
        this.teams = teams;
    }

    /**
     * @param position
     * @param convertView
     * @param parentView
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_ordinal_rank, null);
        //}

        final Team team = teams.get(position);

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        Button button = (Button)convertView.findViewById(R.id.tie);
        if(team.containsMapElement(CustomOrdinalRank.TIE) && team.getMapElement(CustomOrdinalRank.TIE).getInt() > 0)
        {
            button.setText("-");
        }
        button.setId(position);
        button.setOnClickListener(this);
        textView.setText(String.format("%d) %4d", team.getMapElement(CustomOrdinalRank.RANK).getInt(), team.getTeamNumber()));

        return convertView;
    }

    public void remove(int teamNumber)
    {
        int i;
        for(i = 0; i < teams.size(); i++)
        {
            if(teamNumber == teams.get(i).getTeamNumber())
            {
                teams.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * @param position position where to add the team
     * @param number   number of the team to add
     */
    public void add(int position, int number) {
        Team team = new Team(number,"");
        teams.add(position, team);
        for(int i = 0; i < teams.size(); i++)
        {
            team = teams.get(i);
            team.setMapElement(CustomOrdinalRank.RANK,new ScoutValue(i+1));
            team.setMapElement(CustomOrdinalRank.TIE, new ScoutValue(0
            ));
        }
        notifyDataSetChanged();
    }

    public void add(int position, Team team) {
        team.setMapElement(CustomOrdinalRank.RANK, new ScoutValue(position + 1));
        if(team.getMapElement(CustomOrdinalRank.RANK).getInt() > teams.size())
        {
            team.setMapElement(CustomOrdinalRank.RANK,new ScoutValue(teams.size()));
        }
        teams.add(position, team);
        for(int i = position+1; i < teams.size(); i++)
        {
            team = teams.get(i);
            int rank = team.getMapElement(CustomOrdinalRank.RANK).getInt();
            team.setMapElement(CustomOrdinalRank.TIE,new ScoutValue(0));
            rank++;
            if(rank > teams.size())
            {
                rank = teams.size();
            }
            team.setMapElement(CustomOrdinalRank.RANK,new ScoutValue(rank));
        }
        notifyDataSetChanged();
    }

    @Override
    public Team getItem(int position)
    {
        return teams.get(position);
    }

    public int getTeamNumber(int position)
    {
        return teams.get(position).getTeamNumber();
    }

    /**
     * @return number of teams in the ranking
     */
    public int size() {
        return teams.size();
    }

    @Override
    public void onClick(View v) {
        Button thisButton = (Button)v;
        int position = thisButton.getId();
        String text = String.valueOf(thisButton.getText());
        Team team = teams.get(position);
        if(text.equals("+"))
        {

            team.setMapElement(CustomOrdinalRank.TIE,new ScoutValue(1));
            thisButton.setText("-");
            for(int i = position + 1; i < teams.size(); i++) {
                Team team2 = teams.get(i);
                int rank = team2.getMapElement(CustomOrdinalRank.RANK).getInt();
                rank--;
                team2.setMapElement(CustomOrdinalRank.RANK, new ScoutValue(rank));
            }
        }
        else
        {
            team.setMapElement(CustomOrdinalRank.TIE, new ScoutValue(0));
            thisButton.setText("+");
            for(int i = position + 1; i < teams.size(); i++) {
                Team team2 = teams.get(i);
                int rank = team2.getMapElement(CustomOrdinalRank.RANK).getInt();
                rank++;
                team2.setMapElement(CustomOrdinalRank.RANK, new ScoutValue(rank));
            }
        }
        notifyDataSetChanged();
    }
}
