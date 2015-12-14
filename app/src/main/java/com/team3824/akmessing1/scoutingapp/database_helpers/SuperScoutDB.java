package com.team3824.akmessing1.scoutingapp.database_helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.team3824.akmessing1.scoutingapp.ScoutValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Database helper for match scouting data
public class SuperScoutDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private String TAG = "SuperScoutDB";

    // Initial Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_MATCH_NUMBER = "_id";
    public static final String KEY_BLUE1 = "blue1";
    public static final String KEY_BLUE2 = "blue2";
    public static final String KEY_BLUE3 = "blue3";
    public static final String KEY_RED1 = "red1";
    public static final String KEY_RED2 = "red2";
    public static final String KEY_RED3 = "red3";
    public static final String KEY_LAST_UPDATED = "last_updated";


    private String tableName;
    private static SimpleDateFormat dateFormat;


    public SuperScoutDB(Context context, String eventID)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "superScouting_"+eventID;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String queryString = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_ID+" INTEGER PRIMARY KEY UNIQUE NOT NULL,"+
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

    // Add column to table
    public void addColumn(String columnName, String columnType)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
    }

    // Store data in the database for a specific match and team
    public void updateMatch(Map<String, ScoutValue> map)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_MATCH_NUMBER + " = ?", // c. selections
                new String[]{String.valueOf(map.get(KEY_MATCH_NUMBER).getInt())}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        String[] columnNames = cursor.getColumnNames();

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
        db.replace(tableName,null,cvs);
        db.close();
    }

    // Get all the scouting information about a specific match
    public Map<String, ScoutValue> getMatchInfo(int matchNum)
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
        if (cursor == null || cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();
        // Setup map
        Map<String, ScoutValue> map = new HashMap<>();
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
        db.close();
        return map;
    }



    public ArrayList<Integer> getMatchesUpdatedSince(String lastUpdated)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(true, // distinct
                tableName, // a. table
                new String[]{KEY_MATCH_NUMBER}, // b. column names
                KEY_LAST_UPDATED+" > ?", // c. selections
                new String[]{lastUpdated}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if(cursor == null)
        {
            return null;
        }
        cursor.moveToFirst();
        ArrayList<Integer> matchNumbers = new ArrayList<>();
        do{
            matchNumbers.add(cursor.getInt(0));
            cursor.moveToNext();
        }while(!cursor.isAfterLast());
        db.close();
        return matchNumbers;
    }

    public Cursor getAllMatches()
    {
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

    public Cursor getAllMatchesSince(String since)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(tableName, // a. table
                null, // b. column names
                KEY_LAST_UPDATED+" > ?", // c. selections
                new String[]{since}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

}