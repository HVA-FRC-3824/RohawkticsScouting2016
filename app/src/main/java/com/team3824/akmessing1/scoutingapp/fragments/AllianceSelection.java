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

/**
 * Fragment where the elimination alliances are input and saved
 *
 * @author Andrew Messing
 * @version
 */
public class AllianceSelection extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String TAG = "AllianceSelection";
    private Spinner[] spinners;
    private int[] ids;
    private AllianceSelectionAdapter[] adapters;
    private String[] previousTeam;
    private File bracketSaveFile;
    private File alliancesSaveFile;
    private JSONArray json = null;
    private boolean saved = false;

    public AllianceSelection() {
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alliance_selection, container, false);
        spinners = new Spinner[24];
        adapters = new AllianceSelectionAdapter[24];
        previousTeam = new String[24];
        ids = new int[24];

        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_1_INDEX, R.id.captain_1);


        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_1_INDEX, R.id.captain_1);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_2_INDEX, R.id.captain_2);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_3_INDEX, R.id.captain_3);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_4_INDEX, R.id.captain_4);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_5_INDEX, R.id.captain_5);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_6_INDEX, R.id.captain_6);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_7_INDEX, R.id.captain_7);
        setSpinner(view, Constants.Alliance_Selection.CAPTAIN_OFFSET, Constants.Alliance_Selection.ALLIANCE_8_INDEX, R.id.captain_8);

        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_1_INDEX, R.id.first_pick_1);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_2_INDEX, R.id.first_pick_2);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_3_INDEX, R.id.first_pick_3);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_4_INDEX, R.id.first_pick_4);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_5_INDEX, R.id.first_pick_5);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_6_INDEX, R.id.first_pick_6);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_7_INDEX, R.id.first_pick_7);
        setSpinner(view, Constants.Alliance_Selection.FIRST_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_8_INDEX, R.id.first_pick_8);

        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_1_INDEX, R.id.second_pick_1);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_2_INDEX, R.id.second_pick_2);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_3_INDEX, R.id.second_pick_3);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_4_INDEX, R.id.second_pick_4);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_5_INDEX, R.id.second_pick_5);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_6_INDEX, R.id.second_pick_6);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_7_INDEX, R.id.second_pick_7);
        setSpinner(view, Constants.Alliance_Selection.SECOND_PICK_OFFSET, Constants.Alliance_Selection.ALLIANCE_8_INDEX, R.id.second_pick_8);

        Context context = getContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");
        PitScoutDB pitScoutDB = new PitScoutDB(context, eventId);
        ArrayList<Integer> teamList = pitScoutDB.getTeamNumbers();

        bracketSaveFile = new File(context.getFilesDir(), String.format("%s_bracket_results.txt", eventId));

        alliancesSaveFile = new File(getContext().getFilesDir(), String.format("%s_alliance_selection.txt", eventId));
        if (alliancesSaveFile.exists()) {
            try {
                FileInputStream saveFIS = new FileInputStream(alliancesSaveFile);
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
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        for (int i = 0; i < 24; i++) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(Constants.Alliance_Selection.SELECT_TEAM);
            for (int j = 0; j < teamList.size(); j++) {
                temp.add(String.valueOf(teamList.get(j)));
            }
            adapters[i] = new AllianceSelectionAdapter(context, temp);
            spinners[i].setAdapter(adapters[i]);
            previousTeam[i] = Constants.Alliance_Selection.SELECT_TEAM;
            spinners[i].setOnItemSelectedListener(this);
        }
        if (json != null) {
            for (int i = 0; i < 24; i++) {
                try {
                    spinners[i].setSelection(adapters[i].indexOf(json.getString(i)));
                } catch (JSONException e) {
                }
            }
        }

        return view;
    }

    private void setSpinner(View view, int offset, int alliance, int id) {
        spinners[offset + alliance] = (Spinner) view.findViewById(id);
        ids[offset + alliance] = id;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        int index = 0;
        for (; index < ids.length; index++) {
            if (parent.getId() == ids[index])
                break;
        }

        assert index < ids.length;

        String teamSelected = adapters[index].getItem(position);
        String previousSelection = previousTeam[index];

        if (teamSelected.equals(previousSelection))
            return;

        for (int i = 0; i < adapters.length; i++) {
            if (i == index) {
                continue;
            }

            String ts = String.valueOf(spinners[i].getSelectedItem());

            if (!teamSelected.equals(Constants.Alliance_Selection.SELECT_TEAM)) {
                adapters[i].remove(teamSelected);
            }
            if (!previousSelection.equals(Constants.Alliance_Selection.SELECT_TEAM)) {
                adapters[i].add(previousSelection);
            }

            spinners[i].setSelection(adapters[i].indexOf(ts));
        }

        previousTeam[index] = teamSelected;
        save();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void save()
    {
        saved = true;
        json = new JSONArray();
        for (int i = 0; i < spinners.length; i++) {
            String spinnerValue = String.valueOf(spinners[i].getSelectedItem());
            json.put(spinnerValue);
        }
        String text = json.toString();
        Log.d(TAG, text);
        if (alliancesSaveFile.exists()) {
            alliancesSaveFile.delete();
            try {
                alliancesSaveFile.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        try {
            FileOutputStream saveFOS = new FileOutputStream(alliancesSaveFile);
            saveFOS.write(text.getBytes());
            saveFOS.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }

        if(bracketSaveFile.exists())
        {
            bracketSaveFile.delete();
        }
    }

    public boolean getSaved()
    {
        return saved;
    }

    public void resetSaved()
    {
        saved = false;
    }
}
