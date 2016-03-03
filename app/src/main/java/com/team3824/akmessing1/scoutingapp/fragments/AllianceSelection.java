package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.AllianceSelectionAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AllianceSelection extends Fragment implements AdapterView.OnItemSelectedListener, OnClickListener{

    private final String TAG = "AllianceSelection";

    Spinner spinners[];
    int ids[];
    AllianceSelectionAdapter adapters[];
    String previousTeam[];

    File saveFile;
    FileInputStream saveFIS;
    FileOutputStream saveFOS;
    JSONArray json = null;
    ArrayList<Integer> teamList;

    public static final String SELECT_TEAM = "Select Team";
    
    public AllianceSelection(){}

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_alliance_selection, container, false);
        spinners = new Spinner[24];
        adapters = new AllianceSelectionAdapter[24];
        previousTeam = new String[24];
        ids = new int[24];

        setSpinner(view, Constants.CAPTAIN_OFFSET,Constants.ALLIANCE_1_INDEX,R.id.captain_1);
        
        
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_1_INDEX, R.id.captain_1);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_2_INDEX, R.id.captain_2);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_3_INDEX, R.id.captain_3);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_4_INDEX, R.id.captain_4);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_5_INDEX, R.id.captain_5);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_6_INDEX, R.id.captain_6);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_7_INDEX, R.id.captain_7);
        setSpinner(view, Constants.CAPTAIN_OFFSET, Constants.ALLIANCE_8_INDEX, R.id.captain_8);

        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_1_INDEX, R.id.first_pick_1);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_2_INDEX, R.id.first_pick_2);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_3_INDEX, R.id.first_pick_3);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_4_INDEX, R.id.first_pick_4);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_5_INDEX, R.id.first_pick_5);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_6_INDEX, R.id.first_pick_6);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_7_INDEX, R.id.first_pick_7);
        setSpinner(view, Constants.FIRST_PICK_OFFSET, Constants.ALLIANCE_8_INDEX, R.id.first_pick_8);

        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_1_INDEX, R.id.second_pick_1);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_2_INDEX, R.id.second_pick_2);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_3_INDEX, R.id.second_pick_3);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_4_INDEX, R.id.second_pick_4);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_5_INDEX, R.id.second_pick_5);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_6_INDEX, R.id.second_pick_6);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_7_INDEX, R.id.second_pick_7);
        setSpinner(view, Constants.SECOND_PICK_OFFSET, Constants.ALLIANCE_8_INDEX, R.id.second_pick_8);

        Context context = getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.EVENT_ID, "");
        PitScoutDB pitScoutDB = new PitScoutDB(context,eventId);
        teamList = pitScoutDB.getTeamNumbers();

        saveFile = new File(getContext().getFilesDir(), String.format("%s_alliance_selection.txt",eventId));
        if(saveFile.exists())
        {
            try {
                saveFIS = new FileInputStream(saveFile);
                String jsonText = "";
                char current;
                while (saveFIS.available() > 0) {
                    current = (char) saveFIS.read();
                    jsonText += String.valueOf(current);
                }
                Log.d(TAG, jsonText);
                json = new JSONArray(jsonText);
                saveFIS.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
            catch (IOException e) {
                Log.d(TAG,e.getMessage());
            } catch (JSONException e) {
                Log.d(TAG,e.getMessage());
            }
        }

        for(int i = 0; i < 24; i++)
        {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(SELECT_TEAM);
            for(int j = 0; j < teamList.size(); j++)
            {
                temp.add(String.valueOf(teamList.get(j)));
            }
            adapters[i] = new AllianceSelectionAdapter(context,android.R.layout.simple_spinner_dropdown_item,temp);
            spinners[i].setAdapter(adapters[i]);
            previousTeam[i] = SELECT_TEAM;
            spinners[i].setOnItemSelectedListener(this);
        }
        if(json != null) {
            for (int i = 0; i < 24; i++) {
                try {
                    spinners[i].setSelection(adapters[i].indexOf(json.getString(i)));
                } catch (JSONException e) {
                }
            }
        }

        Button save = (Button)view.findViewById(R.id.save);
        save.setOnClickListener(this);

        return view;
    }
    
    private void setSpinner(View view, int offset, int alliance, int id)
    {
        spinners[offset+alliance] = (Spinner)view.findViewById(id);
        ids[offset+alliance] = id;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int index = 0;
        for(; index < ids.length; index++)
        {
            if(parent.getId() == ids[index])
                break;
        }

        assert index < ids.length;

        String teamSelected = adapters[index].getItem(position);
        String previousSelection = previousTeam[index];

        if(teamSelected.equals(previousSelection))
            return;

        for(int i = 0; i < adapters.length; i++)
        {
            if(i == index)
            {
                continue;
            }

            String ts = String.valueOf(spinners[i].getSelectedItem());

            if(!teamSelected.equals(SELECT_TEAM)) {
                adapters[i].remove(teamSelected);
            }
            if(!previousSelection.equals(SELECT_TEAM)) {
                adapters[i].add(previousSelection);
            }

            spinners[i].setSelection(adapters[i].indexOf(ts));
        }

        previousTeam[index] = teamSelected;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.save:
                json = new JSONArray();
                for(int i = 0; i < spinners.length; i++)
                {
                    String spinnerValue = String.valueOf(spinners[i].getSelectedItem());
                    json.put(spinnerValue);
                }
                String text = json.toString();
                Log.d(TAG,text);
                if(saveFile.exists())
                {
                    saveFile.delete();
                    try {
                        saveFile.createNewFile();
                    } catch (IOException e) {
                        Log.d(TAG,e.getMessage());
                    }
                }
                try
                {
                    saveFOS = new FileOutputStream(saveFile);
                    saveFOS.write(text.getBytes());
                    saveFOS.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG,e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG,e.getMessage());
                }
                break;
            default:
                assert false;
        }
    }
}
