package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class CustomDefenseInput extends CustomScoutView {
    private String TAG = "CustomDefenseInput";

    RadioGroup crossNum;
    RadioGroup timeRadios;
    Button submit;
    JSONArray jsonArray;

    String key;

    public CustomDefenseInput(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_defense_input, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        ((TextView)findViewById(R.id.label)).setText(typedArray.getString(R.styleable.CustomScoutView_label));

        jsonArray = new JSONArray();

        timeRadios = (RadioGroup)findViewById(R.id.radiobuttons);

        for(int i = 0; i < Constants.TELEOP_DEFENSE_TIMES.length;i++)
        {
            RadioButton radioButton = new RadioButton(context, attrs);
            radioButton.setText(Constants.TELEOP_DEFENSE_TIMES[i]);
            radioButton.setId(i);
            timeRadios.addView(radioButton);
        }

        crossNum = (RadioGroup)findViewById(R.id.cross_num);

        RadioButton radioButton = new RadioButton(context, attrs);
        radioButton.setId(0);
        crossNum.addView(radioButton);
        crossNum.check(0);

        crossNum.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == jsonArray.length()) {
                    timeRadios.clearCheck();
                } else {
                    try {
                        String time = jsonArray.getString(checkedId);
                        timeRadios.check(Arrays.asList(Constants.TELEOP_DEFENSE_TIMES).indexOf(time));
                    } catch (JSONException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        });

        submit = (Button)findViewById(R.id.submit_button);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int whichCross = crossNum.getCheckedRadioButtonId();
                if (jsonArray.length() == whichCross) {
                    jsonArray.put(Constants.TELEOP_DEFENSE_TIMES[timeRadios.getCheckedRadioButtonId()]);
                    RadioButton radioButton1 = new RadioButton(getContext());
                    radioButton1.setId(whichCross + 1);
                    crossNum.addView(radioButton1);
                    crossNum.check(whichCross + 1);
                    timeRadios.clearCheck();
                }
                else
                {
                    jsonArray.remove(whichCross);
                    try {
                        jsonArray.put(whichCross, Constants.TELEOP_DEFENSE_TIMES[timeRadios.getCheckedRadioButtonId()]);
                        crossNum.check(whichCross + 1);
                        String string1 = jsonArray.getString(whichCross + 1);
                        timeRadios.check(Arrays.asList(Constants.TELEOP_DEFENSE_TIMES).indexOf(string1));
                    } catch (JSONException e) {
                        Log.d(TAG,e.getMessage());
                    }
                }
            }
        });

    }

    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        String saveValue = jsonArray.toString();
        map.put(key,new ScoutValue(saveValue));
    }

    // Custom restore
    @Override
    public void restoreFromMap(Map<String, ScoutValue> map) {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            String restoreValue = sv.getString();
            try {
                jsonArray = new JSONArray(restoreValue);
                if(jsonArray.length() > 0)
                {
                    timeRadios.check(Arrays.asList(Constants.TELEOP_DEFENSE_TIMES).indexOf(jsonArray.getString(0)));
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        RadioButton radioButton = new RadioButton(getContext());
                        radioButton.setId(i+1);
                        crossNum.addView(radioButton);
                    }
                    crossNum.check(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
