package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Database helper for match scouting data
public class MatchScoutDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private String TAG = "MatchScoutDB";

    // Initial Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_MATCH_NUMBER = "match_number";
    public static final String KEY_TEAM_NUMBER = "team_number";
    public static final String KEY_LAST_UPDATED = "last_updated";


    private String tableName;
    private static SimpleDateFormat dateFormat;


    public MatchScoutDB(Context context, String eventID)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "matchScouting_"+eventID;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String queryString = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_ID+" TEXT PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_MATCH_NUMBER+" INTEGER NOT NULL,"+
                " "+KEY_TEAM_NUMBER+" INTEGER NOT NULL,"+
                " "+KEY_LAST_UPDATED+" DATETIME NOT NULL);";
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        this.onCreate(db);
    }

    // Add column to table
    public void addColumn(String columnName, String columnType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        }
        catch(SQLException e)
        {
            e.getMessage();
        }
    }

    // Store data in the database for a specific match and team
    public void updateMatch(Map<String, ScoutValue> map)
    {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ? AND " + KEY_MATCH_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(map.get(KEY_TEAM_NUMBER).getInt()), String.valueOf(map.get(KEY_MATCH_NUMBER).getInt())}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit
        String[] columnNames = cursor.getColumnNames();
        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (!map.containsKey(cursor.getColumnName(i))) {
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            map.put(cursor.getColumnName(i), new ScoutValue(cursor.getInt(i)));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            map.put(cursor.getColumnName(i), new ScoutValue(cursor.getFloat(i)));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            map.put(cursor.getColumnName(i), new ScoutValue(cursor.getString(i)));
                            break;
                    }
                }
            }
        }

        // Make sure the last updated time gets updated
        map.remove(KEY_LAST_UPDATED);

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
        for(Map.Entry<String, ScoutValue> entry : map.entrySet())
        {
            String column = entry.getKey();
            ScoutValue sv = entry.getValue();
            if(Arrays.asList(columnNames).indexOf(column) == -1)
            {
                switch(sv.getType())
                {
                    case FLOAT_TYPE:
                        addColumn(column,"REAL");
                        break;
                    case INT_TYPE:
                        addColumn(column,"INTEGER");
                        break;
                    case STRING_TYPE:
                        addColumn(column,"TEXT");
                        break;
                }
            }

            switch(sv.getType())
            {
                case FLOAT_TYPE:
                    Log.d(TAG,column + "->" + sv.getFloat());
                    cvs.put(column,sv.getFloat());
                    break;
                case INT_TYPE:
                    Log.d(TAG,column + "->" + sv.getInt());
                    cvs.put(column,sv.getInt());
                    break;
                case STRING_TYPE:
                    Log.d(TAG,column + "->" + sv.getString());
                    cvs.put(column,sv.getString());
                    break;
            }
        }
        db.replace(tableName, null, cvs);
    }

    // Get all the scouting information about a specific match
    public Cursor getMatchInfo(int matchNum)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
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

    public Cursor getTeamInfo(int teamNum)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNum)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    // Function for getting the scouting information about a specific team in a specific match
    // Used in restoring field values
    public Map<String, ScoutValue> getTeamMatchInfo(int teamNum, int matchNum)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ? AND " + KEY_MATCH_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNum),String.valueOf(matchNum)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        // First time for a match and team number combination
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG,"No rows came back");
            return null;
        }

        // Setup map
        Map<String, ScoutValue> map = new HashMap<>();
        cursor.moveToFirst();
        for(int i = 1; i < cursor.getColumnCount(); i++)
        {
            switch(cursor.getType(i)) {
                case Cursor.FIELD_TYPE_FLOAT:
                    Log.d(TAG,cursor.getColumnName(i) + "<-" + cursor.getFloat(i));
                    map.put(cursor.getColumnName(i), new ScoutValue(cursor.getFloat(i)));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    Log.d(TAG,cursor.getColumnName(i) + "<-" + cursor.getInt(i));
                    map.put(cursor.getColumnName(i), new ScoutValue(cursor.getInt(i)));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    Log.d(TAG,cursor.getColumnName(i) + "<-" + cursor.getString(i));
                    map.put(cursor.getColumnName(i), new ScoutValue(cursor.getString(i)));
                    break;
            }
        }
        return map;
    }

    public ArrayList<Integer> getTeamsUpdatedSince(String lastUpdated)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if(lastUpdated == null || lastUpdated.equals(""))
        {
            cursor = db.query(true, // distinct
                    tableName, // a. table
                    new String[]{KEY_TEAM_NUMBER}, // b. column names
                    null, // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        }
        else
        {
            cursor = db.query(true, // distinct
                    tableName, // a. table
                    new String[]{KEY_TEAM_NUMBER}, // b. column names
                    KEY_LAST_UPDATED+" > ?", // c. selections
                    new String[]{lastUpdated}, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        }
        if(cursor == null)
        {
            return null;
        }
        cursor.moveToFirst();
        ArrayList<Integer> teamNumbers = new ArrayList<>();
        if(cursor.getCount() > 0) {
            do {
                teamNumbers.add(cursor.getInt(0));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return teamNumbers;
    }

    public Cursor getTeamInfoSince(int teamNumber, String since)
    {
        if(since.equals("") || since == null)
        {
            return getTeamInfo(teamNumber);
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, // distinct
                    tableName, // a. table
                    null, // b. column names
                    KEY_TEAM_NUMBER + " = ?", // c. selections
                    new String[]{String.valueOf(teamNumber)}, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getAllInfoSince(String since)
    {
        if(since.equals("") || since == null)
        {
            return getAllInfo();
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, // distinct
                    tableName, // a. table
                    null, // b. column names
                    KEY_LAST_UPDATED + " > ?", // c. selections
                    new String[]{since}, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit

        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getAllInfo()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, // distinct
                tableName, // a. table
                null, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public void reset()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE "+tableName;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_ID+" TEXT PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_MATCH_NUMBER+" INTEGER NOT NULL,"+
                " "+KEY_TEAM_NUMBER+" INTEGER NOT NULL,"+
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