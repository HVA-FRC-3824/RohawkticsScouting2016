package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

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
/*

        if(statsMap.containsKey("total_matches"))
        {

            TextView textView = (TextView)view.findViewById(R.id.total_matches);
            int numMatches = statsMap.get("total_matches").getInt();
            textView.setText(String.valueOf(numMatches));

            textView = (TextView)view.findViewById(R.id.average_points);
            float averagePoints = statsMap.get("total_points").getInt();
            averagePoints /= (float)numMatches;
            textView.setText(String.valueOf(averagePoints));

            textView = (TextView)view.findViewById(R.id.average_totes);
            float averageTotes = statsMap.get("total_totes").getInt();
            averageTotes /= (float)numMatches;
            textView.setText(String.valueOf(averageTotes));

            textView = (TextView)view.findViewById(R.id.average_cans);
            float averageCans = statsMap.get("total_cans").getInt();
            averageCans /= (float)numMatches;
            textView.setText(String.valueOf(averageCans));

            textView = (TextView)view.findViewById(R.id.average_noodles);
            float averageNoodles = statsMap.get("total_noodles").getInt();
            averageNoodles /= (float)numMatches;
            textView.setText(String.valueOf(averageNoodles));

            textView = (TextView)view.findViewById(R.id.total_auto_three_tote_stack);
            int totalAutoThreeToteStack = statsMap.get("total_auto_three_tote_stack").getInt();
            textView.setText(String.valueOf(totalAutoThreeToteStack));

            textView = (TextView)view.findViewById(R.id.total_auto_step_cans);
            int totalAutoStepCans = statsMap.get("total_auto_step_cans").getInt();
            textView.setText(String.valueOf(totalAutoStepCans));

            textView = (TextView)view.findViewById(R.id.total_teleop_step_cans);
            int totalTeleopStepCans = statsMap.get("total_step_cans").getInt();
            textView.setText(String.valueOf(totalTeleopStepCans));

            textView = (TextView)view.findViewById(R.id.total_coop_totes);
            int totalCoopTotes = statsMap.get("total_coop_totes").getInt();
            textView.setText(String.valueOf(totalCoopTotes));

            textView = (TextView)view.findViewById(R.id.total_knocked_over_stacks);
            int totalKnockedOverStacks = statsMap.get("total_knocked_over_stacks").getInt();
            textView.setText(String.valueOf(totalKnockedOverStacks));

            textView = (TextView)view.findViewById(R.id.total_dropped_stacks);
            int totalDroppedStacks = statsMap.get("total_dropped_stacks").getInt();
            textView.setText(String.valueOf(totalDroppedStacks));

            textView = (TextView)view.findViewById(R.id.position);
            int totalLandfill = statsMap.get("total_landfill").getInt();
            int totalHPFeeder = statsMap.get("total_hp_feeder").getInt();
            if(totalHPFeeder > numMatches/2 && totalLandfill > numMatches/2)
            {
                textView.setText("Both");
            }
            else if(totalHPFeeder > numMatches/2)
            {
                textView.setText("HP Feeder");
            }
            else if(totalLandfill > numMatches/2)
            {
                textView.setText("Landfill");
            }
            else
            {
                textView.setText("Neither");
            }


            textView = (TextView)view.findViewById(R.id.total_fouls);
            int totalFouls = statsMap.get("total_fouls").getInt();
            textView.setText(String.valueOf(totalFouls));

            textView = (TextView)view.findViewById(R.id.total_dqs);
            int totalDqs = statsMap.get("total_dqs").getInt();
            textView.setText(String.valueOf(totalDqs));

            textView = (TextView)view.findViewById(R.id.total_stopped_moving);
            int totalStoppedMoving = statsMap.get("total_stopped_moving").getInt();
            textView.setText(String.valueOf(totalStoppedMoving));

            textView = (TextView)view.findViewById(R.id.total_didnt_show_up);
            int totalDidntShowUp = statsMap.get("total_didnt_show_up").getInt();
            textView.setText(String.valueOf(totalDidntShowUp));

            textView = (TextView)view.findViewById(R.id.driver_ability);
            if(statsMap.containsKey("super_drive_ability_ranking")) {
                String ranking = statsMap.get("super_drive_ability_ranking").getString();
                if(ranking.charAt(ranking.length()-1) == '1')
                    ranking += "st";
                else if(ranking.charAt(ranking.length()-1) == '2')
                    ranking += "nd";
                else if(ranking.charAt(ranking.length()-1) == '3')
                    ranking += "rd";
                else
                    ranking += "th";
                textView.setText(ranking);
            }
            else
            {
                textView.setText("N/A");
            }
        }
        */
        statsDB.close();
        return view;
    }
}
