package com.team3824.akmessing1.scoutingtest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingtest.ScoutValue;
import com.team3824.akmessing1.scoutingtest.fragments.MatchAuto;
import com.team3824.akmessing1.scoutingtest.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingtest.fragments.MatchPost;
import com.team3824.akmessing1.scoutingtest.fragments.MatchTeleop;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Setup up page fragments for match scouting
public class MatchScoutFragmentPagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Autonomous", "Teleop","Post-Match" };
    //private String tabTitles[] = new String[] { "Autonomous", "Teleop", "Endgame", "Post-Match" };

    private Map<Integer,WeakReference<ScoutFragment>> fragments = new HashMap<>();

    private Map<String,ScoutValue> valueMap = null;

    public MatchScoutFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
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
                fragment = new MatchAuto();
                break;
            case 1:
                fragment = new MatchTeleop();
                break;
            case 2:
                fragment = new MatchPost();
                break;
            //case 3: return new MatchEndgame();
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
