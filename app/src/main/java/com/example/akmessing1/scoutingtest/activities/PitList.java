package com.example.akmessing1.scoutingtest.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.akmessing1.scoutingtest.JsonUTF8Request;
import com.example.akmessing1.scoutingtest.PitScoutDB;
import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.ScheduleDB;

import org.json.JSONArray;

import java.util.Set;

public class PitList extends AppCompatActivity {

    private static final String TAG = "PitList";
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_list);

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
                    Intent intent = new Intent(PitList.this, PitList.class);
                    startActivity(intent);
                }
            });
            // Back button goes back to the start screen
            builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(PitList.this, StartScreen.class);
                    startActivity(intent);
                }
            });
            builder.show();
            return;
        }

        // Back button will send user to the home screen
        Button button = (Button)findViewById(R.id.pit_list_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PitList.this,StartScreen.class);
                startActivity(intent);

            }
        });

        final PitScoutDB pitScoutDB = new PitScoutDB(this,eventID);

        // If there are no matches in the database pull from schedule the blue alliance
        if (pitScoutDB.getNumTeams() == 0) {
            Log.d(TAG, "Table empty");
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://www.thebluealliance.com/api/v2/event/" + eventID + "/teams?X-TBA-App-Id=amessing:scoutingTest:v1";
            Log.d(TAG, "url: " + url);

            //Request schedule
            JsonRequest jsonReq = new JsonUTF8Request(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d(TAG, "Schedule received");
                    pitScoutDB.createTeamList(response);
                    //
                    displayListView(pitScoutDB, sharedPreferences);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //TODO: Allow team list building
                    Log.d(TAG, "Error: " + error.getMessage());
                }
            });
            queue.add(jsonReq);
        } else {
            Log.d(TAG, "Table not empty");
            displayListView(pitScoutDB, sharedPreferences);
        }

    }

    // Setup list view with the schedule
    private void displayListView(PitScoutDB pitScoutDB, SharedPreferences sharedPreferences)
    {
        Cursor cursor = pitScoutDB.getAllTeams();

        if(cursor != null && cursor.getCount() > 0)
        {
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.pit_list);

            TableLayout.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(4,4,4,4);

            // Add buttons

            do{

                Button button = new Button(this);
                button.setLayoutParams(lp);
                final int teamNumber = cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_TEAM_NUMBER));
                Log.d(TAG, "Adding Button for" + teamNumber);
                button.setText(String.valueOf(teamNumber));

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PitList.this, PitScouting.class);
                        intent.putExtra("team_number", teamNumber);
                        startActivity(intent);
                    }
                });

                if(cursor.getInt(cursor.getColumnIndex(PitScoutDB.KEY_COMPLETE)) != 0)
                {
                    button.setBackgroundColor(Color.GREEN);
                }
                else
                {
                    button.setBackgroundColor(Color.RED);
                }

                linearLayout.addView(button);
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }
    }

}
