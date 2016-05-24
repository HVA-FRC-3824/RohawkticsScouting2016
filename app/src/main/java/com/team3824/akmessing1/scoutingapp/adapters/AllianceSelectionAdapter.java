package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Adapter for each of the Alliance Selection Spinners
 *
 * @author Andrew Messing
 * @version %I%
 */
public class AllianceSelectionAdapter extends ArrayAdapter<String> {

    private ArrayList<String> teams;

    public AllianceSelectionAdapter(Context context, ArrayList<String> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
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

    /**
     * @param s
     */
    public void remove(String s) {
        teams.remove(s);
        notifyDataSetChanged();
    }

    /**
     * @param s
     */
    public void add(String s) {
        teams.add(s);
        Collections.sort(teams, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                if (lhs.equals(Constants.Alliance_Selection.SELECT_TEAM)) {
                    return -1;
                } else if (rhs.equals(Constants.Alliance_Selection.SELECT_TEAM)) {
                    return 1;
                }
                return Integer.compare(Integer.parseInt(lhs), Integer.parseInt(rhs));
            }
        });
        notifyDataSetChanged();
    }
}
