package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.team3824.akmessing1.scoutingapp.R;


public class CustomDefenses extends RelativeLayout{
    private String TAG = "CustomDefense";
    public CustomDefenses(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_defenses, this);
    }
}
