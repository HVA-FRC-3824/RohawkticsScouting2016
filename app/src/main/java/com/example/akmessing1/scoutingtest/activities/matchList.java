package com.example.akmessing1.scoutingtest.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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

import java.util.Objects;

// Activity which displays each match and corresponding team based on the selected alliance color
// and number as button which lead to match scouting
public class MatchList extends AppCompatActivity {

    private static final String TAG = "MatchList";
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        final SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString("event_id", "");

        // If there is no Event ID stored then a dialog box will popup to set one
        if (eventID.equals("")) {
            Log.d(TAG, "No Event ID");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final View view = inflater.inflate(R.layout.dialog_set_event_id, null);
            builder.setView(view);
            // Save button saves new event id
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    TextView textView = (TextView) view.findViewById(R.id.set_event_id);
                    String eventId = String.valueOf(textView.getText());
                    Log.d(TAG, "Event ID: " + eventId);
                    if (!eventId.equals("")) {
                        SharedPreferences.Editor prefEditor = getSharedPreferences("appData", Context.MODE_PRIVATE).edit();
                        prefEditor.putString("event_id", eventId);
                        prefEditor.commit();
                    }
                    Intent intent = new Intent(MatchList.this, MatchList.class);
                    startActivity(intent);
                }
            });
            // Back button goes back to the start screen
            builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(MatchList.this, StartScreen.class);
                    startActivity(intent);
                }
            });
            builder.show();
            return;
        }

        // Back button will send user to the home screen
        Button button = (Button)findViewById(R.id.match_list_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchList.this,StartScreen.class);
                startActivity(intent);

            }
        });

        final ScheduleDB scheduleDB = new ScheduleDB(this, eventID);

        // If there are no matches in the database pull from schedule the blue alliance
        if (scheduleDB.getNumMatches() == 0) {
            Log.d(TAG, "Table empty");
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/matches?X-TBA-App-Id=amessing:scoutingTest:v1";
            Log.d(TAG, "url: " + url);

            //Request schedule
            JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "Schedule received");
                    scheduleDB.createSchedule(response);
                    //
                    displayListView(scheduleDB, sharedPreferences);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO: Allow schedule building
                    Log.d(TAG, "Error: " + error.getMessage());
                }
            });
            queue.add(jsonReq);
        } else {
            Log.d(TAG, "Table not empty");
            displayListView(scheduleDB, sharedPreferences);
        }


    }

    // Setup list view with the schedule
    private void displayListView(ScheduleDB scheduleDB, SharedPreferences sharedPreferences)
    {
        Cursor cursor = scheduleDB.getSchedule();
        if(cursor != null)
        {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.match_list);
            int alliance_number = sharedPreferences.getInt("alliance_number",0);
            String alliance_color = sharedPreferences.getString("alliance_color","");

            // If no alliance color set, ask for one via dialog box
            if(alliance_color.equals(""))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set Alliance Color");
                String[] colors = new String[]{"Blue","Red"};
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor prefEditor = getSharedPreferences("appData", Context.MODE_PRIVATE).edit();
                        switch (which) {
                            case 0:
                                prefEditor.putString("alliance_color", "Blue");
                                break;
                            case 1:
                                prefEditor.putString("alliance_color", "Red");
                                break;
                        }
                        prefEditor.commit();
                        Intent intent = new Intent(MatchList.this, MatchList.class);
                        startActivity(intent);
                    }
                });
                builder.show();
                return;
            }

            // if no alliance number set, ask for one via dialog box
            if(alliance_number == 0)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set Alliance Color");
                String[] numbers = new String[]{"1","2", "3"};
                builder.setItems(numbers, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor prefEditor = getSharedPreferences("appData", Context.MODE_PRIVATE).edit();
                        prefEditor.putInt("alliance_number", which + 1);
                        prefEditor.commit();
                        Intent intent = new Intent(MatchList.this, MatchList.class);
                        startActivity(intent);
                    }
                });
                builder.show();
                return;
            }

            cursor.moveToFirst();
            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,4,4,4);

            // Add buttons
            do{
                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int matchNumber = cursor.getInt(0);
                final int teamNumber = cursor.getInt(cursor.getColumnIndex(alliance_color.toLowerCase() + alliance_number));
                button.setText("Match " + matchNumber + ": " + teamNumber);
                switch (alliance_color.toLowerCase())
                {
                    case "blue":
                        button.setBackgroundColor(Color.BLUE);
                        break;
                    case "red":
                        button.setBackgroundColor(Color.RED);
                        break;
                }
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MatchList.this, MatchScouting.class);
                        intent.putExtra("team_number",teamNumber);
                        intent.putExtra("match_number",matchNumber);
                        startActivity(intent);
                    }
                });
                linearLayout.addView(button);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }
    }
}
