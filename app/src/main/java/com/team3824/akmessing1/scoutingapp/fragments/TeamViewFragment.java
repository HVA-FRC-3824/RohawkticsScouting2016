package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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

    private String TAG = "TeamViewFragment";

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

    public void setTeamNumber(final int teamNumber, Context context)
    {
        TextView textView = (TextView)view.findViewById(R.id.team_number);
        textView.setText(String.valueOf(teamNumber));

        SharedPreferences sharedPreferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");

        Button button = (Button) view.findViewById(R.id.view_team);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TeamView.class);
                intent.putExtra("team_number", teamNumber);
                startActivity(intent);
            }
        });

        StatsDB statsDB = new StatsDB(context,eventId);
        Map<String, ScoutValue> statsMap = statsDB.getTeamStats(teamNumber);
        Log.d(TAG, String.format("TN: %d", teamNumber));
        if(statsMap.containsKey(Constants.TOTAL_MATCHES)) {
            textView = (TextView) view.findViewById(R.id.total_matches);
            int numMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();
            Log.d(TAG, String.format("TN: %d NM%d", teamNumber, numMatches));
            textView.setText(String.valueOf(numMatches));

            if (numMatches > 0) {

                textView = (TextView) view.findViewById(R.id.bestPosition);

                ArrayList<TeamMatchDataStartP> starts = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    TeamMatchDataStartP start = new TeamMatchDataStartP();
                    start.mDefenseStartAmount += statsMap.get(Constants.TOTAL_DEFENSES_STARTED[i]).getInt();
                    start.mDefenseStart = i;
                    starts.add(start);
                }

                Collections.sort(starts, new Comparator<TeamMatchDataStartP>() {
                    @Override
                    public int compare(TeamMatchDataStartP lhs, TeamMatchDataStartP rhs) {
                        return rhs.mDefenseStartAmount - lhs.mDefenseStartAmount;
                    }
                });

                String BestPosition = String.valueOf(Constants.DEFENSES[starts.get(0).mDefenseStart]).replaceAll("_", " ");

                textView.setText(String.valueOf(BestPosition));

                textView = (TextView) view.findViewById(R.id.average_points);
                int mFoulPoints = statsMap.get(Constants.TOTAL_FOULS).getInt() * -5 +
                        statsMap.get(Constants.TOTAL_TECH_FOULS).getInt() * -5;

                int mEndgamePoints = statsMap.get(Constants.TOTAL_CHALLENGE).getInt() * 5 +
                        statsMap.get(Constants.TOTAL_SCALE).getInt() * 15;

                int mTeleopPoints = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() * 5 +
                        statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() * 2;

                for (int i = 0; i < 9; i++) {
                        mTeleopPoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i]).getInt() * 5;
                }

                int mAutoPoints = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt() * 10 +
                        statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt() * 5;
                for (int i = 0; i < 9; i++) {
                    mAutoPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                    mAutoPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                }
                float mTotalPoints = mEndgamePoints + mTeleopPoints + mAutoPoints + mFoulPoints;


                float averagePoints = mTotalPoints / (float)numMatches;

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
                float mAutoHighAccuracy = ((mAutoHighMade / mAutoHighTotal) * 100);
                float mAutoLowAccuracy = ((mAutoLowMade / mAutoLowTotal) * 100);
                float mAutoReachPoints = 0;
                float mAutoCrossPoints = 0;
                for (int i = 0; i < 9; i++) {
                    mAutoReachPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 5;
                    mAutoCrossPoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                }
                float mAutoDefensePoints = (mAutoReachPoints + mAutoCrossPoints);
                float mOverallAutoTotal = (mAutoHighPointsTotal + mAutoLowPointsTotal + mAutoDefensePoints);
                float mOverallAutoAvg = (mOverallAutoTotal / numMatches);

                textView.setText(String.format("\n %.2f\n %.2f\n %.2f%%\n %.2f%%",mOverallAutoTotal,mOverallAutoAvg,
                        mAutoHighAccuracy,mAutoLowAccuracy));

                textView = (TextView) view.findViewById(R.id.overall_teleop_points);
                float mTeleopFoulPoints = statsMap.get(Constants.TOTAL_FOULS).getInt() * -5 +
                        statsMap.get(Constants.TOTAL_TECH_FOULS).getInt() * -5;
                float mTeleopHighMade = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt();
                float mTeleopHighMissed = statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt();
                float mTeleopHighTotal = (mTeleopHighMade + mTeleopHighMissed);
                float mTeleopHighPointsTotal = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt() * 5;
                float mTeleopLowMade = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt();
                float mTeleopLowMissed = statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt();
                float mTeleopLowTotal = (mTeleopLowMade + mTeleopLowMissed);
                float mTeleopLowPointsTotal = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt() * 2;
                float mTeleopHighAccuracy = ((mTeleopHighMade / mTeleopHighTotal) * 100);
                float mTeleopLowAccuracy = ((mTeleopLowMade / mTeleopLowTotal) * 100);
                float mTeleopCrossPoints = 0;

                for (int i = 0; i < 9; i++) {
                        mTeleopPoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt() * 5;
                }
                float mTeleopDefensePoints = mTeleopCrossPoints;
                float mOverallTeleopTotal = (mTeleopHighPointsTotal + mTeleopLowPointsTotal + mTeleopDefensePoints + mTeleopFoulPoints);
                float mOverallTeleopAvg = (mOverallTeleopTotal / numMatches);
                textView.setText(String.format("\n %.2f\n %.2f\n %.2f%%\n %.2f%%\n",mOverallTeleopTotal,mOverallTeleopAvg,
                        mTeleopHighAccuracy,mTeleopLowAccuracy));

                textView = (TextView) view.findViewById(R.id.can_scale);
                int mCanScale = statsMap.get(Constants.TOTAL_SCALE).getInt();
                textView.setText(String.valueOf(mCanScale));

                textView = (TextView) view.findViewById(R.id.best2_worst2_defenses);


                ArrayList<TeamViewFragmentD> defenses = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    TeamViewFragmentD defense = new TeamViewFragmentD();
                    defense.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt() * 2;
                    defense.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt() * 10;
                    defense.mDefensePoints += statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i]).getInt() * 5;
                    defense.mDefense = i;
                    defenses.add(defense);
                }


                Collections.sort(defenses, new Comparator<TeamViewFragmentD>() {
                    @Override
                    public int compare(TeamViewFragmentD lhs, TeamViewFragmentD rhs) {
                        return rhs.mDefensePoints - lhs.mDefensePoints;
                    }
                });

                if(defenses.get(0).mDefensePoints == 0)
                {
                    String Bestdefense1 = Constants.DEFENSES_LABEL[defenses.get(0).mDefense];
                    String Bestdefense2 = Constants.DEFENSES_LABEL[defenses.get(1).mDefense];
                    String Worstdefense1 = Constants.DEFENSES_LABEL[defenses.get(7).mDefense];
                    String Worstdefense2 = Constants.DEFENSES_LABEL[defenses.get(8).mDefense];
                    textView.setText(String.format("1. %s\n2. %s\n1. %s\n2. %s",Bestdefense1, Bestdefense2,Worstdefense1,Worstdefense2));
                }
                else
                {
                    textView.setText("None\n\nNone\n\n");
                }
                

                textView = (TextView) view.findViewById(R.id.driver_ability);
                if (statsMap.containsKey(Constants.DRIVE_ABILITY_RANKING)) {
                    String ranking = statsMap.get(Constants.DRIVE_ABILITY_RANKING).getString();
                    if (ranking.charAt(ranking.length() - 1) == '1')
                        ranking += "st";
                    else if (ranking.charAt(ranking.length() - 1) == '2')
                        ranking += "nd";
                    else if (ranking.charAt(ranking.length() - 1) == '3')
                        ranking += "rd";
                    else
                        ranking += "th";
                    textView.setText(ranking);
                }
                else {
                    textView.setText("N/A");
                }
                    textView = (TextView) view.findViewById(R.id.defense_ability);
                    if (statsMap.containsKey(Constants.DEFENSE_ABILITY_RANKING)) {
                        String ranking = statsMap.get(Constants.DEFENSE_ABILITY_RANKING).getString();
                        if (ranking.charAt(ranking.length() - 1) == '1')
                            ranking += "st";
                        else if (ranking.charAt(ranking.length() - 1) == '2')
                            ranking += "nd";
                        else if (ranking.charAt(ranking.length() - 1) == '3')
                            ranking += "rd";
                        else
                            ranking += "th";
                        textView.setText(ranking);
                    }
                    else {
                        textView.setText("N/A");
                }
            }
            else {
                textView = (TextView) view.findViewById(R.id.best2_worst2_defenses);
                textView.setText("\n\n\n");
            }
        }
        else
        {
            textView = (TextView) view.findViewById(R.id.total_matches);
            textView.setText("0");

            textView = (TextView) view.findViewById(R.id.best2_worst2_defenses);
            textView.setText("\n\n\n\n\n\n\n\n\n\n\n\n");
        }
    }
}


