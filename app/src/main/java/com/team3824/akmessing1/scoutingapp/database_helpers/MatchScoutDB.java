package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Database helper for match scouting data
 */
public class MatchScoutDB extends SQLiteOpenHelper {

    // Initial Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_MATCH_NUMBER = "match_number";
    public static final String KEY_TEAM_NUMBER = "team_number";
    private static final String KEY_LAST_UPDATED = "last_updated";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private static SimpleDateFormat dateFormat;
    private final String TAG = "MatchScoutDB";
    private String tableName;

    private String eventId;

    /**
     * @param context
     * @param eventID The ID for the event based on FIRST and The Blue Alliance
     */
    public MatchScoutDB(Context context, String eventID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "matchScouting_" + eventID;
        eventId = eventID;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    /**
     * Creates new database table if one does not exist
     *
     * @param db The database to add the table to
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryString = "CREATE TABLE IF NOT EXISTS " + tableName +
                "( " + KEY_ID + " TEXT PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_MATCH_NUMBER + " INTEGER NOT NULL," +
                " " + KEY_TEAM_NUMBER + " INTEGER NOT NULL," +
                " " + KEY_LAST_UPDATED + " DATETIME NOT NULL);";
        db.execSQL(queryString);
    }

    /**
     * Upgrades the table by dropping it and creating a new one
     *
     * @param db         The database to update
     * @param oldVersion Old version number
     * @param newVersion New version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        this.onCreate(db);
    }

    /**
     * Adds a new column to the table
     *
     * @param columnName Name of the new column
     * @param columnType What type the new column should be
     */
    private void addColumn(String columnName, String columnType) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    /**
     * Update the data in the database for a specific match and team
     *
     * @param map New data to add
     */
    public void updateMatch(ScoutMap map) {
        SQLiteDatabase db = getWritableDatabase();

        // Grab anything that isn't being changed
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ? AND " + KEY_MATCH_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(map.get(KEY_TEAM_NUMBER).getInt()), String.valueOf(map.get(KEY_MATCH_NUMBER).getInt())}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit
        String[] columnNames = cursor.getColumnNames();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                if (!map.containsKey(cursor.getColumnName(i))) {
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_INTEGER:
                            map.put(cursor.getColumnName(i), cursor.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            map.put(cursor.getColumnName(i), cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            map.put(cursor.getColumnName(i), cursor.getString(i));
                            break;
                    }
                }
            }
        }

        // Make sure the last updated time gets updated
        map.remove(KEY_LAST_UPDATED);

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
        for (Map.Entry<String, ScoutValue> entry : map.entrySet()) {
            String column = entry.getKey();
            ScoutValue sv = entry.getValue();
            // If column doesn't exist then add it
            if (Arrays.asList(columnNames).indexOf(column) == -1) {
                switch (sv.getType()) {
                    case FLOAT_TYPE:
                        addColumn(column, "REAL");
                        break;
                    case INT_TYPE:
                        addColumn(column, "INTEGER");
                        break;
                    case STRING_TYPE:
                        addColumn(column, "TEXT");
                        break;
                }
            }

            switch (sv.getType()) {
                case FLOAT_TYPE:
                    Log.d(TAG, column + "->" + sv.getFloat());
                    cvs.put(column, sv.getFloat());
                    break;
                case INT_TYPE:
                    Log.d(TAG, column + "->" + sv.getInt());
                    cvs.put(column, sv.getInt());
                    break;
                case STRING_TYPE:
                    Log.d(TAG, column + "->" + sv.getString());
                    cvs.put(column, sv.getString());
                    break;
            }
        }
        db.replace(tableName, null, cvs);
    }

    /**
     * Get all the scouting information about a specific match
     *
     * @param matchNum Number of the match to collect the data about
     * @return Cursor holding the match data
     */
    public Cursor getMatchInfo(int matchNum) {
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

    /**
     * Get all the data about a specific team
     *
     * @param teamNum The number of the team
     * @return Cursor with all the data about a specific team
     */
    public Cursor getTeamInfo(int teamNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNum)}, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_MATCH_NUMBER, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * Get all the data about a specific team in a specific match
     *
     * @param teamNum  The team number
     * @param matchNum The match number
     * @return Map with all the data for that team in that match
     */
    public ScoutMap getTeamMatchInfo(int teamNum, int matchNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ? AND " + KEY_MATCH_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNum), String.valueOf(matchNum)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        // First time for a match and team number combination
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "No rows came back");
            return null;
        }

        // Setup map
        ScoutMap map = new ScoutMap();
        cursor.moveToFirst();
        for (int i = 1; i < cursor.getColumnCount(); i++) {
            switch (cursor.getType(i)) {
                case Cursor.FIELD_TYPE_FLOAT:
                    Log.d(TAG, cursor.getColumnName(i) + "<-" + cursor.getFloat(i));
                    map.put(cursor.getColumnName(i), cursor.getFloat(i));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    Log.d(TAG, cursor.getColumnName(i) + "<-" + cursor.getInt(i));
                    map.put(cursor.getColumnName(i), cursor.getInt(i));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    Log.d(TAG, cursor.getColumnName(i) + "<-" + cursor.getString(i));
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                    break;
            }
        }
        return map;
    }

    /**
     * Get all the teams that have new data since a specific time
     *
     * @param lastUpdated The time cut off
     * @return List of the team numbers
     */
    public ArrayList<Integer> getTeamsUpdatedSince(String lastUpdated) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (lastUpdated == null || lastUpdated.equals("")) {
            cursor = db.query(true, // distinct
                    tableName, // a. table
                    new String[]{KEY_TEAM_NUMBER}, // b. column names
                    null, // c. selections
                    null, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        } else {
            cursor = db.query(true, // distinct
                    tableName, // a. table
                    new String[]{KEY_TEAM_NUMBER}, // b. column names
                    KEY_LAST_UPDATED + " > ?", // c. selections
                    new String[]{lastUpdated}, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit
        }
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        ArrayList<Integer> teamNumbers = new ArrayList<>();
        if (cursor.getCount() > 0) {
            do {
                teamNumbers.add(cursor.getInt(0));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        return teamNumbers;
    }

    /**
     * Get the match data for a team that has been updated after a certain time
     *
     * @param teamNumber The team number
     * @param since      The cutoff time
     * @return the match data for a team that has been updated after a certain time
     */
    public Cursor getTeamInfoSince(int teamNumber, String since) {
        if (since.equals("") || since == null) {
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
                KEY_MATCH_NUMBER, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * Get all the match data that has been updated since a specific date and time
     *
     * @param since The specific date and time
     * @return The match data
     */
    public Cursor getAllInfoSince(String since) {
        if (since.equals("") || since == null) {
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

        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * Get all the match data
     *
     * @return The match data
     */
    @SuppressWarnings("WeakerAccess")
    public Cursor getAllInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, // distinct
                tableName, // a. table
                null, // b. column names
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

    /**
     * Resets the table
     */
    //TODO: use onUpgrade?
    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + tableName;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + tableName +
                "( " + KEY_ID + " TEXT PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_MATCH_NUMBER + " INTEGER NOT NULL," +
                " " + KEY_TEAM_NUMBER + " INTEGER NOT NULL," +
                " " + KEY_LAST_UPDATED + " DATETIME NOT NULL);";
        db.execSQL(query);
    }

    /**
     * Drops the table
     */
    public void remove() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + tableName;
        db.execSQL(query);
    }

    public String getEventID()
    {
        return eventId;
    }

}