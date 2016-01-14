package com.team3824.akmessing1.scoutingapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.team3824.akmessing1.scoutingapp.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.StatsDB;

import java.util.ArrayList;
import java.util.HashMap;


public class Sync extends IntentService {
    public Sync()
    {
        super("Sync");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

    }
}
