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
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.Team;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class PickListAdapter extends ArrayAdapter<Team> {

    private ArrayList<Team> teams;
    int pickNumber;
    Context context;
    StatsDB statsDB;

    public PickListAdapter(Context context, int textViewResourceId, ArrayList<Team> teams, int pickNumber, StatsDB statsDB) {
        super(context, textViewResourceId, teams);
        this.context = context;
        this.teams = teams;
        this.pickNumber = pickNumber;
        this.statsDB = statsDB;
    }

    public void add(int to, Team team)
    {
        teams.add(to,team);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_pick, null);
        }

        final Team t = teams.get(position);
        if(t != null)
        {
            if(t.getMapElement(StatsDB.KEY_PICKED).getInt() > 0)
            {
                convertView.setBackgroundColor(Color.RED);
            }
            else
            {
                convertView.setBackgroundColor(Color.GREEN);
            }

            ScoutValue robotPicture = t.getMapElement("robotPicture");

            Button button = (Button)convertView.findViewById(R.id.pick_view_team);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), TeamView.class);
                    intent.putExtra("team_number", t.getTeamNumber());
                    getContext().startActivity(intent);
                }
            });

            final Comparator<Team> compare = new Comparator<Team>(){
                public int compare(Team a, Team b)
                {
                    int rankA = a.getMapElement(StatsDB.KEY_FIRST_PICK_RANK).getInt();
                    int rankB = b.getMapElement(StatsDB.KEY_FIRST_PICK_RANK).getInt();
                    if(a.getMapElement(StatsDB.KEY_PICKED).getInt() > 0 && b.getMapElement(StatsDB.KEY_PICKED).getInt() > 0)
                    {
                        return rankA - rankB;
                    }
                    else if( a.getMapElement(StatsDB.KEY_PICKED).getInt() > 0 )
                    {
                        return 1;
                    }
                    else if(b.getMapElement(StatsDB.KEY_PICKED).getInt() > 0)
                    {
                        return -1;
                    }
                    else {
                        return rankA - rankB;
                    }
                }
            };

            button = (Button)convertView.findViewById(R.id.team_picked);
            if(t.getMapElement(StatsDB.KEY_PICKED).getInt() > 0)
            {
                button.setText("Unpicked");
                button.setBackgroundColor(Color.GREEN);
            }
            else
            {
                button.setText("Picked");
                button.setBackgroundColor(Color.RED);
            }
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button b = (Button)v;
                    String text = String.valueOf(b.getText());
                    if(text.equals("Picked")) {
                        t.setMapElement(StatsDB.KEY_PICKED, new ScoutValue(1));
                        teams.remove(t);
                        teams.add(t);
                        HashMap<String, ScoutValue> map = new HashMap<>();
                        map.put(StatsDB.KEY_TEAM_NUMBER,new ScoutValue(t.getTeamNumber()));
                        map.put(StatsDB.KEY_PICKED,new ScoutValue(1));
                        statsDB.updateStats(map);
                    }
                    else
                    {
                        t.setMapElement(StatsDB.KEY_PICKED, new ScoutValue(0));
                        Collections.sort(teams,compare);
                        HashMap<String, ScoutValue> map = new HashMap<>();
                        map.put(StatsDB.KEY_TEAM_NUMBER,new ScoutValue(t.getTeamNumber()));
                        map.put(StatsDB.KEY_PICKED, new ScoutValue(0));
                        statsDB.updateStats(map);
                    }
                    notifyDataSetChanged();
                }
            });

            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            if(robotPicture != null)
            {
                String imagePath = robotPicture.getString();
                if(imagePath != "") {
                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath() +"/"+ imagePath),
                            64, 64);
                    imageView.setImageBitmap(thumbnail);
                }
            }

            int teamNumber = t.getTeamNumber();
            String nickname = t.getNickname();
            TextView topText = (TextView)convertView.findViewById(R.id.topText);
            topText.setText(String.valueOf(teamNumber)+ " - "+nickname);

            TextView bottomText = (TextView)convertView.findViewById(R.id.bottomText);
            switch (pickNumber)
            {
                case 1:
                    bottomText.setText(t.getMapElement("first_pick_bottom_text").getString());
                    break;
                case 2:
                    bottomText.setText(t.getMapElement("second_pick_bottom_text").getString());
                    break;
                case 3:
                    bottomText.setText(t.getMapElement("third_pick_bottom_text").getString());
                    break;
            }
        }

        return convertView;
    }
}
