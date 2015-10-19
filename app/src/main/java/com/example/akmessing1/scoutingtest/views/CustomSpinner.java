package com.example.akmessing1.scoutingtest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.akmessing1.scoutingtest.R;

public class CustomSpinner extends CustomScoutView{

    private TextView label;
    private Spinner spinner;
    private String[] resourceStrings;

    public CustomSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_spinner, this);

        label = (TextView)this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        TypedArray typedArray1 = context.obtainStyledAttributes(attrs,R.styleable.CustomSpinner);
        int spinnerValuesId = typedArray1.getResourceId(R.styleable.CustomSpinner_values, 0);
        // Store a local copy of resource strings
        CharSequence[] strings = context.getResources().getTextArray(spinnerValuesId);

        resourceStrings = new String[strings.length];
        System.arraycopy(strings, 0, resourceStrings, 0, strings.length);
        typedArray.recycle();

        spinner = (Spinner)this.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, resourceStrings);
        spinner.setAdapter(adapter);


    }
}
