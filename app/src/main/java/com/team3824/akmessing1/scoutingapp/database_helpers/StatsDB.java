package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class StatsDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private final String TAG = "StatsDB";

    // Initial Table Columns names
    private static final String KEY_ID = "_id"; // _id is needed for updating
    // The team number is going to be the id, but another variable is set up for convenience
    public static final String KEY_TEAM_NUMBER = "_id";
    public static final String KEY_PICKED = "picked";
    public static final String KEY_DNP = "dnp";
    private final String KEY_LAST_UPDATED = "last_updated";


    private String tableName;
    private static SimpleDateFormat dateFormat;

    /**
     * @param context
     * @param eventID The ID for the event based on FIRST and The Blue Alliance
     */
    public StatsDB(Context context, String eventID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "stats_" + eventID;
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
                "( " + KEY_ID + " INTEGER PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_PICKED + " INTEGER NOT NULL," +
                " " + KEY_DNP + " INTEGER NOT NULL," +
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

    // Check if column exists
    public boolean hasColumn(String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit
        return cursor.getColumnIndex(columnName) != -1;
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
            Log.d(TAG, e.getMessage());
        }
    }

    /**
     * @param array
     */
    public void createTeamList(JSONArray array) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                int teamNumber = jsonObject.getInt("team_number");
                ContentValues values = new ContentValues();
                values.put(KEY_TEAM_NUMBER, teamNumber);
                values.put(KEY_PICKED, 0);
                values.put(KEY_DNP, 0);
                values.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
                db.insert(tableName, null, values);
            } catch (JSONException e) {
                Log.d(TAG, "Exception: " + e.toString());
            }
        }
    }

    /**
     * @param teamNumber
     */
    public void addTeamNumber(int teamNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numEntries = (int) DatabaseUtils.queryNumEntries(db, tableName);
        ContentValues values = new ContentValues();
        values.put(KEY_TEAM_NUMBER, teamNumber);
        values.put(KEY_PICKED, 0);
        values.put(KEY_DNP, 0);
        values.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
        db.insert(tableName, null, values);
    }

    /**
     * @param teamNumber
     */
    public void removeTeamNumber(int teamNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, KEY_TEAM_NUMBER + " = ?", new String[]{String.valueOf(teamNumber)});
    }

    /**
     * @param map
     */
    public void updateStats(ScoutMap map) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ? ", // c. selections
                new String[]{map.getString(KEY_TEAM_NUMBER)}, // d. selections args
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
        Log.d(TAG, dateFormat.format(new Date()));
        for (Map.Entry<String, ScoutValue> entry : map.entrySet()) {
            String column = entry.getKey();
            ScoutValue sv = entry.getValue();
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
     * @return
     */
    public Cursor getStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_TEAM_NUMBER, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * @param since
     * @return
     */
    public Cursor getStatsSince(String since) {
        if (since.equals("") || since == null)
            return getStats();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_LAST_UPDATED + " > ?", // c. selections
                new String[]{since}, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_TEAM_NUMBER, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * @param teamNum
     * @return
     */
    public ScoutMap getTeamStats(int teamNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNum)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null) {
            return null;
        }
        ScoutMap map = new ScoutMap();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
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
                        if (cursor.getString(i) == null) {
                            Log.d(TAG, cursor.getColumnName(i) + "<-\"\"");
                            map.put(cursor.getColumnName(i), "");
                        } else {
                            Log.d(TAG, cursor.getColumnName(i) + "<-" + cursor.getString(i));
                            map.put(cursor.getColumnName(i), cursor.getString(i));
                        }
                        break;
                }
            }
        }
        return map;
    }

    /**
     *
     * @return
     */
    public ArrayList<Integer> getTeamNumbers() {
        ArrayList<Integer> teamNumbers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                new String[]{KEY_TEAM_NUMBER}, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null || cursor.getCount() == 0) {
            Log.d(TAG, "No rows came back");
            return null;
        }
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++) {
            teamNumbers.add(cursor.getInt(cursor.getColumnIndex(KEY_TEAM_NUMBER)));
            cursor.moveToNext();
        }

        return teamNumbers;
    }

    /**
     * @return
     */
    public String getLastUpdatedTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                new String[]{KEY_LAST_UPDATED}, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_LAST_UPDATED + " DESC", // g. order by
                "1"); // h. limit
        if (cursor == null)
            return null;

        if (cursor.getCount() == 0)
            return "";

        cursor.moveToFirst();

        return cursor.getString(0);
    }

    /**
     * @param teamNum
     * @return
     */
    public String getLastUpdatedTime(int teamNum) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                new String[]{KEY_LAST_UPDATED}, // b. column names
                KEY_TEAM_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNum)}, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_LAST_UPDATED + " DESC", // g. order by
                "1"); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        else
            return null;

        return cursor.getString(0);
    }

    /**
     * Resets the table
     */
    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Integer> teams = new ArrayList<>();
        Cursor cursor = db.query(tableName, // a. table
                new String[]{KEY_TEAM_NUMBER}, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                KEY_TEAM_NUMBER + " DESC", // g. order by
                null); // h. limit
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            teams.add(cursor.getInt(cursor.getColumnIndex(KEY_TEAM_NUMBER)));
            cursor.moveToNext();
        }

        String query = "DROP TABLE " + tableName;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + tableName +
                "( " + KEY_ID + " INTEGER PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_PICKED + " INTEGER NOT NULL," +
                " " + KEY_DNP + " INTEGER NOT NULL," +
                " " + KEY_LAST_UPDATED + " DATETIME NOT NULL);";
        db.execSQL(query);
        for (int i = 0; i < teams.size(); i++) {
            int teamNumber = teams.get(i);
            ContentValues values = new ContentValues();
            values.put(KEY_TEAM_NUMBER, teamNumber);
            values.put(KEY_PICKED, 0);
            values.put(KEY_DNP, 0);
            values.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
            db.insert(tableName, null, values);
        }
    }

    /**
     * Drops the table
     */
    public void remove() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + tableName;
        db.execSQL(query);
    }
}
