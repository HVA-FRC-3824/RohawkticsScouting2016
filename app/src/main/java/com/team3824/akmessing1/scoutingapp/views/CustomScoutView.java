package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.util.Map;

public abstract class CustomScoutView extends RelativeLayout {
    public CustomScoutView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void writeToMap(Map<String, ScoutValue> map)
    {
    }

    public void restoreFromMap(Map<String, ScoutValue> map)
    {

    }
}
