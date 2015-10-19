package com.example.akmessing1.scoutingtest.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.akmessing1.scoutingtest.R;

/**
 * Created by akmessing1 on 10/17/15.
 */
public class CustomEdittext extends CustomScoutView {

    private TextView label;
    private EditText edittext;

    public CustomEdittext(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_edittext, this);

        edittext = (EditText)this.findViewById(R.id.edittext);
        label = (TextView)this.findViewById(R.id.label);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        typedArray.recycle();
    }

}
