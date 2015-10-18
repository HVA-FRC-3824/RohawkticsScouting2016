package com.example.akmessing1.scoutingtest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akmessing1.scoutingtest.R;

/**
 * Created by akmessing1 on 10/17/15.
 */
public class CustomCheckbox extends RelativeLayout {

    private TextView label;
    private CheckBox checkbox;

    public CustomCheckbox(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_checkbox, this);

        checkbox = (CheckBox)this.findViewById(R.id.checkbox);
        label = (TextView)this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCheckbox);
        label.setText(typedArray.getString(R.styleable.CustomCheckbox_label));
        typedArray.recycle();
    }

}
