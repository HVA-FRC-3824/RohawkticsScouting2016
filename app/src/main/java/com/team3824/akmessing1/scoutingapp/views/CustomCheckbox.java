package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;

import java.util.Map;

// Adds a label to the checkbox and allows custom saving and restoring
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

        // Set label and get key
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();

        // Clicking anywhere on the field will affect the checkbox
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(!checkbox.isChecked());
            }
        });
    }

    // Custom save
    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        map.put(key, new ScoutValue(checkbox.isChecked()? 1 : 0));
    }

    // Custom restore
    @Override
    public void restoreFromMap(Map<String, ScoutValue> map)
    {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            checkbox.setChecked(sv.getInt() != 0);
        }
    }
}
