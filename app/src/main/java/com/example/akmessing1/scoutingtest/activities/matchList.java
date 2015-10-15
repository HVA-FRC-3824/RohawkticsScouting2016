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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.akmessing1.scoutingtest.JsonUTF8Request;
import com.example.akmessing1.scoutingtest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchList extends AppCompatActivity {

    private static final String TAG = MatchList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        final LinearLayout matchList = (LinearLayout)findViewById(R.id.matchList);
        SharedPreferences sharedPreferences = getSharedPreferences( "appData", Context.MODE_PRIVATE );

        /*
        boolean matchScout = sharedPreferences.getBoolean("match_scout", false);

        if(!matchScout)
        {
            TextView tv = new TextView(getApplicationContext());
            tv.setText("Not a match scout");
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(42);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            MatchList.addView(tv);
            return;
        }
        */

        final String eventID = sharedPreferences.getString("event_id", "");

        if(eventID == "")
        {
            Log.d(TAG,"No eventID");
        }
        else {

            final SQLiteDatabase database = openOrCreateDatabase("RoHawkticsScouting", MODE_PRIVATE, null);
            String queryString = "CREATE TABLE IF NOT EXISTS schedule_" + eventID + "( matchNum INTEGER PRIMARY KEY UNIQUE NOT NULL, blue1 INTEGER NOT NULL, blue2 INTEGER NOT NULL, blue3 INT NOT NULL, red1 INTEGER NOT NULL, red2 INTEGER NOT NULL, red3 INTEGER NOT NULL);";
            Log.d(TAG, "Query: " + queryString);
            database.execSQL(queryString);
            queryString = "select * from schedule_" + eventID + ";";
            Log.d(TAG, "Query: " + queryString);
            Cursor cursor = database.rawQuery(queryString, null);

            if (cursor.getCount() == 0) {
                Log.d(TAG, "Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/matches?X-TBA-App-Id=amessing:scoutingTest:v1";
                Log.d(TAG, "url: " + url);
                JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Schedule received");

                        for (int i = 0; i < response.length(); i++) {

                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Log.d(TAG, "comp_level: " + jsonObject.getString("comp_level"));
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
                                    String queryString = "insert into schedule_" + eventID + " (matchNum, blue1, blue2, blue3, red1, red2, red3) values (" + matchNum + ", " + blue1 + ", " + blue2 + ", " + blue3 + ", " + red1 + ", " + red2 + ", " + red3 + ");";
                                    Log.d(TAG, "Query: " + queryString);
                                    database.execSQL(queryString);
                                }

                            } catch (JSONException e) {
                                Log.d(TAG, "Exception: " + e.toString());
                            }

                        }
                        Intent intent = new Intent(getApplicationContext(), MatchList.class);
                        startActivity(intent);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "Error: " + error.getMessage());

                    }
                });

                queue.add(jsonReq);

            } else {
                Log.d(TAG, "Table not empty");
                String color = sharedPreferences.getString("alliance_color","Blue");
                int num = sharedPreferences.getInt("alliance_number", 1);
                TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                trlp.setMargins(2, 2, 2, 2);
                cursor.moveToFirst();
                for(int i = 0; i < cursor.getCount(); i++)
                {
                    Button button = new Button(getApplicationContext());
                    String buttonText = "Match " + (i + 1)+": ";
                    button.setLayoutParams(trlp);
                    if(color.equals("Blue"))
                    {
                        button.setBackgroundColor(Color.BLUE);
                        if (num == 1)
                        {
                            buttonText += cursor.getString(1);
                        }
                        else if (num == 2)
                        {
                            buttonText += cursor.getString(2);
                        }
                        else if(num == 3)
                        {
                            buttonText += cursor.getString(3);
                        }
                    }
                    else
                    {
                        button.setBackgroundColor(Color.RED);
                        if (num == 1)
                        {
                            buttonText += cursor.getString(4);
                        }
                        else if (num == 2)
                        {
                            buttonText += cursor.getString(5);
                        }
                        else if(num == 3)
                        {
                            buttonText += cursor.getString(6);
                        }
                    }
                    button.setText(buttonText);
                    final int matchNum = i+1;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MatchList.this,MatchScouting.class);
                            intent.putExtra("match_number",matchNum);
                            startActivity(intent);
                        }
                    });
                    matchList.addView(button);
                    cursor.moveToNext();
                }
            }
        }

    }
}
