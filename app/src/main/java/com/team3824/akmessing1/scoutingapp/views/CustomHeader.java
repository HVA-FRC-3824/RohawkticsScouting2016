package com.team3824.akmessing1.scoutingapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.activities.StartScreen;

public class CustomHeader extends RelativeLayout {

    final Context mContext;
    Button back;
    Button home;
    public CustomHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_header, this);

        back = (Button)this.findViewById(R.id.back_button);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)mContext).finish();
            }
        });
        home = (Button)this.findViewById(R.id.home_button);
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StartScreen.class);
                mContext.startActivity(intent);
            }
        });
    }

    public void removeHome()
    {
        home.setVisibility(INVISIBLE);
        home.setOnClickListener(null);
    }

}
