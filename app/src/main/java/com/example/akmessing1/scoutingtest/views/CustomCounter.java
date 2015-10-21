package com.example.akmessing1.scoutingtest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akmessing1.scoutingtest.R;
import com.example.akmessing1.scoutingtest.ScoutValue;

import java.util.Map;

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

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();

        count = 0;

        countView = (TextView)this.findViewById(R.id.counter);
        countView.setText(Integer.toString(count));

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                countView.setText(Integer.toString(count));
            }
        });

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

    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        map.put(key,new ScoutValue(count));
    }

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
