package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncDB extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private String TAG = "SyncDB";

    // Initial Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_MATCH_LAST_UPDATED = "match_last_updated";
    public static final String KEY_PIT_LAST_UPDATED = "pit_last_updated";
    public static final String KEY_SUPER_LAST_UPDATED = "super_last_updated";
    public static final String KEY_DRIVE_TEAM_LAST_UPDATED = "drive_team_last_updated";
    public static final String KEY_STATS_LAST_UPDATED = "stats_last_updated";

    private String tableName;
    private static SimpleDateFormat dateFormat;

    public SyncDB(Context context, String eventID)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "sync_"+eventID;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String queryString = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_ID+" STRING PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_MATCH_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_PIT_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_SUPER_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_DRIVE_TEAM_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_STATS_LAST_UPDATED+" DATETIME NOT NULL);";
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        this.onCreate(db);
    }

    public void updateMatchSync(String id)
    {
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
        cvs.put(KEY_ID,id);
        cvs.put(KEY_MATCH_LAST_UPDATED, dateFormat.format(new Date()));
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    public void updatePitSync(String id)
    {
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
        cvs.put(KEY_ID,id);
        cvs.put(KEY_PIT_LAST_UPDATED, dateFormat.format(new Date()));
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    public void updateSuperSync(String id)
    {
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
        cvs.put(KEY_ID,id);
        cvs.put(KEY_SUPER_LAST_UPDATED, dateFormat.format(new Date()));
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    public void updateDriveTeamSync(String id)
    {
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
        cvs.put(KEY_ID,id);
        cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, dateFormat.format(new Date()));
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    public void updateStatsSync(String id)
    {
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
        cvs.put(KEY_ID,id);
        cvs.put(KEY_STATS_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED)));
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            cvs.put(KEY_MATCH_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED)));
            cvs.put(KEY_PIT_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED)));
            cvs.put(KEY_SUPER_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED)));
            cvs.put(KEY_DRIVE_TEAM_LAST_UPDATED, cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED)));
        }
        db.replace(tableName, null, cvs);
        db.close();
    }

    public String getMatchLastUpdated(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_MATCH_LAST_UPDATED},
                KEY_ID+" = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_MATCH_LAST_UPDATED));
    }

    public String getPitLastUpdated(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_PIT_LAST_UPDATED},
                KEY_ID+" = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_PIT_LAST_UPDATED));
    }

    public String getSuperLastUpdated(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_SUPER_LAST_UPDATED},
                KEY_ID+" = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_SUPER_LAST_UPDATED));
    }
    public String getDriveTeamLastUpdated(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_DRIVE_TEAM_LAST_UPDATED},
                KEY_ID+" = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_DRIVE_TEAM_LAST_UPDATED));
    }

    public String getStatsLastUpdated(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_STATS_LAST_UPDATED},
                KEY_ID+" = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_STATS_LAST_UPDATED));
    }

    public void reset()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE "+tableName;
        db.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_ID+" STRING PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_MATCH_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_PIT_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_SUPER_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_DRIVE_TEAM_LAST_UPDATED+" DATETIME NOT NULL" +
                " "+KEY_STATS_LAST_UPDATED+" DATETIME NOT NULL);";
        db.execSQL(query);
    }

    public void remove()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DROP TABLE "+tableName;
        db.execSQL(query);
    }
}
