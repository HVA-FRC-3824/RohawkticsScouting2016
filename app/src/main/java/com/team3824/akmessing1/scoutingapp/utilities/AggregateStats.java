package com.team3824.akmessing1.scoutingapp.utilities;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class for aggregating the match and super data to be used for statistics.
 *
 * @author Andrew Messing
 * @version 1
 */
public class AggregateStats {

    private static final String TAG = "AggregateStats";

    /**
     * Updates the aggregated statistics for multiple teams
     *
     * @param teams        Set of all the teams to update
     * @param matchScoutDB The helper for the match scout database table
     * @param superScoutDB The helper for the super scout database table
     * @param statsDB      The helper for the stats database table
     */
    public static void updateTeams(Set<Integer> teams, MatchScoutDB matchScoutDB, SuperScoutDB superScoutDB,
                                   StatsDB statsDB) {
        for (Integer team : teams) {
            updateTeam(team, matchScoutDB, superScoutDB, statsDB);
        }
    }

    /**
     * Updates the aggregated statistics for an individual team
     *
     * @param team         The team to update the statistics for
     * @param matchScoutDB The helper for the match scout database table
     * @param superScoutDB The helper for the super scout database table
     * @param statsDB      The helper for the stats database table
     */
    private static void updateTeam(int team, MatchScoutDB matchScoutDB, SuperScoutDB superScoutDB,
                                   StatsDB statsDB) {
        Cursor teamInfo = matchScoutDB.getTeamInfo(team);

        //Might change for update feature
        ScoutMap teamStats = new ScoutMap();

        // Calculate metrics and insert in map

        //Auto
        int[] totalStartPosition = new int[Constants.Defense_Arrays.DEFENSES.length + 2];
        int[] totalDefenseReaches = new int[Constants.Defense_Arrays.DEFENSES.length];
        int[] totalDefenseCrosses = new int[Constants.Defense_Arrays.DEFENSES.length];
        int totalAutoHighGoalHit = 0;
        int totalAutoHighGoalMiss = 0;
        int totalAutoLowGoalHit = 0;
        int totalAutoLowGoalMiss = 0;


        //Teleop
        int[] totalTeleopDefenses = new int[Constants.Defense_Arrays.DEFENSES.length];
        int[] totalTeleopNotCross = new int[Constants.Defense_Arrays.DEFENSES.length];
        int[] totalTeleopDefensesPoints = new int[Constants.Defense_Arrays.DEFENSES.length];
        int[] totalDefensesSeen = new int[Constants.Defense_Arrays.DEFENSES.length];
        int[] totalDefensesTime = new int[Constants.Defense_Arrays.DEFENSES.length];

        int[] totalTeleopHighShotPosition = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1];
        int[] totalTeleopHighShotPositionHit = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1];
        int[] totalTeleopHighShotPositionMiss = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1];
        int totalTeleopHighAimTime = 0;
        int totalTeleopHighGoalHit = 0;
        int totalTeleopHighGoalMiss = 0;

        int[] totalTeleopLowShotPosition = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4];
        int[] totalTeleopLowShotPositionHit = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4];
        int[] totalTeleopLowShotPositionMiss = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4];
        int totalTeleopLowAimTime = 0;
        int totalTeleopLowGoalHit = 0;
        int totalTeleopLowGoalMiss = 0;


        //Endgame
        int totalFailedChallenge = 0;
        int totalChallenge = 0;
        int totalFailedScale = 0;
        int totalScale = 0;

        //Post
        int totalDQ = 0;
        int totalStopped = 0;
        int totalDidntShowUp = 0;

        //Fouls
        int totalFouls = 0;
        int totalTechFouls = 0;
        int totalYellowCards = 0;
        int totalRedCards = 0;


        int totalMatches = 0;



        for (teamInfo.moveToFirst(); !teamInfo.isAfterLast(); teamInfo.moveToNext()) {
            int matchNumber = teamInfo.getInt(teamInfo.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER));
            ScoutMap superMatch = superScoutDB.getMatchInfo(matchNumber);

            //Log.d(TAG, String.valueOf(matchNumber));

            /*
                If superscout has not yet recorded the match change the update time so that this
                match can be aggregated later
            */
            if (superMatch != null) {

                if (superMatch.get(Constants.Super_Inputs.DEFENSE_3) != null) {

                    //Auto
                    int[] tempStartPosition = new int[Constants.Defense_Arrays.DEFENSES.length + 2];
                    int[] tempDefenseReaches = new int[Constants.Defense_Arrays.DEFENSES.length];
                    int[] tempDefenseCrosses = new int[Constants.Defense_Arrays.DEFENSES.length];
                    int tempAutoHighGoalHit = 0;
                    int tempAutoHighGoalMiss = 0;
                    int tempAutoLowGoalHit = 0;
                    int tempAutoLowGoalMiss = 0;


                    //Teleop
                    int[] tempTeleopDefenses = new int[Constants.Defense_Arrays.DEFENSES.length];
                    int[] tempTeleopNotCross = new int[Constants.Defense_Arrays.DEFENSES.length];
                    int[] tempTeleopDefensesPoints = new int[Constants.Defense_Arrays.DEFENSES.length];
                    int[] tempDefensesSeen = new int[Constants.Defense_Arrays.DEFENSES.length];
                    int[] tempDefensesTime = new int[Constants.Defense_Arrays.DEFENSES.length];

                    int[] tempTeleopHighShotPosition = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1];
                    int[] tempTeleopHighShotPositionHit = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1];
                    int[] tempTeleopHighShotPositionMiss = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1];
                    int tempTeleopHighAimTime = 0;
                    int tempTeleopHighGoalHit = 0;
                    int tempTeleopHighGoalMiss = 0;

                    int[] tempTeleopLowShotPosition = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4];
                    int[] tempTeleopLowShotPositionHit = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4];
                    int[] tempTeleopLowShotPositionMiss = new int[Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4];
                    int tempTeleopLowAimTime = 0;
                    int tempTeleopLowGoalHit = 0;
                    int tempTeleopLowGoalMiss = 0;


                    //Endgame
                    int tempFailedChallenge = 0;
                    int tempChallenge = 0;
                    int tempFailedScale = 0;
                    int tempScale = 0;

                    //Post
                    int tempDQ = 0;
                    int tempStopped = 0;
                    int tempDidntShowUp = 0;

                    //Fouls
                    int tempFouls = 0;
                    int tempTechFouls = 0;
                    int tempYellowCards = 0;
                    int tempRedCards = 0;

                    // try to get the data
                    try {
                        totalMatches++;

                        String defense2, defense3, defense4, defense5;
                        defense3 = superMatch.getString(Constants.Super_Inputs.DEFENSE_3).toLowerCase().replace(" ", "_");
                        if (superMatch.getInt(SuperScoutDB.KEY_BLUE1) == team ||
                                superMatch.getInt(SuperScoutDB.KEY_BLUE2) == team ||
                                superMatch.getInt(SuperScoutDB.KEY_BLUE3) == team) {
                            defense2 = superMatch.getString(Constants.Super_Inputs.BLUE_DEFENSE_2).toLowerCase().replace(" ", "_");
                            defense4 = superMatch.getString(Constants.Super_Inputs.BLUE_DEFENSE_4).toLowerCase().replace(" ", "_");
                            defense5 = superMatch.getString(Constants.Super_Inputs.BLUE_DEFENSE_5).toLowerCase().replace(" ", "_");
                        } else {
                            defense2 = superMatch.getString(Constants.Super_Inputs.RED_DEFENSE_2).toLowerCase().replace(" ", "_");
                            defense4 = superMatch.getString(Constants.Super_Inputs.RED_DEFENSE_4).toLowerCase().replace(" ", "_");
                            defense5 = superMatch.getString(Constants.Super_Inputs.RED_DEFENSE_5).toLowerCase().replace(" ", "_");
                        }

                        increment_array(teamInfo, tempDefensesSeen, defense2, defense3, defense4, defense5, "seen");

                        //Auto
                        increment_array(teamInfo, tempStartPosition, defense2, defense3, defense4, defense5, "start");
                        increment_array(teamInfo, tempDefenseReaches, defense2, defense3, defense4, defense5, "reach");
                        increment_array(teamInfo, tempDefenseCrosses, defense2, defense3, defense4, defense5, "cross");
                        tempAutoHighGoalHit = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Auto_Inputs.AUTO_HIGH_HIT));
                        tempAutoHighGoalMiss = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Auto_Inputs.AUTO_HIGH_MISS));
                        tempAutoLowGoalHit = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Auto_Inputs.AUTO_LOW_HIT));
                        tempAutoLowGoalMiss = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Auto_Inputs.AUTO_LOW_MISS));

                        //Teleop
                        increment_array(teamInfo, tempTeleopDefenses, defense2, defense3, defense4, defense5, "teleop");
                        increment_array(teamInfo, tempTeleopNotCross, defense2, defense3, defense4, defense5, "not");
                        increment_array(teamInfo, tempTeleopDefensesPoints, defense2, defense3, defense4, defense5, "teleop_points");
                        increment_array(teamInfo, tempDefensesTime, defense2, defense3, defense4, defense5, "time");

                        ArrayList<String> positionList = new ArrayList<>(Arrays.asList(Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS));
                        positionList.remove(Constants.Teleop_Inputs.SELECT_SHOT_POSITION);

                        ArrayList<String> timeList = new ArrayList<>(Arrays.asList(Constants.Teleop_Inputs.TELEOP_AIM_TIMES));
                        timeList.remove(Constants.Teleop_Inputs.SELECT_AIM_TIME);

                        String high_goal_string = teamInfo.getString(teamInfo.getColumnIndex(Constants.Teleop_Inputs.TELEOP_HIGH_SHOT));
                        try {
                            JSONArray jsonArray = new JSONArray(high_goal_string);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String position = jsonObject.getString(Constants.Teleop_Inputs.SHOT_POSITION);
                                tempTeleopHighShotPosition[positionList.indexOf(position)]++;
                                String time = jsonObject.getString(Constants.Teleop_Inputs.AIM_TIME);
                                tempTeleopHighAimTime += Constants.Teleop_Inputs.TELEOP_AIM_TIMES_VALUE[timeList.indexOf(time)];
                                if (jsonObject.getBoolean(Constants.Teleop_Inputs.SHOT_HIT_MISS)) {
                                    tempTeleopHighGoalHit++;
                                    tempTeleopHighShotPositionHit[positionList.indexOf(position)]++;
                                } else {
                                    tempTeleopHighGoalMiss++;
                                    tempTeleopHighShotPositionMiss[positionList.indexOf(position)]++;
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error: ", e);
                        }


                        positionList.remove(Constants.Teleop_Inputs.OUTER_WORKS);
                        positionList.remove(Constants.Teleop_Inputs.ALIGNMENT_LINE);
                        positionList.remove(Constants.Teleop_Inputs.ON_NEAR_CENTER_BATTER);

                        String low_goal_string = teamInfo.getString(teamInfo.getColumnIndex(Constants.Teleop_Inputs.TELEOP_LOW_SHOT));
                        try {
                            JSONArray jsonArray = new JSONArray(low_goal_string);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String position = jsonObject.getString(Constants.Teleop_Inputs.SHOT_POSITION);
                                tempTeleopLowShotPosition[positionList.indexOf(position)]++;
                                String time = jsonObject.getString(Constants.Teleop_Inputs.AIM_TIME);
                                tempTeleopLowAimTime += Constants.Teleop_Inputs.TELEOP_AIM_TIMES_VALUE[timeList.indexOf(time)];
                                if (jsonObject.getBoolean(Constants.Teleop_Inputs.SHOT_HIT_MISS)) {
                                    tempTeleopLowGoalHit++;
                                    tempTeleopLowShotPositionHit[positionList.indexOf(position)]++;
                                } else {
                                    tempTeleopLowGoalMiss++;
                                    tempTeleopLowShotPositionMiss[positionList.indexOf(position)]++;
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error: ", e);
                        }

                        //Endgame
                        tempFailedChallenge = (teamInfo.getString(teamInfo.getColumnIndex(Constants.Endgame_Inputs.ENDGAME_CHALLENGE_SCALE)).equals(Constants.Endgame_Inputs.ENDGAME_OPTIONS[Constants.Endgame_Inputs.ENDGAME_FAILED_CHALLENGE])) ? 1 : 0;
                        tempChallenge = (teamInfo.getString(teamInfo.getColumnIndex(Constants.Endgame_Inputs.ENDGAME_CHALLENGE_SCALE)).equals(Constants.Endgame_Inputs.ENDGAME_OPTIONS[Constants.Endgame_Inputs.ENDGAME_CHALLENGE])) ? 1 : 0;
                        tempFailedScale = (teamInfo.getString(teamInfo.getColumnIndex(Constants.Endgame_Inputs.ENDGAME_CHALLENGE_SCALE)).equals(Constants.Endgame_Inputs.ENDGAME_OPTIONS[Constants.Endgame_Inputs.ENDGAME_FAILED_SCALE])) ? 1 : 0;
                        tempScale = (teamInfo.getString(teamInfo.getColumnIndex(Constants.Endgame_Inputs.ENDGAME_CHALLENGE_SCALE)).equals(Constants.Endgame_Inputs.ENDGAME_OPTIONS[Constants.Endgame_Inputs.ENDGAME_SCALE])) ? 1 : 0;

                        //Post Match
                        tempDQ = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Post_Match_Inputs.POST_DQ));
                        tempStopped = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Post_Match_Inputs.POST_STOPPED));
                        tempDidntShowUp = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Post_Match_Inputs.POST_DIDNT_SHOW_UP));

                        //Fouls
                        tempFouls = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Foul_Inputs.FOUL_STANDARD));
                        tempTechFouls = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Foul_Inputs.FOUL_TECH));
                        tempYellowCards = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Foul_Inputs.FOUL_YELLOW_CARD));
                        tempRedCards = teamInfo.getInt(teamInfo.getColumnIndex(Constants.Foul_Inputs.FOUL_RED_CARD));

                        // if all the data can be pulled down cleanly then add it to the totals

                        for(int i = 0; i < Constants.Defense_Arrays.DEFENSES.length; i++)
                        {
                            totalStartPosition[i] += tempStartPosition[i];
                            totalDefenseReaches[i] += tempDefenseReaches[i];
                            totalDefenseCrosses[i] += tempDefenseCrosses[i];

                            totalTeleopDefenses[i] += tempTeleopDefenses[i];
                            totalTeleopNotCross[i] += tempTeleopNotCross[i];
                            totalTeleopDefensesPoints[i] += tempTeleopDefensesPoints[i];
                            totalDefensesSeen[i] += tempDefensesSeen[i];
                            totalDefensesTime[i] += tempDefensesTime[i];
                        }
                        totalStartPosition[Constants.Defense_Arrays.DEFENSES.length] += tempStartPosition[Constants.Defense_Arrays.DEFENSES.length];
                        totalStartPosition[Constants.Defense_Arrays.DEFENSES.length + 1] += tempStartPosition[Constants.Defense_Arrays.DEFENSES.length + 1];


                        totalAutoHighGoalHit += tempAutoHighGoalHit;
                        totalAutoHighGoalMiss += tempAutoHighGoalMiss;
                        totalAutoLowGoalHit += tempAutoLowGoalHit;
                        totalAutoLowGoalMiss += tempAutoLowGoalMiss;


                        //Teleop
                        for(int i = 0; i < Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 1; i++)
                        {
                            totalTeleopHighShotPosition[i] += tempTeleopHighShotPosition[i];
                            totalTeleopHighShotPositionHit[i] += tempTeleopHighShotPositionHit[i];
                            totalTeleopHighShotPositionMiss[i] += tempTeleopHighShotPositionMiss[i];
                        }

                        totalTeleopHighAimTime += tempTeleopHighAimTime;
                        totalTeleopHighGoalHit += tempTeleopHighGoalHit;
                        totalTeleopHighGoalMiss += tempTeleopHighGoalMiss;

                        for(int i = 0; i < Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS.length - 4; i++)
                        {
                            totalTeleopLowShotPosition[i] += tempTeleopLowShotPosition[i];
                            totalTeleopLowShotPositionHit[i] += tempTeleopLowShotPositionHit[i];
                            totalTeleopLowShotPositionMiss[i] += tempTeleopLowShotPositionMiss[i];
                        }

                        totalTeleopLowAimTime += tempTeleopLowAimTime;
                        totalTeleopLowGoalHit += tempTeleopLowGoalHit;
                        totalTeleopLowGoalMiss += tempTeleopLowGoalMiss;


                        //Endgame
                        totalFailedChallenge += tempFailedChallenge;
                        totalChallenge += tempChallenge;
                        totalFailedScale += tempFailedScale;
                        totalScale += tempScale;

                        //Post
                        totalDQ += tempDQ;
                        totalStopped += tempStopped;
                        totalDidntShowUp += tempDidntShowUp;

                        //Fouls
                        totalFouls += tempFouls;
                        totalTechFouls += tempTechFouls;
                        totalYellowCards += tempYellowCards;
                        totalRedCards += tempRedCards;
                    }
                    catch(Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                        totalMatches--;
                    }
                }
            }
        }

        // Load up new scoutmap to update the database with
        ScoutMap newTeamStats = new ScoutMap();

        for (int j = 0; j < 9; j++) {
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_STARTED[j], totalStartPosition[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[j], totalDefensesSeen[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[j], totalDefenseReaches[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[j], totalDefenseCrosses[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED[j], totalTeleopDefenses[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[j], totalTeleopNotCross[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[j], totalTeleopDefensesPoints[j]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_TIME[j], totalDefensesTime[j]);
        }
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_STARTED[9], totalStartPosition[9]);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_DEFENSES_STARTED[10], totalStartPosition[10]);

        newTeamStats.put(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT, totalAutoHighGoalHit);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_MISS, totalAutoHighGoalMiss);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_AUTO_LOW_HIT, totalAutoLowGoalHit);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_AUTO_LOW_MISS, totalAutoLowGoalMiss);

        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_HIT, totalTeleopHighGoalHit);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_MISS, totalTeleopHighGoalMiss);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_AIM_TIME, totalTeleopHighAimTime);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_HIT, totalTeleopLowGoalHit);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_MISS, totalTeleopLowGoalMiss);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_AIM_TIME, totalTeleopLowAimTime);
        for (int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length; i++) {
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS[i], totalTeleopHighShotPosition[i]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_HIT[i], totalTeleopHighShotPositionHit[i]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_MISS[i], totalTeleopHighShotPositionMiss[i]);
        }

        for (int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length; i++) {
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS[i], totalTeleopLowShotPosition[i]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_HIT[i], totalTeleopLowShotPositionHit[i]);
            newTeamStats.put(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_MISS[i], totalTeleopLowShotPositionMiss[i]);
        }

        newTeamStats.put(Constants.Calculated_Totals.TOTAL_FAILED_CHALLENGE, totalFailedChallenge);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_CHALLENGE, totalChallenge);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_FAILED_SCALE, totalFailedScale);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_SCALE, totalScale);

        newTeamStats.put(Constants.Calculated_Totals.TOTAL_DQ, totalDQ);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_STOPPED, totalStopped);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_DIDNT_SHOW_UP, totalDidntShowUp);

        newTeamStats.put(Constants.Calculated_Totals.TOTAL_FOULS, totalFouls);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_TECH_FOULS, totalTechFouls);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_YELLOW_CARDS, totalYellowCards);
        newTeamStats.put(Constants.Calculated_Totals.TOTAL_RED_CARDS, totalRedCards);

        newTeamStats.put(Constants.Calculated_Totals.TOTAL_MATCHES, totalMatches);

        newTeamStats.put(StatsDB.KEY_TEAM_NUMBER, team);

        statsDB.updateStats(newTeamStats);
    }

    /**
     * @param matches      All the match numbers for matches which there is data in the super scout table
     * @param matchScoutDB The helper for the match scout database table
     * @param superScoutDB The helper for the super scout database table
     * @param statsDB
     * @param eventID
     * @param context
     */
    public static void updateSuper(Set<Integer> matches, MatchScoutDB matchScoutDB, SuperScoutDB superScoutDB,
                                   StatsDB statsDB, String eventID, Context context) {

        /*
        // Calculate qualitative rankings
        Cursor superData = superScoutDB.getAllMatches();
        ArrayList<Integer> teamList = statsDB.getTeamNumbers();
        for (int i = 0; i < Constants.Super_Inputs.SUPER_QUALITATIVE.length; i++) {
            Log.d(TAG, Constants.Super_Inputs.SUPER_QUALITATIVE[i]);
            String[] rankings = SchulzeMethod.CardinalRankCalc(teamList, superData, eventID, Constants.Super_Inputs.SUPER_QUALITATIVE[i], context);
            for (int j = 0; j < teamList.size(); j++) {
                ScoutMap map = new ScoutMap();
                map.put(StatsDB.KEY_TEAM_NUMBER, teamList.get(j));
                map.put(Constants.Qualitative_Rankings.QUALITATIVE_RANKING[i], rankings[j]);
                statsDB.updateStats(map);
            }
        }
        */

        Set<Integer> teams = new HashSet<>();
        for (Integer matchNum : matches) {
            ScoutMap match = superScoutDB.getMatchInfo(matchNum);
            int team = match.getInt(SuperScoutDB.KEY_BLUE1);
            if (!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team, matchNum) != null) {

                updateTeam(team, matchScoutDB, superScoutDB, statsDB);
                teams.add(team);
            }

            team = match.getInt(SuperScoutDB.KEY_BLUE2);
            if (!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team, matchNum) != null) {
                updateTeam(team, matchScoutDB, superScoutDB, statsDB);
                teams.add(team);
            }

            team = match.getInt(SuperScoutDB.KEY_BLUE3);
            if (!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team, matchNum) != null) {
                updateTeam(team, matchScoutDB, superScoutDB, statsDB);
                teams.add(team);
            }

            team = match.getInt(SuperScoutDB.KEY_RED1);
            if (!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team, matchNum) != null) {
                updateTeam(team, matchScoutDB, superScoutDB, statsDB);
                teams.add(team);
            }

            team = match.getInt(SuperScoutDB.KEY_RED2);
            if (!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team, matchNum) != null) {
                updateTeam(team, matchScoutDB, superScoutDB, statsDB);
                teams.add(team);
            }

            team = match.getInt(SuperScoutDB.KEY_RED3);
            if (!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team, matchNum) != null) {
                updateTeam(team, matchScoutDB, superScoutDB, statsDB);
                teams.add(team);
            }
        }
    }


    private static int set_start(ScoutMap teamStats, String key) {
        if (teamStats.containsKey(key))
            return teamStats.getInt(key);
        else
            return 0;
    }

    private static int[] set_start_array(ScoutMap teamStats, String type) {
        int[] array;
        if (type.equals("start"))
            array = new int[11];
        else
            array = new int[9];
        for (int i = 0; i < 9; i++) {
            switch (type) {
                case "start":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_STARTED[i]);
                    break;
                case "reach":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[i]);
                    break;
                case "cross":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i]);
                    break;
                case "teleop":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED[i]);
                    break;
                case "not":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[i]);
                    break;
                case "teleop_points":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i]);
                    break;
                case "seen":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[i]);
                    break;
                case "time":
                    array[i] = set_start(teamStats, Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_TIME[i]);
                    break;
            }
        }
        if (type.equals("start")) {
            array[9] = set_start(teamStats, "total_start_spy");
            array[10] = set_start(teamStats, "total_start_secret_passage");
        }
        return array;
    }

    private static void increment_array(Cursor cursor, int[] array, String defense2, String defense3, String defense4, String defense5, String type) {
        List defensesList = Arrays.asList(Constants.Defense_Arrays.DEFENSES);

        int index;

        switch (type) {
            case "start":
                switch (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_START_POSITION))) {
                    case "Defense 1 (Low bar)":
                        index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                        array[index]++;
                        break;
                    case "Defense 2":
                        index = defensesList.indexOf(defense2);
                        array[index]++;
                        break;
                    case "Defense 3":
                        index = defensesList.indexOf(defense3);
                        array[index]++;
                        break;
                    case "Defense 4":
                        index = defensesList.indexOf(defense4);
                        array[index]++;
                        break;
                    case "Defense 5":
                        index = defensesList.indexOf(defense5);
                        array[index]++;
                        break;
                    case "Spybox":
                        array[9]++;
                        break;
                    case "Secret Passage":
                        array[10]++;
                        break;
                }
                break;
            case "reach":
                if (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_REACH_CROSS)).equals("Reach")) {
                    switch (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_START_POSITION))) {
                        case "Defense 1 (Low bar)":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            array[index]++;
                            break;
                        case "Defense 2":
                            index = defensesList.indexOf(defense2);
                            array[index]++;
                            break;
                        case "Defense 3":
                            index = defensesList.indexOf(defense3);
                            array[index]++;
                            break;
                        case "Defense 4":
                            index = defensesList.indexOf(defense4);
                            array[index]++;
                            break;
                        case "Defense 5":
                            index = defensesList.indexOf(defense5);
                            array[index]++;
                            break;
                    }
                }
                break;
            case "cross":
                if (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_REACH_CROSS)).equals("Cross")) {
                    switch (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_START_POSITION))) {
                        case "Defense 1 (Low bar)":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            array[index]++;
                            break;
                        case "Defense 2":
                            index = defensesList.indexOf(defense2);
                            array[index]++;
                            break;
                        case "Defense 3":
                            index = defensesList.indexOf(defense3);
                            array[index]++;
                            break;
                        case "Defense 4":
                            index = defensesList.indexOf(defense4);
                            array[index]++;
                            break;
                        case "Defense 5":
                            index = defensesList.indexOf(defense5);
                            array[index]++;
                            break;
                    }
                }
                break;
            case "teleop":
                index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));

                index = defensesList.indexOf(defense2);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));

                index = defensesList.indexOf(defense3);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));

                index = defensesList.indexOf(defense4);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));

                index = defensesList.indexOf(defense5);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                break;
            case "not":
                index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense2);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense3);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense4);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense5);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5))) > 0) ? 0 : 1;
                break;
            case "teleop_points":
                if (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_REACH_CROSS)).equals("Cross")) {
                    int numCross = 0;
                    switch (cursor.getString(cursor.getColumnIndex(Constants.Auto_Inputs.AUTO_START_POSITION))) {
                        case "Defense 1 (Low bar)":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 2":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 3":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 4":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 5":
                            index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 1) ? 1 : numCross;
                            break;
                    }
                } else {
                    index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                    int numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense2);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense3);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense4);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense5);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                    array[index] += (numCross > 2) ? 2 : numCross;
                }
                break;
            case "seen":
                index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                array[index]++;

                index = defensesList.indexOf(defense2);
                array[index]++;

                index = defensesList.indexOf(defense3);
                array[index]++;

                index = defensesList.indexOf(defense4);
                array[index]++;

                index = defensesList.indexOf(defense5);
                array[index]++;
                break;
            case "time":
                index = Constants.Defense_Arrays.LOW_BAR_INDEX;
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_1)));

                index = defensesList.indexOf(defense2);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_2)));

                index = defensesList.indexOf(defense3);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_3)));

                index = defensesList.indexOf(defense4);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_4)));

                index = defensesList.indexOf(defense5);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.Teleop_Inputs.TELEOP_DEFENSE_5)));
                break;
        }
    }

    private static int stringToCount(String jsonText) {
        try {
            JSONArray jsonArray = new JSONArray(jsonText);
            return jsonArray.length();
        } catch (JSONException e) {
        }
        return 0;
    }

    private static int stringToTime(String jsonText) {
        int sum = 0;
        try {
            JSONArray jsonArray = new JSONArray(jsonText);
            for (int i = 0; i < jsonArray.length(); i++) {
                String tmp = jsonArray.getString(i);
                for (int j = 0; j < Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES.length; j++) {
                    if (tmp.equals(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES[j])) {
                        sum += Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES_VALUE[j];
                        break;
                    }
                }
            }
            if (jsonArray.length() > 0) {
                sum /= jsonArray.length();
            }
        } catch (JSONException e) {
        }
        return sum;
    }


}
