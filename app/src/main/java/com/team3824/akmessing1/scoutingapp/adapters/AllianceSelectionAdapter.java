package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.fragments.AllianceSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 */
public class AllianceSelectionAdapter extends ArrayAdapter<String> {

    ArrayList<String> teams;

    public AllianceSelectionAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        teams = objects;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_string, null);
        }

        ((TextView) convertView.findViewById(R.id.text)).setText(teams.get(position));

        return convertView;
    }

    /**
     * @param position
     * @return
     */
    @Override
    public String getItem(int position) {
        return teams.get(position);
    }

    public int indexOf(String s) {
        return teams.indexOf(s);
    }

    public void remove(String s) {
        teams.remove(s);
        notifyDataSetChanged();
    }

    public void add(String s) {
        teams.add(s);
        Collections.sort(teams, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.equals(AllianceSelection.SELECT_TEAM)) {
                    return -1;
                } else if (rhs.equals(AllianceSelection.SELECT_TEAM)) {
                    return 1;
                }
                return Integer.compare(Integer.parseInt(lhs), Integer.parseInt(rhs));
            }
        });
        notifyDataSetChanged();
    }
}
