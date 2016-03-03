package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.util.Arrays;
import java.util.Map;

public class CustomSpinner extends CustomScoutView{

    private TextView label;
    private Spinner spinner;
    private String[] resourceStrings;
    private String key;

    public CustomSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_spinner, this);

        label = (TextView)this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        TypedArray typedArray1 = context.obtainStyledAttributes(attrs,R.styleable.CustomSpinner);
        int spinnerValuesId = typedArray1.getResourceId(R.styleable.CustomSpinner_spinner_values, 0);
        // Store a local copy of resource strings
        CharSequence[] strings = context.getResources().getTextArray(spinnerValuesId);

        resourceStrings = new String[strings.length];
        System.arraycopy(strings, 0, resourceStrings, 0, strings.length);
        typedArray.recycle();

        spinner = (Spinner)this.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, resourceStrings);
        spinner.setAdapter(adapter);
    }

    @Override
    public String writeToMap(ScoutMap map)
    {
        map.put(key, String.valueOf(spinner.getSelectedItem()));
        return "";
    }

    @Override
    public void restoreFromMap(ScoutMap map)
    {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            spinner.setSelection(Arrays.asList(resourceStrings).indexOf(sv.getString()));
        }
    }
}
