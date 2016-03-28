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

/**
 * @author Andrew Messing
 */
public class CustomIntake extends CustomScoutView implements View.OnClickListener, OnCheckedChangeListener {

    private final String TAG = "CustomIntake";

    private RadioGroup numIntake;
    private Spinner intakeTime, intakePosition;
    private JSONArray jsonArray;
    private Button submit, delete;

    ArrayList<String> positionList, timeList;

    private String key;

    public CustomIntake(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_intake, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        ((TextView) findViewById(R.id.label)).setText(typedArray.getString(R.styleable.CustomScoutView_label));

        jsonArray = new JSONArray();

        numIntake = (RadioGroup) findViewById(R.id.intake_num);
        RadioButton radioButton = new RadioButton(context, attrs);
        radioButton.setId(0);
        numIntake.addView(radioButton);
        numIntake.check(0);

        numIntake.setOnCheckedChangeListener(this);

        intakePosition = (Spinner) findViewById(R.id.intake_position);
        positionList = new ArrayList<>(Arrays.asList(Constants.Teleop_Inputs.TELEOP_INTAKE_POSITIONS));
        ArrayAdapter<String> positionAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, positionList);
        intakePosition.setAdapter(positionAdapter);

        intakeTime = (Spinner) findViewById(R.id.intake_time);
        timeList = new ArrayList<>(Arrays.asList(Constants.Teleop_Inputs.TELEOP_INTAKE_TIMES));
        ArrayAdapter<String> speedAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, timeList);
        intakeTime.setAdapter(speedAdapter);

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
                        String position = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_POSITION);
                        String speed = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_TIME);
                        intakePosition.setSelection(positionList.indexOf(position));
                        intakeTime.setSelection(timeList.indexOf(speed));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error: ", e);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        RadioButton radioButton = new RadioButton(getContext());
                        radioButton.setId(i + 1);
                        numIntake.addView(radioButton);
                    }
                    numIntake.check(0);
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
        int whichIntake = numIntake.getCheckedRadioButtonId();
        switch (v.getId()) {
            case R.id.submit_button:
                if (jsonArray.length() == whichIntake) {
                    if (intakePosition.getSelectedItemPosition() != 0 && intakeTime.getSelectedItemPosition() != 0) {
                        String position = (String) intakePosition.getSelectedItem();
                        String speed = (String) intakeTime.getSelectedItem();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(Constants.Teleop_Inputs.INTAKE_POSITION, position);
                            jsonObject.put(Constants.Teleop_Inputs.INTAKE_TIME, speed);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error: ", e);
                        }
                        jsonArray.put(jsonObject);
                        intakePosition.setSelection(0);
                        intakeTime.setSelection(0);
                        RadioButton radioButton1 = new RadioButton(getContext());
                        radioButton1.setId(whichIntake + 1);
                        numIntake.addView(radioButton1);
                        numIntake.check(whichIntake + 1);
                    }
                } else {
                    if (intakePosition.getSelectedItemPosition() != 0 && intakeTime.getSelectedItemPosition() != 0) {
                        String position = (String) intakePosition.getSelectedItem();
                        String speed = (String) intakeTime.getSelectedItem();
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(Constants.Teleop_Inputs.INTAKE_POSITION, position);
                            jsonObject.put(Constants.Teleop_Inputs.INTAKE_TIME, speed);
                            jsonArray.put(whichIntake, jsonObject);
                            jsonObject = jsonArray.getJSONObject(whichIntake + 1);
                            position = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_POSITION);
                            speed = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_TIME);
                        } catch (JSONException e) {
                            Log.e(TAG, "Error: ", e);
                        }
                        intakePosition.setSelection(positionList.indexOf(position));
                        intakeTime.setSelection(timeList.indexOf(speed));
                        numIntake.check(whichIntake + 1);
                    }
                }
                break;
            case R.id.delete_button:
                int lastRadio = jsonArray.length();
                jsonArray.remove(whichIntake);
                numIntake.removeViewAt(lastRadio);
                numIntake.check(whichIntake);
                if (whichIntake < jsonArray.length()) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(whichIntake);
                        String position = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_POSITION);
                        String speed = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_TIME);
                        intakePosition.setSelection(positionList.indexOf(position));
                        intakeTime.setSelection(timeList.indexOf(speed));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error: ", e);
                    }
                } else {
                    intakePosition.setSelection(0);
                    intakeTime.setSelection(0);
                    submit.setText("Submit");
                    delete.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == jsonArray.length()) {
            intakePosition.setSelection(0);
            intakeTime.setSelection(0);
            delete.setVisibility(GONE);
            submit.setText("Submit");
        } else {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(checkedId);
                String position = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_POSITION);
                String speed = jsonObject.getString(Constants.Teleop_Inputs.INTAKE_TIME);
                intakePosition.setSelection(positionList.indexOf(position));
                intakeTime.setSelection(timeList.indexOf(speed));
            } catch (JSONException e) {
                Log.e(TAG, "Error: ", e);
            }
            delete.setVisibility(VISIBLE);
            submit.setText("Update");
        }
    }
}
