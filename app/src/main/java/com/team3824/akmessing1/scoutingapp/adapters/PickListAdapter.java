package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.Team;

import java.util.ArrayList;

public class PickListAdapter extends ArrayAdapter<Team> {

    private ArrayList<Team> teams;
    int pickNumber;

    public PickListAdapter(Context context, int textViewResourceId, ArrayList<Team> teams, int pickNumber) {
        super(context, textViewResourceId, teams);
        this.teams = teams;
        this.pickNumber = pickNumber;
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

        Team t = teams.get(position);
        if(t != null)
        {
            ScoutValue robotPicture = t.getMapElement("robotPicture");
            ImageView imageView = (ImageView)convertView.findViewById(R.id.imageView);
            if(robotPicture != null)
            {
                String imagePath = robotPicture.getString();
                if(imagePath != "") {
                    Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imagePath),
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
