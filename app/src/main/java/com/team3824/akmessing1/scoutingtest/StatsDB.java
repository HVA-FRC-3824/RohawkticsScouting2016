package com.team3824.akmessing1.scoutingtest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by akmessing1 on 11/8/15.
 */
public class StatsDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "RohawkticsDB";
    private String TAG = "StatsDB";

    // Initial Table Columns names
    public static final String KEY_ID = "_id"; // _id is needed for updating
    // The team number is going to be the id, but another variable is set up for convenience
    public static final String KEY_TEAM_NUMBER = "_id";
    public static final String KEY_COMPUTED_FIRST_PICK_RANK = "computed_first_pick_rank";
    public static final String KEY_COMPUTED_SECOND_PICK_RANK = "computed_second_pick_rank";
    public static final String KEY_COMPUTED_THIRD_PICK_RANK = "computed_third_pick_rank";
    public static final String KEY_FIRST_PICK_RANK = "first_pick_rank";
    public static final String KEY_SECOND_PICK_RANK = "second_pick_rank";
    public static final String KEY_THIRD_PICK_RANK = "third_pick_rank";
    public static final String KEY_LAST_UPDATED = "last_updated";


    private String tableName;
    private static SimpleDateFormat dateFormat;

    public StatsDB(Context context, String eventID)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        tableName = "stats_"+eventID;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String queryString = "CREATE TABLE IF NOT EXISTS "+tableName +
                "( "+KEY_ID+" INTEGER PRIMARY KEY UNIQUE NOT NULL,"+
                " "+KEY_COMPUTED_FIRST_PICK_RANK+" INTEGER NOT NULL,"+
                " "+KEY_COMPUTED_SECOND_PICK_RANK+" INTEGER NOT NULL,"+
                " "+KEY_COMPUTED_THIRD_PICK_RANK+" INTEGER NOT NULL,"+
                " "+KEY_FIRST_PICK_RANK+" INTEGER NOT NULL,"+
                " "+KEY_SECOND_PICK_RANK+" INTEGER NOT NULL,"+
                " "+KEY_THIRD_PICK_RANK+" INTEGER NOT NULL,"+
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

    public void createTeamList(JSONArray array)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        for(int i = 0; i < array.length(); i++) {
            try {
                JSONObject jsonObject = array.getJSONObject(i);
                int teamNumber = jsonObject.getInt("team_number");
                ContentValues values = new ContentValues();
                values.put(KEY_TEAM_NUMBER,teamNumber);
                values.put(KEY_COMPUTED_FIRST_PICK_RANK,i+1);
                values.put(KEY_COMPUTED_SECOND_PICK_RANK,i+1);
                values.put(KEY_COMPUTED_THIRD_PICK_RANK,i+1);
                values.put(KEY_FIRST_PICK_RANK,i+1);
                values.put(KEY_SECOND_PICK_RANK,i+1);
                values.put(KEY_THIRD_PICK_RANK,i+1);
                values.put(KEY_LAST_UPDATED, dateFormat.format(new Date()));
                db.insert(tableName,null,values);
            }catch (JSONException e) {
                Log.d(TAG, "Exception: " + e.toString());
            }
        }
        db.close();
    }

    public void updateStats(Map<String, ScoutValue> map)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor =db.query(tableName, // a. table
                null, // b. column names
                KEY_TEAM_NUMBER + " = ? ", // c. selections
                new String[]{String.valueOf(map.get(KEY_TEAM_NUMBER).getInt())}, // d. selections args
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
                    Log.d(TAG, column + "->" + sv.getFloat());
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

    public Cursor getStats()
    {
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

    public Cursor getTeamStats(int teamNum)
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
}
