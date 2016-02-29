package com.team3824.akmessing1.scoutingapp.utilities;

import android.database.Cursor;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AggregateStats {

    public static void updateTeams(Set<Integer> teams, MatchScoutDB matchScoutDB, SuperScoutDB superScoutDB,
                            ScheduleDB scheduleDB, StatsDB statsDB)
    {
        for(Integer team: teams)
        {
            updateTeam(team, matchScoutDB, superScoutDB, scheduleDB, statsDB);
        }
    }

    public static void updateTeam(int team, MatchScoutDB matchScoutDB, SuperScoutDB superScoutDB,
                           ScheduleDB scheduleDB, StatsDB statsDB)
    {
        Cursor teamInfo = matchScoutDB.getTeamInfo(team);

        //Might change for update feature
        Map<String, ScoutValue> teamStats = new HashMap<>();

        // Calculate metrics and insert in map

        //Auto
        int[] totalStartPosition = set_start_array(teamStats,"start");
        int[] totalDefenseReaches = set_start_array(teamStats, "reach");
        int[] totalDefenseCrosses = set_start_array(teamStats, "cross");
        int totalAutoHighGoalHit = set_start(teamStats,Constants.TOTAL_AUTO_HIGH_HIT);
        int totalAutoHighGoalMiss = set_start(teamStats,Constants.TOTAL_AUTO_HIGH_MISS);
        int totalAutoLowGoalHit = set_start(teamStats, Constants.TOTAL_AUTO_LOW_HIT);
        int totalAutoLowGoalMiss = set_start(teamStats, Constants.TOTAL_AUTO_LOW_MISS);


        //Teleop
        int[] totalTeleopDefenses = set_start_array(teamStats, "teleop");
        int[] totalTeleopNotCross = set_start_array(teamStats, "not");
        int[] totalTeleopDefensesPoints = set_start_array(teamStats, "teleop_points");
        int[] totalDefensesSeen = set_start_array(teamStats, "seen");
        int[] totalDefensesTime = set_start_array(teamStats, "time");
        int totalTeleopHighGoalHit = set_start(teamStats,Constants.TOTAL_TELEOP_HIGH_HIT);
        int totalTeleopHighGoalMiss = set_start(teamStats,Constants.TOTAL_TELEOP_HIGH_MISS);
        int totalTeleopLowGoalHit = set_start(teamStats,Constants.TOTAL_TELEOP_LOW_HIT);
        int totalTeleopLowGoalMiss = set_start(teamStats,Constants.TOTAL_TELEOP_LOW_MISS);

        //Endgame
        int totalChallenge = set_start(teamStats,Constants.TOTAL_CHALLENGE);
        int totalScale = set_start(teamStats,Constants.TOTAL_SCALE);

        //Post
        int totalDQ = set_start(teamStats,Constants.TOTAL_DQ);
        int totalStopped = set_start(teamStats,Constants.TOTAL_STOPPED);
        int totalDidntShowUp = set_start(teamStats,Constants.TOTAL_DIDNT_SHOW_UP);

        //Fouls
        int totalFouls = set_start(teamStats,Constants.TOTAL_FOULS);
        int totalTechFouls = set_start(teamStats,Constants.TOTAL_TECH_FOULS);
        int totalYellowCards = set_start(teamStats,Constants.TOTAL_YELLOW_CARDS);
        int totalRedCards = set_start(teamStats,Constants.TOTAL_RED_CARDS);


        int totalMatches = set_start(teamStats,Constants.TOTAL_MATCHES);

        for(teamInfo.moveToFirst(); !teamInfo.isAfterLast(); teamInfo.moveToNext())
        {
            int matchNumber = teamInfo.getInt(teamInfo.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER));
            Map<String, ScoutValue> superMatch = superScoutDB.getMatchInfo(matchNumber);
            // If superscout has not yet recorded the match change the update time so that this
            // match can be aggregated later
            if(superMatch != null) {
                totalMatches++;

                Cursor match = scheduleDB.getMatch(matchNumber);

                String defense2, defense3, defense4, defense5;
                defense3 = superMatch.get(Constants.DEFENSE_3).getString().toLowerCase().replace(" ", "_");
                if (match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE1)) == team ||
                        match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE2)) == team ||
                        match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE3)) == team) {
                    defense2 = superMatch.get(Constants.BLUE_DEFENSE_2).getString().toLowerCase().replace(" ", "_");
                    defense4 = superMatch.get(Constants.BLUE_DEFENSE_4).getString().toLowerCase().replace(" ", "_");
                    defense5 = superMatch.get(Constants.BLUE_DEFENSE_5).getString().toLowerCase().replace(" ", "_");
                } else {
                    defense2 = superMatch.get(Constants.RED_DEFENSE_2).getString().toLowerCase().replace(" ", "_");
                    defense4 = superMatch.get(Constants.RED_DEFENSE_4).getString().toLowerCase().replace(" ", "_");
                    defense5 = superMatch.get(Constants.RED_DEFENSE_5).getString().toLowerCase().replace(" ", "_");
                }

                increment_array(teamInfo, totalDefensesSeen, defense2, defense3, defense4, defense5, "seen");

                //Auto
                increment_array(teamInfo, totalStartPosition, defense2, defense3, defense4, defense5, "start");
                increment_array(teamInfo, totalDefenseReaches, defense2, defense3, defense4, defense5, "reach");
                increment_array(teamInfo, totalDefenseCrosses, defense2, defense3, defense4, defense5, "cross");
                totalAutoHighGoalHit += teamInfo.getInt(teamInfo.getColumnIndex(Constants.AUTO_HIGH_HIT));
                totalAutoHighGoalMiss += teamInfo.getInt(teamInfo.getColumnIndex(Constants.AUTO_HIGH_MISS));
                totalAutoLowGoalHit += teamInfo.getInt(teamInfo.getColumnIndex(Constants.AUTO_LOW_HIT));
                totalAutoLowGoalMiss += teamInfo.getInt(teamInfo.getColumnIndex(Constants.AUTO_LOW_MISS));

                //Teleop
                increment_array(teamInfo, totalTeleopDefenses, defense2, defense3, defense4, defense5, "teleop");
                increment_array(teamInfo, totalTeleopNotCross, defense2, defense3, defense4, defense5, "not");
                increment_array(teamInfo, totalTeleopDefensesPoints, defense2, defense3, defense4, defense5, "teleop_points");
                increment_array(teamInfo, totalDefensesTime, defense2, defense3, defense4, defense5, "time");
                totalTeleopHighGoalHit += teamInfo.getInt(teamInfo.getColumnIndex(Constants.TELEOP_HIGH_HIT));
                totalTeleopHighGoalMiss += teamInfo.getInt(teamInfo.getColumnIndex(Constants.TELEOP_HIGH_MISS));
                totalTeleopLowGoalHit += teamInfo.getInt(teamInfo.getColumnIndex(Constants.TELEOP_LOW_HIT));
                totalTeleopLowGoalMiss += teamInfo.getInt(teamInfo.getColumnIndex(Constants.TELEOP_LOW_MISS));

                //Endgame
                totalChallenge += (teamInfo.getString(teamInfo.getColumnIndex(Constants.ENDGAME_CHALLENGE_SCALE)).equals("Challenge")) ? 1 : 0;
                totalScale += (teamInfo.getString(teamInfo.getColumnIndex(Constants.ENDGAME_CHALLENGE_SCALE)).equals("Scale")) ? 1 : 0;

                //Post
                totalDQ += teamInfo.getInt(teamInfo.getColumnIndex(Constants.POST_DQ));
                totalStopped += teamInfo.getInt(teamInfo.getColumnIndex(Constants.POST_STOPPED));
                totalDidntShowUp += teamInfo.getInt(teamInfo.getColumnIndex(Constants.POST_DIDNT_SHOW_UP));

                //Fouls
                totalFouls += teamInfo.getInt(teamInfo.getColumnIndex(Constants.FOUL_STANDARD));
                totalTechFouls += teamInfo.getInt(teamInfo.getColumnIndex(Constants.FOUL_TECH));
                totalYellowCards += teamInfo.getInt(teamInfo.getColumnIndex(Constants.FOUL_YELLOW_CARD));
                totalRedCards += teamInfo.getInt(teamInfo.getColumnIndex(Constants.FOUL_RED_CARD));
            }
        }

        HashMap<String, ScoutValue> newTeamStats = new HashMap<>();

        for(int j = 0; j < 9; j++)
        {
            newTeamStats.put(Constants.TOTAL_DEFENSES_STARTED[j], new ScoutValue(totalStartPosition[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_SEEN[j], new ScoutValue(totalDefensesSeen[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_AUTO_REACHED[j], new ScoutValue(totalDefenseReaches[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_AUTO_CROSSED[j], new ScoutValue(totalDefenseCrosses[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[j], new ScoutValue(totalTeleopDefenses[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[j],new ScoutValue(totalTeleopNotCross[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[j], new ScoutValue(totalTeleopDefensesPoints[j]));
            newTeamStats.put(Constants.TOTAL_DEFENSES_TELEOP_TIME[j], new ScoutValue(totalDefensesTime[j]));
        }
        newTeamStats.put(Constants.TOTAL_DEFENSES_STARTED[9], new ScoutValue(totalStartPosition[9]));
        newTeamStats.put(Constants.TOTAL_DEFENSES_STARTED[10], new ScoutValue(totalStartPosition[10]));

        newTeamStats.put(Constants.TOTAL_AUTO_HIGH_HIT, new ScoutValue(totalAutoHighGoalHit));
        newTeamStats.put(Constants.TOTAL_AUTO_HIGH_MISS, new ScoutValue(totalAutoHighGoalMiss));
        newTeamStats.put(Constants.TOTAL_AUTO_LOW_HIT, new ScoutValue(totalAutoLowGoalHit));
        newTeamStats.put(Constants.TOTAL_AUTO_LOW_MISS, new ScoutValue(totalAutoLowGoalMiss));

        newTeamStats.put(Constants.TOTAL_TELEOP_HIGH_HIT, new ScoutValue(totalTeleopHighGoalHit));
        newTeamStats.put(Constants.TOTAL_TELEOP_HIGH_MISS, new ScoutValue(totalTeleopHighGoalMiss));
        newTeamStats.put(Constants.TOTAL_TELEOP_LOW_HIT, new ScoutValue(totalTeleopLowGoalHit));
        newTeamStats.put(Constants.TOTAL_TELEOP_LOW_MISS, new ScoutValue(totalTeleopLowGoalMiss));

        newTeamStats.put(Constants.TOTAL_CHALLENGE, new ScoutValue(totalChallenge));
        newTeamStats.put(Constants.TOTAL_SCALE, new ScoutValue(totalScale));

        newTeamStats.put(Constants.TOTAL_DQ, new ScoutValue(totalDQ));
        newTeamStats.put(Constants.TOTAL_STOPPED, new ScoutValue(totalStopped));
        newTeamStats.put(Constants.TOTAL_DIDNT_SHOW_UP, new ScoutValue(totalDidntShowUp));

        newTeamStats.put(Constants.TOTAL_FOULS, new ScoutValue(totalFouls));
        newTeamStats.put(Constants.TOTAL_TECH_FOULS, new ScoutValue(totalTechFouls));
        newTeamStats.put(Constants.TOTAL_YELLOW_CARDS, new ScoutValue(totalYellowCards));
        newTeamStats.put(Constants.TOTAL_RED_CARDS, new ScoutValue(totalRedCards));

        newTeamStats.put(Constants.TOTAL_MATCHES, new ScoutValue(totalMatches));

        newTeamStats.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(team));

        statsDB.updateStats(newTeamStats);
    }

    public static void updateSuper(Set<Integer> matches, MatchScoutDB matchScoutDB, SuperScoutDB superScoutDB,
                            ScheduleDB scheduleDB, StatsDB statsDB)
    {
        Set<Integer> teams = new HashSet<>();
        for(Integer matchNum: matches)
        {
            Cursor match = scheduleDB.getMatch(matchNum);
            int team = match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE1));
            if(!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team,matchNum) != null)
            {

                updateTeam(team, matchScoutDB,superScoutDB,scheduleDB,statsDB);
                teams.add(team);
            }

            match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE2));
            if(!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team,matchNum) != null)
            {
                updateTeam(team,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                teams.add(team);
            }

            match.getInt(match.getColumnIndex(ScheduleDB.KEY_BLUE3));
            if(!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team,matchNum) != null)
            {
                updateTeam(team,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                teams.add(team);
            }

            match.getInt(match.getColumnIndex(ScheduleDB.KEY_RED1));
            if(!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team,matchNum) != null)
            {
                updateTeam(team,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                teams.add(team);
            }

            match.getInt(match.getColumnIndex(ScheduleDB.KEY_RED2));
            if(!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team,matchNum) != null)
            {
                updateTeam(team,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                teams.add(team);
            }

            match.getInt(match.getColumnIndex(ScheduleDB.KEY_RED3));
            if(!teams.contains(team) && matchScoutDB.getTeamMatchInfo(team,matchNum) != null)
            {
                updateTeam(team,matchScoutDB,superScoutDB,scheduleDB,statsDB);
                teams.add(team);
            }
        }
    }



    private static int set_start(Map<String, ScoutValue> teamStats, String key)
    {
        if(teamStats.containsKey(key))
            return teamStats.get(key).getInt();
        else
            return 0;
    }

    private static int[] set_start_array(Map<String, ScoutValue> teamStats, String type)
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
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_STARTED[i]);
                    break;
                case "reach":
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_AUTO_REACHED[i]);
                    break;
                case "cross":
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]);
                    break;
                case "teleop":
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]);
                    break;
                case "not":
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[i]);
                    break;
                case "teleop_points":
                    array[i] = set_start(teamStats, Constants.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i]);
                    break;
                case "seen":
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_SEEN[i]);
                    break;
                case "time":
                    array[i] = set_start(teamStats,Constants.TOTAL_DEFENSES_TELEOP_TIME[i]);
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

    private static void increment_array(Cursor cursor, int[] array, String defense2, String defense3, String defense4, String defense5, String type)
    {
        List defensesList = Arrays.asList(Constants.DEFENSES);

        int index;

        switch(type)
        {
            case "start":
                switch(cursor.getString(cursor.getColumnIndex(Constants.AUTO_START_POSITION)))
                {
                    case "Defense 1 (Low bar)":
                        index = Constants.LOW_BAR_INDEX;
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
                if(cursor.getString(cursor.getColumnIndex(Constants.AUTO_REACH_CROSS)).equals("Reach"))
                {
                    switch(cursor.getString(cursor.getColumnIndex(Constants.AUTO_START_POSITION)))
                    {
                        case "Defense 1 (Low bar)":
                            index = Constants.LOW_BAR_INDEX;
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
                if(cursor.getString(cursor.getColumnIndex(Constants.AUTO_REACH_CROSS)).equals("Cross"))
                {
                    switch(cursor.getString(cursor.getColumnIndex(Constants.AUTO_START_POSITION)))
                    {
                        case "Defense 1 (Low bar)":
                            index = Constants.LOW_BAR_INDEX;
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
                index = Constants.LOW_BAR_INDEX;
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));

                index = defensesList.indexOf(defense2);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));

                index = defensesList.indexOf(defense3);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));

                index = defensesList.indexOf(defense4);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));

                index = defensesList.indexOf(defense5);
                array[index] += stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                break;
            case "not":
                index = Constants.LOW_BAR_INDEX;
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense2);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense3);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense4);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4))) > 0) ? 0 : 1;

                index = defensesList.indexOf(defense5);
                array[index] += (stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5))) > 0) ? 0 : 1;
                break;
            case "teleop_points":
                if(cursor.getString(cursor.getColumnIndex(Constants.AUTO_REACH_CROSS)).equals("Cross"))
                {
                    int numCross = 0;
                    switch (cursor.getString(cursor.getColumnIndex(Constants.AUTO_START_POSITION)))
                    {
                        case "Defense 1 (Low bar)":
                            index = Constants.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 2":
                            index = Constants.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 3":
                            index = Constants.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 4":
                            index = Constants.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 1) ? 1 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 2) ? 2 : numCross;
                            break;
                        case "Defense 5":
                            index = Constants.LOW_BAR_INDEX;
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense2);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense3);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense4);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));
                            array[index] += (numCross > 2) ? 2 : numCross;

                            index = defensesList.indexOf(defense5);
                            numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                            array[index] += (numCross > 1) ? 1 : numCross;
                            break;
                    }
                }
                else
                {
                    index = Constants.LOW_BAR_INDEX;
                    int numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense2);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense3);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense4);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));
                    array[index] += (numCross > 2) ? 2 : numCross;

                    index = defensesList.indexOf(defense5);
                    numCross = stringToCount(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                    array[index] += (numCross > 2) ? 2 : numCross;
                }
                break;
            case "seen":
                index = Constants.LOW_BAR_INDEX;
                array[index] ++;

                index = defensesList.indexOf(defense2);
                array[index] ++;

                index = defensesList.indexOf(defense3);
                array[index] ++;

                index = defensesList.indexOf(defense4);
                array[index] ++;

                index = defensesList.indexOf(defense5);
                array[index] ++;
                break;
            case "time":
                index = Constants.LOW_BAR_INDEX;
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_1)));

                index = defensesList.indexOf(defense2);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_2)));

                index = defensesList.indexOf(defense3);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_3)));

                index = defensesList.indexOf(defense4);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_4)));

                index = defensesList.indexOf(defense5);
                array[index] += stringToTime(cursor.getString(cursor.getColumnIndex(Constants.TELEOP_DEFENSE_5)));
                break;
        }
    }

    private static int stringToCount(String jsonText)
    {
        try {
            JSONArray jsonArray = new JSONArray(jsonText);
            return jsonArray.length();
        } catch (JSONException e) {
        }
        return 0;
    }

    private static int stringToTime(String jsonText)
    {
        int sum = 0;
        try{
            JSONArray jsonArray = new JSONArray(jsonText);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                String tmp = jsonArray.getString(i);
                for(int j = 0; j < Constants.TELEOP_DEFENSE_TIMES.length; j++)
                {
                    if(tmp.equals(Constants.TELEOP_DEFENSE_TIMES[j]))
                    {
                        sum += Constants.TELEOP_DEFENSE_TIMES_VALUE[j];
                        break;
                    }
                }
            }
            if(jsonArray.length() > 0) {
                sum /= jsonArray.length();
            }
        } catch (JSONException e){
        }
        return sum;
    }


}
