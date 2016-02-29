package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.SuperDefenses;
import com.team3824.akmessing1.scoutingapp.fragments.SuperNotes;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.fragments.SuperQualitative;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Setup up page fragments for pit scouting
public class FPA_SuperScout extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Defenses", "Qualitative", "Notes" };

    private Map<Integer,WeakReference<ScoutFragment>> fragments = new HashMap<>();

    private Map<String,ScoutValue> valueMap = null;
    private ArrayList<Integer> teams;

    public FPA_SuperScout(FragmentManager fm, ArrayList<Integer> teams) {
        super(fm);
        this.teams = teams;
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
                fragment = new SuperDefenses();
                break;
            case 1:
                fragment = new SuperQualitative();
                ((SuperQualitative)fragment).setTeams(teams);
                break;
            case 2:
                fragment = new SuperNotes();
                break;
            default:
                fragment = null; // There has been a problem!
                break;
        }

        // sets the value map for restoring values
        if(valueMap != null) {
            fragment.setValuesMap(valueMap);
        }
        fragments.put(position,new WeakReference<>(fragment));
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
        for(Map.Entry<Integer,WeakReference<ScoutFragment>> entry: fragments.entrySet())
        {
            fragmentList.add(entry.getValue().get());
        }
        return fragmentList;
    }

}
