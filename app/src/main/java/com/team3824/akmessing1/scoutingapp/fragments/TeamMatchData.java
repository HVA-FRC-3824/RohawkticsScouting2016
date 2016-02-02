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

import com.team3824.akmessing1.scoutingapp.Constants;
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


        if(statsMap.containsKey("total_matches"))
        {



            TextView textView = (TextView)view.findViewById(R.id.total_matches);
            float numMatches = statsMap.get(Constants.TOTAL_MATCHES).getInt();
            textView.setText(String.valueOf(numMatches));

            if(numMatches > 0) {


                int mFoulPoints = statsMap.get(Constants.TOTAL_FOULS).getInt() * -5 +
                        statsMap.get(Constants.TOTAL_TECH_FOULS).getInt() * -5;

                int mEndgamePoints = statsMap.get(Constants.TOTAL_CHALLENGE).getInt() * 5 +
                        statsMap.get(Constants.TOTAL_SCALE).getInt() * 15;

                //NEED TO UPDATE START POSITION

                textView = (TextView) view.findViewById(R.id.position);
                int totalLandfill = statsMap.get("total_landfill").getInt();
                int totalHPFeeder = statsMap.get("total_hp_feeder").getInt();
                if (totalHPFeeder > numMatches / 2 && totalLandfill > numMatches / 2) {
                    textView.setText("Both");
                } else if (totalHPFeeder > numMatches / 2) {
                    textView.setText("HP Feeder");
                } else if (totalLandfill > numMatches / 2) {
                    textView.setText("Landfill");
                } else {
                    textView.setText("Neither");
                }

                textView = (TextView) view.findViewById(R.id.average_points);
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
                int mTotalPoints = mEndgamePoints + mTeleopPoints + mAutoPoints + mFoulPoints;
                float mAvgPoints = (numMatches == 0.0f) ? 0.0f : (float) mTotalPoints / numMatches;

                float averagePoints = mTotalPoints;
                averagePoints /= (float) numMatches;
                textView.setText(String.valueOf("average_points"));

                textView = (TextView) view.findViewById(R.id.auto_high_goal);
                float mAutoHighPointsMade = statsMap.get(Constants.TOTAL_AUTO_HIGH_HIT).getInt();
                float mAutoHighPointsMissed = statsMap.get(Constants.TOTAL_AUTO_HIGH_MISS).getInt();
                float mAutoHighPointsTotal = (mAutoHighPointsMade + mAutoHighPointsMissed);
                float mAutoHighAccuracy = mAutoHighPointsMade / mAutoHighPointsTotal;
                textView.setText(String.valueOf(mAutoHighPointsMade) + " (" + String.valueOf(mAutoHighPointsTotal) + ") : " + String.valueOf(mAutoHighAccuracy) + "%");


                textView = (TextView) view.findViewById(R.id.auto_low_goal);
                float mAutoLowPointsMade = statsMap.get(Constants.TOTAL_AUTO_LOW_HIT).getInt();
                float mAutoLowPointsMissed = statsMap.get(Constants.TOTAL_AUTO_LOW_MISS).getInt();
                float mAutoLowPointsTotal = (mAutoLowPointsMade + mAutoLowPointsMissed);
                float mAutoLowAccuracy = mAutoLowPointsMade / mAutoLowPointsTotal;
                textView.setText(String.valueOf(mAutoLowPointsMade) + " (" + String.valueOf(mAutoLowPointsTotal) + ") : " + String.valueOf(mAutoLowAccuracy) + "%");

                textView = (TextView) view.findViewById(R.id.teleop_high_goal);
                float mTeleopHighPointsMade = statsMap.get(Constants.TOTAL_TELEOP_HIGH_HIT).getInt();
                float mTeleopHighPointsMissed = statsMap.get(Constants.TOTAL_TELEOP_HIGH_MISS).getInt();
                float mTeleopHighPointsTotal = (mTeleopHighPointsMade + mTeleopHighPointsMissed);
                float mTeleopHighAccuracy = mTeleopHighPointsMade / mTeleopHighPointsTotal;
                textView.setText(String.valueOf(mTeleopHighPointsMade) + " (" + String.valueOf(mTeleopHighPointsTotal) + ") : " + String.valueOf(mTeleopHighAccuracy) + "%");

                textView = (TextView) view.findViewById(R.id.teleop_low_goal);
                float mTeleopLowPointsMade = statsMap.get(Constants.TOTAL_TELEOP_LOW_HIT).getInt();
                float mTeleopLowPointsMissed = statsMap.get(Constants.TOTAL_TELEOP_LOW_MISS).getInt();
                float mTeleopLowPointsTotal = (mTeleopLowPointsMade + mTeleopLowPointsMissed);
                float mTeleopLowAccuracy = mTeleopLowPointsMade / mTeleopLowPointsTotal;
                textView.setText(String.valueOf(mTeleopLowPointsMade) + " (" + String.valueOf(mTeleopLowPointsTotal) + ") : " + String.valueOf(mTeleopLowAccuracy) + "%");

                textView = (TextView) view.findViewById(R.id.total_low_bar);
                int mTotalDefensesStartedLowBar = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.LOW_BAR_INDEX]).getInt();
                int mAutoReachedLowBar = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.LOW_BAR_INDEX]).getInt();
                int mAutoCrossedLowBar = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.LOW_BAR_INDEX]).getInt();
                int mTeleopDefensesSeenLowBar = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.LOW_BAR_INDEX]).getInt();
                int mTeleopDefensesCrossedLowBar = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.LOW_BAR_INDEX]).getInt();
                int mDefenseTimeLowBar = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.LOW_BAR_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedLowBar) + "Auto Reach: " + String.valueOf(mAutoReachedLowBar)
                        + "Auto Cross: " + String.valueOf(mAutoCrossedLowBar) + "\n" + "Teleop Cross/Seen: " + String.valueOf(mTeleopDefensesCrossedLowBar) + " / " +
                        String.valueOf(mTeleopDefensesSeenLowBar) + "Time: " + String.valueOf(mDefenseTimeLowBar));

                textView = (TextView) view.findViewById(R.id.total_portcullis);
                int mTotalDefensesStartedPortcullis = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.PORTCULLIS_INDEX]).getInt();
                int mAutoReachedPortcullis = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.PORTCULLIS_INDEX]).getInt();
                int mAutoCrossedPortcullis = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.PORTCULLIS_INDEX]).getInt();
                int mTeleopDefensesSeenPortcullis = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.PORTCULLIS_INDEX]).getInt();
                int mTeleopDefensesCrossedPortcullis = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.PORTCULLIS_INDEX]).getInt();
                int mDefenseTimePortcullis = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.PORTCULLIS_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedPortcullis) + "Auto Reach: " +
                        String.valueOf(mAutoReachedPortcullis) + "Auto Cross: " + String.valueOf(mAutoCrossedPortcullis) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedPortcullis) + " / " + String.valueOf(mTeleopDefensesSeenPortcullis) + "Time: " +
                        String.valueOf(mDefenseTimePortcullis));

                textView = (TextView) view.findViewById(R.id.total_cheval_de_frise);
                int mTotalDefensesStartedChevalDeFrise = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt();
                int mAutoReachedChevalDeFrise = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt();
                int mAutoCrossedChevalDeFrise = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt();
                int mTeleopDefensesSeenChevalDeFrise = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.CHEVAL_DE_FRISE_INDEX]).getInt();
                int mTeleopDefensesCrossedChevalDeFrise = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.CHEVAL_DE_FRISE_INDEX]).getInt();
                int mDefenseTimeChevalDeFrise = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.CHEVAL_DE_FRISE_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedChevalDeFrise) + "Auto Reach: " +
                        String.valueOf(mAutoReachedChevalDeFrise) + "Auto Cross: " + String.valueOf(mAutoCrossedChevalDeFrise) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedChevalDeFrise) + " / " + String.valueOf(mTeleopDefensesSeenChevalDeFrise) + "Time: " +
                        String.valueOf(mDefenseTimeChevalDeFrise));

                textView = (TextView) view.findViewById(R.id.total_moat);
                int mTotalDefensesStartedMoat = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.MOAT_INDEX]).getInt();
                int mAutoReachedMoat = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.MOAT_INDEX]).getInt();
                int mAutoCrossedMoat = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.MOAT_INDEX]).getInt();
                int mTeleopDefensesSeenMoat = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.MOAT_INDEX]).getInt();
                int mTeleopDefensesCrossedMoat = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.MOAT_INDEX]).getInt();
                int Moat = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.MOAT_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedMoat) + "Auto Reach: " +
                        String.valueOf(mAutoReachedMoat) + "Auto Cross: " + String.valueOf(mAutoCrossedMoat) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedMoat) + " / " + String.valueOf(mTeleopDefensesSeenMoat) + "Time: " +
                        String.valueOf(mDefenseTimeMoat));

                textView = (TextView) view.findViewById(R.id.total_ramparts);
                int mTotalDefensesStartedRamparts = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.RAMPARTS_INDEX]).getInt();
                int mAutoReachedRamparts = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.RAMPARTS_INDEX]).getInt();
                int mAutoCrossedRamparts = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.RAMPARTS_INDEX]).getInt();
                int mTeleopDefensesSeenRamparts = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.RAMPARTS_INDEX]).getInt();
                int mTeleopDefensesCrossedRamparts = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.RAMPARTS_INDEX]).getInt();
                int mDefenseTimeRamparts = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.RAMPARTS_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedRamparts) + "Auto Reach: " +
                        String.valueOf(mAutoReachedRamparts) + "Auto Cross: " + String.valueOf(mAutoCrossedRamparts) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedRamparts) + " / " + String.valueOf(mTeleopDefensesSeenRamparts) + "Time: " +
                        String.valueOf(mDefenseTimeRamparts));

                textView = (TextView) view.findViewById(R.id.total_drawbridge);
                int mTotalDefensesStartedDrawbridge = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.DRAWBRIDGE_INDEX]).getInt();
                int mAutoReachedDrawbridge = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.DRAWBRIDGE_INDEX]).getInt();
                int mAutoCrossedDrawbridge = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.DRAWBRIDGE_INDEX]).getInt();
                int mTeleopDefensesSeenDrawbridge = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.DRAWBRIDGE_INDEX]).getInt();
                int mTeleopDefensesCrossedDrawbridge = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.DRAWBRIDGE_INDEX]).getInt();
                int mDefenseTimeDrawbridge = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.DRAWBRIDGE_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedDrawbridge) + "Auto Reach: " +
                        String.valueOf(mAutoReachedDrawbridge) + "Auto Cross: " + String.valueOf(mAutoCrossedDrawbridge) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedDrawbridge) + " / " + String.valueOf(mTeleopDefensesSeenDrawbridge) + "Time: " +
                        String.valueOf(mDefenseTimeDrawbridge));

                textView = (TextView) view.findViewById(R.id.total_sally_port);
                int mTotalDefensesStartedSallyPort = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.SALLY_PORT_INDEX]).getInt();
                int mAutoReachedSallyPort = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.SALLY_PORT_INDEX]).getInt();
                int mAutoCrossedSallyPort = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.SALLY_PORT_INDEX]).getInt();
                int mTeleopDefensesSeenSallyPort = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.SALLY_PORT_INDEX]).getInt();
                int mTeleopDefensesCrossedSallyPort = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.SALLY_PORT_INDEX]).getInt();
                int mDefenseTimeSallyPort = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.SALLY_PORT_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedSallyPort) + "Auto Reach: " +
                        String.valueOf(mAutoReachedSallyPort) + "Auto Cross: " + String.valueOf(mAutoCrossedSallyPort) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedSallyPort) + " / " + String.valueOf(mTeleopDefensesSeenSallyPort) + "Time: " +
                        String.valueOf(mDefenseTimeSallyPort));

                textView = (TextView) view.findViewById(R.id.total_rock_wall);
                int mTotalDefensesStartedRockWall = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.ROCK_WALL_INDEX]).getInt();
                int mAutoReachedRockWall = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.ROCK_WALL_INDEX]).getInt();
                int mAutoCrossedRockWall = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROCK_WALL_INDEX]).getInt();
                int mTeleopDefensesSeenRockWall = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.ROCK_WALL_INDEX]).getInt();
                int mTeleopDefensesCrossedRockWall = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROCK_WALL_INDEX]).getInt();
                int mDefenseTimeRockWall = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.ROCK_WALL_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedRockWall) + "Auto Reach: " +
                        String.valueOf(mAutoReachedRockWall) + "Auto Cross: " + String.valueOf(mAutoCrossedRockWall) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedRockWall) + " / " + String.valueOf(mTeleopDefensesSeenRockWall) + "Time: " +
                        String.valueOf(mDefenseTimeRockWall));

                textView = (TextView) view.findViewById(R.id.total_rough_terrain);
                int mTotalDefensesStartedRoughTerrain = statsMap.get(Constants.TOTAL_DEFENSES_STARTED[Constants.ROUGH_TERRAIN_INDEX]).getInt();
                int mAutoReachedRoughTerrain = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[Constants.ROUGH_TERRAIN_INDEX]).getInt();
                int mAutoCrossedRoughTerrain = statsMap.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[Constants.ROUGH_TERRAIN_INDEX]).getInt();
                int mTeleopDefensesSeenRoughTerrain = statsMap.get(Constants.TOTAL_DEFENSES_SEEN[Constants.ROUGH_TERRAIN_INDEX]).getInt();
                int mTeleopDefensesCrossedRoughTerrain = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[Constants.ROUGH_TERRAIN_INDEX]).getInt();
                int mDefenseTimeRoughTerrain = statsMap.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[Constants.ROUGH_TERRAIN_INDEX]).getInt();
                textView.setText("LowBar\n" + "Started: " + String.valueOf(mTotalDefensesStartedRoughTerrain) + "Auto Reach: " +
                        String.valueOf(mAutoReachedRoughTerrain) + "Auto Cross: " + String.valueOf(mAutoCrossedRoughTerrain) + "\n" + "Teleop Cross/Seen: " +
                        String.valueOf(mTeleopDefensesCrossedRoughTerrain) + " / " + String.valueOf(mTeleopDefensesSeenRoughTerrain) + "Time: " +
                        String.valueOf(mDefenseTimeRoughTerrain));

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

                textView = (TextView) view.findViewById(R.id.total_fouls);
                int mTotalFouls = statsMap.get(Constants.TOTAL_FOULS).getInt();
                int mTotalTechFouls = statsMap.get(Constants.TOTAL_TECH_FOULS).getInt();
                int mTotalYellowCardFouls = statsMap.get(Constants.TOTAL_YELLOW_CARDS).getInt();
                int mTotalRedCardFouls = statsMap.get(Constants.TOTAL_RED_CARDS).getInt();
                textView.setText("Total Fouls: " + String.valueOf(mTotalFouls) + "Total Techfouls: " + String.valueOf(mTotalTechFouls) +
                        "Total Yellowcards: " + String.valueOf(mTotalYellowCardFouls) + "Total Redcrads: " + String.valueOf(mTotalRedCardFouls));

                textView = (TextView) view.findViewById(R.id.total_dq);
                int totalDqs = statsMap.get(Constants.TOTAL_DQ).getInt();
                textView.setText("Total DQs: " + String.valueOf(totalDqs));

                textView = (TextView) view.findViewById(R.id.total_stopped_moving);
                int totalStoppedMoving = statsMap.get(Constants.TOTAL_STOPPED).getInt();
                textView.setText("Stopped Moving: " + String.valueOf(totalStoppedMoving));

                textView = (TextView) view.findViewById(R.id.total_didnt_show_up);
                int totalDidntShowUp = statsMap.get(Constants.TOTAL_DIDNT_SHOW_UP).getInt();
                textView.setText("Didnt Show:" + String.valueOf(totalDidntShowUp));
            }else{
                textView.setText("They had no Matches");
            }


        }
        statsDB.close();
        return view;
    }
}
