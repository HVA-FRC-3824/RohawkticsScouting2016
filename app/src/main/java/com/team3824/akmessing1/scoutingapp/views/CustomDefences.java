package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;

import java.util.Arrays;
import java.util.Map;

public class CustomDefences extends CustomScoutView
{
    public CustomDefences(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_defences, this);
    }

    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
    }

    @Override
    public void restoreFromMap(Map<String, ScoutValue> map)
    {
    }
}
