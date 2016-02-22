package com.team3824.akmessing1.scoutingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class MatchTeamFragment extends Fragment {

    private String TAG = "MatchTeamFragment";

    private class StartPosition {

        int mDefenseIndex;
        int mNumCrosses;
        int mNumSeen;
    }
    private class Defense {

        int mDefenseIndex;
        float mTime;
    }
    View view;

    public MatchTeamFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match_team, container, false);

        return view;
    }

    public void setTeamNumber(final int teamNumber, Context context)
    {
        TextView textView = (TextView)view.findViewById(R.id.team_number);
        textView.setText(String.valueOf(teamNumber));

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");

        Button button = (Button) view.findViewById(R.id.view_team);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TeamView.class);
                intent.putExtra(Constants.TEAM_NUMBER, teamNumber);
                startActivity(intent);
            }
        });

        StatsDB statsDB = new StatsDB(context,eventId);
        Map<String, ScoutValue> statsMap = statsDB.getTeamStats(teamNumber);
        if(statsMap.containsKey(Constants.TOTAL_MATCHES)) {
            textView = (TextView) view.findViewById(R.id.total_matches);
            int numMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();
            textView.setText(String.valueOf(numMatches));

            if (numMatches > 0) {

                textView = (TextView) view.findViewById(R.id.best_start_position);

                ArrayList<StartPosition> starts = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    StartPosition start = new StartPosition();
                    start.mNumCrosses = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt();
                    start.mNumSeen = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[i]).getInt();
                    start.mDefenseIndex = i;
                    starts.add(start);
                }

                Collections.sort(starts, new Comparator<StartPosition>() {
                    @Override
                    public int compare(StartPosition lhs, StartPosition rhs) {

                        if (rhs.mNumSeen > 0 && lhs.mNumSeen > 0) {
                            return Float.compare((float) rhs.mNumCrosses / (float) rhs.mNumSeen, (float) lhs.mNumCrosses / (float) lhs.mNumSeen);
                        } else if (rhs.mNumSeen > 0) {
                            if (rhs.mNumCrosses > 0) {
                                return 1;
                            } else {
                                return 0;
                            }
                        } else if (lhs.mNumSeen > 0) {
                            if (lhs.mNumCrosses > 0) {
                                return -1;
                            } else {
                                return 0;
                            }
                        } else {
                            return 0;
                        }
                    }
                });
                if(starts.get(0).mNumCrosses > 0) {
                    textView.setText(Constants.DEFENSES_LABEL[starts.get(0).mDefenseIndex]);
                }
                else
                {
                    textView.setText("None");
                }

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

                textView = (TextView) view.findViewById(R.id.auto_high);

                float mAutoHighMade = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt();
                float mAutoHighMissed = statsMap.get(Constants.TOTAL_AUTO_HIGH_MISS).getInt();
                float mAutoHighTotal = (mAutoHighMade + mAutoHighMissed);
                float mAutoHighAccuracy = 0;
                if(mAutoHighTotal != 0) {
                    mAutoHighAccuracy = ((mAutoHighMade / mAutoHighTotal) * 100);
                }
                float mAutoHighAverage = mAutoHighMade / (float)numMatches;
                textView.setText(String.format("%.2f (%.2f%%)",mAutoHighAverage,mAutoHighAccuracy));

                textView = (TextView) view.findViewById(R.id.auto_low);

                float mAutoLowMade = statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt();
                float mAutoLowMissed = statsMap.get(Constants.TOTAL_AUTO_LOW_MISS).getInt();
                float mAutoLowTotal = (mAutoLowMade + mAutoLowMissed);
                float mAutoLowAccuracy = 0;
                if(mAutoLowTotal != 0) {
                    mAutoLowAccuracy = ((mAutoLowMade / mAutoLowTotal) * 100);
                }
                float mAutoLowAverage = mAutoLowMade / (float)numMatches;
                textView.setText(String.format("%.2f (%.2f%%)",mAutoLowAverage,mAutoLowAccuracy));


                textView = (TextView) view.findViewById(R.id.teleop_high);

                float mTeleopHighMade = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt();
                float mTeleopHighMissed = statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt();
                float mTeleopHighTotal = (mTeleopHighMade + mTeleopHighMissed);
                float mTeleopHighAccuracy = 0;
                if(mTeleopHighTotal != 0) {
                    mTeleopHighAccuracy = ((mTeleopHighMade / mTeleopHighTotal) * 100);
                }
                float mTeleopHighAverage = mTeleopHighMade / (float)numMatches;
                textView.setText(String.format("%.2f (%.2f%%)",mTeleopHighAverage,mTeleopHighAccuracy));

                textView = (TextView) view.findViewById(R.id.teleop_low);

                float mTeleopLowMade = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt();
                float mTeleopLowMissed = statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt();
                float mTeleopLowTotal = (mTeleopLowMade + mTeleopLowMissed);
                float mTeleopLowAccuracy = 0;
                if(mTeleopLowTotal != 0) {
                    mTeleopLowAccuracy = ((mTeleopLowMade / mTeleopLowTotal) * 100);
                }
                float mTeleopLowAverage = mTeleopLowMade / (float)numMatches;

                textView.setText(String.format("%.2f (%.2f%%)",mTeleopLowAverage,mTeleopLowAccuracy));


                textView = (TextView) view.findViewById(R.id.can_scale);
                int mCanScale = statsMap.get(Constants.TOTAL_SCALE).getInt();
                textView.setText(String.valueOf(mCanScale));

                textView = (TextView) view.findViewById(R.id.defenses);


                ArrayList<Defense> defenses = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    Defense defense = new Defense();
                    float seen = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[i]).getInt();
                    float notCross = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[i]).getInt();

                    if(seen == 0)
                    {
                        defense.mTime = -1;
                    }
                    else if(seen == notCross)
                    {
                        defense.mTime = 0;
                    }
                    else
                    {
                        defense.mTime = ((float)statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[i]).getInt()) / (seen - notCross);
                    }
                    defense.mDefenseIndex = i;
                    defenses.add(defense);
                }


                Collections.sort(defenses, new Comparator<Defense>() {
                    @Override
                    public int compare(Defense lhs, Defense rhs) {
                        if(rhs.mTime > 0 && lhs.mTime > 0) {
                            return Float.compare(lhs.mTime, rhs.mTime);
                        }
                        else if(rhs.mTime > 0)
                        {
                            return 1;
                        }
                        else if(lhs.mTime > 0)
                        {
                            return -1;
                        }
                        else if(lhs.mTime == 0 && rhs.mTime == 0)
                        {
                            return 0;
                        }
                        else if(rhs.mTime > -1)
                        {
                            return -1;
                        }
                        else if(lhs.mTime > -1)
                        {
                            return 1;
                        }
                        else
                        {
                            return 0;
                        }
                    }
                });

                if(defenses.get(0).mTime > 0)
                {
                    String DefenseString = "";
                    for(int i = 0; i < 9; i++)
                    {
                        Defense defense = defenses.get(i);
                        String defenseString = Constants.DEFENSES_LABEL[defense.mDefenseIndex];
                        if(defense.mTime > 0)
                        {
                            defenseString += String.format(" (~ %.1f s)",defense.mTime);
                        }
                        else if(defense.mTime == 0)
                        {
                            defenseString += " (NC)";
                        }
                        else
                        {
                            defenseString += " (NS)";
                        }

                        defenseString = String.format("%d. %s\n",i+1,defenseString);
                        DefenseString += defenseString;
                    }
                    DefenseString = DefenseString.substring(0,DefenseString.length()-1);
                    textView.setText(DefenseString);
                }
                else
                {
                    String bestDefenseString = "None";
                    for(int i = 0; i < 8; i++);
                        bestDefenseString += "\n";
                    textView.setText(bestDefenseString);
                }

                textView = (TextView) view.findViewById(R.id.driver_ability);
                if (statsMap.containsKey(Constants.DRIVER_ABILITY_RANKING)) {
                    String ranking = statsMap.get(Constants.DRIVER_ABILITY_RANKING).getString();
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
                textView = (TextView) view.findViewById(R.id.defenses);
                String spacing = "";
                for(int i = 0; i < 8; i++)
                    spacing += "\n";

                textView.setText(spacing);
            }
        }
        else
        {
            textView = (TextView) view.findViewById(R.id.total_matches);
            textView.setText("0");

            textView = (TextView) view.findViewById(R.id.defenses);
            String spacing = "";
            for(int i = 0; i < 8; i++)
                spacing += "\n";

            textView.setText(spacing);
        }
    }
}


