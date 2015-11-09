package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;

import java.util.Map;

// Custom field with label and clickable counter
public class CustomCounter extends CustomScoutView{

    private TextView label;
    private TextView countView;
    private int count;
    private String key;

    public CustomCounter(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_counter, this);

        label = (TextView)this.findViewById(R.id.label);

        // Set label and get key
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();

        // Setup counter
        count = 0;

        countView = (TextView)this.findViewById(R.id.counter);
        countView.setText(Integer.toString(count));

        // clicking on the field increments the counter
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                countView.setText(Integer.toString(count));
            }
        });

        // a long click decrements the counter
        this.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (count > 0) {
                    count--;
                    playSoundEffect(SoundEffectConstants.CLICK);
                }
                countView.setText(Integer.toString(count));
                return true;
            }
        });
    }

    // Custom save
    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        map.put(key,new ScoutValue(count));
    }

    // Custom restore
    @Override
    public void restoreFromMap(Map<String, ScoutValue> map)
    {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            count = sv.getInt();
            countView.setText(Integer.toString(count));
        }
    }
}
