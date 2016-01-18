package com.team3824.akmessing1.scoutingapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.team3824.akmessing1.scoutingapp.AggregateUtil;
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

public class AggregateService extends IntentService {
    private String TAG ="AggregateService";
    static Map<String, SchulzeMethod> rankingCalcs;

    public AggregateService()
    {
        super("AggregateService");
        rankingCalcs = new HashMap<>();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(TAG, "Aggregate Service Started");
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");
        PitScoutDB pitScoutDB = new PitScoutDB(context,eventID);
        SuperScoutDB superScoutDB = new SuperScoutDB(context, eventID);
        MatchScoutDB matchScoutDB = new MatchScoutDB(context, eventID);
        StatsDB statsDB = new StatsDB(context, eventID);

        AggregateUtil.aggregate(matchScoutDB, pitScoutDB, superScoutDB, statsDB, getApplicationContext(), eventID, true);

        statsDB.close();
        matchScoutDB.close();
        superScoutDB.close();
        Log.d(TAG, "Aggregate Service Finished");
    }
}
