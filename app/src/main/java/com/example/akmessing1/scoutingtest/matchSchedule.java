package com.example.akmessing1.scoutingtest;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class matchSchedule extends AppCompatActivity {
    private static final String TAG = matchSchedule.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_schedule);


        TableLayout scheduleTable = (TableLayout)findViewById(R.id.scheduleTable);

        SharedPreferences sharedPreferences = getSharedPreferences( "appData", Context.MODE_PRIVATE );
        final String eventID = sharedPreferences.getString("event_id","");

        if(eventID == "")
        {
            Log.d(TAG,"No eventID");
        }
        else
        {

            final SQLiteDatabase database = openOrCreateDatabase("RoHawkticsScouting", MODE_PRIVATE, null);
            String queryString = "CREATE TABLE IF NOT EXISTS schedule_" + eventID + "( matchNum INTEGER PRIMARY KEY UNIQUE NOT NULL, blue1 INTEGER NOT NULL, blue2 INTEGER NOT NULL, blue3 INT NOT NULL, red1 INTEGER NOT NULL, red2 INTEGER NOT NULL, red3 INTEGER NOT NULL);";
            Log.d(TAG, "Query: "+queryString);
            database.execSQL(queryString);
            queryString = "select * from schedule_" + eventID + ";";
            Log.d(TAG, "Query: "+queryString);
            Cursor cursor = database.rawQuery(queryString, null);
            cursor.moveToFirst();

            if(cursor.getCount() == 0) {
                Log.d(TAG,"Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/matches?X-TBA-App-Id=amessing:scoutingTest:v1";
                Log.d(TAG,"url: "+url);
                JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET,url, null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG,"Schedule received");

                        for(int i = 0; i < response.length(); i++)
                        {

                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Log.d(TAG,"comp_level: "+jsonObject.getString("comp_level"));
                                if (jsonObject.getString("comp_level").equals("qm")) {

                                    String matchNum = jsonObject.getString("match_number");

                                    JSONObject alliances = jsonObject.getJSONObject("alliances");

                                    JSONObject blue = alliances.getJSONObject("blue");
                                    JSONArray blueTeams = blue.getJSONArray("teams");
                                    String blue1 = blueTeams.getString(0).substring(3);
                                    String blue2 = blueTeams.getString(1).substring(3);
                                    String blue3 = blueTeams.getString(2).substring(3);

                                    JSONObject red = alliances.getJSONObject("red");
                                    JSONArray redTeams = red.getJSONArray("teams");
                                    String red1 = redTeams.getString(0).substring(3);
                                    String red2 = redTeams.getString(1).substring(3);
                                    String red3 = redTeams.getString(2).substring(3);
                                    String queryString = "insert into schedule_"+eventID+" (matchNum, blue1, blue2, blue3, red1, red2, red3) values ("+matchNum+", "+blue1+", "+blue2+", "+blue3+", "+red1+", "+red2+", "+red3+");";
                                    Log.d(TAG, "Query: "+queryString);
                                    database.execSQL(queryString);
                                }

                            }catch (JSONException e) {
                                Log.d(TAG, "Exception: " + e.toString());
                            }

                        }
                        Intent intent = new Intent(getApplicationContext(), matchSchedule.class);
                        startActivity(intent);

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
