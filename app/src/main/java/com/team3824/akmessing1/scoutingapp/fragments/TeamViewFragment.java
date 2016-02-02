package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

import java.util.Map;

public class TeamViewFragment extends Fragment {

    View view;

    public TeamViewFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_team_view, container, false);

        return view;
    }

    public void setTeamNumber(final int teamNumber)
    {
        TextView textView = (TextView)view.findViewById(R.id.team_number);
        textView.setText(String.valueOf(teamNumber));


        Activity activity = getActivity();

        SharedPreferences sharedPreferences = activity.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString("event_id", "");

        StatsDB statsDB = new StatsDB(activity,eventId);
        Map<String, ScoutValue> statsMap = statsDB.getTeamStats(teamNumber);
        if(statsMap.containsKey("total_matches"))
        {
            int numMatches = statsMap.get("total_matches").getInt();
            textView = (TextView)view.findViewById(R.id.num_matches);
            textView.setText(String.valueOf(numMatches));


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

            textView = (TextView)view.findViewById(R.id.avg_points);
            float averagePoints = statsMap.get("").getInt();
            averagePoints /= (float)numMatches;
            textView.setText(String.valueOf(averagePoints));

            textView = (TextView)view.findViewById(R.id.three_tote_autos);
            int totalAutoThreeToteStack = statsMap.get("total_auto_three_tote_stack").getInt();
            textView.setText(String.valueOf(totalAutoThreeToteStack));

            textView = (TextView)view.findViewById(R.id.auto_step_cans);
            int totalAutoStepCans = statsMap.get("total_auto_step_cans").getInt();
            textView.setText(String.valueOf(totalAutoStepCans));

            textView = (TextView)view.findViewById(R.id.coop_totes_placed);
            int totalCoopTotes = statsMap.get("total_coop_totes").getInt();
            textView.setText(String.valueOf(totalCoopTotes));

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
        else
        {
            textView = (TextView)view.findViewById(R.id.num_matches);
            textView.setText(String.valueOf(0));
        }
        Button button = (Button)view.findViewById(R.id.view_team);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TeamView.class);
                intent.putExtra("team_number",teamNumber);
                startActivity(intent);
            }
        });
    }
}
