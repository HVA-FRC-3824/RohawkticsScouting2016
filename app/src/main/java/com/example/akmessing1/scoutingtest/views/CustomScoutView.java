package com.example.akmessing1.scoutingtest.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.akmessing1.scoutingtest.ScoutValue;

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
