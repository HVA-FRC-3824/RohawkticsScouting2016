package com.example.akmessing1.scoutingtest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.ScoutValue;

import java.util.Map;

public class CustomCheckbox extends CustomScoutView {

    private String TAG = "CustomCheckbox";
    private final CheckBox checkbox;
    private String key;

    public CustomCheckbox(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_checkbox, this);

        checkbox = (CheckBox)this.findViewById(R.id.checkbox);
        TextView label = (TextView) this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(!checkbox.isChecked());
            }
        });
    }

    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        map.put(key, new ScoutValue(checkbox.isChecked()? 1 : 0));
    }

    @Override
    public void restoreFromMap(Map<String, ScoutValue> map)
    {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            checkbox.setChecked(sv.getInt() != 0);
        }
    }
}
