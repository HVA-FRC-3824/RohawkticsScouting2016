package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Adapter for the list view that holds all the teams for a given picklist
 */
public class PickListAdapter extends ArrayAdapter<Team> {

    String pickType;
    Context context;
    StatsDB statsDB;
    Comparator<Team> comparator;
    private ArrayList<Team> teams;

    /**
     * @param context
     * @param textViewResourceId
     * @param teams
     * @param pickType           The tag for the type of pick.
     * @param statsDB            The database helper for the stats database table
     * @param comparator         The comparator to use to sort the teams
     */
    public PickListAdapter(Context context, int textViewResourceId, ArrayList<Team> teams, String pickType, StatsDB statsDB, Comparator<Team> comparator) {
        super(context, textViewResourceId, teams);
        this.context = context;
        this.teams = teams;
        this.pickType = pickType;
        this.statsDB = statsDB;
        this.comparator = comparator;
    }

    /**
     * @param to   Position to add the team to
     * @param team Team to add
     */
    public void add(int to, Team team) {
        teams.add(to, team);
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
            convertView = inflater.inflate(R.layout.list_item_pick, null);
        }

        final Team t = teams.get(position);
        if (t != null) {
            // Sets color of row based on if the team has been picked or not
            if (t.getMapElement(StatsDB.KEY_PICKED).getInt() > 0) {
                convertView.setBackgroundColor(Color.RED);
            } else {
                convertView.setBackgroundColor(Color.GREEN);
            }

            // Set up view team button
            Button button = (Button) convertView.findViewById(R.id.pick_view_team);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TeamView.class);
                    intent.putExtra(Constants.TEAM_NUMBER, t.getTeamNumber());
                    getContext().startActivity(intent);
                }
            });

            // Set up picked/unpicked button
            button = (Button) convertView.findViewById(R.id.team_picked);
            if (t.getMapElement(StatsDB.KEY_PICKED).getInt() > 0) {
                button.setText("Unpicked");
                button.setBackgroundColor(Color.GREEN);
            } else {
                button.setText("Picked");
                button.setBackgroundColor(Color.RED);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button) v;
                    String text = String.valueOf(b.getText());
                    if (text.equals("Picked")) {
                        t.setMapElement(StatsDB.KEY_PICKED, new ScoutValue(1));
                        teams.remove(t);
                        teams.add(t);
                        ScoutMap map = new ScoutMap();
                        map.put(StatsDB.KEY_TEAM_NUMBER, t.getTeamNumber());
                        map.put(StatsDB.KEY_PICKED, 1);
                        statsDB.updateStats(map);
                    } else {
                        t.setMapElement(StatsDB.KEY_PICKED, new ScoutValue(0));
                        Collections.sort(teams, comparator);
                        ScoutMap map = new ScoutMap();
                        map.put(StatsDB.KEY_TEAM_NUMBER,t.getTeamNumber());
                        map.put(StatsDB.KEY_PICKED, 0);
                        statsDB.updateStats(map);
                    }
                    notifyDataSetChanged();
                }
            });

            // Set up thumbnail
            //TODO: change to be based on the event id and the team number
            //TODO: use thumbnail creator?
            ScoutValue robotPicture = t.getMapElement(Constants.PIT_ROBOT_PICTURE);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
            if (robotPicture != null) {
                String imagePath = robotPicture.getString();
                if (imagePath != "") {
                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath() + "/" + imagePath),
                            64, 64);
                    imageView.setImageBitmap(thumbnail);
                }
            }

            //Add whether they received a yellow card or a red card
            if (t.containsMapElement(Constants.TOTAL_YELLOW_CARDS)) {
                if (t.getMapElement(Constants.TOTAL_YELLOW_CARDS).getInt() > 0) {
                    convertView.findViewById(R.id.yellow_card).setVisibility(View.VISIBLE);
                }

                if (t.getMapElement(Constants.TOTAL_RED_CARDS).getInt() > 0) {
                    convertView.findViewById(R.id.red_card).setVisibility(View.VISIBLE);
                }
            }

            //TODO: Add flag for if they stopped moving/didn't show up (Maybe how many times)

            int teamNumber = t.getTeamNumber();
            String nickname = t.getNickname();
            TextView topText = (TextView) convertView.findViewById(R.id.topText);
            topText.setText(String.format("%d - %s", teamNumber, nickname));

            TextView bottomText = (TextView) convertView.findViewById(R.id.bottomText);
            bottomText.setText(t.getMapElement(Constants.BOTTOM_TEXT).getString());

        }

        return convertView;
    }

    /**
     * Sorts the teams
     */
    public void sort() {
        Collections.sort(teams, comparator);
    }
}
