package com.team3824.akmessing1.scoutingapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregateUtil {
    private static String TAG = "AggregateUtil";

    public static void aggregate(MatchScoutDB matchScoutDB, PitScoutDB pitScoutDB, SuperScoutDB superScoutDB, StatsDB statsDB, Context context, String eventID, boolean update) {
        String lastUpdated = statsDB.getLastUpdatedTime();
        Log.d(TAG, "Last Time Updated: " + lastUpdated);
        ArrayList<Integer> matchesUpdated;
        if (lastUpdated == null || lastUpdated == "" || update) {
            matchesUpdated = null;
        } else {
            matchesUpdated = superScoutDB.getMatchesUpdatedSince(lastUpdated);
            Log.d(TAG, String.valueOf(matchesUpdated.size()));
        }

        if (matchesUpdated == null || matchesUpdated.size() > 0) {
            Cursor matchCursor;

//            if(matchesUpdated == null)
            matchCursor = superScoutDB.getAllMatches();
//            else
//                matchCursor = superScoutDB.getAllMatchesSince(lastUpdated);

            Integer[] teamNumbers = pitScoutDB.getTeamNumbers();
            if (matchCursor.getCount() > 0) {
                //String[] defenseRanking = CardinalRankCalc(teamNumbers, matchCursor, eventID, "super_defense");

                // Normal updateStats method does not seem to give the table enough time to update
                // the columns for the following loop
                if (!statsDB.hasColumn("super_drive_ability_ranking")) {
                    statsDB.addColumn("super_drive_ability_ranking", "TEXT");
                }

                String[] driveAbilityRanking = CardinalRankCalc(teamNumbers, matchCursor, eventID, "super_drive_ability", context);
                HashMap<String, ScoutValue> map;
                for (int i = 0; i < teamNumbers.length; i++) {
                    map = new HashMap<>();
                    map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumbers[i]));
                    map.put("super_drive_ability_ranking", new ScoutValue(driveAbilityRanking[i]));
                    statsDB.updateStats(map);
                    //map.clear();
                }
            }
        }


        ArrayList<Integer> teamsUpdated;
        if(update) {
            teamsUpdated = matchScoutDB.getTeamsUpdatedSince(lastUpdated);
        }
        else{
            teamsUpdated = matchScoutDB.getTeamsUpdatedSince("");
        }
        for (int i = 0; i < teamsUpdated.size(); i++) {
            Cursor teamCursor = matchScoutDB.getTeamInfoSince(teamsUpdated.get(i), lastUpdated);
            Map<String, ScoutValue> teamStats = statsDB.getTeamStats(teamsUpdated.get(i));
            HashMap<String, ScoutValue> teamMap = new HashMap<>();

            // Calculate metrics and insert in map
            //Auto
            int totalAutoTotes = 0;
            if (teamStats.containsKey("total_auto_totes") && update) {
                totalAutoTotes = teamStats.get("total_auto_totes").getInt();
            }
            int totalAutoCans = 0;
            if (teamStats.containsKey("total_auto_cans") && update) {
                totalAutoCans = teamStats.get("total_auto_cans").getInt();
            }
            int totalAutoStepCans = 0;
            if (teamStats.containsKey("total_auto_step_cans") && update) {
                totalAutoStepCans = teamStats.get("total_auto_step_cans").getInt();
            }
            int totalAuto3ToteStacks = 0;
            if (teamStats.containsKey("total_auto_three_tote_stack") && update) {
                totalAuto3ToteStacks = teamStats.get("total_auto_three_tote_stack").getInt();
            }

            //Teleop
            int totalPoints = 0;
            if (teamStats.containsKey("total_points") && update) {
                totalPoints = teamStats.get("total_points").getInt();
            }
            int totalTotes = 0;
            if (teamStats.containsKey("total_totes") && update) {
                totalTotes = teamStats.get("total_totes").getInt();
            }
            int totalCans = 0;
            if (teamStats.containsKey("total_cans") && update) {
                totalCans = teamStats.get("total_cans").getInt();
            }
            int totalNoodles = 0;
            if (teamStats.containsKey("total_noodles") && update) {
                totalNoodles = teamStats.get("total_noodles").getInt();
            }
            int totalDroppedStacks = 0;
            if (teamStats.containsKey("total_dropped_stacks") && update) {
                totalDroppedStacks = teamStats.get("total_dropped_stacks").getInt();
            }
            int totalCoopTotes = 0;
            if (teamStats.containsKey("total_coop_totes") && update) {
                totalCoopTotes = teamStats.get("total_coop_totes").getInt();
            }
            int totalStepCans = 0;
            if (teamStats.containsKey("total_step_cans") && update) {
                totalStepCans = teamStats.get("total_step_cans").getInt();
            }
            int totalKnockedOverStacks = 0;
            if (teamStats.containsKey("total_knocked_over_stacks") && update) {
                totalStepCans = teamStats.get("total_knocked_over_stacks").getInt();
            }

            int totalLandfill = 0;
            if (teamStats.containsKey("total_landfill") && update) {
                totalLandfill = teamStats.get("total_landfill").getInt();
            }
            int totalHPFeeder = 0;
            if (teamStats.containsKey("total_hp_feeder") && update) {
                totalHPFeeder = teamStats.get("total_hp_feeder").getInt();
            }

            //Post
            int totalDQs = 0;
            if (teamStats.containsKey("total_dqs") && update) {
                totalDQs = teamStats.get("total_dqs").getInt();
            }
            int totalStoppedMoving = 0;
            if (teamStats.containsKey("total_stopped_moving") && update) {
                totalStoppedMoving = teamStats.get("total_stopped_moving").getInt();
            }
            int totalDidntShowUp = 0;
            if (teamStats.containsKey("total_didnt_show_up") && update) {
                totalDidntShowUp = teamStats.get("total_didnt_show_up").getInt();
            }
            int totalFouls = 0;
            if (teamStats.containsKey("total_fouls") && update) {
                totalFouls = teamStats.get("total_fouls").getInt();
            }

            int totalMatches = 0;
            if (teamStats.containsKey("total_matches") && update) {
                totalMatches = teamStats.get("total_matches").getInt();
            }

            while (!teamCursor.isAfterLast()) {
                totalMatches++;
                //Auto
                totalAutoTotes += teamCursor.getInt(teamCursor.getColumnIndex("auto_totesMoved"));
                totalAutoCans += teamCursor.getInt(teamCursor.getColumnIndex("auto_cansMoved"));
                totalAutoStepCans += teamCursor.getInt(teamCursor.getColumnIndex("auto_stepCans"));
                totalAuto3ToteStacks += teamCursor.getInt(teamCursor.getColumnIndex("auto_threeToteStack"));

                // Individual cans or totes in auto can be ignored...
                totalPoints += 20 * teamCursor.getInt(teamCursor.getColumnIndex("auto_threeToteStack"));

                //Teleop
                String matchStacks = teamCursor.getString(teamCursor.getColumnIndex("teleop_stacks"));
                try {
                    JSONArray jsonArray = new JSONArray(matchStacks);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        int numTotes = jsonObject.getInt("toteCount");
                        int preexistingTotes = jsonObject.getInt("preexistingToteCount");
                        boolean isCanned = jsonObject.getBoolean("isCanned");
                        boolean isNoodled = jsonObject.getBoolean("isNoodled");
                        boolean isStackDropped = jsonObject.getBoolean("isStackDropped");
                        if (!isStackDropped) {
                            totalTotes += numTotes;
                            totalCans += isCanned ? 1 : 0;
                            totalNoodles += isNoodled ? 1 : 0;
                            totalPoints += numTotes * 2;
                            // Giving half credit for preexisting totes for can points
                            totalPoints += (isCanned ? 1 : 0) * 4 * (numTotes + preexistingTotes / 2);
                        } else {
                            totalDroppedStacks++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                totalCoopTotes += teamCursor.getInt(teamCursor.getColumnIndex("teleop_coopTotes"));
                totalStepCans += teamCursor.getInt(teamCursor.getColumnIndex("teleop_stepCans"));
                totalKnockedOverStacks += teamCursor.getInt(teamCursor.getColumnIndex("teleop_knockedOverStacks"));
                String position = teamCursor.getString(teamCursor.getColumnIndex("teleop_landfillFeederNeither"));
                if (position.equals("Both")) {
                    totalLandfill++;
                    totalHPFeeder++;
                } else if (position.equals("Landfill")) {
                    totalLandfill++;
                } else if (position.equals("HP Feeder")) {
                    totalHPFeeder++;
                }

                //Post
                totalDQs += teamCursor.getInt(teamCursor.getColumnIndex("post_dq"));
                totalStoppedMoving += teamCursor.getInt(teamCursor.getColumnIndex("post_stoppedMoving"));
                totalDidntShowUp += teamCursor.getInt(teamCursor.getColumnIndex("post_didntShowedUp"));
                totalFouls += teamCursor.getInt(teamCursor.getColumnIndex("post_fouls"));

                teamCursor.moveToNext();
            }

            teamMap.put("total_auto_totes", new ScoutValue(totalAutoTotes));
            teamMap.put("total_auto_cans", new ScoutValue(totalAutoCans));
            teamMap.put("total_auto_step_cans", new ScoutValue(totalAutoStepCans));
            teamMap.put("total_auto_three_tote_stack", new ScoutValue(totalAuto3ToteStacks));

            teamMap.put("total_points", new ScoutValue(totalPoints));
            teamMap.put("total_totes", new ScoutValue(totalTotes));
            teamMap.put("total_cans", new ScoutValue(totalCans));
            teamMap.put("total_step_cans", new ScoutValue(totalStepCans));
            teamMap.put("total_noodles", new ScoutValue(totalNoodles));
            teamMap.put("total_dropped_stacks", new ScoutValue(totalDroppedStacks));
            teamMap.put("total_knocked_over_stacks", new ScoutValue(totalKnockedOverStacks));
            teamMap.put("total_coop_totes", new ScoutValue(totalCoopTotes));
            teamMap.put("total_landfill", new ScoutValue(totalLandfill));
            teamMap.put("total_hp_feeder", new ScoutValue(totalHPFeeder));

            teamMap.put("total_dqs", new ScoutValue(totalDQs));
            teamMap.put("total_didnt_show_up", new ScoutValue(totalDidntShowUp));
            teamMap.put("total_fouls", new ScoutValue(totalFouls));
            teamMap.put("total_stopped_moving", new ScoutValue(totalStoppedMoving));

            teamMap.put("total_matches", new ScoutValue(totalMatches));

            teamMap.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamsUpdated.get(i)));

            statsDB.updateStats(teamMap);
        }
    }

        /*
        This function is designed to combine qualitative rankings of subsets of teams into a ranking
         of all the teams. It uses Schulze Method for voting.
        */

    public static String[] CardinalRankCalc(Integer[] teamNumbers, Cursor matchCursor, String eventID, String key, Context context)
    {
        int numTeams = teamNumbers.length;
        Set<Integer> ranking = new HashSet<Integer>();
        final List<Integer> teamNumList = Arrays.asList(teamNumbers);
        String[] output;


        // Create empty matrix
        Integer[][] matrix = new Integer[numTeams][numTeams];
        for (Integer[] line : matrix) {
            Arrays.fill(line, 0);
        }
/*
        try {
            FileInputStream inputStream = openFileInput(eventID+"_"+key);
            String matrixText = "";
            int c;
            while((c = inputStream.read()) != -1)
            {
                matrixText += (char)c;
            }
            inputStream.close();
            List<String> matrixList = Arrays.asList(matrixText.split(","));
            for(int i = 0; i < matrix.length; i++)
            {
                for(int j = 0; j < matrix[i].length; j++)
                {
                    matrix[i][j] = Integer.parseInt(matrixList.get(i*matrix.length+j));
                    if(matrix[i][j] > 0)
                    {
                        ranking.add(teamNumbers[i]);
                        ranking.add(teamNumbers[j]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG,"File not found");
        } catch (IOException e) {
            Log.d(TAG, "IOException");
        }
*/
        // Go through the cursor and get all the subset rankings
        // Each team that is ranked higher in a subset ranking gets an increase in the direct
        // path between it and those ranked lower than it
        JSONArray jsonArray = null;
        ArrayList<Integer> before = new ArrayList<>();
        while(!matchCursor.isAfterLast()){

            String line = matchCursor.getString(matchCursor.getColumnIndex(key));
            try {
                jsonArray = new JSONArray(line);
                before.clear();
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    int index2 = teamNumList.indexOf(jsonArray.getInt(i));
                    for(int j = 0; j < before.size(); j++)
                    {
                        int index1 = teamNumList.indexOf(before.get(j));
                        matrix[index1][index2]++;
                    }
                    before.add(jsonArray.getInt(i));
                    ranking.add(jsonArray.getInt(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            matchCursor.moveToNext();
        }
/*
        try {
            FileOutputStream outputStream = openFileOutput(eventID+"_"+key, Context.MODE_PRIVATE);
            String string = "";
            for(int i = 0; i < matrix.length;i++)
            {
                for(int j = 0; j < matrix[i].length; j++)
                {
                    string += matrix[i][j] +",";
                }
            }
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d(TAG,"Exception");
        }
*/
        // If one team has more higher rankings over another team then that gets put in the
        // strongest path matrix otherwise it is left at 0
        final int[][] strongestPathMatrix = new int[numTeams][numTeams];
        for (int[] line : strongestPathMatrix) {
            Arrays.fill(line, 0);
        }

        for(int i = 0; i < numTeams; i++)
        {
            for(int j = 0; j < numTeams; j++)
            {
                if(matrix[i][j] > matrix[j][i])
                {
                    strongestPathMatrix[i][j] = matrix[i][j];
                }
            }
        }

        // find the strongest path from each team to each other team
        for(int i = 0; i < numTeams; i++)
        {
            for(int j = 0; j < numTeams; j++)
            {
                if(i == j) {
                    continue;
                }
                for(int k = 0; k < numTeams; k++)
                {
                    if(i != k && j != k)
                    {
                        strongestPathMatrix[j][k] = Math.max(strongestPathMatrix[j][k], Math.min(strongestPathMatrix[j][i], strongestPathMatrix[i][k]));

                    }
                }
            }
        }
/*
        try {
            FileOutputStream outputStream = openFileOutput("strongestpathmatrix.csv", Context.MODE_PRIVATE);
            String string = "";
            for(int i = 0; i < strongestPathMatrix.length;i++)
            {
                for(int j = 0; j < strongestPathMatrix[i].length; j++)
                {
                    string += strongestPathMatrix[i][j] +", ";
                }
                string += "\n";
            }
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
*/

        List<Integer> sortedRanking = new ArrayList<>();
        sortedRanking.addAll(ranking);
        // sort the teams based on their paths to another team
        Collections.sort(sortedRanking, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                int indexA = teamNumList.indexOf(a);
                int indexB = teamNumList.indexOf(b);
                Log.i(TAG, indexA + " " + indexB + " " + strongestPathMatrix[indexA][indexB] + " " + strongestPathMatrix[indexB][indexA]);
                if (strongestPathMatrix[indexA][indexB] > strongestPathMatrix[indexB][indexA]) {
                    return -1;
                } else if (strongestPathMatrix[indexA][indexB] == strongestPathMatrix[indexB][indexA]) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        try {
            FileOutputStream outputStream = context.openFileOutput("ranking.csv", Context.MODE_PRIVATE);
            String string = "";
            for(int j = 0; j < ranking.size(); j++)
            {
                string += sortedRanking.get(j) +", ";
            }

            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create output that includes ties
        output = new String[teamNumbers.length];
        int rank = 1;
        for(int i = 0; i < sortedRanking.size(); i++)
        {
            boolean tied = false;
            int currentTeamNumber = sortedRanking.get(i);
            int currentTeamNumberIndex = teamNumList.indexOf(currentTeamNumber);
            if(i > 0)
            {
                int previousTeamNumber = sortedRanking.get(i-1);
                int previousTeamNumberIndex = teamNumList.indexOf(previousTeamNumber);

                if(strongestPathMatrix[currentTeamNumberIndex][previousTeamNumberIndex] == strongestPathMatrix[previousTeamNumberIndex][currentTeamNumberIndex])
                {
                    tied = true;
                }
            }
            if(!tied && i < ranking.size()-1)
            {
                int nextTeamNumber = sortedRanking.get(i+1);
                int nextTeamNumberIndex = teamNumList.indexOf(nextTeamNumber);

                if(strongestPathMatrix[currentTeamNumberIndex][nextTeamNumberIndex] == strongestPathMatrix[nextTeamNumberIndex][currentTeamNumberIndex])
                {
                    tied = true;
                }
            }
            if(tied)
            {
                output[currentTeamNumberIndex] = "T"+String.valueOf(rank);
            }
            else
            {
                rank = i+1;
                output[currentTeamNumberIndex] = String.valueOf(rank);
            }
            Log.i(TAG,teamNumbers[currentTeamNumberIndex]+": "+output[currentTeamNumberIndex]);
        }
        rank = sortedRanking.size()+1;
        for(int i = 0; i < teamNumbers.length; i++)
        {
            if(sortedRanking.indexOf(teamNumbers[i]) == -1)
            {
                output[i] = "T"+ String.valueOf(rank);
                Log.i(TAG,teamNumbers[i]+": "+output[i]);
            }
        }

        return output;
    }
}
