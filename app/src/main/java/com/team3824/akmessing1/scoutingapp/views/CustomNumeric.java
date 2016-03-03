package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.util.Map;

// Custom textbox with label only for numbers
public class CustomNumeric extends CustomScoutView{

    private String TAG = "CustomNumeric";

    private TextView label;
    private EditText numeric;
    private String key;

    public CustomNumeric(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_numeric, this);

        numeric = (EditText)this.findViewById(R.id.numeric);
        //numeric.setText("0");
        label = (TextView)this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();
    }

    @Override
    public String writeToMap(ScoutMap map)
    {
        if(String.valueOf(numeric.getText()).equals(""))
        {
            map.put(key, 0.0f);
        }
        else {
            map.put(key, Float.valueOf(String.valueOf(numeric.getText())));
        }

        return "";
    }

    @Override
    public void restoreFromMap(ScoutMap map)
    {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            numeric.setText(String.valueOf(sv.getFloat()));
        }
    }
}
