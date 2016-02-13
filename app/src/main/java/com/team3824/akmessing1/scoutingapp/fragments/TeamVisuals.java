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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
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
    RadarDataSet mSeen, mStarted, mReached, mAutoCross, mTeleopCross, mTime;

    LineChart mLineChart;
    YAxis mLineY;
    LineDataSet mAutoHighMade, mAutoHighPercent, mAutoLowMade, mAutoLowPercent, mTeleopHighMade,
            mTeleopHighPercent, mTeleopLowMade, mTeleopLowPercent;

    ValueFormatter intVF;
    ValueFormatter percentVF;
    YAxisValueFormatter intYVF;
    YAxisValueFormatter percentYVF;

    BarChart mBarChart;
    BarDataSet mEndgame;

    ArrayList<String> mMatches;

    public TeamVisuals()
    {
        intVF = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int)value);
            }
        };

        percentVF = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                value *= 10;
                value = (int)value;
                value /= 10;
                return String.valueOf(value)+"%";
            }
        };

        intYVF = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.valueOf((int)value);
            }
        };

        percentYVF = new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.valueOf((int)value)+"%";
            }
        };
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
        mRadarChart.getYAxis().setValueFormatter(intYVF);

        generate_radar_data(statsMap);

        mRadarChart.setData(new RadarData(Constants.DEFENSES, mSeen));
        mRadarChart.getYAxis().setAxisMaxValue((int) mSeen.getYMax() + 1);
        mRadarChart.getYAxis().setLabelCount((int) mSeen.getYMax() + 2, true);
        mRadarChart.notifyDataSetChanged();
        mRadarChart.invalidate();

        RadioButton radioButton = (RadioButton)view.findViewById(R.id.seen);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mSeen));
                mRadarChart.getYAxis().setAxisMaxValue((int) mSeen.getYMax() + 1);
                mRadarChart.getYAxis().setLabelCount((int) mSeen.getYMax() + 2, true);
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton.setChecked(true);

        radioButton = (RadioButton)view.findViewById(R.id.started);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mStarted));
                mRadarChart.getYAxis().setAxisMaxValue((int) mStarted.getYMax() + 1);
                mRadarChart.getYAxis().setLabelCount((int) mStarted.getYMax() + 2, true);
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.time);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mTime));
                switch ((int) mTime.getYMax()) {
                    case 30:
                        mRadarChart.getYAxis().setAxisMaxValue(30);
                        mRadarChart.getYAxis().setLabelCount(7, true);
                        break;
                    case 15:
                        mRadarChart.getYAxis().setAxisMaxValue(15);
                        mRadarChart.getYAxis().setLabelCount(4, true);
                        break;
                    case 10:
                        mRadarChart.getYAxis().setAxisMaxValue(10);
                        mRadarChart.getYAxis().setLabelCount(3, true);
                        break;
                    case 5:
                        mRadarChart.getYAxis().setAxisMaxValue(5);
                        mRadarChart.getYAxis().setLabelCount(2, true);
                        break;
                    case 0:
                        mRadarChart.getYAxis().setAxisMaxValue(1);
                        mRadarChart.getYAxis().setLabelCount(2, true);
                        break;
                }
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.auto);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mAutoCross));
                mRadarChart.getYAxis().setAxisMaxValue((int) mAutoCross.getYMax() + 1);
                mRadarChart.getYAxis().setLabelCount((int) mAutoCross.getYMax() + 2, true);
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES,mTeleopCross));
                mRadarChart.getYAxis().setAxisMaxValue((int)mTeleopCross.getYMax()+1);
                mRadarChart.getYAxis().setLabelCount((int) mTeleopCross.getYMax() + 2, true);
                mRadarChart.notifyDataSetChanged();
                mRadarChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.reach);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRadarChart.setData(new RadarData(Constants.DEFENSES, mReached));
                mRadarChart.getYAxis().setAxisMaxValue((int)mReached.getYMax()+1);
                mRadarChart.getYAxis().setLabelCount((int) mReached.getYMax() + 2, true);
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
        mLineY = mLineChart.getAxisLeft();
        mLineY.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setPinchZoom(false);

        generate_line_data(matchCursor);

        mLineChart.clear();
        mLineChart.setData(new LineData(mMatches, mAutoHighMade));
        mLineY.setAxisMaxValue((int) mAutoHighMade.getYMax() + 1);
        mLineY.setLabelCount((int) mAutoHighMade.getYMax() + 2, true);
        mLineY.setValueFormatter(intYVF);
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();

        radioButton = (RadioButton)view.findViewById(R.id.auto_high_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mAutoHighMade));
                mLineY.setAxisMaxValue((int) mAutoHighMade.getYMax() + 1);
                mLineY.setLabelCount((int) mAutoHighMade.getYMax() + 2, true);
                mLineY.setValueFormatter(intYVF);
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton.setChecked(true);

        radioButton = (RadioButton)view.findViewById(R.id.auto_low_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mAutoLowMade));
                mLineY.setAxisMaxValue((int) mAutoLowMade.getYMax()+1);
                mLineY.setLabelCount((int) mAutoLowMade.getYMax() + 2, true);
                mLineY.setValueFormatter(intYVF);
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop_high_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mTeleopHighMade));
                mLineY.setAxisMaxValue((int) mTeleopHighMade.getYMax()+1);
                mLineY.setLabelCount((int) mTeleopHighMade.getYMax() + 2, true);
                mLineY.setValueFormatter(intYVF);
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        radioButton = (RadioButton)view.findViewById(R.id.teleop_low_made);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLineChart.clear();
                mLineChart.setData(new LineData(mMatches, mTeleopLowMade));
                mLineY.setAxisMaxValue((int) mTeleopLowMade.getYMax()+1);
                mLineY.setLabelCount((int) mTeleopLowMade.getYMax() + 2, true);
                mLineY.setValueFormatter(intYVF);
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
                mLineY.setValueFormatter(percentYVF);
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
                mLineY.setValueFormatter(percentYVF);
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
                mLineY.setValueFormatter(percentYVF);
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
                mLineY.setValueFormatter(percentYVF);
                mLineChart.notifyDataSetChanged();
                mLineChart.invalidate();
            }
        });

        mBarChart = (BarChart)view.findViewById(R.id.bar_chart);
        mBarChart.setDescription("");
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mBarChart.getAxisLeft().setAxisMaxValue(15);
        mBarChart.getAxisLeft().setLabelCount(4, true);
        mBarChart.getAxisLeft().setValueFormatter(intYVF);
        mBarChart.setPinchZoom(false);
        mBarChart.setDoubleTapToZoomEnabled(false);

        generate_bar_data(matchCursor);
        mBarChart.setData(new BarData(mMatches,mEndgame));

        return view;
    }

    private void generate_radar_data(Map<String, ScoutValue> map)
    {
        ArrayList<Entry> entries = new ArrayList<>();
        if(map.containsKey(Constants.TOTAL_DEFENSES_SEEN[0])) {
            for (int i = 0; i < 9; i++) {
                entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_SEEN[i]).getInt(), i));
            }
        }
        mSeen = new RadarDataSet(entries,"Seen");
        mSeen.setColor(Color.RED);
        mSeen.setValueFormatter(intVF);

        entries = new ArrayList<>();
        if(map.containsKey(Constants.TOTAL_DEFENSES_STARTED[0])) {
            for (int i = 0; i < 9; i++) {
                entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_STARTED[i]).getInt(), i));
            }
        }
        mStarted = new RadarDataSet(entries,"Started in front of");
        mStarted.setColor(Color.RED);
        mStarted.setValueFormatter(intVF);

        entries = new ArrayList<>();
        if(map.containsKey(Constants.TOTAL_DEFENSES_AUTO_REACHED[0])) {
            for (int i = 0; i < 9; i++) {
                entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_AUTO_REACHED[i]).getInt(), i));
            }
        }
        mReached = new RadarDataSet(entries,"Auto Reach");
        mReached.setColor(Color.RED);
        mReached.setValueFormatter(intVF);

        entries = new ArrayList<>();
        if(map.containsKey(Constants.TOTAL_DEFENSES_AUTO_CROSSED[0])) {
            for (int i = 0; i < 9; i++) {
                entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_AUTO_CROSSED[i]).getInt(), i));
            }
        }
        mAutoCross = new RadarDataSet(entries,"Auto Cross");
        mAutoCross.setColor(Color.RED);
        mAutoCross.setValueFormatter(intVF);

        entries = new ArrayList<>();
        if(map.containsKey(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[0])) {
            for (int i = 0; i < 9; i++) {
                entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_TELEOP_CROSSED[i]).getInt(), i));
            }
        }
        mTeleopCross = new RadarDataSet(entries,"Teleop Cross");
        mTeleopCross.setColor(Color.RED);
        mTeleopCross.setValueFormatter(intVF);

        entries = new ArrayList<>();
        if(map.containsKey(Constants.TOTAL_DEFENSES_TELEOP_TIME[0]) && map.get(Constants.TOTAL_MATCHES).getInt() > 0) {
            for (int i = 0; i < 9; i++) {
                entries.add(new Entry(map.get(Constants.TOTAL_DEFENSES_TELEOP_TIME[i]).getInt() / map.get(Constants.TOTAL_MATCHES).getInt(), i));
            }
        }
        mTime = new RadarDataSet(entries,"Time");
        mTime.setColor(Color.RED);
        mTime.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "<" + String.valueOf(value);
            }
        });
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
        mAutoHighMade.setValueFormatter(intVF);

        mAutoLowMade = new LineDataSet(autoLowMadeEntries,"Auto Low Made");
        mAutoLowMade.setColor(Color.RED);
        mAutoLowMade.setAxisDependency(YAxis.AxisDependency.LEFT);
        mAutoLowMade.setValueFormatter(intVF);

        mTeleopHighMade = new LineDataSet(teleopHighMadeEntries,"Teleop High Made");
        mTeleopHighMade.setColor(Color.RED);
        mTeleopHighMade.setAxisDependency(YAxis.AxisDependency.LEFT);
        mTeleopHighMade.setValueFormatter(intVF);

        mTeleopLowMade = new LineDataSet(teleopLowMadeEntries,"Teleop Low Made");
        mTeleopLowMade.setColor(Color.RED);
        mTeleopLowMade.setAxisDependency(YAxis.AxisDependency.LEFT);
        mTeleopLowMade.setValueFormatter(intVF);

        mAutoHighPercent = new LineDataSet(autoHighPercentEntries,"Auto High Percent");
        mAutoHighPercent.setColor(Color.RED);
        mAutoHighPercent.setAxisDependency(YAxis.AxisDependency.LEFT);
        mAutoHighPercent.setValueFormatter(percentVF);

        mAutoLowPercent = new LineDataSet(autoLowPercentEntries,"Auto Low Percent");
        mAutoLowPercent.setColor(Color.RED);
        mAutoLowPercent.setAxisDependency(YAxis.AxisDependency.LEFT);
        mAutoLowPercent.setValueFormatter(percentVF);

        mTeleopHighPercent = new LineDataSet(teleopHighPercentEntries,"Teleop High Percent");
        mTeleopHighPercent.setColor(Color.RED);
        mTeleopHighPercent.setAxisDependency(YAxis.AxisDependency.LEFT);
        mTeleopHighPercent.setValueFormatter(percentVF);

        mTeleopLowPercent = new LineDataSet(teleopLowPercentEntries,"Teleop Low Percent");
        mTeleopLowPercent.setColor(Color.RED);
        mTeleopLowPercent.setAxisDependency(YAxis.AxisDependency.LEFT);
        mTeleopLowPercent.setValueFormatter(percentVF);
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
        mEndgame.setValueFormatter(intVF);
    }


}
