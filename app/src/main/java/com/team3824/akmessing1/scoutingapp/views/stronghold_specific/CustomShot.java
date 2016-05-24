package com.team3824.akmessing1.scoutingapp.views.stronghold_specific;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.views.CustomScoutView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andrew Messing
 */
public class CustomShot extends CustomScoutView implements View.OnClickListener, OnCheckedChangeListener {

    private final String TAG = "CustomShot";

    private RadioGroup numShots;
    private Spinner aimTime, shotPosition;
    private ToggleButton shotHitMiss;
    private JSONArray jsonArray;
    private Button submit, delete;

    ArrayList<String> positionList;
    ArrayList<String> timeList;

    private String key;

    public CustomShot(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_shot, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        ((TextView) findViewById(R.id.label)).setText(typedArray.getString(R.styleable.CustomScoutView_label));

        jsonArray = new JSONArray();


        numShots = (RadioGroup) findViewById(R.id.shot_num);
        RadioButton radioButton = new RadioButton(context, attrs);
        radioButton.setId(0);
        numShots.addView(radioButton);
        numShots.check(0);

        numShots.setOnCheckedChangeListener(this);

        shotPosition = (Spinner) findViewById(R.id.shot_position);
        //There are fewer places to score low goal
        positionList = new ArrayList<>(Arrays.asList(Constants.Teleop_Inputs.TELEOP_SHOT_POSITIONS));
        if (key.equals(Constants.Teleop_Inputs.TELEOP_LOW_SHOT)) {
            positionList.remove(Constants.Teleop_Inputs.OUTER_WORKS);
            positionList.remove(Constants.Teleop_Inputs.ON_NEAR_CENTER_BATTER);
            positionList.remove(Constants.Teleop_Inputs.ALIGNMENT_LINE);
        }
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, positionList);
        shotPosition.setAdapter(positionAdapter);

