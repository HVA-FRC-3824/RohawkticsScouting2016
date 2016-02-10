package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.list_items.ELI_Points;

import java.util.Map;


public class TeamMatchData extends Fragment {

    public TeamMatchData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_match_data, container, false);

        Bundle args = getArguments();
        int teamNumber = args.getInt("teamNumber", -1);
        Activity activity = getActivity();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");
        StatsDB statsDB = new StatsDB(activity,eventID);
        Map<String, ScoutValue> statsMap = statsDB.getTeamStats(teamNumber);

        // Setup Points header row
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.points_header);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.event_teamNum).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_avg_points)).setText("Avg Points");
        ((TextView)linearLayout.findViewById(R.id.event_points)).setText("Total Points");
        ((TextView)linearLayout.findViewById(R.id.event_high_points)).setText("High Goal Points");
        ((TextView)linearLayout.findViewById(R.id.event_low_points)).setText("Low Goal Points");
        ((TextView)linearLayout.findViewById(R.id.event_defense_points)).setText("Defenses Points");
        ((TextView)linearLayout.findViewById(R.id.event_auto_points)).setText("Auto Points");
        ((TextView)linearLayout.findViewById(R.id.event_teleop_points)).setText("Teleop Points");
        ((TextView)linearLayout.findViewById(R.id.event_endgame_points)).setText("Endgame Points");
        ((TextView)linearLayout.findViewById(R.id.event_foul_points)).setText("Foul Points");


        linearLayout = (LinearLayout)view.findViewById(R.id.points);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.event_teamNum).setVisibility(View.GONE);

        ELI_Points team = new ELI_Points(teamNumber);

        boolean hasPlayed = statsMap.containsKey(Constants.TOTAL_MATCHES);

        if(hasPlayed) {
            float totalMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();
            ((TextView)view.findViewById(R.id.num_matches)).setText(String.valueOf((int)totalMatches));

            team.mHighPoints = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() * 5 +
                    statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() * 10;

            team.mLowPoints = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() * 2 +
                    statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() * 5;

            team.mEndgamePoints = statsMap.get(Constants.TOTAL_CHALLENGE).getInt() * 5 +
                    statsMap.get(Constants.TOTAL_SCALE).getInt() * 15;

            for (int i = 0; i < 9; i++) {
                team.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                team.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                team.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt() * 5;
            }

            team.mTeleopPoints = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() * 5 +
                    statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() * 2;
            for (int i = 0; i < 9; i++) {
                team.mTeleopPoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt() * 5;
            }

            team.mAutoPoints = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() * 10 +
                    statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() * 5;
            for (int i = 0; i < 9; i++) {
                team.mAutoPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                team.mAutoPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
            }


            team.mFoulPoints = statsMap.get(Constants.TOTAL_FOULS).getInt() * -5 +
                    statsMap.get(Constants.TOTAL_TECH_FOULS).getInt() * -5;

            team.mTotalPoints = team.mEndgamePoints + team.mTeleopPoints + team.mAutoPoints + team.mFoulPoints;

            team.mAvgPoints = (totalMatches == 0.0f) ? 0.0f : (float) team.mTotalPoints / totalMatches;

            ((TextView)linearLayout.findViewById(R.id.event_avg_points)).setText(String.valueOf(team.mAvgPoints));
            ((TextView)linearLayout.findViewById(R.id.event_points)).setText(String.valueOf(team.mTotalPoints));
            ((TextView)linearLayout.findViewById(R.id.event_high_points)).setText(String.valueOf(team.mHighPoints));
            ((TextView)linearLayout.findViewById(R.id.event_low_points)).setText(String.valueOf(team.mLowPoints));
            ((TextView)linearLayout.findViewById(R.id.event_defense_points)).setText(String.valueOf(team.mDefensePoints));
            ((TextView)linearLayout.findViewById(R.id.event_auto_points)).setText(String.valueOf(team.mAutoPoints));
            ((TextView)linearLayout.findViewById(R.id.event_teleop_points)).setText(String.valueOf(team.mTeleopPoints));
            ((TextView)linearLayout.findViewById(R.id.event_endgame_points)).setText(String.valueOf(team.mEndgamePoints));
            ((TextView)linearLayout.findViewById(R.id.event_foul_points)).setText(String.valueOf(team.mFoulPoints));
        }
        else
        {
            ((TextView)view.findViewById(R.id.num_matches)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_avg_points)).setText("0.0");
            ((TextView)linearLayout.findViewById(R.id.event_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_high_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_low_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_defense_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_auto_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_teleop_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_endgame_points)).setText("0");
            ((TextView)linearLayout.findViewById(R.id.event_foul_points)).setText("0");
        }


        // Setup Defenses header row
        linearLayout = (LinearLayout)view.findViewById(R.id.defense_header);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Defense");
        ((TextView)linearLayout.findViewById(R.id.event_cross)).setText("Auto Cross");
        ((TextView)linearLayout.findViewById(R.id.event_reach)).setText("Auto Reach");
        ((TextView)linearLayout.findViewById(R.id.event_seen)).setText("Seen");
        ((TextView)linearLayout.findViewById(R.id.event_teleop_cross)).setText("Teleop Cross");
        ((TextView)linearLayout.findViewById(R.id.event_time)).setText("Time (s)");

        // Portcullis row
        linearLayout = (LinearLayout)view.findViewById(R.id.portcullis);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Portcullis");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.PORTCULLIS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.PORTCULLIS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.PORTCULLIS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.PORTCULLIS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.PORTCULLIS_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.PORTCULLIS_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Cheval de Frise row
        linearLayout = (LinearLayout)view.findViewById(R.id.cheval_de_frise);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Cheval de Frise");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.CHEVAL_DE_FRISE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.CHEVAL_DE_FRISE_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Moat row
        linearLayout = (LinearLayout)view.findViewById(R.id.moat);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Moat");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.MOAT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.MOAT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.MOAT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.MOAT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.MOAT_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.MOAT_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Ramparts row
        linearLayout = (LinearLayout)view.findViewById(R.id.ramparts);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Ramparts");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.RAMPARTS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.RAMPARTS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.RAMPARTS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.RAMPARTS_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.RAMPARTS_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.RAMPARTS_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Drawbridge row
        linearLayout = (LinearLayout)view.findViewById(R.id.drawbridge);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Drawbridge");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.DRAWBRIDGE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.DRAWBRIDGE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.DRAWBRIDGE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.DRAWBRIDGE_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.DRAWBRIDGE_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Sally Port row
        linearLayout = (LinearLayout)view.findViewById(R.id.sally_port);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Sally Port");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.SALLY_PORT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.SALLY_PORT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.SALLY_PORT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.SALLY_PORT_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.SALLY_PORT_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Rock Wall row
        linearLayout = (LinearLayout)view.findViewById(R.id.rock_wall);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Rock Wall");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROCK_WALL_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.ROCK_WALL_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.ROCK_WALL_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROCK_WALL_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROCK_WALL_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.ROCK_WALL_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Rough Terrain row
        linearLayout = (LinearLayout)view.findViewById(R.id.rough_terrain);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Rough Terrain");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROUGH_TERRAIN_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.ROUGH_TERRAIN_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.ROUGH_TERRAIN_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROUGH_TERRAIN_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROUGH_TERRAIN_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.ROUGH_TERRAIN_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Low Bar row
        linearLayout = (LinearLayout)view.findViewById(R.id.low_bar);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Low Bar");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.LOW_BAR_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.LOW_BAR_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.LOW_BAR_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText(String.valueOf(statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.LOW_BAR_INDEX]).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText(String.valueOf((statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.LOW_BAR_INDEX]).getInt() > 0) ? statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.LOW_BAR_INDEX]).getInt() : -1));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_reach)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_seen)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_cross)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_time)).setText("-1");
        }

        // Setup shooting header row
        linearLayout = (LinearLayout)view.findViewById(R.id.shot_header);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Goal");
        ((TextView)linearLayout.findViewById(R.id.event_auto_made)).setText("Auto Made");
        ((TextView)linearLayout.findViewById(R.id.event_auto_taken)).setText("Auto Taken");
        ((TextView)linearLayout.findViewById(R.id.event_auto_percentage)).setText("Auto Percentage");
        ((TextView)linearLayout.findViewById(R.id.event_teleop_made)).setText("Teleop Made");
        ((TextView)linearLayout.findViewById(R.id.event_teleop_taken)).setText("Teleop Taken");
        ((TextView)linearLayout.findViewById(R.id.event_teleop_percentage)).setText("Teleop Percentage");

        // High Goal
        linearLayout = (LinearLayout)view.findViewById(R.id.high);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("High");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_auto_made)).setText(String.valueOf(statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_auto_taken)).setText(String.valueOf(statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() + statsMap.get(Constants.TOTAL_AUTO_HIGH_MISS).getInt()));
            float percent = (statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt()+statsMap.get(Constants.TOTAL_AUTO_HIGH_MISS).getInt() > 0) ? statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() / (float)(statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt()+statsMap.get(Constants.TOTAL_AUTO_HIGH_MISS).getInt()) : 0.0f;
            percent *= 1000.0;
            percent = (int)percent;
            percent /= 10.0;
            ((TextView) linearLayout.findViewById(R.id.event_auto_percentage)).setText(String.valueOf(percent)+"%");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_made)).setText(String.valueOf(statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_taken)).setText(String.valueOf(statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt()+statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt()));
            percent = (statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt()+statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt() > 0) ? statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() / (float)(statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt()+statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt()) : 0.0f;
            percent *= 1000.0;
            percent = (int)percent;
            percent /= 10.0;
            ((TextView) linearLayout.findViewById(R.id.event_teleop_percentage)).setText(String.valueOf(percent)+"%");
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_auto_made)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_auto_taken)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_auto_percentage)).setText("0.0%");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_made)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_taken)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_percentage)).setText("0.0%");
        }

        // Low Goal
        linearLayout = (LinearLayout)view.findViewById(R.id.low);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_teamNum)).setText("Low");
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_auto_made)).setText(String.valueOf(statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_auto_taken)).setText(String.valueOf(statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() + statsMap.get(Constants.TOTAL_AUTO_LOW_MISS).getInt()));
            float percent = (statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt()+statsMap.get(Constants.TOTAL_AUTO_LOW_MISS).getInt() > 0) ? statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() / (float)(statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt()+statsMap.get(Constants.TOTAL_AUTO_LOW_MISS).getInt()) : 0.0f;
            percent *= 1000.0;
            percent = (int)percent;
            percent /= 10.0;
            ((TextView) linearLayout.findViewById(R.id.event_auto_percentage)).setText(String.valueOf(percent)+"%");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_made)).setText(String.valueOf(statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_teleop_taken)).setText(String.valueOf(statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt()+statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt()));
            percent = (statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt()+statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt() > 0) ? statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() / (float)(statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt()+statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt()) : 0.0f;
            percent *= 1000.0;
            percent = (int)percent;
            percent /= 10.0;
            ((TextView) linearLayout.findViewById(R.id.event_teleop_percentage)).setText(String.valueOf(percent)+"%");
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_auto_made)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_auto_taken)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_auto_percentage)).setText("0.0%");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_made)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_taken)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_teleop_percentage)).setText("0.0%");
        }

        if(hasPlayed) {
            String ranking = statsMap.get(Constants.DEFENSE_ABILITY_RANKING).getString();
            switch(ranking.charAt(ranking.length()-1))
            {
                case '1':
                    ranking += "st";
                    break;
                case '2':
                    ranking += "nd";
                    break;
                case '3':
                    ranking += "rd";
                    break;
                default:
                    ranking += "th";
                    break;
            }
            ((TextView) view.findViewById(R.id.defense_ability)).setText(ranking);
            ranking = statsMap.get(Constants.DRIVE_ABILITY_RANKING).getString();
            switch(ranking.charAt(ranking.length()-1))
            {
                case '1':
                    ranking += "st";
                    break;
                case '2':
                    ranking += "nd";
                    break;
                case '3':
                    ranking += "rd";
                    break;
                default:
                    ranking += "th";
                    break;
            }
            ((TextView) view.findViewById(R.id.driver_ability)).setText(ranking);
        }


        // Setup endgame header row
        linearLayout = (LinearLayout)view.findViewById(R.id.endgame_header);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.event_teamNum).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_challenge)).setText("Challenge");
        ((TextView)linearLayout.findViewById(R.id.event_scale)).setText("Scale");

        linearLayout = (LinearLayout)view.findViewById(R.id.endgame);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.event_teamNum).setVisibility(View.GONE);
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_challenge)).setText(String.valueOf(statsMap.get(Constants.TOTAL_CHALLENGE).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_scale)).setText(String.valueOf(statsMap.get(Constants.TOTAL_SCALE).getInt()));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_challenge)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_scale)).setText("0");
        }

        // Setup fouls header row
        linearLayout = (LinearLayout)view.findViewById(R.id.fouls_header);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.event_teamNum).setVisibility(View.GONE);
        ((TextView)linearLayout.findViewById(R.id.event_fouls)).setText("Fouls");
        ((TextView)linearLayout.findViewById(R.id.event_tech_fouls)).setText("Tech Fouls");
        ((TextView)linearLayout.findViewById(R.id.event_yellow_cards)).setText("Yellow Cards");
        ((TextView)linearLayout.findViewById(R.id.event_red_cards)).setText("Red Cards");

        linearLayout = (LinearLayout)view.findViewById(R.id.fouls);
        linearLayout.findViewById(R.id.event_rank).setVisibility(View.GONE);
        linearLayout.findViewById(R.id.event_teamNum).setVisibility(View.GONE);
        if(hasPlayed) {
            ((TextView) linearLayout.findViewById(R.id.event_fouls)).setText(String.valueOf(statsMap.get(Constants.TOTAL_FOULS).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_tech_fouls)).setText(String.valueOf(statsMap.get(Constants.TOTAL_TECH_FOULS).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_yellow_cards)).setText(String.valueOf(statsMap.get(Constants.TOTAL_YELLOW_CARDS).getInt()));
            ((TextView) linearLayout.findViewById(R.id.event_red_cards)).setText(String.valueOf(statsMap.get(Constants.TOTAL_RED_CARDS).getInt()));
        }
        else
        {
            ((TextView) linearLayout.findViewById(R.id.event_fouls)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_tech_fouls)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_yellow_cards)).setText("0");
            ((TextView) linearLayout.findViewById(R.id.event_red_cards)).setText("0");
        }

        statsDB.close();
        return view;
    }
}
