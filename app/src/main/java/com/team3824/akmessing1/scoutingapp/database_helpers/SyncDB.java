package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    public static final String KEY_LAST_UPDATED = "last_updated";

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
                " "+KEY_LAST_UPDATED+" DATETIME NOT NULL);";
        db.execSQL(queryString);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+tableName);
        this.onCreate(db);
    }

    public void updateSync(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cvs = new ContentValues();
        cvs.put(KEY_ID,id);
        cvs.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
        db.replace(tableName, null, cvs);
        db.close();
    }

    public String getLastUpdated(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true,
                tableName,
                new String[]{KEY_LAST_UPDATED},
                KEY_ID+" = ?", // c. selections
                new String[]{id}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null || cursor.getCount() == 0)
            return "";
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(KEY_LAST_UPDATED));
    }
}
