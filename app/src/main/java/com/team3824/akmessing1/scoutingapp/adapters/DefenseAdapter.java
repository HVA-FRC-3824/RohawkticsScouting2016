package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;

import java.util.ArrayList;

/**
 *  Adapter for the defense dropdown menus for super scouting
 */
public class DefenseAdapter extends ArrayAdapter<String> {

    boolean black;
    private ArrayList<String> options;

    /**
     * @param context Context of the application
     * @param resource The layout id
     * @param defenseOptions List of the defenses
     */
    public DefenseAdapter(Context context, int resource, ArrayList<String> defenseOptions) {
        super(context, resource, defenseOptions);
        options = defenseOptions;
        black = false;
    }

    /**
     *
     * @param context Context of the application
     * @param resource The layout id
     * @param defenseOptions List of the defenses
     * @param black Sets whether the text is black or white
     */
    public DefenseAdapter(Context context, int resource, ArrayList<String> defenseOptions, boolean black) {
        super(context, resource, defenseOptions);
        options = defenseOptions;
        this.black = black;
    }

    /**
     *
     * @param position The position of the view in the dropdown menu
     * @param convertView The view to be converted
     * @param parent The parent view group
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_string, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.text);
        tv.setText(options.get(position));
        if (black)
            tv.setTextColor(Color.BLACK);
        else
            tv.setTextColor(Color.WHITE);

        return convertView;
    }

    /**
     * Sets new list of defense possibilities
     * @param newDefenseOptions The new options for defenses
     */
    public void setOptions(ArrayList<String> newDefenseOptions) {
        options = newDefenseOptions;
        notifyDataSetChanged();
        notifyDataSetInvalidated();
    }

}
