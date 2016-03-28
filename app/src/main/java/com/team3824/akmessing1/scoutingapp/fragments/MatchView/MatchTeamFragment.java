package com.team3824.akmessing1.scoutingapp.fragments.MatchView;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.activities.TeamView;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Fragment for a individual team in the match view.
 *
 * @author Andrew Messing
 * @version %I%
 */
public class MatchTeamFragment extends Fragment {

    private View view;
    private final String TAG = "MatchTeamFragment";

    /**
     *
     */
    public MatchTeamFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match_team, container, false);

        return view;
    }

    /**
     * @param teamNumber
     * @param context
     */
    public void setTeamNumber(final int teamNumber, Context context) {
        TextView textView = (TextView) view.findViewById(R.id.team_number);
        textView.setText(String.valueOf(teamNumber));

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        Button button = (Button) view.findViewById(R.id.view_team);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TeamView.class);
                intent.putExtra(Constants.Intent_Extras.TEAM_NUMBER, teamNumber);
                startActivity(intent);
            }
        });

        StatsDB statsDB = new StatsDB(context, eventId);
        ScoutMap statsMap = statsDB.getTeamStats(teamNumber);
        if (statsMap.containsKey(Constants.Calculated_Totals.TOTAL_MATCHES)) {
            textView = (TextView) view.findViewById(R.id.total_matches);
            int numMatches = statsMap.get(Constants.Calculated_Totals.TOTAL_MATCHES).getInt();
            textView.setText(String.valueOf(numMatches));

            if (numMatches > 0) {

                textView = (TextView) view.findViewById(R.id.best_start_position);

                ArrayList<StartPosition> starts = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    StartPosition start = new StartPosition();
                    start.mNumCrosses = statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i]);
                    start.mNumSeen = statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[i]);
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
                if (starts.get(0).mNumCrosses > 0) {
                    textView.setText(Constants.Defense_Arrays.DEFENSES_LABEL[starts.get(0).mDefenseIndex]);
                } else {
                    textView.setText("None");
                }

                textView = (TextView) view.findViewById(R.id.average_points);
                int mFoulPoints = statsMap.getInt(Constants.Calculated_Totals.TOTAL_FOULS) * -5 +
                        statsMap.getInt(Constants.Calculated_Totals.TOTAL_TECH_FOULS) * -5;

                int mEndgamePoints = statsMap.getInt(Constants.Calculated_Totals.TOTAL_CHALLENGE) * 5 +
                        statsMap.getInt(Constants.Calculated_Totals.TOTAL_SCALE) * 15;

                int mTeleopPoints = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_HIT) * 5 +
                        statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_HIT) * 2;

                for (int i = 0; i < 9; i++) {
                    mTeleopPoints += statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i]) * 5;
                }

                int mAutoPoints = statsMap.getInt(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT) * 10 +
                        statsMap.getInt(Constants.Calculated_Totals.TOTAL_AUTO_LOW_HIT) * 5;
                for (int i = 0; i < 9; i++) {
                    mAutoPoints += statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[i]) * 2;
                    mAutoPoints += statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i]) * 10;
                }
                float mTotalPoints = mEndgamePoints + mTeleopPoints + mAutoPoints + mFoulPoints;


                float averagePoints = mTotalPoints / (float) numMatches;

                textView.setText(String.valueOf(averagePoints));

                textView = (TextView) view.findViewById(R.id.auto_high);

                float mAutoHighMade = statsMap.getInt(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT);
                float mAutoHighMissed = statsMap.getInt(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_MISS);
                float mAutoHighTotal = (mAutoHighMade + mAutoHighMissed);
                float mAutoHighAccuracy = 0;
                if (mAutoHighTotal != 0) {
                    mAutoHighAccuracy = ((mAutoHighMade / mAutoHighTotal) * 100);
                }
                float mAutoHighAverage = mAutoHighMade / (float) numMatches;
                textView.setText(String.format("%.2f (%.2f%%)", mAutoHighAverage, mAutoHighAccuracy));

                textView = (TextView) view.findViewById(R.id.auto_low);

                float mAutoLowMade = statsMap.getInt(Constants.Calculated_Totals.TOTAL_AUTO_LOW_HIT);
                float mAutoLowMissed = statsMap.getInt(Constants.Calculated_Totals.TOTAL_AUTO_LOW_MISS);
                float mAutoLowTotal = (mAutoLowMade + mAutoLowMissed);
                float mAutoLowAccuracy = 0;
                if (mAutoLowTotal != 0) {
                    mAutoLowAccuracy = ((mAutoLowMade / mAutoLowTotal) * 100);
                }
                float mAutoLowAverage = mAutoLowMade / (float) numMatches;
                textView.setText(String.format("%.2f (%.2f%%)", mAutoLowAverage, mAutoLowAccuracy));


                textView = (TextView) view.findViewById(R.id.teleop_high);

                float mTeleopHighMade = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_HIT);
                float mTeleopHighMissed = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_MISS);
                float mTeleopHighTotal = (mTeleopHighMade + mTeleopHighMissed);
                float mTeleopHighAccuracy = 0;
                if (mTeleopHighTotal != 0) {
                    mTeleopHighAccuracy = ((mTeleopHighMade / mTeleopHighTotal) * 100);
                }
                float mTeleopHighAverage = mTeleopHighMade / (float) numMatches;
                textView.setText(String.format("%.2f (%.2f%%)", mTeleopHighAverage, mTeleopHighAccuracy));

                textView = (TextView) view.findViewById(R.id.teleop_low);

                float mTeleopLowMade = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_HIT);
                float mTeleopLowMissed = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_MISS);
                float mTeleopLowTotal = (mTeleopLowMade + mTeleopLowMissed);
                float mTeleopLowAccuracy = 0;
                if (mTeleopLowTotal != 0) {
                    mTeleopLowAccuracy = ((mTeleopLowMade / mTeleopLowTotal) * 100);
                }
                float mTeleopLowAverage = mTeleopLowMade / (float) numMatches;

                textView.setText(String.format("%.2f (%.2f%%)", mTeleopLowAverage, mTeleopLowAccuracy));

                textView = (TextView)view.findViewById(R.id.best_shooting_location_high);

                ArrayList<ShootingLocation> shootingLocations = new ArrayList<>();
                for(int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length; i++)
                {
                    ShootingLocation shootingLocation = new ShootingLocation();
                    shootingLocation.mLocationIndex = i;
                    shootingLocation.mMade = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_HIT[i]);
                    shootingLocation.mTotal = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS[i]);
                    shootingLocations.add(shootingLocation);
                }

                Collections.sort(shootingLocations, new Comparator<ShootingLocation>() {
                    @Override
                    public int compare(ShootingLocation lhs, ShootingLocation rhs) {
                        if (lhs.mMade < 5 && rhs.mMade < 5) {
                            return rhs.mMade - lhs.mMade;
                        } else if (lhs.mMade < 5) {
                            return -1;
                        } else if (rhs.mMade < 5) {
                            return 1;
                        } else {
                            float l_percent = 0.0f;
                            if (lhs.mTotal != 0) {
                                l_percent = (float) lhs.mMade / (float) lhs.mTotal;
                            }
                            float r_percent = 0.0f;
                            if (rhs.mTotal != 0) {
                                r_percent = (float) rhs.mMade / (float) rhs.mTotal;
                            }
                            return Float.compare(r_percent, l_percent);
                        }
                    }
                });

                if(shootingLocations.get(0).mMade > 0)
                {
                    textView.setText(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_LABEL[shootingLocations.get(0).mLocationIndex]);
                }
                else
                {
                    textView.setText("None");
                }

                textView = (TextView)view.findViewById(R.id.best_shooting_location_low);

                shootingLocations = new ArrayList<>();
                for(int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length; i++)
                {
                    ShootingLocation shootingLocation = new ShootingLocation();
                    shootingLocation.mLocationIndex = i;
                    shootingLocation.mMade = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_HIT[i]);
                    shootingLocation.mTotal = statsMap.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS[i]);
                    shootingLocations.add(shootingLocation);
                }

                Collections.sort(shootingLocations, new Comparator<ShootingLocation>() {
                    @Override
                    public int compare(ShootingLocation lhs, ShootingLocation rhs) {
                        if(lhs.mMade < 5 && rhs.mMade < 5 )
                        {
                            return rhs.mMade - lhs.mMade;
                        }
                        else if(lhs.mMade < 5)
                        {
                            return -1;
                        }
                        else if(rhs.mMade < 5)
                        {
                            return 1;
                        }
                        else
                        {
                            float l_percent = 0.0f;
                            if(lhs.mTotal != 0)
                            {
                                l_percent = (float)lhs.mMade / (float)lhs.mTotal;
                            }
                            float r_percent = 0.0f;
                            if(rhs.mTotal != 0)
                            {
                                r_percent = (float)rhs.mMade / (float)rhs.mTotal;
                            }
                            return Float.compare(r_percent,l_percent);
                        }
                    }
                });

                if(shootingLocations.get(0).mMade > 0)
                {
                    textView.setText(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_LABEL[shootingLocations.get(0).mLocationIndex]);
                }
                else
                {
                    textView.setText("None");
                }

                textView = (TextView) view.findViewById(R.id.can_scale);
                int mCanScale = statsMap.getInt(Constants.Calculated_Totals.TOTAL_SCALE);
                textView.setText(String.valueOf(mCanScale));

                textView = (TextView) view.findViewById(R.id.defenses);


                ArrayList<Defense> defenses = new ArrayList<>();

                for (int i = 0; i < 9; i++) {
                    Defense defense = new Defense();
                    float seen = statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[i]);
                    float notCross = statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[i]);

                    if (seen == 0) {
                        defense.mTime = -1;
                    } else if (seen == notCross) {
                        defense.mTime = 0;
                    } else {
                        defense.mTime = ((float) statsMap.getInt(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_TIME[i])) / (seen - notCross);
                    }
                    defense.mDefenseIndex = i;
                    defenses.add(defense);
                }


                Collections.sort(defenses, new Comparator<Defense>() {
                    @Override
                    public int compare(Defense lhs, Defense rhs) {
                        if (rhs.mTime > 0 && lhs.mTime > 0) {
                            return Float.compare(lhs.mTime, rhs.mTime);
                        } else if (rhs.mTime > 0) {
                            return 1;
                        } else if (lhs.mTime > 0) {
                            return -1;
                        } else if (lhs.mTime == 0 && rhs.mTime == 0) {
                            return 0;
                        } else if (rhs.mTime > -1) {
                            return -1;
                        } else if (lhs.mTime > -1) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                });

                if (defenses.get(0).mTime > 0) {
                    String DefenseString = "";
                    for (int i = 0; i < 9; i++) {
                        Defense defense = defenses.get(i);
                        String defenseString = Constants.Defense_Arrays.DEFENSES_LABEL[defense.mDefenseIndex];
                        if (defense.mTime > 0) {
                            defenseString += String.format(" (~ %.1f s)", defense.mTime);
                        } else if (defense.mTime == 0) {
                            defenseString += " (NC)";
                        } else {
                            defenseString += " (NS)";
                        }

                        defenseString = String.format("%d. %s\n", i + 1, defenseString);
                        DefenseString += defenseString;
                    }
                    DefenseString = DefenseString.substring(0, DefenseString.length() - 1);
                    textView.setText(DefenseString);
                } else {
                    String bestDefenseString = "None";
                    for (int i = 0; i < 8; i++) {
                        bestDefenseString += "\n";
                    }
                    textView.setText(bestDefenseString);
                }

            } else {
                textView = (TextView) view.findViewById(R.id.defenses);
                String spacing = "";
                for (int i = 0; i < 8; i++)
                    spacing += "\n";

                textView.setText(spacing);
            }
        } else {
            textView = (TextView) view.findViewById(R.id.total_matches);
            textView.setText("0");

            textView = (TextView) view.findViewById(R.id.defenses);
            String spacing = "";
            for (int i = 0; i < 8; i++)
                spacing += "\n";

            textView.setText(spacing);
        }
    }

    /**
     *
     */
    private class StartPosition {

        int mDefenseIndex;
        int mNumCrosses;
        int mNumSeen;
    }

    private class ShootingLocation{
        int mLocationIndex;
        int mMade;
        int mTotal;
    }

    /**
     *
     */
    private class Defense {

        int mDefenseIndex;
        float mTime;
    }
}


