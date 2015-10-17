package com.example.akmessing1.scoutingtest.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.akmessing1.scoutingtest.JsonUTF8Request;
import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.ScheduleDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MatchSchedule extends AppCompatActivity {
    private static final String TAG = "MatchSchedule";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_schedule);

        final TableLayout scheduleTable = (TableLayout)findViewById(R.id.scheduleTable);

        SharedPreferences sharedPreferences = getSharedPreferences( "appData", Context.MODE_PRIVATE );
        final String eventID = sharedPreferences.getString("event_id","");

        if(eventID == "")
        {
            Log.d(TAG,"No Event ID");
        }
        else
        {
            Log.d(TAG,"Event ID found");
            final ScheduleDB scheduleDB = new ScheduleDB(this,eventID);

            if(scheduleDB.getNumMatches() == 0) {
                Log.d(TAG,"Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/matches?X-TBA-App-Id=amessing:scoutingTest:v2";
                Log.d(TAG,"url: "+url);
                JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET,url, null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG,"Schedule received");
                        scheduleDB.createSchedule(response);
                        Cursor cursor = scheduleDB.getSchedule();

                        TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        trlp.setMargins(1, 1, 1, 1);

                        TableRow tr = new TableRow(getApplicationContext());
                        tr.setBackgroundColor(Color.BLACK);
                        tr.setLayoutParams(trlp);

                        TextView matchNum = new TextView(getApplicationContext());
                        matchNum.setBackgroundColor(Color.WHITE);
                        matchNum.setTextColor(Color.BLACK);
                        matchNum.setLayoutParams(trlp);
                        matchNum.setGravity(Gravity.CENTER_HORIZONTAL);
                        matchNum.setText("Match Number");

                        TextView blue1 = new TextView(getApplicationContext());
                        blue1.setBackgroundColor(Color.BLUE);
                        blue1.setTextColor(Color.BLACK);
                        blue1.setGravity(Gravity.CENTER_HORIZONTAL);
                        blue1.setLayoutParams(trlp);
                        blue1.setText("Blue 1");

                        TextView blue2 = new TextView(getApplicationContext());
                        blue2.setBackgroundColor(Color.BLUE);
                        blue2.setTextColor(Color.BLACK);
                        blue2.setGravity(Gravity.CENTER_HORIZONTAL);
                        blue2.setLayoutParams(trlp);
                        blue2.setText("Blue 2");

                        TextView blue3 = new TextView(getApplicationContext());
                        blue3.setBackgroundColor(Color.BLUE);
                        blue3.setTextColor(Color.BLACK);
                        blue3.setGravity(Gravity.CENTER_HORIZONTAL);
                        blue3.setLayoutParams(trlp);
                        blue3.setText("Blue 3");

                        TextView red1 = new TextView(getApplicationContext());
                        red1.setBackgroundColor(Color.RED);
                        red1.setTextColor(Color.BLACK);
                        red1.setGravity(Gravity.CENTER_HORIZONTAL);
                        red1.setLayoutParams(trlp);
                        red1.setText("Red 1");

                        TextView red2 = new TextView(getApplicationContext());
                        red2.setBackgroundColor(Color.RED);
                        red2.setTextColor(Color.BLACK);
                        red2.setGravity(Gravity.CENTER_HORIZONTAL);
                        red2.setLayoutParams(trlp);
                        red2.setText("Red 2");

                        TextView red3 = new TextView(getApplicationContext());
                        red3.setBackgroundColor(Color.RED);
                        red3.setTextColor(Color.BLACK);
                        red3.setGravity(Gravity.CENTER_HORIZONTAL);
                        red3.setLayoutParams(trlp);
                        red3.setText("Red 3");

                        tr.addView(matchNum);
                        tr.addView(blue1);
                        tr.addView(blue2);
                        tr.addView(blue3);
                        tr.addView(red1);
                        tr.addView(red2);
                        tr.addView(red3);
                        Log.d(TAG, "Row added - " + matchNum.getText() + " " + blue1.getText() + " " + blue2.getText() + " " + blue3.getText() + " " + red1.getText() + " " + red2.getText() + " " + red3.getText());
                        scheduleTable.addView(tr);
                        for(int i = 0; i < cursor.getCount(); i++)
                        {
                            tr = new TableRow(getApplicationContext());
                            tr.setBackgroundColor(Color.BLACK);
                            tr.setLayoutParams(trlp);

                            matchNum = new TextView(getApplicationContext());
                            matchNum.setBackgroundColor(Color.WHITE);
                            matchNum.setTextColor(Color.BLACK);
                            matchNum.setLayoutParams(trlp);
                            matchNum.setGravity(Gravity.CENTER_HORIZONTAL);
                            matchNum.setText(cursor.getString(0));

                            blue1 = new TextView(getApplicationContext());
                            String temp = cursor.getString(1);
                            if(temp.equals("3824"))
                            {
                                blue1.setBackgroundColor(Color.GREEN);
                            }
                            else
                            {
                                blue1.setBackgroundColor(Color.WHITE);
                            }
                            blue1.setTextColor(Color.BLACK);
                            blue1.setGravity(Gravity.CENTER_HORIZONTAL);
                            blue1.setLayoutParams(trlp);
                            blue1.setText(temp);

                            blue2 = new TextView(getApplicationContext());
                            temp = cursor.getString(2);
                            if(temp.equals("3824"))
                            {
                                blue2.setBackgroundColor(Color.GREEN);
                            }
                            else
                            {
                                blue2.setBackgroundColor(Color.WHITE);
                            }
                            blue2.setTextColor(Color.BLACK);
                            blue2.setGravity(Gravity.CENTER_HORIZONTAL);
                            blue2.setLayoutParams(trlp);
                            blue2.setText(temp);

                            blue3 = new TextView(getApplicationContext());
                            temp = cursor.getString(3);
                            if(temp.equals("3824"))
                            {
                                blue3.setBackgroundColor(Color.GREEN);
                            }
                            else
                            {
                                blue3.setBackgroundColor(Color.WHITE);
                            }
                            blue3.setTextColor(Color.BLACK);
                            blue3.setGravity(Gravity.CENTER_HORIZONTAL);
                            blue3.setLayoutParams(trlp);
                            blue3.setText(temp);

                            red1 = new TextView(getApplicationContext());
                            temp = cursor.getString(4);
                            if(temp.equals("3824"))
                            {
                                red1.setBackgroundColor(Color.GREEN);
                            }
                            else
                            {
                                red1.setBackgroundColor(Color.WHITE);
                            }
                            red1.setTextColor(Color.BLACK);
                            red1.setGravity(Gravity.CENTER_HORIZONTAL);
                            red1.setLayoutParams(trlp);
                            red1.setText(cursor.getString(4));

                            red2 = new TextView(getApplicationContext());
                            temp = cursor.getString(5);
                            if(temp.equals("3824"))
                            {
                                red2.setBackgroundColor(Color.GREEN);
                            }
                            else
                            {
                                red2.setBackgroundColor(Color.WHITE);
                            }
                            red2.setTextColor(Color.BLACK);
                            red2.setGravity(Gravity.CENTER_HORIZONTAL);
                            red2.setLayoutParams(trlp);
                            red2.setText(temp);

                            red3 = new TextView(getApplicationContext());
                            temp = cursor.getString(6);
                            if(temp.equals("3824"))
                            {
                                red3.setBackgroundColor(Color.GREEN);
                            }
                            else
                            {
                                red3.setBackgroundColor(Color.WHITE);
                            }
                            red3.setTextColor(Color.BLACK);
                            red3.setGravity(Gravity.CENTER_HORIZONTAL);
                            red3.setLayoutParams(trlp);
                            red3.setText(temp);

                            tr.addView(matchNum);
                            tr.addView(blue1);
                            tr.addView(blue2);
                            tr.addView(blue3);
                            tr.addView(red1);
                            tr.addView(red2);
                            tr.addView(red3);
                            Log.d(TAG, "Row added - " + matchNum.getText() + " " + blue1.getText() + " " + blue2.getText() + " " + blue3.getText() + " " + red1.getText() + " " + red2.getText() + " " + red3.getText());
                            scheduleTable.addView(tr);
                            cursor.moveToNext();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());

                    }
                });

                queue.add(jsonReq);

            }
            else
            {
                Log.d(TAG, "Table not empty");
                Cursor cursor = scheduleDB.getSchedule();

                TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                trlp.setMargins(1, 1, 1, 1);

                TableRow tr = new TableRow(getApplicationContext());
                tr.setBackgroundColor(Color.BLACK);
                tr.setLayoutParams(trlp);

                TextView matchNum = new TextView(getApplicationContext());
                matchNum.setBackgroundColor(Color.WHITE);
                matchNum.setTextColor(Color.BLACK);
                matchNum.setLayoutParams(trlp);
                matchNum.setGravity(Gravity.CENTER_HORIZONTAL);
                matchNum.setText("Match Number");

                TextView blue1 = new TextView(getApplicationContext());
                blue1.setBackgroundColor(Color.BLUE);
                blue1.setTextColor(Color.BLACK);
                blue1.setGravity(Gravity.CENTER_HORIZONTAL);
                blue1.setLayoutParams(trlp);
                blue1.setText("Blue 1");

                TextView blue2 = new TextView(getApplicationContext());
                blue2.setBackgroundColor(Color.BLUE);
                blue2.setTextColor(Color.BLACK);
                blue2.setGravity(Gravity.CENTER_HORIZONTAL);
                blue2.setLayoutParams(trlp);
                blue2.setText("Blue 2");

                TextView blue3 = new TextView(getApplicationContext());
                blue3.setBackgroundColor(Color.BLUE);
                blue3.setTextColor(Color.BLACK);
                blue3.setGravity(Gravity.CENTER_HORIZONTAL);
                blue3.setLayoutParams(trlp);
                blue3.setText("Blue 3");

                TextView red1 = new TextView(getApplicationContext());
                red1.setBackgroundColor(Color.RED);
                red1.setTextColor(Color.BLACK);
                red1.setGravity(Gravity.CENTER_HORIZONTAL);
                red1.setLayoutParams(trlp);
                red1.setText("Red 1");

                TextView red2 = new TextView(getApplicationContext());
                red2.setBackgroundColor(Color.RED);
                red2.setTextColor(Color.BLACK);
                red2.setGravity(Gravity.CENTER_HORIZONTAL);
                red2.setLayoutParams(trlp);
                red2.setText("Red 2");

                TextView red3 = new TextView(getApplicationContext());
                red3.setBackgroundColor(Color.RED);
                red3.setTextColor(Color.BLACK);
                red3.setGravity(Gravity.CENTER_HORIZONTAL);
                red3.setLayoutParams(trlp);
                red3.setText("Red 3");

                tr.addView(matchNum);
                tr.addView(blue1);
                tr.addView(blue2);
                tr.addView(blue3);
                tr.addView(red1);
                tr.addView(red2);
                tr.addView(red3);
                Log.d(TAG, "Row added - " + matchNum.getText() + " " + blue1.getText() + " " + blue2.getText() + " " + blue3.getText() + " " + red1.getText() + " " + red2.getText() + " " + red3.getText());
                scheduleTable.addView(tr);
                for(int i = 0; i < cursor.getCount(); i++)
                {
                    tr = new TableRow(getApplicationContext());
                    tr.setBackgroundColor(Color.BLACK);
                    tr.setLayoutParams(trlp);

                    matchNum = new TextView(getApplicationContext());
                    matchNum.setBackgroundColor(Color.WHITE);
                    matchNum.setTextColor(Color.BLACK);
                    matchNum.setLayoutParams(trlp);
                    matchNum.setGravity(Gravity.CENTER_HORIZONTAL);
                    matchNum.setText(cursor.getString(0));

                    blue1 = new TextView(getApplicationContext());
                    String temp = cursor.getString(1);
                    if(temp.equals("3824"))
                    {
                        blue1.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        blue1.setBackgroundColor(Color.WHITE);
                    }
                    blue1.setTextColor(Color.BLACK);
                    blue1.setGravity(Gravity.CENTER_HORIZONTAL);
                    blue1.setLayoutParams(trlp);
                    blue1.setText(temp);

                    blue2 = new TextView(getApplicationContext());
                    temp = cursor.getString(2);
                    if(temp.equals("3824"))
                    {
                        blue2.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        blue2.setBackgroundColor(Color.WHITE);
                    }
                    blue2.setTextColor(Color.BLACK);
                    blue2.setGravity(Gravity.CENTER_HORIZONTAL);
                    blue2.setLayoutParams(trlp);
                    blue2.setText(temp);

                    blue3 = new TextView(getApplicationContext());
                    temp = cursor.getString(3);
                    if(temp.equals("3824"))
                    {
                        blue3.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        blue3.setBackgroundColor(Color.WHITE);
                    }
                    blue3.setTextColor(Color.BLACK);
                    blue3.setGravity(Gravity.CENTER_HORIZONTAL);
                    blue3.setLayoutParams(trlp);
                    blue3.setText(temp);

                    red1 = new TextView(getApplicationContext());
                    temp = cursor.getString(4);
                    if(temp.equals("3824"))
                    {
                        red1.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        red1.setBackgroundColor(Color.WHITE);
                    }
                    red1.setTextColor(Color.BLACK);
                    red1.setGravity(Gravity.CENTER_HORIZONTAL);
                    red1.setLayoutParams(trlp);
                    red1.setText(cursor.getString(4));

                    red2 = new TextView(getApplicationContext());
                    temp = cursor.getString(5);
                    if(temp.equals("3824"))
                    {
                        red2.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        red2.setBackgroundColor(Color.WHITE);
                    }
                    red2.setTextColor(Color.BLACK);
                    red2.setGravity(Gravity.CENTER_HORIZONTAL);
                    red2.setLayoutParams(trlp);
                    red2.setText(temp);

                    red3 = new TextView(getApplicationContext());
                    temp = cursor.getString(6);
                    if(temp.equals("3824"))
                    {
                        red3.setBackgroundColor(Color.GREEN);
                    }
                    else
                    {
                        red3.setBackgroundColor(Color.WHITE);
                    }
                    red3.setTextColor(Color.BLACK);
                    red3.setGravity(Gravity.CENTER_HORIZONTAL);
                    red3.setLayoutParams(trlp);
                    red3.setText(temp);

                    tr.addView(matchNum);
                    tr.addView(blue1);
                    tr.addView(blue2);
                    tr.addView(blue3);
                    tr.addView(red1);
                    tr.addView(red2);
                    tr.addView(red3);
                    Log.d(TAG, "Row added - " + matchNum.getText() + " " + blue1.getText() + " " + blue2.getText() + " " + blue3.getText() + " " + red1.getText() + " " + red2.getText() + " " + red3.getText());
                    scheduleTable.addView(tr);
                    cursor.moveToNext();
                }
            }
        }
    }
}
