package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by akmessing1 on 10/17/15.
 */

public class ScheduleDB extends SQLiteOpenHelper
{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private String TAG = "ScheduleDB";

    // Table Columns names
    public static final String KEY_MATCH_NUMBER = "_id";
    public static final String KEY_BLUE1 = "blue1";
    public static final String KEY_BLUE2 = "blue2";
    public static final String KEY_BLUE3 = "blue3";
    public static final String KEY_RED1 = "red1";
    public static final String KEY_RED2 = "red2";
    public static final String KEY_RED3 = "red3";
    public static final String KEY_LAST_UPDATED = "last_updated";

    private static String[] COLUMNS = {KEY_MATCH_NUMBER,KEY_BLUE1,KEY_BLUE2,KEY_BLUE3,KEY_RED1,KEY_RED2,KEY_RED3,KEY_LAST_UPDATED};
    private String tableName;
    private static SimpleDateFormat dateFormat;


    public ScheduleDB(Context context, String eventID)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "schedule_"+eventID;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String queryString = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_MATCH_NUMBER+" INTEGER PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_BLUE1+" INTEGER NOT NULL,"+
                " "+KEY_BLUE2+" INTEGER NOT NULL,"+
                " "+KEY_BLUE3+" INTEGER NOT NULL,"+
                " "+KEY_RED1+" INTEGER NOT NULL,"+
                " "+KEY_RED2+" INTEGER NOT NULL,"+
                " "+KEY_RED3+" INTEGER NOT NULL,"+
                " "+KEY_LAST_UPDATED+" DATETIME NOT NULL);";
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        this.onCreate(db);
    }

    public void addMatch(int matchNum, int blue1, int blue2, int blue3, int red1, int red2, int red3)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MATCH_NUMBER, matchNum);
        values.put(KEY_BLUE1, blue1);
        values.put(KEY_BLUE2, blue2);
        values.put(KEY_BLUE3, blue3);
        values.put(KEY_RED1, red1);
        values.put(KEY_RED2, red2);
        values.put(KEY_RED3, red3);
        values.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
        db.replace(tableName, null, values);
        db.close();
    }

    public void removeMatch(int matchNum)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, KEY_MATCH_NUMBER + " = ?", new String[]{String.valueOf(matchNum)});
    }

    public Cursor getMatch(int matchNum)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(tableName, // a. table
                        COLUMNS, // b. column names
                        KEY_MATCH_NUMBER + " = ?", // c. selections
                        new String[]{String.valueOf(matchNum)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public void createSchedule(JSONArray array)
    {
        for(int i = 0; i < array.length(); i++)
        {
            try
            {
                JSONObject jsonObject = array.getJSONObject(i);
                if (jsonObject.getString("comp_level").equals("qm"))
                {
                    String matchNum = jsonObject.getString("match_number");
                    JSONObject alliances = jsonObject.getJSONObject("alliances");

                    JSONObject blue = alliances.getJSONObject("blue");
                    JSONArray blueTeams = blue.getJSONArray("teams");
                    String blue1 = blueTeams.getString(0).substring(3);
                    String blue2 = blueTeams.getString(1).substring(3);
                    String blue3 = blueTeams.getString(2).substring(3);

                    JSONObject red = alliances.getJSONObject("red");
                    JSONArray redTeams = red.getJSONArray("teams");
                    String red1 = redTeams.getString(0).substring(3);
                    String red2 = redTeams.getString(1).substring(3);
                    String red3 = redTeams.getString(2).substring(3);

                    addMatch(Integer.parseInt(matchNum),Integer.parseInt(blue1),Integer.parseInt(blue2),Integer.parseInt(blue3),Integer.parseInt(red1),Integer.parseInt(red2),Integer.parseInt(red3));
                }
            }catch (JSONException e) {
                Log.d(TAG, "Exception: " + e.toString());
            }
        }
    }

    public Cursor getSchedule()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(tableName, // a. table
                        COLUMNS, // b. column names
                        null, // c. selections
                        null, // d. selections args
                        null, // e. group by
                        null, // f. having
                        KEY_MATCH_NUMBER, // g. order by
                        null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getTeamsMatches(int teamNum)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(tableName, // a. table
                        COLUMNS, // b. column names
                        KEY_BLUE1 + " = ? or " + KEY_BLUE2 + " = ? or " + KEY_BLUE3 + " = ? or " + KEY_RED1 + " = ? or " + KEY_RED2 + " = ? or " + KEY_RED3 + " = ?", // c. selections
                        new String[]{String.valueOf(teamNum), String.valueOf(teamNum), String.valueOf(teamNum), String.valueOf(teamNum), String.valueOf(teamNum), String.valueOf(teamNum)}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        KEY_MATCH_NUMBER, // g. order by
                        null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public int getNumMatches()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int)DatabaseUtils.queryNumEntries(db, tableName);
    }

    public void reset()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE "+tableName;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_MATCH_NUMBER+" INTEGER PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_BLUE1+" INTEGER NOT NULL,"+
                " "+KEY_BLUE2+" INTEGER NOT NULL,"+
                " "+KEY_BLUE3+" INTEGER NOT NULL,"+
                " "+KEY_RED1+" INTEGER NOT NULL,"+
                " "+KEY_RED2+" INTEGER NOT NULL,"+
                " "+KEY_RED3+" INTEGER NOT NULL,"+
                " "+KEY_LAST_UPDATED+" DATETIME NOT NULL);";
        db.execSQL(query);
    }

    public void remove()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE "+tableName;
        db.execSQL(query);
    }
}
