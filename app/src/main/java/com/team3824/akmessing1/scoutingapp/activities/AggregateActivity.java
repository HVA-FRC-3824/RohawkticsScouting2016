package com.team3824.akmessing1.scoutingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.team3824.akmessing1.scoutingapp.R;

public class AggregateActivity extends AppCompatActivity {
    private String TAG = "AggregateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregate);

        // Back button takes the user back to the start screen
        Button button = (Button)findViewById(R.id.back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AggregateActivity.this,StartScreen.class);
                startActivity(intent);
            }
        });

        // Aggregate Update starts the aggregate service with the update option
        button = (Button)findViewById(R.id.aggregate_update);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AggregateActivity.this, Aggregate.class);
                intent.putExtra("update",true);
                startService(intent);
            }
        });

        // Aggregate Reset starts the aggregate service with the update option off
        button = (Button)findViewById(R.id.aggregate_reset);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AggregateActivity.this,Aggregate.class);
                intent.putExtra("update",false);
                startService(intent);
            }
        });

    }
}
