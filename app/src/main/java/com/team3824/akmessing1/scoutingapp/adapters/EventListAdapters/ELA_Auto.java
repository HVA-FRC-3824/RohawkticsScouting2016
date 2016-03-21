package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Auto;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import java.util.ArrayList;

/**
 * Adapter for the Event List when autonomous is used to compare teams
 *
 * @author Andrew Messing
 * @version %I%
 */

public class ELA_Auto extends ArrayAdapter<ELI_Auto> {

    private final String TAG = "ELA_Auto";

    private ArrayList<ELI_Auto> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_Auto(Context context, ArrayList<ELI_Auto> teams) {
        super(context, R.layout.list_item_event_auto, teams);
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
            convertView = inflater.inflate(R.layout.list_item_event_auto, null);
        }

        ELI_Auto team = mTeams.get(position);

        team.mRank = position;
        TextView textView;
        //Header Row
        if (team.mTeamNumber == -1) {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText("Rank");
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText("Team Number");
            textView = (TextView) convertView.findViewById(R.id.event_Portcullis);
            textView.setText("Portcullis" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText("Cheval de Frise" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Moat);
            textView.setText("Moat" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Ramparts);
            textView.setText("Ramparts" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Drawbridge);
            textView.setText("Drawbridge" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Sally_Port);
            textView.setText("Sally Port" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText("Rough Terrain" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText("Rock Wall" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Low_Bar);
            textView.setText("Low Bar" + Html.fromHtml("<sup>*</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_High);
            textView.setText("High Goal" + Html.fromHtml("<sup>**</sup"));
            textView = (TextView) convertView.findViewById(R.id.event_Low);
            textView.setText("Low Goal" + Html.fromHtml("<sup>**</sup"));
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));
            textView = (TextView) convertView.findViewById(R.id.event_Portcullis);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.PORTCULLIS_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.PORTCULLIS_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.CHEVAL_DE_FRISE_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.CHEVAL_DE_FRISE_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Moat);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.MOAT_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.MOAT_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Ramparts);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.RAMPARTS_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.RAMPARTS_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Drawbridge);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.DRAWBRIDGE_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.DRAWBRIDGE_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Sally_Port);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.SALLY_PORT_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.SALLY_PORT_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.ROUGH_TERRAIN_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.ROUGH_TERRAIN_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.ROCK_WALL_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.ROCK_WALL_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_Low_Bar);
            textView.setText(create_text(team.mDefenses.crosses[Constants.Defense_Arrays.LOW_BAR_INDEX], team.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX]));
            textView = (TextView) convertView.findViewById(R.id.event_High);
            textView.setText(create_text(team.mHigh.mAutoMade, team.mHigh.mAutoTaken));
            textView = (TextView) convertView.findViewById(R.id.event_Low);
            textView.setText(create_text(team.mLow.mAutoTaken, team.mLow.mAutoTaken));
        }

        return convertView;
    }

    /**
     * Formats data to be easily displayed
     *
     * @param cross Number of times defense was crossed
     * @param seen  Number of times the defense was seen
     * @return Formatted string with the number of crosses, number of times seen, and an average
     */
    private String create_text(int cross, int seen) {
        String text = String.format("%d (%d) : ", cross, seen);
        if (seen == 0)
            text += "0";
        else
            text += String.valueOf((float) cross / (float) seen);
        return text;
    }
}