        aimTime = (Spinner) findViewById(R.id.aim_time);
        timeList = new ArrayList<>(Arrays.asList(Constants.Teleop_Inputs.TELEOP_AIM_TIMES));
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, timeList);
        aimTime.setAdapter(timeAdapter);

        shotHitMiss = (ToggleButton) findViewById(R.id.shot_hit_miss);

        submit = (Button) findViewById(R.id.submit_button);
        submit.setOnClickListener(this);

        delete = (Button) findViewById(R.id.delete_button);
        delete.setOnClickListener(this);

    }

    @Override
    public String writeToMap(ScoutMap map) {
        String saveValue = jsonArray.toString();
        map.put(key, saveValue);
        return "";
    }

    // Custom restore
    @Override
    public void restoreFromMap(ScoutMap map) {
        ScoutValue sv = map.get(key);
        if (sv != null) {
            String restoreValue = sv.getString();
            try {
                jsonArray = new JSONArray(restoreValue);
                if (jsonArray.length() > 0) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String position = jsonObject.getString(Constants.Teleop_Inputs.SHOT_POSITION);
                        String time = jsonObject.getString(Constants.Teleop_Inputs.AIM_TIME);
                        Boolean hit = jsonObject.getBoolean(Constants.Teleop_Inputs.SHOT_HIT_MISS);
                        shotPosition.setSelection(positionList.indexOf(position));
                        aimTime.setSelection(timeList.indexOf(time));
                        shotHitMiss.setChecked(hit);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error: ", e);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        RadioButton radioButton = new RadioButton(getContext());
                        radioButton.setId(i + 1);
                        numShots.addView(radioButton);
                    }
                    numShots.check(0);
                    delete.setVisibility(VISIBLE);
                    submit.setText("Update");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error: ", e);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int whichShot = numShots.getCheckedRadioButtonId();
        switch (v.getId()) {
            case R.id.submit_button:
                if (jsonArray.length() == whichShot) {
                    if (shotPosition.getSelectedItemPosition() != 0 && aimTime.getSelectedItemPosition() != 0) {
                        String position = (String) shotPosition.getSelectedItem();
                        String time = (String) aimTime.getSelectedItem();
                        Boolean hit = shotHitMiss.isChecked();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(Constants.Teleop_Inputs.SHOT_POSITION, position);
                            jsonObject.put(Constants.Teleop_Inputs.AIM_TIME, time);
                            jsonObject.put(Constants.Teleop_Inputs.SHOT_HIT_MISS, hit);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error: ", e);
                        }
                        jsonArray.put(jsonObject);
                        shotPosition.setSelection(0);
                        aimTime.setSelection(0);
                        shotHitMiss.setChecked(false);
                        RadioButton radioButton1 = new RadioButton(getContext());
                        radioButton1.setId(whichShot + 1);
                        numShots.addView(radioButton1);
                        numShots.check(whichShot + 1);
                    }
                } else {
                    if (shotPosition.getSelectedItemPosition() != 0 && aimTime.getSelectedItemPosition() != 0) {
                        String position = (String) shotPosition.getSelectedItem();
                        String time = (String) aimTime.getSelectedItem();
                        Boolean hit = shotHitMiss.isChecked();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(Constants.Teleop_Inputs.SHOT_POSITION, position);
                            jsonObject.put(Constants.Teleop_Inputs.AIM_TIME, time);
                            jsonObject.put(Constants.Teleop_Inputs.SHOT_HIT_MISS, hit);
                            jsonArray.put(whichShot, jsonObject);
                            jsonObject = jsonArray.getJSONObject(whichShot + 1);
                            position = jsonObject.getString(Constants.Teleop_Inputs.SHOT_POSITION);
                            time = jsonObject.getString(Constants.Teleop_Inputs.AIM_TIME);
                            hit = jsonObject.getBoolean(Constants.Teleop_Inputs.SHOT_HIT_MISS);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error: ", e);
                        }
                        shotPosition.setSelection(positionList.indexOf(position));
                        aimTime.setSelection(timeList.indexOf(time));
                        shotHitMiss.setChecked(hit);
                        numShots.check(whichShot + 1);
                    }
                }
                break;
            case R.id.delete_button:
                int lastRadio = jsonArray.length();
                jsonArray.remove(whichShot);
                numShots.removeViewAt(lastRadio);
                numShots.check(whichShot);
                if (whichShot < jsonArray.length()) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(whichShot);
                        String position = jsonObject.getString(Constants.Teleop_Inputs.SHOT_POSITION);
                        String time = jsonObject.getString(Constants.Teleop_Inputs.AIM_TIME);
                        Boolean hit = jsonObject.getBoolean(Constants.Teleop_Inputs.SHOT_HIT_MISS);
                        shotPosition.setSelection(positionList.indexOf(position));
                        aimTime.setSelection(timeList.indexOf(time));
                        shotHitMiss.setChecked(hit);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error: ", e);
                    }
                } else {
                    shotPosition.setSelection(0);
                    aimTime.setSelection(0);
                    shotHitMiss.setChecked(false);
                    submit.setText("Submit");
                    delete.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == jsonArray.length()) {
            shotPosition.setSelection(0);
            aimTime.setSelection(0);
            shotHitMiss.setChecked(false);
            delete.setVisibility(GONE);
            submit.setText("Submit");
        } else {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(checkedId);
                String position = jsonObject.getString(Constants.Teleop_Inputs.SHOT_POSITION);
                String time = jsonObject.getString(Constants.Teleop_Inputs.AIM_TIME);
                Boolean hit = jsonObject.getBoolean(Constants.Teleop_Inputs.SHOT_HIT_MISS);
                shotPosition.setSelection(positionList.indexOf(position));
                aimTime.setSelection(timeList.indexOf(time));
                shotHitMiss.setChecked(hit);
            } catch (JSONException e) {
                Log.e(TAG, "Error: ", e);
            }
            delete.setVisibility(VISIBLE);
            submit.setText("Update");
        }
    }
}
