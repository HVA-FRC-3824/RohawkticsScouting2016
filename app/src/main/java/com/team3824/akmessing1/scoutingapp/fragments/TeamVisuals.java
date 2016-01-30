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

    LineChart mLineChart;
    LineDataSet mAutoHighMade, mAutoHighPercent, mAutoLowMade, mAutoLowPercent, mTeleopHighMade,
            mTeleopHighPercent, mTeleopLowMade, mTeleopLowPercent;

    BarChart mBarChart;
    BarDataSet mEndgame;

    ArrayList<String> mMatches;

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

        RadioButton radioButton = (RadioButton)view.findViewById(R.id.seen);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mSeen));
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.started);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES,mStarted));
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.speed);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES,mSpeed));
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.auto);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES,mAutoCross));
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES,mTeleopCross));
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.reach);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mReached));
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });
        
        mLineChart = (LineChart)view.findViewById(R.id.line_chart);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setDescription("");
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(true);
        mLineChart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setExtraLeftOffset(15);

        generate_line_data(matchCursor);

        radioButton = (RadioButton)view.findViewById(R.id.auto_high_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().resetAxisMaxValue();
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mAutoHighMade));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.auto_low_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().resetAxisMaxValue();
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mAutoLowMade));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop_high_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().resetAxisMaxValue();
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mTeleopHighMade));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop_low_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().resetAxisMaxValue();
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mTeleopLowMade));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.auto_high_percent);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().setAxisMaxValue(100.0f);
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mAutoHighPercent));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.auto_low_percent);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().setAxisMaxValue(100.0f);
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mAutoLowPercent));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop_high_percent);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().setAxisMaxValue(100.0f);
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mTeleopHighPercent));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop_low_percent);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.getAxisLeft().setAxisMaxValue(100.0f);
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches,mTeleopLowPercent));
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        mBarChart = (BarChart)view.findViewById(R.id.bar_chart);
        mBarChart.setDescription("");
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        generate_bar_data(matchCursor);
        mBarChart.setData(new BarData(mMatches,mEndgame));

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

    private void generate_line_data(Cursor cursor)
    {
        ArrayList<Entry> autoHighMadeEntries = new ArrayList<>();
        ArrayList<Entry> autoLowMadeEntries = new ArrayList<>();
        ArrayList<Entry> teleopHighMadeEntries = new ArrayList<>();
        ArrayList<Entry> teleopLowMadeEntries = new ArrayList<>();
        ArrayList<Entry> autoHighPercentEntries = new ArrayList<>();
        ArrayList<Entry> autoLowPercentEntries = new ArrayList<>();
        ArrayList<Entry> teleopHighPercentEntries = new ArrayList<>();
        ArrayList<Entry> teleopLowPercentEntries = new ArrayList<>();
        mMatches = new ArrayList<>();
        cursor.moveToFirst();
        float percent;
        for(int i = 0; i < cursor.getCount(); i++)
        {
            int autoHighHit = cursor.getInt(cursor.getColumnIndex(Constants.AUTO_HIGH_HIT));
            int autoHighMiss = cursor.getInt(cursor.getColumnIndex(Constants.AUTO_HIGH_MISS));

            int autoLowHit = cursor.getInt(cursor.getColumnIndex(Constants.AUTO_LOW_HIT));
            int autoLowMiss = cursor.getInt(cursor.getColumnIndex(Constants.AUTO_LOW_MISS));

            int teleopHighHit = cursor.getInt(cursor.getColumnIndex(Constants.TELEOP_HIGH_HIT));
            int teleopHighMiss = cursor.getInt(cursor.getColumnIndex(Constants.TELEOP_HIGH_MISS));

            int teleopLowHit = cursor.getInt(cursor.getColumnIndex(Constants.TELEOP_LOW_HIT));
            int teleopLowMiss = cursor.getInt(cursor.getColumnIndex(Constants.TELEOP_LOW_MISS));

            autoHighMadeEntries.add(new Entry(autoHighHit, i));
            autoLowMadeEntries.add(new Entry(autoLowHit, i));
            teleopHighMadeEntries.add(new Entry(teleopHighHit, i));
            teleopLowMadeEntries.add(new Entry(teleopLowHit,i));

            percent = ((autoHighHit+autoHighMiss) == 0) ? 0 : autoHighHit / (float)(autoHighHit + autoHighMiss) * 100.0f;
            autoHighPercentEntries.add(new Entry(percent,i));

            percent = ((autoLowHit + autoLowMiss) == 0) ? 0 : autoLowHit / (float)(autoLowHit + autoLowMiss) * 100.0f;
            autoLowPercentEntries.add(new Entry(percent,i));

            percent = ((teleopHighHit + teleopHighMiss) == 0) ? 0 : teleopHighHit / (float)(teleopHighHit + teleopHighMiss) * 100.0f;
            teleopHighPercentEntries.add(new Entry(percent,i));
            
            percent = ((teleopLowHit + teleopLowMiss) == 0) ? 0 : teleopLowHit / (float)(teleopLowHit + teleopLowMiss) * 100.0f;
            teleopLowPercentEntries.add(new Entry(percent,i));

            mMatches.add("M"+String.valueOf(cursor.getInt(cursor.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER))));

            cursor.moveToNext();
        }
        mAutoHighMade = new LineDataSet(autoHighMadeEntries,"Auto High Made");
        mAutoHighMade.setColor(Color.RED);
        mAutoHighMade.setAxisDependency(YAxis.AxisDependency.LEFT);

        mAutoLowMade = new LineDataSet(autoLowMadeEntries,"Auto Low Made");
        mAutoLowMade.setColor(Color.RED);
        mAutoLowMade.setAxisDependency(YAxis.AxisDependency.LEFT);

        mTeleopHighMade = new LineDataSet(teleopHighMadeEntries,"Teleop High Made");
        mTeleopHighMade.setColor(Color.RED);
        mTeleopHighMade.setAxisDependency(YAxis.AxisDependency.LEFT);

        mTeleopLowMade = new LineDataSet(teleopLowMadeEntries,"Teleop Low Made");
        mTeleopLowMade.setColor(Color.RED);
        mTeleopLowMade.setAxisDependency(YAxis.AxisDependency.LEFT);

        mAutoHighPercent = new LineDataSet(autoHighPercentEntries,"Auto High Percent");
        mAutoHighPercent.setColor(Color.RED);
        mAutoHighPercent.setAxisDependency(YAxis.AxisDependency.LEFT);

        mAutoLowPercent = new LineDataSet(autoLowPercentEntries,"Auto Low Percent");
        mAutoLowPercent.setColor(Color.RED);
        mAutoLowPercent.setAxisDependency(YAxis.AxisDependency.LEFT);

        mTeleopHighPercent = new LineDataSet(teleopHighPercentEntries,"Teleop High Percent");
        mTeleopHighPercent.setColor(Color.RED);
        mTeleopHighPercent.setAxisDependency(YAxis.AxisDependency.LEFT);

        mTeleopLowPercent = new LineDataSet(teleopLowPercentEntries,"Teleop Low Percent");
        mTeleopLowPercent.setColor(Color.RED);
        mTeleopLowPercent.setAxisDependency(YAxis.AxisDependency.LEFT);
    }

    private void generate_bar_data(Cursor cursor)
    {
        cursor.moveToFirst();
        ArrayList<BarEntry> entries = new ArrayList<>();
        for(int i = 0; i < cursor.getCount(); i++)
        {
            int value = 0;
            if(cursor.getString(cursor.getColumnIndex(Constants.ENDGAME_CHALLENGE_SCALE)).equals("Challenge"))
            {
                value = 5;
            }
            else if(cursor.getString(cursor.getColumnIndex(Constants.ENDGAME_CHALLENGE_SCALE)).equals("Scale"))
            {
                value = 15;
            }
            entries.add(new BarEntry(value,i));

            cursor.moveToNext();
        }
        mEndgame = new BarDataSet(entries,"Endgame");
        mEndgame.setColor(Color.RED);
        mEndgame.setAxisDependency(YAxis.AxisDependency.LEFT);
    }


}
