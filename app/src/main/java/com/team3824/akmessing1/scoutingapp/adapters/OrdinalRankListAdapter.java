package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;

import java.util.ArrayList;

/**
 * Adapter for the Ordinal rank view (Used for qualitative rankings)
 */
public class OrdinalRankListAdapter extends ArrayAdapter<Integer> {

    ArrayList<Integer> teams;
    Context context;

    /**
     * @param context
     * @param textViewResourceId
     * @param teams
     */
    public OrdinalRankListAdapter(Context context, int textViewResourceId, ArrayList<Integer> teams) {
        super(context, textViewResourceId, teams);
        this.context = context;
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_ordinal_rank, null);
        }

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(String.format("%d) %d", position + 1, teams.get(position)));
        return convertView;
    }

    /**
     * @param position position where to add the team
     * @param number   number of the team to add
     */
    public void add(int position, int number) {
        teams.add(position, number);
        notifyDataSetChanged();
    }

    /**
     * @param position position of the team to get
     * @return team at the position
     */
    public int get(int position) {
        return teams.get(position);
    }

    /**
     * @return number of teams in the ranking
     */
    public int size() {
        return teams.size();
    }

}
