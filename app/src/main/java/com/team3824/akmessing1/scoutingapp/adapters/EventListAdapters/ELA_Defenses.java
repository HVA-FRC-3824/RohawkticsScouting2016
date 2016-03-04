package com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Defenses;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import java.util.ArrayList;

/**
 * Adapter for the Event List when crossing defenses is used to compare teams
 *
 * @author Andrew Messing
 * @version %I%
 */
@SuppressWarnings("ALL")
public class ELA_Defenses extends ArrayAdapter<ELI_Defenses> {

    private final String TAG = "ELA_Defenses";

    private ArrayList<ELI_Defenses> mTeams;

    /**
     * @param context
     * @param teams
     */
    public ELA_Defenses(Context context, ArrayList<ELI_Defenses> teams) {
        super(context, R.layout.list_item_event_defenses, teams);
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
            convertView = inflater.inflate(R.layout.list_item_event_defenses, null);
        }

        ELI_Defenses team = mTeams.get(position);

        team.mRank = position;
        TextView textView;
        //Header row
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
            textView.setText("Moat");
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
        } else {
            textView = (TextView) convertView.findViewById(R.id.event_rank);
            textView.setText(String.valueOf(position));
            textView = (TextView) convertView.findViewById(R.id.event_teamNum);
            textView.setText(String.valueOf(team.mTeamNumber));

            textView = (TextView) convertView.findViewById(R.id.event_Portcullis);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.PORTCULLIS_INDEX], team.seens[Constants.Defense_Arrays.PORTCULLIS_INDEX], team.time[Constants.Defense_Arrays.PORTCULLIS_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Cheval_de_Frise);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.CHEVAL_DE_FRISE_INDEX], team.seens[Constants.Defense_Arrays.CHEVAL_DE_FRISE_INDEX], team.time[Constants.Defense_Arrays.CHEVAL_DE_FRISE_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Moat);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.MOAT_INDEX], team.seens[Constants.Defense_Arrays.MOAT_INDEX], team.time[Constants.Defense_Arrays.MOAT_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Ramparts);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.RAMPARTS_INDEX], team.seens[Constants.Defense_Arrays.RAMPARTS_INDEX], team.time[Constants.Defense_Arrays.RAMPARTS_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Drawbridge);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.DRAWBRIDGE_INDEX], team.seens[Constants.Defense_Arrays.DRAWBRIDGE_INDEX], team.time[Constants.Defense_Arrays.DRAWBRIDGE_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Sally_Port);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.SALLY_PORT_INDEX], team.seens[Constants.Defense_Arrays.SALLY_PORT_INDEX], team.time[Constants.Defense_Arrays.SALLY_PORT_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Rough_Terrain);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.ROUGH_TERRAIN_INDEX], team.seens[Constants.Defense_Arrays.ROUGH_TERRAIN_INDEX], team.time[Constants.Defense_Arrays.ROUGH_TERRAIN_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Rock_Wall);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.ROCK_WALL_INDEX], team.seens[Constants.Defense_Arrays.ROCK_WALL_INDEX], team.time[Constants.Defense_Arrays.ROCK_WALL_INDEX]));

            textView = (TextView) convertView.findViewById(R.id.event_Low_Bar);
            textView.setText(create_text(team.crosses[Constants.Defense_Arrays.LOW_BAR_INDEX], team.seens[Constants.Defense_Arrays.LOW_BAR_INDEX], team.time[Constants.Defense_Arrays.LOW_BAR_INDEX]));
        }

        return convertView;
    }

    /**
     * Formats text from data
     *
     * @param cross Number of times crossed
     * @param seen  Number of times seen
     * @param speed Speed at which the robot crossed
     * @return formated text
     */
    private String create_text(int cross, int seen, float speed) {
        return String.format("%d (%d) : %.2f", cross, seen, speed);
    }
}
