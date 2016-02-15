package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.util.Map;

public class CustomEdittext extends CustomScoutView {

    private String TAG = "CustomEdittext";

    private TextView label;
    private EditText edittext;
    private String key;

    public CustomEdittext(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_edittext, this);

        edittext = (EditText)this.findViewById(R.id.edittext);
        label = (TextView)this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();
    }

    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        map.put(key, new ScoutValue(String.valueOf(edittext.getText())));
    }

    @Override
    public void restoreFromMap(Map<String, ScoutValue> map)
    {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            edittext.setText(sv.getString());
        }
    }
}
