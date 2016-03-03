package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncDB extends SQLiteOpenHelper {
    // Initial Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_MATCH_LAST_UPDATED = "match_last_updated";
    public static final String KEY_PIT_LAST_UPDATED = "pit_last_updated";
    public static final String KEY_SUPER_LAST_UPDATED = "super_last_updated";
    public static final String KEY_DRIVE_TEAM_LAST_UPDATED = "drive_team_last_updated";
    public static final String KEY_STATS_LAST_UPDATED = "stats_last_updated";
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private static SimpleDateFormat dateFormat;
    private String TAG = "SyncDB";
    private String tableName;

    /**
     * @param context
     * @param eventID The ID for the event based on FIRST and The Blue Alliance
     */
    public SyncDB(Context context, String eventID) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "sync_" + eventID;
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
                "( " + KEY_ID + " STRING PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_MATCH_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_PIT_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_SUPER_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_DRIVE_TEAM_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_STATS_LAST_UPDATED + " DATETIME NOT NULL);";
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
     *
     * @param id
     */
    public void updateMatchSync(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_ID, id);
        cvs.put(KEY_MATCH_LAST_UPDATED, dateFormat.format(new Date()));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        else
        {
            cvs.put(KEY_PIT_LAST_UPDATED, "");
            cvs.put(KEY_SUPER_LAST_UPDATED, "");
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, "");
            cvs.put(KEY_STATS_LAST_UPDATED, "");
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    /**
     *
     * @param id
     */
    public void updatePitSync(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_ID, id);
        cvs.put(KEY_PIT_LAST_UPDATED, dateFormat.format(new Date()));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        else
        {
            cvs.put(KEY_MATCH_LAST_UPDATED, "");
            cvs.put(KEY_SUPER_LAST_UPDATED, "");
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, "");
            cvs.put(KEY_STATS_LAST_UPDATED, "");
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    /**
     *
     * @param id
     */
    public void updateSuperSync(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_ID, id);
        cvs.put(KEY_SUPER_LAST_UPDATED, dateFormat.format(new Date()));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        else
        {
            cvs.put(KEY_MATCH_LAST_UPDATED, "");
            cvs.put(KEY_PIT_LAST_UPDATED, "");
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, "");
            cvs.put(KEY_STATS_LAST_UPDATED, "");
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    /**
     *
     * @param id
     */
    public void updateDriveTeamSync(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_ID, id);
        cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, dateFormat.format(new Date()));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        else
        {
            cvs.put(KEY_MATCH_LAST_UPDATED, "");
            cvs.put(KEY_PIT_LAST_UPDATED, "");
            cvs.put(KEY_SUPER_LAST_UPDATED, "");
            cvs.put(KEY_STATS_LAST_UPDATED, "");
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    /**
     *
     * @param id
     */
    public void updateStatsSync(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                "1"); // h. limit

        ContentValues cvs = new ContentValues();
        cvs.put(KEY_ID, id);
        cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
        }
        else
        {
            cvs.put(KEY_MATCH_LAST_UPDATED, "");
            cvs.put(KEY_PIT_LAST_UPDATED, "");
            cvs.put(KEY_SUPER_LAST_UPDATED, "");
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, "");
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    /**
     *
     * @param id
     * @return
     */
    public String getMatchLastUpdated(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_MATCH_LAST_UPDATED},
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED));
    }

    /**
     *
     * @param id
     * @return
     */
    public String getPitLastUpdated(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_PIT_LAST_UPDATED},
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED));
    }

    /**
     *
     * @param id
     * @return
     */
    public String getSuperLastUpdated(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_SUPER_LAST_UPDATED},
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED));
    }

    /**
     *
     * @param id
     * @return
     */
    public String getDriveTeamLastUpdated(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_DRIVE_TEAM_LAST_UPDATED},
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED));
    }

    /**
     *
     * @param id
     * @return
     */
    public String getStatsLastUpdated(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_STATS_LAST_UPDATED},
                KEY_ID + " = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED));
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
                "( " + KEY_ID + " STRING PRIMARY KEY UNIQUE NOT NULL," +
                " " + KEY_MATCH_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_PIT_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_SUPER_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_DRIVE_TEAM_LAST_UPDATED + " DATETIME NOT NULL," +
                " " + KEY_STATS_LAST_UPDATED + " DATETIME NOT NULL);";
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
