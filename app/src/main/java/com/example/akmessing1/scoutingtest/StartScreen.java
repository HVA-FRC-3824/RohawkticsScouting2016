package com.example.akmessing1.scoutingtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;

public class StartScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("appData", Context.MODE_PRIVATE);
        String type = sharedPreferences.getString("type","Match Scout");
        if(type.equals("Match Scout"))
        {
            Button button = (Button)findViewById(R.id.scoutMatch_button);
            button.setVisibility(View.VISIBLE);
        }
        else if(type.equals("Pit Scout"))
        {
            Button button = (Button)findViewById(R.id.scoutPit_button);
            button.setVisibility(View.VISIBLE);
        }
        else if(type.equals("Super Scout"))
        {
            Button button = (Button)findViewById(R.id.superScout_button);
            button.setVisibility(View.VISIBLE);
        }
        else if(type.equals("Drive Team"))
        {
            Button button = (Button)findViewById(R.id.viewTeam_button);
            button.setVisibility(View.VISIBLE);
        }
        else if(type.equals("Strategy"))
        {
            Button button = (Button)findViewById(R.id.viewTeam_button);
            button.setVisibility(View.VISIBLE);
            button = (Button)findViewById(R.id.viewPicklist_button);
            button.setVisibility(View.VISIBLE);
        }
        else if(type.equals("Admin"))
        {
            Button button = (Button)findViewById(R.id.scoutMatch_button);
            button.setVisibility(View.VISIBLE);
            button = (Button)findViewById(R.id.scoutPit_button);
            button.setVisibility(View.VISIBLE);
            button = (Button)findViewById(R.id.superScout_button);
            button.setVisibility(View.VISIBLE);
            button = (Button)findViewById(R.id.viewTeam_button);
            button.setVisibility(View.VISIBLE);
            button = (Button)findViewById(R.id.viewPicklist_button);
            button.setVisibility(View.VISIBLE);
        }
    }

    public void match_schedule(View view)
    {
        Intent intent = new Intent(this, matchSchedule.class);
        startActivity(intent);
    }

    public void scout_match(View view)
    {
        Intent intent = new Intent(this, matchList.class);
        startActivity(intent);
    }

    public void scout_pit(View view)
    {

    }

    public void view_team(View view)
    {

    }

    public void view_picklist(View view)
    {

    }

    public void settings(View view)
    {
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }
}
