package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;

import java.util.Arrays;
import java.util.Map;

public class CustomRadioButtons extends CustomScoutView {
    private TextView label;
    private String[] resourceStrings;
    RadioGroup radios;
    private String key;

    public CustomRadioButtons(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_radio, this);

        label = (TextView)findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        TypedArray typedArray1 = context.obtainStyledAttributes(attrs,R.styleable.CustomRadioButtons);
        int radioValuesId = typedArray1.getResourceId(R.styleable.CustomRadioButtons_radio_values, 0);
        // Store a local copy of resource strings
        CharSequence[] strings = context.getResources().getTextArray(radioValuesId);

        resourceStrings = new String[strings.length];
        System.arraycopy(strings, 0, resourceStrings, 0, strings.length);
        typedArray.recycle();

        radios = (RadioGroup)findViewById(R.id.radiobuttons);

        for(int i = 0; i < resourceStrings.length;i++)
        {
            RadioButton radioButton = new RadioButton(context, attrs);
            radioButton.setText(resourceStrings[i]);
            radioButton.setId(i);
            radios.addView(radioButton);
        }
        radios.check(0);
    }

    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        if(radios.getCheckedRadioButtonId() != -1) {
            map.put(key, new ScoutValue(resourceStrings[radios.getCheckedRadioButtonId()]));
        }
        else
        {
            map.put(key, new ScoutValue(""));
        }
    }

    @Override
    public void restoreFromMap(Map<String, ScoutValue> map) {
        ScoutValue sv = map.get(key);
        if (sv != null) {
            String selectedString = sv.getString();
            int index = Arrays.asList(resourceStrings).indexOf(selectedString);
            if(index != -1) {
                radios.check(index);
            }
        }
    }
}
