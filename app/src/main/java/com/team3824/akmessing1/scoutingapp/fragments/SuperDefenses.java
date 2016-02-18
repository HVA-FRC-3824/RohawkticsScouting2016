package com.team3824.akmessing1.scoutingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.DefenseAdapter;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SuperDefenses extends ScoutFragment implements AdapterView.OnItemSelectedListener{

    ArrayList<String> defensesLists[];
    DefenseAdapter defensesAdapters[];
    Spinner defensesSpinners[];
    String previousSelection[];
    private int BLUE_2 = 0, BLUE_4=1, BLUE_5 = 2, RED_2 = 3, RED_4=4, RED_5 = 5, BOTH_3 = 6;
    private int BLUE = 0, RED = 3;

    private String SELECT_DEFENSE = "Select Defense";

    public SuperDefenses() {

        defensesLists = new ArrayList[7];
        defensesAdapters = new DefenseAdapter[7];
        defensesSpinners = new Spinner[7];
        previousSelection = new String[7];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_defenses, container, false);

        for(int i = 0; i < 7; i++) {

            defensesLists[i] = new ArrayList<>(Arrays.asList(Constants.DEFENSES_LABEL));
            defensesLists[i].set(0, SELECT_DEFENSE);
            previousSelection[i] = SELECT_DEFENSE;
        }

        defensesSpinners[BLUE_2] = (Spinner)view.findViewById(R.id.blue_defense2);
        defensesSpinners[BLUE_4] = (Spinner)view.findViewById(R.id.blue_defense4);
        defensesSpinners[BLUE_5] = (Spinner)view.findViewById(R.id.blue_defense5);
        defensesSpinners[RED_2] = (Spinner)view.findViewById(R.id.red_defense2);
        defensesSpinners[RED_4] = (Spinner)view.findViewById(R.id.red_defense4);
        defensesSpinners[RED_5] = (Spinner)view.findViewById(R.id.red_defense5);
        defensesSpinners[BOTH_3] = (Spinner)view.findViewById(R.id.defense3);

        for(int i = 0; i < 7; i++)
        {
            defensesAdapters[i] = new DefenseAdapter(getContext(),R.layout.list_item_string,defensesLists[i],(i==6));
            defensesSpinners[i].setAdapter(defensesAdapters[i]);
            defensesSpinners[i].setOnItemSelectedListener(this);
        }

        if(valueMap != null) {
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }
        Utilities.setupUI(getActivity(), view);
        return view;
    }

    @Override
    public String writeContentsToMap(Map<String, ScoutValue> map, ViewGroup vg)
    {
        for(int i = 0; i < 7; i++)
        {
            String value = String.valueOf(defensesSpinners[i].getSelectedItem());
            if(value.equals(SELECT_DEFENSE))
            {
                return "Not all defenses have been selected\n";
            }
            else
            {
                map.put(Constants.SUPER_DEFENSES[i], new ScoutValue(value));
            }
        }
        return "";
    }

    @Override
    public void restoreContentsFromMap(Map<String, ScoutValue> map, ViewGroup vg)
    {
        for(int i = 0; i < 7; i++)
        {
            ScoutValue sv = map.get(Constants.SUPER_DEFENSES[i]);
            if(sv != null) {
                defensesSpinners[i].setSelection(Arrays.asList(Constants.DEFENSES_LABEL).indexOf(sv.getString()));
            }
        }
        optimizeLists();
    }

    public void optimizeLists() {
        for(int i = 0; i < 7; i++) {
            defensesLists[i] = new ArrayList<>(Arrays.asList(Constants.DEFENSES_LABEL));
            defensesLists[i].set(0, SELECT_DEFENSE);
        }

        // Both 3
        String both3 = String.valueOf(defensesSpinners[BOTH_3].getSelectedItem());

        if (both3.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]))
        {
            for(int i = 0; i < 6; i++)
            {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
            }
        }
        else if(both3.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]))
        {
            for(int i = 0; i < 6; i++) {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
            }
        }
        else if(both3.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX])) {
            for(int i = 0; i < 6; i++)
            {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
            }
        }
        else if(both3.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]))
        {
            for(int i = 0; i < 6; i++) {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
            }

        }

        for(int i = 0; i < 3; i++)
        {
            String blue = String.valueOf(defensesSpinners[i].getSelectedItem());
            String red = String.valueOf(defensesSpinners[i+RED].getSelectedItem());
            for(int j = 0; j < 3; j++)
            {
                if(i==j)continue;

                if (blue.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]))
                {
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
                }
                else if(blue.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]))
                {
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
                }
                else if(blue.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]))
                {

                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
                }
                else if(blue.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]))
                {
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
                }

                if (red.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]))
                {
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
                }
                else if(red.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]))
                {
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
                }
                else if(red.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]))
                {

                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
                }
                else if(red.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]))
                {
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
                }
            }
        }

        for(int i = 0; i < 7; i++) {
            String current = String.valueOf(defensesSpinners[i].getSelectedItem());
            defensesAdapters[i] = new DefenseAdapter(getContext(), R.layout.list_item_string, defensesLists[i],(i==6));
            defensesSpinners[i].setAdapter(defensesAdapters[i]);
            defensesSpinners[i].setSelection(defensesLists[i].indexOf(current));
        }

    }

    private void optimizeLists(int index, int offset)
    {
        for(int i = 0; i < 3; i++) {
            if(i+offset == index) continue;
            defensesLists[i+offset] = new ArrayList<>(Arrays.asList(Constants.DEFENSES_LABEL));
            defensesLists[i+offset].set(0, SELECT_DEFENSE);
        }

        defensesLists[BOTH_3] = new ArrayList<>(Arrays.asList(Constants.DEFENSES_LABEL));
        defensesLists[BOTH_3].set(0, SELECT_DEFENSE);

        String current;
        for(int i = 0; i < 3; i++) {
            current = String.valueOf(defensesSpinners[i+offset].getSelectedItem());
            for (int j = 0; j < 3; j++) {
                if(i == j) continue;
                if (index == j + offset) continue;

                if (current.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                        current.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX])) {
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
                } else if (current.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                        current.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX])) {
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
                } else if (current.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                        current.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX])) {

                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
                } else if (current.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                        current.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX])) {
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[j + offset].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);

                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[BOTH_3].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
                }
            }
        }

        current = String.valueOf(defensesSpinners[BOTH_3].getSelectedItem());

        for(int i = 0; i < 3; i++)
        {
            if(i+offset == index) continue;

            if (current.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                    current.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX])) {
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
            } else if (current.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                    current.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX])) {
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
            } else if (current.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                    current.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX])) {
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
            } else if (current.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                    current.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX])) {
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                defensesLists[i + offset].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
            }

        }

        defensesAdapters[BOTH_3] = new DefenseAdapter(getContext(), R.layout.list_item_string, defensesLists[BOTH_3], true);
        defensesSpinners[BOTH_3].setAdapter(defensesAdapters[BOTH_3]);
        defensesSpinners[BOTH_3].setSelection(defensesLists[BOTH_3].indexOf(current));

        for(int i = 0; i < 3; i++) {
            if(i+offset == index) continue;
            current = String.valueOf(defensesSpinners[i+offset].getSelectedItem());
            defensesAdapters[i+offset] = new DefenseAdapter(getContext(), R.layout.list_item_string, defensesLists[i+offset]);
            defensesSpinners[i+offset].setAdapter(defensesAdapters[i+offset]);
            defensesSpinners[i+offset].setSelection(defensesLists[i+offset].indexOf(current));
        }
    }

    public void optimizeListsBoth()
    {
        for(int i = 0; i < 6; i++) {
            defensesLists[i] = new ArrayList<>(Arrays.asList(Constants.DEFENSES_LABEL));
            defensesLists[i].set(0, SELECT_DEFENSE);
        }

        String both3 = String.valueOf(defensesSpinners[BOTH_3].getSelectedItem());

        if (both3.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]))
        {
            for(int i = 0; i < 6; i++)
            {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
            }
        }
        else if(both3.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]))
        {
            for(int i = 0; i < 6; i++) {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
            }
        }
        else if(both3.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX])) {
            for(int i = 0; i < 6; i++)
            {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
            }
        }
        else if(both3.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                both3.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]))
        {
            for(int i = 0; i < 6; i++) {
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                defensesLists[i].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
            }

        }

        for(int i = 0; i < 3; i++)
        {
            String blue = String.valueOf(defensesSpinners[i].getSelectedItem());
            String red = String.valueOf(defensesSpinners[i+RED].getSelectedItem());
            for(int j = 0; j < 3; j++)
            {
                if(i==j)continue;

                if (blue.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]))
                {
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
                }
                else if(blue.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]))
                {
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
                }
                else if(blue.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]))
                {

                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
                }
                else if(blue.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                        blue.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]))
                {
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[j].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
                }

                if (red.equals(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]))
                {
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.PORTCULLIS_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.CHEVAL_DE_FRISE_INDEX]);
                }
                else if(red.equals(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]))
                {
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.MOAT_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.RAMPARTS_INDEX]);
                }
                else if(red.equals(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]))
                {

                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.DRAWBRIDGE_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.SALLY_PORT_INDEX]);
                }
                else if(red.equals(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]) ||
                        red.equals(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]))
                {
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.ROCK_WALL_INDEX]);
                    defensesLists[j+RED].remove(Constants.DEFENSES_LABEL[Constants.ROUGH_TERRAIN_INDEX]);
                }
            }
        }

        for(int i = 0; i < 6; i++) {
            String current = String.valueOf(defensesSpinners[i].getSelectedItem());
            defensesAdapters[i] = new DefenseAdapter(getContext(), R.layout.list_item_string, defensesLists[i]);
            defensesSpinners[i].setAdapter(defensesAdapters[i]);
            defensesSpinners[i].setSelection(defensesLists[i].indexOf(current));
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch ((int)parent.getId())
        {
            case R.id.blue_defense2:
                if(!String.valueOf(defensesSpinners[BLUE_2].getSelectedItem()).equals(previousSelection[BLUE_2]))
                {
                    optimizeLists(BLUE_2, BLUE);
                    previousSelection[BLUE_2] = String.valueOf(defensesSpinners[BLUE_2].getSelectedItem());
                }
                break;
            case R.id.blue_defense4:
                if(!String.valueOf(defensesSpinners[BLUE_4].getSelectedItem()).equals(previousSelection[BLUE_4]))
                {
                    optimizeLists(BLUE_4, BLUE);
                    previousSelection[BLUE_4] = String.valueOf(defensesSpinners[BLUE_4].getSelectedItem());
                }
                break;
            case R.id.blue_defense5:
                if(!String.valueOf(defensesSpinners[BLUE_5].getSelectedItem()).equals(previousSelection[BLUE_5]))
                {
                    optimizeLists(BLUE_5, BLUE);
                    previousSelection[BLUE_5] = String.valueOf(defensesSpinners[BLUE_5].getSelectedItem());
                }
                break;
            case R.id.red_defense2:
                if(!String.valueOf(defensesSpinners[RED_2].getSelectedItem()).equals(previousSelection[RED_2]))
                {
                    optimizeLists(RED_2, RED);
                    previousSelection[RED_2] = String.valueOf(defensesSpinners[RED_2].getSelectedItem());
                }
                break;
            case R.id.red_defense4:
                if(!String.valueOf(defensesSpinners[RED_4].getSelectedItem()).equals(previousSelection[RED_4]))
                {
                    optimizeLists(RED_4, RED);
                    previousSelection[RED_4] = String.valueOf(defensesSpinners[RED_4].getSelectedItem());
                }
                break;
            case R.id.red_defense5:
                if(!String.valueOf(defensesSpinners[RED_5].getSelectedItem()).equals(previousSelection[RED_5]))
                {
                    optimizeLists(RED_5, RED);
                    previousSelection[RED_5] = String.valueOf(defensesSpinners[RED_5].getSelectedItem());
                }
                break;
            case R.id.defense3:
                if(!String.valueOf(defensesSpinners[BOTH_3].getSelectedItem()).equals(previousSelection[BOTH_3]))
                {
                    optimizeListsBoth();
                    previousSelection[BOTH_3] = String.valueOf(defensesSpinners[BOTH_3].getSelectedItem());
                }
                break;
            default:
                return;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
