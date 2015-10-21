package com.example.akmessing1.scoutingtest.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.akmessing1.scoutingtest.R;

import java.util.Arrays;

public class Settings extends AppCompatActivity {

    // Populate the settings fields with their respective values
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sharedPref = getSharedPreferences( "appData", Context.MODE_PRIVATE );

        Spinner typeSelector = (Spinner)findViewById(R.id.typeSelector);
        String[] types = new String[]{"Match Scout", "Pit Scout", "Super Scout", "Drive Team", "Strategy", "Admin"};
        ArrayAdapter<String> adapter0 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types);
        typeSelector.setAdapter(adapter0);
        typeSelector.setSelection(Arrays.asList(types).indexOf(sharedPref.getString("type", "Match Scout")));
        typeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }
        });

        Spinner colorSelector = (Spinner)findViewById(R.id.colorSelector);
        String[] colors = new String[]{"Blue", "Red"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colors);
        colorSelector.setAdapter(adapter1);
        colorSelector.setSelection(Arrays.asList(colors).indexOf(sharedPref.getString("alliance_color", "Blue")));
        colorSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }
        });

        Spinner numSelector = (Spinner)findViewById(R.id.numSelector);
        String[] numbers = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, numbers);
        numSelector.setAdapter(adapter2);
        numSelector.setSelection(Arrays.asList(numbers).indexOf(Integer.toString(sharedPref.getInt("alliance_number", 1))));
        numSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }
        });

        EditText eventID = (EditText)findViewById(R.id.eventID);
        eventID.setText(sharedPref.getString("event_id",""));
        eventID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView tv = (TextView)findViewById(R.id.settingsSavedText);
                tv.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // back button goes to the start screen
    public void back(View view)
    {
        Intent intent = new Intent(this,StartScreen.class);
        startActivity(intent);
    }

    // Save the current settings to shared preferences
    public void save_settings(View view)
    {
        Spinner typeSelector = (Spinner)findViewById(R.id.typeSelector);
        Spinner colorSelector = (Spinner)findViewById(R.id.colorSelector);
        Spinner numSelector = (Spinner)findViewById(R.id.numSelector);
        EditText eventID = (EditText)findViewById(R.id.eventID);

        SharedPreferences.Editor prefEditor = getSharedPreferences( "appData", Context.MODE_PRIVATE ).edit();
        prefEditor.putString("type", String.valueOf(typeSelector.getSelectedItem()));
        prefEditor.putString("alliance_color", String.valueOf(colorSelector.getSelectedItem()));
        prefEditor.putInt("alliance_number", Integer.parseInt(String.valueOf(numSelector.getSelectedItem())));
        prefEditor.putString("event_id", String.valueOf(eventID.getText()));
        prefEditor.commit();

        TextView tv = (TextView)findViewById(R.id.settingsSavedText);
        tv.setVisibility(View.VISIBLE);
    }
}
