package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Database helper for the drive team feedback table
 */
public class DriveTeamFeedbackDB extends SQLiteOpenHelper {
    // Initial Table Columns names
    private static final String KEY_ID = "_id";
    public static final String KEY_TEAM_NUMBER = "_id";
    private static final String KEY_LAST_UPDATED = "last_updated";
    public static final String KEY_COMMENTS = "comments";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private static SimpleDateFormat dateFormat;
    private String TAG = "DriveTeamFeedbackDB";
    private String tableName;

    /**
     * @param context
     * @param eventID The ID for the event based on FIRST and The Blue Alliance
     */
    public DriveTeamFeedbackDB(Context context, String eventID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "driveteamFeedback_" + eventID;
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
                "( " + KEY_ID + " INT PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_COMMENTS + " TEXT NOT NULL," +
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
    public void addColumn(String columnName, String columnType) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    /**
     * Adds a comment for the team
     *
     * @param teamNumber The number of the team for which a comment is added
     * @param comment    The comment to be added
     */
    public void updateComments(int teamNumber, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Make sure the last updated time gets updated
        ContentValues cvs = new ContentValues();
        cvs.put(KEY_TEAM_NUMBER, teamNumber);
        cvs.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
        cvs.put(KEY_COMMENTS, comment);
        db.replace(tableName, null, cvs);
    }

    /**
     * Gets the comments for the given team
     *
     * @param teamNumber
     * @return The comments for the given team
     */
    public String getComments(int teamNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(teamNumber)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit

        if (cursor == null || cursor.getCount() == 0)
            return "";

        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_COMMENTS));
    }

    /**
     * Gets all the comments in the table
     *
     * @return A cursor with all the comments in the table
     */
    public Cursor getAllComments() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    /**
     * Gets all the comments since a specific date and time
     *
     * @param since The data and time to get comments updated afterward
     * @return A cursor with all the comments since the specific data and time
     */
    public Cursor getAllCommentsSince(String since) {
        if (since.equals("")) {
            return getAllComments();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
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
     * Resets the table
     */
    //TODO: use onUpgrade?
    public void reset() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE " + tableName;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS " + tableName +
                "( " + KEY_ID + " INT PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_COMMENTS + " TEXT NOT NULL," +
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
}

