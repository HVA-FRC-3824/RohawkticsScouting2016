package com.team3824.akmessing1.scoutingapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

import java.util.ArrayList;
import java.util.HashMap;

public class Aggregate extends IntentService {
    public Aggregate()
    {
        super("Aggregate");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("eventID", "");
        MatchScoutDB matchScoutDB = new MatchScoutDB(context,eventID);
        StatsDB statsDB = new StatsDB(context,eventID);

        String lastUpdated = statsDB.getLastUpdatedTime();

        ArrayList<Integer> teamsUpdated = matchScoutDB.getTeamsUpdatedSince(lastUpdated);
        for(int i = 0; i < teamsUpdated.size(); i++)
        {
            Cursor teamCursor = matchScoutDB.getTeamInfo(teamsUpdated.get(i));

            // Calculate metrics

            HashMap<String, ScoutValue> map = new HashMap<>();
            map.put(StatsDB.KEY_TEAM_NUMBER,new ScoutValue(teamsUpdated.get(i)));

            // Insert metrics in map

            statsDB.updateStats(map);
        }
    }
}
