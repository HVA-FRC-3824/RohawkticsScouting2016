package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class TeamViewFragment extends Fragment {

    private class TeamMatchDataStartP{

        int mDefenseStart;
        int mDefenseStartAmount;
    }
 private class TeamViewFragmentD{

     int mDefense;
     int mDefensePoints;
 }
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
        if(statsMap.containsKey(Constants.TOTAL_MATCHES)) {


            textView = (TextView) view.findViewById(R.id.total_matches);
            float numMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();
            textView.setText(String.valueOf(numMatches));

            if (numMatches > 0) {

                float totalMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();

                textView = (TextView) view.findViewById(R.id.bestPosition);

                ArrayList<TeamMatchDataStartP> starts = new ArrayList<>();

                TeamMatchDataStartP start = new TeamMatchDataStartP();

                totalMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();

                if (totalMatches > 0) {
                    totalMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();

                    for (int i = 0; i < 9; i++) {
                        start.mDefenseStartAmount += statsMap.get(Constants.TOTAL_DEFENSES_STARTED[i]).getInt();



                        start.mDefenseStart = i;

                        starts.add(start);
                    }
                }


                Collections.sort(starts, new Comparator<TeamMatchDataStartP>() {
                    @Override
                    public int compare(TeamMatchDataStartP lhs, TeamMatchDataStartP rhs) {
                        return rhs.mDefenseStartAmount - lhs.mDefenseStartAmount;
                    }
                });

                String BestPosition =  String.valueOf(Constants.DEFENSES[starts.get(0).mDefenseStart]).replaceAll("_", " ");


                textView.setText(String.valueOf(BestPosition));

                textView = (TextView) view.findViewById(R.id.average_points);
                int mFoulPoints = statsMap.get(Constants.TOTAL_FOULS).getInt() * -5 +
                        statsMap.get(Constants.TOTAL_TECH_FOULS).getInt() * -5;

                int mEndgamePoints = statsMap.get(Constants.TOTAL_CHALLENGE).getInt() * 5 +
                        statsMap.get(Constants.TOTAL_SCALE).getInt() * 15;

                int mTeleopPoints = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() * 5 +
                        statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() * 2;
                for (int i = 0; i < 9; i++) {
                    mTeleopPoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt() * 5;
                }

                int mAutoPoints = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() * 10 +
                        statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() * 5;
                for (int i = 0; i < 9; i++) {
                    mAutoPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                    mAutoPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                }
                float mTotalPoints = mEndgamePoints + mTeleopPoints + mAutoPoints + mFoulPoints;


                float averagePoints = mTotalPoints / numMatches;

                textView.setText(String.valueOf(averagePoints));

                textView = (TextView) view.findViewById(R.id.overall_auto_points);
                float mAutoHighMade = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt();
                float mAutoHighMissed = statsMap.get(Constants.TOTAL_AUTO_HIGH_MISS).getInt();
                float mAutoHighTotal = (mAutoHighMade + mAutoHighMissed);
                float mAutoHighPointsTotal = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() * 10;
                float mAutoLowMade = statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt();
                float mAutoLowMissed = statsMap.get(Constants.TOTAL_AUTO_LOW_MISS).getInt();
                float mAutoLowTotal = (mAutoLowMade + mAutoLowMissed);
                float mAutoLowPointsTotal = statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() * 5;
                double mAutoAccuracy = ((mAutoHighMade + mAutoLowMade) / (mAutoHighTotal + mAutoLowTotal));
                float mAutoReachPoints = 0;
                float mAutoCrossPoints = 0;
                for (int i = 0; i < 9; i++) {
                    mAutoReachPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                    mAutoCrossPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                }
                float mAutoDefensePoints = (mAutoReachPoints + mAutoCrossPoints);
                float mOverallAutoTotal = (mAutoHighPointsTotal + mAutoLowPointsTotal + mAutoDefensePoints);
                float mOverallAutoAvg = (mOverallAutoTotal / numMatches);
                textView.setText(String.valueOf(mOverallAutoTotal) + " " + String.valueOf(mOverallAutoAvg) + " "
                        + String.valueOf(mAutoAccuracy));

                textView = (TextView) view.findViewById(R.id.overall_teleop_points);
                float mTeleopFoulPoints = statsMap.get(Constants.TOTAL_FOULS).getInt() * -5 +
                        statsMap.get(Constants.TOTAL_TECH_FOULS).getInt() * -5;
                float mTeleopHighMade = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt();
                float mTeleopHighMissed = statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt();
                float mTeleopHighTotal = (mTeleopHighMade + mTeleopHighMissed);
                float mTeleopHighPointsTotal = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() * 10;
                float mTeleopLowMade = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt();
                float mTeleopLowMissed = statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt();
                float mTeleopLowTotal = (mTeleopLowMade + mTeleopLowMissed);
                float mTeleopLowPointsTotal = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() * 2;
                float mTeleopAccuracy = ((mTeleopHighMade + mTeleopLowMade) / (mTeleopHighTotal + mTeleopLowTotal));
                float mTeleopCrossPoints = 0 ;
                //need some way of stopping after one time per defense
                for (int i = 0; i < 9; i++) {
                    mTeleopCrossPoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt() * 5;
                }
                float mTeleopDefensePoints = mTeleopCrossPoints;
                float mOverallTeleopTotal = (mTeleopHighPointsTotal + mTeleopLowPointsTotal + mTeleopDefensePoints + mTeleopFoulPoints);
                float mOverallTeleopAvg = (mOverallTeleopTotal / numMatches);
                textView.setText(String.valueOf(mOverallTeleopTotal) + " " + String.valueOf(mOverallTeleopAvg) + " "
                        + String.valueOf(mTeleopAccuracy));

                textView = (TextView) view.findViewById(R.id.best2_worst2_defenses);




                ArrayList<TeamViewFragmentD> defenses = new ArrayList<>();

                TeamViewFragmentD defense = new TeamViewFragmentD();

                totalMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();

                if (totalMatches > 0) {
                    totalMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();

                    for (int i = 0; i < 9; i++) {
                        defense.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                        defense.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                        defense.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt() * 5;


                        defense.mDefense = i;

                        defenses.add(defense);
                    }
                }


                Collections.sort(defenses, new Comparator<TeamViewFragmentD>() {
                    @Override
                    public int compare(TeamViewFragmentD lhs, TeamViewFragmentD rhs) {
                        return rhs.mDefensePoints - lhs.mDefensePoints;
                    }
                });

                        String Bestdefense1 =  String.valueOf(Constants.DEFENSES[defenses.get(0).mDefense]).replaceAll("_", " ");
                        String Bestdefense2 =  String.valueOf(Constants.DEFENSES[defenses.get(1).mDefense]).replaceAll("_", " ");
                        String Worstdefense1 = String.valueOf(Constants.DEFENSES[defenses.get(7).mDefense]).replaceAll("_", " ");
                        String Worstdefense2 = String.valueOf(Constants.DEFENSES[defenses.get(8).mDefense]).replaceAll("_", " ");

                textView.setText("Best: " + "1." + Bestdefense1 + " 2." +
                        Bestdefense2 + "\n" +
                        "Worst: " + "1." + Worstdefense1 + " 2." +
                        Worstdefense2);

                }


                textView = (TextView) view.findViewById(R.id.driver_ability);
                if (statsMap.containsKey("super_drive_ability_ranking")) {
                    String ranking = statsMap.get("super_drive_ability_ranking").getString();
                    if (ranking.charAt(ranking.length() - 1) == '1')
                        ranking += "st";
                    else if (ranking.charAt(ranking.length() - 1) == '2')
                        ranking += "nd";
                    else if (ranking.charAt(ranking.length() - 1) == '3')
                        ranking += "rd";
                    else
                        ranking += "th";
                    textView.setText(ranking);
                } else {
                    textView.setText("N/A");
                }
            } else {
                textView = (TextView) view.findViewById(R.id.total_matches);
                textView.setText(String.valueOf(0));
            }
            Button button = (Button) view.findViewById(R.id.view_team);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TeamView.class);
                        intent.putExtra("team_number", teamNumber);
                        startActivity(intent);
                    }
                });
    }
}

