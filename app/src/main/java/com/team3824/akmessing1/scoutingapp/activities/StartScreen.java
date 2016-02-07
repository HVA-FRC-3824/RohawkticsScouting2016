package com.team3824.akmessing1.scoutingapp.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.services.SyncService;

import java.util.Set;

public class StartScreen extends AppCompatActivity implements View.OnClickListener{
    private static String TAG = "StartScreen";
    private BluetoothAdapter bluetoothAdapter = null;

    // Buttons become visible based on the role
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        ((TextView)findViewById(R.id.version)).setText("Version: 1.0.1");

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString(Constants.USER_TYPE, "");
        setupButton(R.id.settings_button);
        switch (type) {
            case Constants.MATCH_SCOUT: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.scoutMatch_button);
                break;
            }
            case Constants.PIT_SCOUT: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.scoutPit_button);
                break;
            }
            case Constants.SUPER_SCOUT: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.superScout_button);
                setupButton(R.id.sync_button);
                break;
            }
            case Constants.DRIVE_TEAM: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.matchPlanning_button);
                setupButton(R.id.viewTeam_button);
                setupButton(R.id.viewMatch_button);
                setupButton(R.id.viewEvent_button);
                setupButton(R.id.sync_button);
                setupButton(R.id.aggregate_button);
                setupButton(R.id.feedback_button);
                setupButton(R.id.bluetooth_button);
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(bluetoothAdapter.isEnabled()) {
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.GREEN);
                }
                else
                {
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.RED);
                }
                break;
            }
            case Constants.STRATEGY: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.matchPlanning_button);
                setupButton(R.id.viewTeam_button);
                setupButton(R.id.viewMatch_button);
                setupButton(R.id.viewEvent_button);
                setupButton(R.id.viewPicklist_button);
                setupButton(R.id.sync_button);
                setupButton(R.id.aggregate_button);
                break;
            }
            case Constants.ADMIN: {
                setupButton(R.id.matchSchedule_button);
                setupButton(R.id.scoutMatch_button);
                setupButton(R.id.scoutPit_button);
                setupButton(R.id.superScout_button);
                setupButton(R.id.matchPlanning_button);
                setupButton(R.id.viewTeam_button);
                setupButton(R.id.viewMatch_button);
                setupButton(R.id.viewEvent_button);
                setupButton(R.id.viewPicklist_button);
                setupButton(R.id.sync_button);
                setupButton(R.id.aggregate_button);
                setupButton(R.id.feedback_button);
                setupButton(R.id.database_button);
                setupButton(R.id.bluetooth_button);
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if(bluetoothAdapter.isEnabled()) {
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.GREEN);
                }
                else
                {
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.RED);
                }
                break;
            }
        }
    }

    private void setupButton(int btn)
    {
        Button button = (Button)findViewById(btn);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId())
        {
            case R.id.settings_button:
                intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.matchSchedule_button:
                intent = new Intent(this, MatchSchedule.class);
                startActivity(intent);
                break;
            case R.id.scoutMatch_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra("nextPage", "match_scouting");
                startActivity(intent);
                break;
            case R.id.scoutPit_button:
                intent = new Intent(this, PitList.class);
                startActivity(intent);
                break;
            case R.id.superScout_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra("nextPage", "super_scouting");
                startActivity(intent);
                break;
            case R.id.feedback_button:
                intent = new Intent(this, OurMatchList.class);
                startActivity(intent);
                break;
            case R.id.matchPlanning_button:
                intent = new Intent(this, MatchPlanning.class);
                startActivity(intent);
                break;
            case R.id.viewTeam_button:
                intent = new Intent(this, TeamList.class);
                startActivity(intent);
                break;
            case R.id.viewMatch_button:
                intent = new Intent(this, MatchList.class);
                intent.putExtra("nextPage","match_viewing");
                startActivity(intent);
                break;
            case R.id.viewEvent_button:
                intent = new Intent(this, EventView.class);
                startActivity(intent);
                break;
            case R.id.viewPicklist_button:
                intent = new Intent(this, PickList.class);
                startActivity(intent);
                break;
            case R.id.sync_button:
                intent = new Intent(this, SyncActivity.class);
                startActivity(intent);
                break;
            case R.id.aggregate_button:
                intent = new Intent(this, AggregateActivity.class);
                startActivity(intent);
                break;
            case R.id.database_button:
                intent = new Intent(this, DatabaseManagement.class);
                startActivity(intent);
                break;
            case R.id.bluetooth_button:
                if(bluetoothAdapter.isEnabled())
                {
                    bluetoothAdapter.disable();
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.RED);
                }
                else
                {
                    bluetoothAdapter.enable();
                    findViewById(R.id.bluetooth_button).setBackgroundColor(Color.GREEN);
                    startService(new Intent(this, SyncService.class));
                }
        }
    }
}
