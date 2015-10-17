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
import com.example.akmessing1.scoutingtest.ScheduleDB;

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
        final SharedPreferences sharedPreferences = getSharedPreferences( "appData", Context.MODE_PRIVATE );

        final String eventID = sharedPreferences.getString("event_id", "");

        if(eventID == "")
        {
            Log.d(TAG,"No eventID");
        }
        else {

            final ScheduleDB scheduleDB = new ScheduleDB(this,eventID);

            if(scheduleDB.getNumMatches() == 0) {
                Log.d(TAG, "Table empty");
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/matches?X-TBA-App-Id=amessing:scoutingTest:v1";
                Log.d(TAG, "url: " + url);
                JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "Schedule received");
                        scheduleDB.createSchedule(response);
                        Cursor cursor = scheduleDB.getSchedule();
                        String color = sharedPreferences.getString("alliance_color", "Blue");
                        int num = sharedPreferences.getInt("alliance_number", 1);
                        TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        trlp.setMargins(2, 2, 2, 2);

                        for(int i = 0; i < cursor.getCount(); i++)
                        {
                            Button button = new Button(getApplicationContext());
                            String buttonText = "Match " + (i + 1)+": ";
                            button.setLayoutParams(trlp);
                            int tn = -1;
                            if(color.equals("Blue"))
                            {
                                button.setBackgroundColor(Color.BLUE);
                                tn = Integer.parseInt(cursor.getString(num));
                                buttonText += tn;

                            }
                            else
                            {
                                button.setBackgroundColor(Color.RED);
                                tn = Integer.parseInt(cursor.getString(num+3));
                                buttonText += tn;
                            }
                            button.setText(buttonText);
                            final int matchNum = i+1;
                            final int teamNum = tn;
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MatchList.this,MatchScouting.class);
                                    intent.putExtra("match_number",matchNum);
                                    intent.putExtra("team_number",teamNum);
                                    startActivity(intent);
                                }
                            });
                            matchList.addView(button);
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

            } else {
                Log.d(TAG, "Table not empty");
                Cursor cursor = scheduleDB.getSchedule();
                cursor.moveToFirst();
                String color = sharedPreferences.getString("alliance_color", "Blue");
                int num = sharedPreferences.getInt("alliance_number", 1);
                TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                trlp.setMargins(2, 2, 2, 2);

                for(int i = 0; i < cursor.getCount(); i++)
                {
                    Button button = new Button(getApplicationContext());
                    String buttonText = "Match " + (i + 1)+": ";
                    button.setLayoutParams(trlp);
                    int tn = -1;
                    if(color.equals("Blue"))
                    {
                        button.setBackgroundColor(Color.BLUE);
                        if (num == 1)
                        {
                            tn = Integer.parseInt(cursor.getString(1));
                            buttonText += tn;

                        }
                        else if (num == 2)
                        {
                            tn = Integer.parseInt(cursor.getString(2));
                            buttonText += tn;
                        }
                        else if(num == 3)
                        {
                            tn = Integer.parseInt(cursor.getString(3));
                            buttonText += tn;
                        }
                    }
                    else
                    {
                        button.setBackgroundColor(Color.RED);
                        if (num == 1)
                        {
                            tn = Integer.parseInt(cursor.getString(4));
                            buttonText += tn;
                        }
                        else if (num == 2)
                        {
                            tn = Integer.parseInt(cursor.getString(5));
                            buttonText += tn;
                        }
                        else if(num == 3)
                        {
                            tn = Integer.parseInt(cursor.getString(6));
                            buttonText += tn;
                        }
                    }
                    button.setText(buttonText);
                    final int matchNum = i+1;
                    final int teamNum = tn;
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MatchList.this,MatchScouting.class);
                            intent.putExtra("match_number",matchNum);
                            intent.putExtra("team_number",teamNum);
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
