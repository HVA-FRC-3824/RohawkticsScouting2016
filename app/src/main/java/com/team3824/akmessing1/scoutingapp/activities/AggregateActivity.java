package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.services.AggregateService;

public class AggregateActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = "AggregateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate);

        // Back button takes the user back to the start screen
        Button button = (Button)findViewById(R.id.back);
        button.setOnClickListener(this);

        // AggregateService Update starts the aggregate service with the update option
        button = (Button)findViewById(R.id.aggregate_update);
        button.setOnClickListener(this);

        // AggregateService Reset starts the aggregate service with the update option off
        button = (Button)findViewById(R.id.aggregate_reset);
        button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.back:
                this.finish();
                break;
            case R.id.aggregate_update:
                intent = new Intent(AggregateActivity.this, AggregateService.class);
                intent.putExtra(Constants.UPDATE,true);
                startService(intent);
                break;
            case R.id.aggregate_reset:
                intent = new Intent(AggregateActivity.this,AggregateService.class);
                intent.putExtra(Constants.UPDATE,false);
                startService(intent);
                break;
        }
    }
}
