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
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    static Map<String, SchulzeMethod> rankingCalcs;
    private String[] defenses = {"low_bar","portcullis","cheval_de_frise","moat","ramparts",
            "drawbridge","sally_port","rock_wall","rough_terrain"};

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
        ScheduleDB scheduleDB = new ScheduleDB(context,eventID);
        StatsDB statsDB = new StatsDB(context, eventID);

        String lastUpdated = statsDB.getLastUpdatedTime();
        Log.d(TAG, "Last Time Updated: "+lastUpdated);
        ArrayList<Integer> matchesUpdated;
        if(lastUpdated == null || lastUpdated == "")
        {
            matchesUpdated = null;
        }
        else {
            matchesUpdated = superScoutDB.getMatchesUpdatedSince(lastUpdated);
            Log.d(TAG,String.valueOf(matchesUpdated.size()));
        }

        if(matchesUpdated == null || matchesUpdated.size() > 0)
        {
            Cursor matchCursor;

//            if(matchesUpdated == null)
                matchCursor = superScoutDB.getAllMatches();
//            else
//                matchCursor = superScoutDB.getAllMatchesSince(lastUpdated);

            Integer[] teamNumbers = pitScoutDB.getTeamNumbers();
            if(matchCursor.getCount() > 0) {

                // Normal updateStats method does not seem to give the table enough time to update
                // the columns for the following loop
                if(!statsDB.hasColumn("super_drive_ability_ranking"))
                {
                    statsDB.addColumn("super_drive_ability_ranking","TEXT");
                }

                String[] driveAbilityRanking = CardinalRankCalc(teamNumbers, matchCursor, eventID, "super_drive_ability");
                HashMap<String, ScoutValue> map;
                for (int i = 0; i < teamNumbers.length; i++) {
                    map = new HashMap<>();
                    map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumbers[i]));
                    map.put("super_drive_ability_ranking", new ScoutValue(driveAbilityRanking[i]));
                    statsDB.updateStats(map);
                }

                matchCursor.moveToFirst();

                // Normal updateStats method does not seem to give the table enough time to update
                // the columns for the following loop
                if(!statsDB.hasColumn("super_defense_ability_ranking"))
                {
                    statsDB.addColumn("super_defense_ability_ranking","TEXT");
                }

                String[] defenseAbilityRanking = CardinalRankCalc(teamNumbers, matchCursor, eventID, "super_defense_ability");
                for (int i = 0; i < teamNumbers.length; i++) {
                    map = new HashMap<>();
                    map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumbers[i]));
                    map.put("super_defense_ability_ranking", new ScoutValue(defenseAbilityRanking[i]));
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

            int totalPoints = 0;

            //Auto
            int[] totalStartPosition = set_start_array(teamStats,"start");
            int[] totalDefenseReaches = set_start_array(teamStats,"reaches");
            int[] totalDefenseCrosses = set_start_array(teamStats,"crosses");
            int totalAutoHighGoalHit = set_start(teamStats,"total_auto_high_hit");
            int totalAutoHighGoalMiss = set_start(teamStats,"total_auto_high_miss");
            int totalAutoLowGoalHit = set_start(teamStats,"total_auto_low_hit");
            int totalAutoLowGoalMiss = set_start(teamStats,"total_auto_low_miss");


            //Teleop
            int[] totalTeleopDefenses = set_start_array(teamStats,"teleop");
            int[] totalDefensesSeen = set_start_array(teamStats, "seen");
            int totalTeleopHighGoalHit = set_start(teamStats,"total_teleop_high_hit");
            int totalTeleopHighGoalMiss = set_start(teamStats,"total_teleop_high_miss");
            int totalTeleopLowGoalHit = set_start(teamStats,"total_teleop_low_hit");
            int totalTeleopLowGoalMiss = set_start(teamStats,"total_teleop_low_miss");

            //Endgame
            int totalChallenge = set_start(teamStats,"total_endgame_challenge");
            int totalScale = set_start(teamStats,"total_endgame_scale");

            //Post
            int totalDQ = set_start(teamStats,"total_post_dq");
            int totalStopped = set_start(teamStats,"total_post_stopped");
            int totalDidntShowUp = set_start(teamMap,"total_post_didnt_show_up");

            //Fouls
            int totalFouls = set_start(teamStats,"total_fouls");
            int totalTechFouls = set_start(teamStats,"total_tech_fouls");
            int totalYellowCards = set_start(teamStats,"total_yellow_cards");
            int totalRedCards = set_start(teamStats,"total_red_cards");


            int totalMatches = set_start(teamStats,"total_matches");

            while(!teamCursor.isAfterLast())
            {
                totalMatches++;

                int matchNumber = teamCursor.getInt(teamCursor.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER));
                Map<String, ScoutValue> superMatch = superScoutDB.getMatchInfo(matchNumber);
                Cursor match = scheduleDB.getMatch(matchNumber);

                String defense2, defense3, defense4, defense5;
                defense3 = superMatch.get("defense3").getString();
                if(match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE1)) == teamsUpdated.get(i) ||
                        match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE2)) == teamsUpdated.get(i) ||
                        match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE3)) == teamsUpdated.get(i))
                {
                    defense2 = superMatch.get("blue_defense2").getString();
                    defense4 = superMatch.get("blue_defense4").getString();
                    defense5 = superMatch.get("blue_defense5").getString();
                }
                else
                {
                    defense2 = superMatch.get("red_defense2").getString();
                    defense4 = superMatch.get("red_defense4").getString();
                    defense5 = superMatch.get("red_defense5").getString();
                }

                increment_array(teamCursor,totalDefensesSeen,defense2, defense3,defense4, defense5,"seen");

                //Auto
                increment_array(teamCursor,totalStartPosition,defense2, defense3,defense4, defense5,"start");
                increment_array(teamCursor,totalDefenseReaches,defense2, defense3,defense4, defense5,"reach");
                increment_array(teamCursor,totalDefenseCrosses,defense2, defense3,defense4, defense5,"cross");
                totalAutoHighGoalHit += teamCursor.getInt(teamCursor.getColumnIndex("auto_shoot_high_scored"));
                totalAutoHighGoalMiss += teamCursor.getInt(teamCursor.getColumnIndex("auto_shoot_high_missed"));
                totalAutoLowGoalHit += teamCursor.getInt(teamCursor.getColumnIndex("auto_shoot_low_scored"));
                totalAutoLowGoalMiss += teamCursor.getInt(teamCursor.getColumnIndex("auto_shoot_low_missed"));

                //Teleop
                increment_array(teamCursor,totalTeleopDefenses,defense2, defense3,defense4, defense5,"teleop");
                totalTeleopHighGoalHit += teamCursor.getInt(teamCursor.getColumnIndex("teleop_high_score"));
                totalTeleopHighGoalMiss += teamCursor.getInt(teamCursor.getColumnIndex("teleop_high_missed"));
                totalTeleopLowGoalHit += teamCursor.getInt(teamCursor.getColumnIndex("teleop__low_score"));
                totalTeleopLowGoalMiss += teamCursor.getInt(teamCursor.getColumnIndex("teleop_low_missed"));

                //Endgame
                totalChallenge += (teamCursor.getString(teamCursor.getColumnIndex("endGame_challenge_scale")).equals("Challenge"))?1:0;
                totalScale += (teamCursor.getString(teamCursor.getColumnIndex("endGame_challenge_scale")).equals("Scale"))?1:0;

                //Post
                totalDQ += teamCursor.getInt(teamCursor.getColumnIndex("post_dq"));
                totalStopped += teamCursor.getInt(teamCursor.getColumnIndex("post_stoppedMoving"));
                totalDidntShowUp += teamCursor.getInt(teamCursor.getColumnIndex("post_didntShowedUp"));

                //Fouls
                totalFouls += teamCursor.getInt(teamCursor.getColumnIndex("foul_standard"));
                totalTechFouls += teamCursor.getInt(teamCursor.getColumnIndex("foul_tech"));
                totalYellowCards += teamCursor.getInt(teamCursor.getColumnIndex("foul_yellow_card"));
                totalRedCards += teamCursor.getInt(teamCursor.getColumnIndex("foul_red_card"));

                teamCursor.moveToNext();
            }

            teamMap.put("total_matches", new ScoutValue(totalMatches));

            teamMap.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamsUpdated.get(i)));

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

    public String[] CardinalRankCalc(Integer[] teamNumbers, Cursor matchCursor, String eventID, String key)
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


    private int set_start(Map<String, ScoutValue> teamStats, String key)
    {
        if(teamStats.containsKey(key))
            return teamStats.get(key).getInt();
        else
            return 0;
    }

    private int[] set_start_array(Map<String, ScoutValue> teamStats, String type)
    {
        int[] array;
        if(type.equals("start"))
            array = new int[11];
        else
            array = new int[9];
        for(int i = 0; i < 9; i++)
        {
            switch(type)
            {
                case "start":
                    array[i] = set_start(teamStats,"total_start_"+defenses[i]);
                    break;
                case "reach":
                    array[i] = set_start(teamStats,"total_reach_"+defenses[i]);
                    break;
                case "cross":
                    array[i] = set_start(teamStats,"total_cross_"+defenses[i]);
                    break;
                case "teleop":
                    array[i] = set_start(teamStats,"total_teleop_"+defenses[i]);
                    break;
                case "seen":
                    array[i] = set_start(teamStats,"total_seen_"+defenses[i]);
                    break;
            }
        }
        if(type.equals("start"))
        {
            array[9] = set_start(teamStats,"total_start_spy");
            array[10] = set_start(teamStats,"total_start_secret_passage");
        }
        return array;
    }

    private void increment_array(Cursor cursor, int[] array, String defense2, String defense3, String defense4, String defense5, String type)
    {
        List defensesList = Arrays.asList(defenses);

        int index;

        switch(type)
        {
            case "start":
                switch(cursor.getString(cursor.getColumnIndex("auto_start_position")))
                {
                    case "Defense 1 (Low bar)":
                        index = defensesList.indexOf("low_bar");
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
                if(cursor.getString(cursor.getColumnIndex("auto_reach_cross")).equals("Reach"))
                {
                    switch(cursor.getString(cursor.getColumnIndex("auto_start_position")))
                    {
                        case "Defense 1 (Low bar)":
                            index = defensesList.indexOf("low_bar");
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
                if(cursor.getString(cursor.getColumnIndex("auto_reach_cross")).equals("Cross"))
                {
                    switch(cursor.getString(cursor.getColumnIndex("auto_start_position")))
                    {
                        case "Defense 1 (Low bar)":
                            index = defensesList.indexOf("low_bar");
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
                index = defensesList.indexOf(defense2);
                array[index] += cursor.getInt(cursor.getColumnIndex("teleop_defense_2"));
                index = defensesList.indexOf(defense3);
                array[index] += cursor.getInt(cursor.getColumnIndex("teleop_defense_3"));
                index = defensesList.indexOf(defense4);
                array[index] += cursor.getInt(cursor.getColumnIndex("teleop_defense_4"));
                index = defensesList.indexOf(defense5);
                array[index] += cursor.getInt(cursor.getColumnIndex("teleop_defense_5"));
                index = defensesList.indexOf("low_bar");
                array[index] += cursor.getInt(cursor.getColumnIndex("teleop_defense_1"));
                break;
            case "seen":
                index = defensesList.indexOf(defense2);
                array[index] ++;
                index = defensesList.indexOf(defense3);
                array[index] ++;
                index = defensesList.indexOf(defense4);
                array[index] ++;
                index = defensesList.indexOf(defense5);
                array[index] ++;
                index = defensesList.indexOf("low_bar");
                array[index] ++;
                break;
        }
    }
}
