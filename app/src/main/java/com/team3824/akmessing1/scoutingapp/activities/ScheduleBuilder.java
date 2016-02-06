package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.ScheduleDB;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

public class ScheduleBuilder extends AppCompatActivity {

    private static final String TAG = "ScheduleBuilder";
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_builder);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        final String eventID = sharedPreferences.getString(Constants.EVENT_ID, "");
        final ScheduleDB scheduleDB = new ScheduleDB(this, eventID);

        layout = (LinearLayout)findViewById(R.id.schedule);

        Cursor schedule = scheduleDB.getSchedule();
        View view;
        TextView matchNumber;
        EditText blue1, blue2, blue3, red1, red2, red3;
        int matchNum = 0;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(schedule.getCount()>0)
        {
            while(!schedule.isAfterLast())
            {
                matchNum++;
                view = inflater.inflate(R.layout.list_item_schedule_match_build, null);

                matchNumber = (TextView)view.findViewById(R.id.schedule_matchNum);
                matchNumber.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_MATCH_NUMBER))));

                blue1 = (EditText)view.findViewById(R.id.schedule_blue1);
                blue1.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_BLUE1))));

                blue2 = (EditText)view.findViewById(R.id.schedule_blue2);
                blue2.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_BLUE2))));

                blue3 = (EditText)view.findViewById(R.id.schedule_blue3);
                blue3.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_BLUE3))));

                red1 = (EditText)view.findViewById(R.id.schedule_red1);
                red1.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_RED1))));

                red2 = (EditText)view.findViewById(R.id.schedule_red2);
                red2.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_RED2))));

                red3 = (EditText)view.findViewById(R.id.schedule_red3);
                red3.setText(String.valueOf(schedule.getInt(schedule.getColumnIndex(ScheduleDB.KEY_RED3))));
                int numChildren = layout.getChildCount();
                layout.addView(view,numChildren-1);
                schedule.moveToNext();
            }
        }
        matchNum++;
        addRow(matchNum);

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First child is the header
                // Second child is the table header
                for(int i = 2; i < layout.getChildCount()-1; i++)
                {
                    ViewGroup row = (ViewGroup)layout.getChildAt(i);

                    TextView matchNumber = (TextView)row.getChildAt(0);
                    String matchNumber_string = String.valueOf(matchNumber.getText());
                    int matchNumber_value = Integer.parseInt(matchNumber_string);

                    EditText blue1 = (EditText)row.getChildAt(1);
                    String blue1_string = String.valueOf(blue1.getText());
                    int blue1_value = -1;

                    EditText blue2 = (EditText)row.getChildAt(2);
                    String blue2_string = String.valueOf(blue2.getText());
                    int blue2_value = -1;

                    EditText blue3 = (EditText)row.getChildAt(3);
                    String blue3_string = String.valueOf(blue3.getText());
                    int blue3_value = -1;

                    EditText red1 = (EditText)row.getChildAt(4);
                    String red1_string = String.valueOf(red1.getText());
                    int red1_value = -1;

                    EditText red2 = (EditText)row.getChildAt(5);
                    String red2_string = String.valueOf(red2.getText());
                    int red2_value = -1;

                    EditText red3 = (EditText)row.getChildAt(6);
                    String red3_string = String.valueOf(red3.getText());
                    int red3_value = -1;

                    try {
                        blue1_value = Integer.parseInt(blue1_string);
                        blue2_value = Integer.parseInt(blue2_string);
                        blue3_value = Integer.parseInt(blue3_string);
                        red1_value = Integer.parseInt(red1_string);
                        red2_value = Integer.parseInt(red2_string);
                        red3_value = Integer.parseInt(red3_string);
                    }
                    catch (NumberFormatException e)
                    {

                    }

                    if(blue1_value > 0 && blue2_value > 0 && blue3_value > 0 &&
                            red1_value > 0 && red2_value > 0 && red3_value > 0)
                    {
                        Log.d("ScheduleBuilder",matchNumber_string+" "+blue1_string+" "+
                                blue2_string+" "+blue3_string+" "+red1_string+" "+red2_string+" "+
                                red3_string);
                        scheduleDB.addMatch(matchNumber_value,
                                blue1_value, blue2_value, blue3_value,
                                red1_value, red2_value, red3_value);
                    }
                    else
                    {
                        scheduleDB.removeMatch(matchNumber_value);
                    }

                }

                Intent intent = new Intent(ScheduleBuilder.this, MatchSchedule.class);
                startActivity(intent);
            }
        });
    }

    public void addRow(int matchNum)
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_schedule_match_build, null);
        Button add = new Button(this);
        add.setText("Add");
        final int nextMatchNum = matchNum+1;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRow(nextMatchNum);
                ((ViewGroup)v.getParent()).removeView(v);
            }
        });
        ((ViewGroup)view).addView(add);

        TextView matchNumber = (TextView)view.findViewById(R.id.schedule_matchNum);
        matchNumber.setText(String.valueOf(matchNum));
        int numChildren = layout.getChildCount();
        layout.addView(view,numChildren-1);
    }
}
