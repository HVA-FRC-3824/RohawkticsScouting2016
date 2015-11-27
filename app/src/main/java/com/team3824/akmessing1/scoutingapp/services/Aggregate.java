package com.team3824.akmessing1.scoutingapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.team3824.akmessing1.scoutingapp.SchulzeMethod;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Aggregate extends IntentService {
    private String TAG ="Aggregate";
    Map<String, SchulzeMethod> rankingCalcs;

    public Aggregate()
    {
        super("Aggregate");
        rankingCalcs = new HashMap<>();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "Aggregate Started");
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");
        PitScoutDB pitScoutDB = new PitScoutDB(context,eventID);
        SuperScoutDB superScoutDB = new SuperScoutDB(context, eventID);
        MatchScoutDB matchScoutDB = new MatchScoutDB(context, eventID);
        StatsDB statsDB = new StatsDB(context, eventID);

        String lastUpdated = statsDB.getLastUpdatedTime();

        ArrayList<Integer> matchesUpdated;
        if(lastUpdated == null)
        {
            matchesUpdated = null;
        }
        else {
            matchesUpdated = superScoutDB.getMatchesUpdatedSince(lastUpdated);
        }


        if(matchesUpdated == null || matchesUpdated.size() > 0)
        {
            Cursor matchCursor = superScoutDB.getAllMatches();
            Integer[] teamNumbers = pitScoutDB.getTeamNumbers();
            if(matchCursor.getCount() > 0) {
                //String[] defenseRanking = CardinalRankCalc(teamNumbers, matchCursor, "super_defense");
                String[] driveAbilityRanking = CardinalRankCalc(teamNumbers, matchCursor, "super_drive_ability");
                HashMap<String, ScoutValue> map;
                for (int i = 0; i < teamNumbers.length; i++) {
                    map = new HashMap<>();
                    map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumbers[i]));
                    //map.put("super_defense",new ScoutValue(defenseRanking.indexOf(teamNumbers[i])+1));
                    map.put("super_drive_ability", new ScoutValue(driveAbilityRanking[i]));
                    statsDB.updateStats(map);
                }
            }
        }


        ArrayList<Integer> teamsUpdated = matchScoutDB.getTeamsUpdatedSince(lastUpdated);
        for(int i = 0; i < teamsUpdated.size(); i++)
        {
            Cursor teamCursor = matchScoutDB.getTeamInfoSince(teamsUpdated.get(i), lastUpdated);
            Map<String, ScoutValue> teamStats = statsDB.getTeamStats(teamsUpdated.get(i));
            HashMap<String, ScoutValue> teamMap = new HashMap<>();

            // Calculate metrics and insert in map

            //Auto
            int totalAutoTotes = 0;
            if(teamStats.containsKey("total_auto_totes"))
            {
                totalAutoTotes = teamStats.get("total_auto_totes").getInt();
            }
            int totalAutoCans = 0;
            if(teamStats.containsKey("total_auto_cans"))
            {
                totalAutoCans = teamStats.get("total_auto_cans").getInt();
            }
            int totalAutoStepCans = 0;
            if(teamStats.containsKey("total_auto_step_cans"))
            {
                totalAutoStepCans = teamStats.get("total_auto_step_cans").getInt();
            }
            int totalAuto3ToteStacks = 0;
            if(teamStats.containsKey("total_auto_three_tote_stack"))
            {
                totalAuto3ToteStacks = teamStats.get("total_auto_three_tote_stack").getInt();
            }

            //Teleop
            int totalPoints = 0;
            if(teamStats.containsKey("total_points"))
            {
                totalPoints = teamStats.get("total_points").getInt();
            }
            int totalTotes = 0;
            if(teamStats.containsKey("total_totes"))
            {
                totalTotes = teamStats.get("total_totes").getInt();
            }
            int totalCans = 0;
            if(teamStats.containsKey("total_cans"))
            {
                totalCans = teamStats.get("total_cans").getInt();
            }
            int totalNoodles = 0;
            if(teamStats.containsKey("total_noodles"))
            {
                totalNoodles = teamStats.get("total_noodles").getInt();
            }
            int totalDroppedStacks = 0;
            if(teamStats.containsKey("total_dropped_stacks"))
            {
                totalDroppedStacks = teamStats.get("total_dropped_stacks").getInt();
            }
            int totalCoopTotes = 0;
            if(teamStats.containsKey("total_coop_totes"))
            {
                totalCoopTotes = teamStats.get("total_coop_totes").getInt();
            }
            int totalStepCans = 0;
            if(teamStats.containsKey("total_step_cans"))
            {
                totalStepCans = teamStats.get("total_step_cans").getInt();
            }
            int totalKnockedOverStacks = 0;
            if(teamStats.containsKey("total_knocked_over_stacks"))
            {
                totalStepCans = teamStats.get("total_knocked_over_stacks").getInt();
            }

            int totalLandfill = 0;
            if(teamStats.containsKey("total_landfill"))
            {
                totalLandfill = teamStats.get("total_landfill").getInt();
            }
            int totalHPFeeder = 0;
            if(teamStats.containsKey("total_hp_feeder"))
            {
                totalHPFeeder = teamStats.get("total_hp_feeder").getInt();
            }

            //Post
            int totalDQs = 0;
            if(teamStats.containsKey("total_dqs"))
            {
                totalDQs = teamStats.get("total_dqs").getInt();
            }
            int totalStoppedMoving = 0;
            if(teamStats.containsKey("total_stopped_moving"))
            {
                totalStoppedMoving = teamStats.get("total_stopped_moving").getInt();
            }
            int totalDidntShowUp = 0;
            if(teamStats.containsKey("total_didnt_show_up"))
            {
                totalDidntShowUp = teamStats.get("total_didnt_show_up").getInt();
            }
            int totalFouls = 0;
            if(teamStats.containsKey("total_fouls"))
            {
                totalFouls = teamStats.get("total_fouls").getInt();
            }

            int totalMatches = 0;
            if(teamStats.containsKey("total_matches"))
            {
                totalMatches = teamStats.get("total_matches").getInt();
            }

            do{
                totalMatches++;
                //Auto
                totalAutoTotes += teamCursor.getInt(teamCursor.getColumnIndex("auto_totesMoved"));
                totalAutoCans += teamCursor.getInt(teamCursor.getColumnIndex("auto_cansMoved"));
                totalAutoStepCans += teamCursor.getInt(teamCursor.getColumnIndex("auto_stepCans"));
                totalAuto3ToteStacks += teamCursor.getInt(teamCursor.getColumnIndex("auto_threeToteStack"));

                // Individual cans or totes in auto can be ignored...
                totalPoints += teamCursor.getInt(teamCursor.getColumnIndex("auto_threeToteStack"));

                //Teleop
                String matchStacks = teamCursor.getString(teamCursor.getColumnIndex("teleop_stacks"));
                try {
                    JSONArray jsonArray = new JSONArray(matchStacks);
                    for(int j = 0; j < jsonArray.length(); j++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        int numTotes = jsonObject.getInt("toteCount");
                        int preexistingTotes = jsonObject.getInt("preexistingToteCount");
                        boolean isCanned = jsonObject.getBoolean("isCanned");
                        boolean isNoodled = jsonObject.getBoolean("isNoodled");
                        boolean isStackDropped = jsonObject.getBoolean("isStackDropped");
                        if(!isStackDropped) {
                            totalTotes += numTotes;
                            totalCans += isCanned ? 1 : 0;
                            totalNoodles += isNoodled ? 1 : 0;
                            totalPoints += numTotes * 2;
                            // Giving half credit for preexisting totes for can points
                            totalPoints += (isCanned ? 1 : 0) * 4 * (numTotes + preexistingTotes / 2);
                        }
                        else
                        {
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
                if(position.equals("Both"))
                {
                    totalLandfill++;
                    totalHPFeeder++;
                }
                else if(position.equals("Landfill"))
                {
                    totalLandfill++;
                }
                else if(position.equals("HP Feeder"))
                {
                    totalHPFeeder++;
                }

                //Post
                totalDQs += teamCursor.getInt(teamCursor.getColumnIndex("post_dq"));
                totalStoppedMoving += teamCursor.getInt(teamCursor.getColumnIndex("post_stoppedMoving"));
                totalDidntShowUp += teamCursor.getInt(teamCursor.getColumnIndex("post_didntShowedUp"));
                totalFouls += teamCursor.getInt(teamCursor.getColumnIndex("post_fouls"));

                teamCursor.moveToNext();
            }while(!teamCursor.isAfterLast());

            teamMap.put("total_auto_totes",new ScoutValue(totalAutoTotes));
            teamMap.put("total_auto_cans",new ScoutValue(totalAutoCans));
            teamMap.put("total_auto_step_cans",new ScoutValue(totalAutoStepCans));
            teamMap.put("total_auto_three_tote_stack",new ScoutValue(totalAuto3ToteStacks));

            teamMap.put("total_points",new ScoutValue(totalPoints));
            teamMap.put("total_totes",new ScoutValue(totalTotes));
            teamMap.put("total_cans",new ScoutValue(totalCans));
            teamMap.put("total_step_cans",new ScoutValue(totalStepCans));
            teamMap.put("total_noodles",new ScoutValue(totalNoodles));
            teamMap.put("total_dropped_stacks",new ScoutValue(totalDroppedStacks));
            teamMap.put("total_knocked_over_stacks",new ScoutValue(totalKnockedOverStacks));
            teamMap.put("total_coop_totes",new ScoutValue(totalCoopTotes));
            teamMap.put("total_landfill",new ScoutValue(totalLandfill));
            teamMap.put("total_hp_feeder",new ScoutValue(totalHPFeeder));

            teamMap.put("total_dqs",new ScoutValue(totalDQs));
            teamMap.put("total_didnt_show_up",new ScoutValue(totalDidntShowUp));
            teamMap.put("total_fouls",new ScoutValue(totalFouls));
            teamMap.put("total_stopped_moving",new ScoutValue(totalStoppedMoving));

            teamMap.put("total_matches",new ScoutValue(totalMatches));

            teamMap.put(StatsDB.KEY_TEAM_NUMBER,new ScoutValue(teamsUpdated.get(i)));

            statsDB.updateStats(teamMap);
        }

        statsDB.close();
        matchScoutDB.close();
        superScoutDB.close();
        Log.d(TAG, "Aggregate Finished");
    }

    /*
        This function is designed to combine qualitative rankings of subsets of teams into a ranking
         of all the teams. It uses Schulze Method for voting.
     */

    public String[] CardinalRankCalc(Integer[] teamNumbers, Cursor matchCursor, String key)
    {
        int numTeams = teamNumbers.length;
        //Set<Integer> ranking = new HashSet<Integer>();
        List<Integer> teamNumList = Arrays.asList(teamNumbers);
        String[] output;
        if(rankingCalcs.containsKey(key))
        {
            output = rankingCalcs.get(key).calcRanking(matchCursor);
        }
        else
        {
            SchulzeMethod ranking = new SchulzeMethod(key, teamNumList);
            output = ranking.calcRanking(matchCursor);
            rankingCalcs.put(key,ranking);

        }
        /*
        // Create empty matrix
        Integer[][] matrix = new Integer[numTeams][numTeams];
        for (Integer[] line : matrix) {
            Arrays.fill(line, 0);
        }
        // Go through the cursor and get all the subset rankings
        // Each team that is ranked higher in a subset ranking gets an increase in the direct
        // path between it and those ranked lower than it
        JSONArray jsonArray = null;
        ArrayList<Integer> before = new ArrayList<>();
        do{

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
        }while(!matchCursor.isAfterLast());

        try {
            FileOutputStream outputStream = openFileOutput("matrix.csv", Context.MODE_PRIVATE);
            String string = "";
            for(int i = 0; i < matrix.length;i++)
            {
                for(int j = 0; j < matrix[i].length; j++)
                {
                    string += matrix[i][j] +", ";
                }
                string += "\n";
            }
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

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


        List<Integer> sortedRanking = new ArrayList<>();
        sortedRanking.addAll(ranking);
        // sort the teams based on their paths to another team
        Collections.sort(sortedRanking, new Comparator<Integer>() {
            public int compare(Integer a, Integer b) {
                int indexA = teamNumList.indexOf(a);
                int indexB = teamNumList.indexOf(b);
                Log.i(TAG,indexA+" "+indexB+" "+strongestPathMatrix[indexA][indexB]+" "+strongestPathMatrix[indexB][indexA]);
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
            FileOutputStream outputStream = openFileOutput("ranking.csv", Context.MODE_PRIVATE);
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
        String[] output = new String[teamNumbers.length];
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
        */
        return output;
    }
}
