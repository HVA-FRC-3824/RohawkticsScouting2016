package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.PitDimensions;
import com.team3824.akmessing1.scoutingapp.fragments.PitMisc;
import com.team3824.akmessing1.scoutingapp.fragments.PitNotes;
import com.team3824.akmessing1.scoutingapp.fragments.PitRobotPicture;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Setup up page fragments for pit scouting
public class FPA_PitScout extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Robot Picture", "Dimensions","Misc","Notes" };

    private PitRobotPicture pitRobotPicture;
    private PitDimensions pitDimensions;
    private PitMisc pitMisc;
    private PitNotes pitNotes;

    private Map<String,ScoutValue> valueMap = null;

    public FPA_PitScout(FragmentManager fm) {
        super(fm);
        pitRobotPicture = new PitRobotPicture();
        pitDimensions = new PitDimensions();
        pitMisc = new PitMisc();
        pitNotes = new PitNotes();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        ScoutFragment fragment;
        switch (position)
        {
            case 0:
                fragment = pitRobotPicture;
                break;
            case 1:
                fragment = pitDimensions;
                break;
            case 2:
                fragment = pitMisc;
                break;
            case 3:
                fragment = pitNotes;
                break;
            default:
                fragment = null; // There has been a problem!
                break;
        }

        // sets the value map for restoring values
        if(valueMap != null) {
            fragment.setValuesMap(valueMap);
        }
        return fragment;
    }

    // sets the value map for restoring values
    public void setValueMap(Map<String, ScoutValue> map)
    {
        valueMap = map;
    }

    // returns all the fragments
    // used to get all the values for saving
    public List<ScoutFragment> getAllFragments(){
        List<ScoutFragment> fragmentList = new ArrayList<>();
        fragmentList.add(pitRobotPicture);
        fragmentList.add(pitDimensions);
        fragmentList.add(pitMisc);
        fragmentList.add(pitNotes);
        return fragmentList;
    }

}
