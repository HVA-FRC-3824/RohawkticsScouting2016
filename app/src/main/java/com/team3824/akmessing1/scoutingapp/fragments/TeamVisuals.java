package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;

import java.util.ArrayList;
import java.util.Map;

public class TeamVisuals extends Fragment {
    private String TAG = "TeamVisuals";

    RadarChart mRadarChart;
    RadarDataSet mSeen, mStarted, mReached, mAutoCross, mTeleopCross, mSpeed;

    CombinedChart mAutoChart;
    CombinedChart mTeleopChart;
    BarChart mBarChart;

    public TeamVisuals()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_visuals, container, false);
        Bundle args = getArguments();
        int teamNumber = args.getInt("teamNumber", -1);

        Activity activity = getActivity();
        SharedPreferences sharedPreferences = activity.getSharedPreferences("appData", Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString("event_id", "");

        PitScoutDB pitScoutDB = new PitScoutDB(activity, eventID);
        Map<String, ScoutValue> pitMap = pitScoutDB.getTeamMap(teamNumber);

        MatchScoutDB matchScoutDB = new MatchScoutDB(activity, eventID);
        Cursor matchCursor = matchScoutDB.getTeamInfo(teamNumber);

        StatsDB statsDB = new StatsDB(activity,eventID);
        Map<String, ScoutValue> statsMap = statsDB.getTeamStats(teamNumber);

        ImageView imageView = (ImageView)view.findViewById(R.id.robotPicture);
        if (pitMap.containsKey("robotPicture")) {
            String robotPhoto = pitMap.get("robotPicture").getString();
            Log.d(TAG, robotPhoto);
            String fullPath = getContext().getFilesDir().getAbsolutePath() + "/" + robotPhoto;
            int targetW = 400;
            int targetH = 600;
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fullPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(fullPath, bmOptions);
            imageView.setImageBitmap(bitmap);
        }

        mRadarChart = (RadarChart)view.findViewById(R.id.radar_chart);
        mRadarChart.getLegend().setEnabled(false);
        mRadarChart.setDescription("");
        mRadarChart.getYAxis().setShowOnlyMinMax(true);

        generate_radar_data(statsMap);

        final String[] defenses = {"Low Bar", "Portcullis", "Cheval de Frise","Moat","Ramparts","Drawbridge","Sally Port","Rough Terrain","Rock Wall"};
        RadioButton radioButton = (RadioButton)view.findViewById(R.id.seen);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(defenses, mSeen));
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.started);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(defenses,mStarted));
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.speed);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(defenses,mSpeed));
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.auto);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(defenses,mAutoCross));
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(defenses,mTeleopCross));
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.reach);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(defenses,mReached));
                mRadarChart.invalidate();
            }
        });

        mAutoChart = (CombinedChart)view.findViewById(R.id.auto_chart);
        mAutoChart.setDrawOrder(new CombinedChart.DrawOrder[]{CombinedChart.DrawOrder.BAR,CombinedChart.DrawOrder.LINE});
        mAutoChart.setDescription("");
        

        return view;
    }

    private void generate_radar_data(Map<String, ScoutValue> map)
    {
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_SEEN[i]).getInt(), i));
        }
        mSeen = new RadarDataSet(entries,"Seen");
        mSeen.setColor(Color.RED);

        entries = new ArrayList<>();
        for(int i = 0; i < 9; i++)
        {
            entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_STARTED[i]).getInt(), i));
        }
        mStarted = new RadarDataSet(entries,"Started in front of");
        mStarted.setColor(Color.RED);

        entries = new ArrayList<>();
        for(int i = 0; i < 9; i++ ) {
            entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt(), i));
        }
        mReached = new RadarDataSet(entries,"Started in front of");
        mReached.setColor(Color.RED);

        entries = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt(), i));
        }
        mAutoCross = new RadarDataSet(entries,"Auto Cross");
        mAutoCross.setColor(Color.RED);

        entries = new ArrayList<>();
        for(int i = 0; i < 9; i++) {
            entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt(), i));
        }
        mTeleopCross = new RadarDataSet(entries,"Teleop Cross");
        mTeleopCross.setColor(Color.RED);
    }



}
