package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

public abstract class CustomScoutView extends RelativeLayout {
    public CustomScoutView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public String writeToMap(ScoutMap map)
    {
        return "";
    }

    public void restoreFromMap(ScoutMap map)
    {

    }
}
