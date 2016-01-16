package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.fragments.MatchAuto;
import com.team3824.akmessing1.scoutingapp.fragments.MatchEndgame;
import com.team3824.akmessing1.scoutingapp.fragments.MatchFouls;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.fragments.MatchPost;
import com.team3824.akmessing1.scoutingapp.fragments.MatchTeleop;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Setup up page fragments for match scouting
public class MatchScoutFragmentPagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Autonomous", "Teleop","Post-Match","EndGame","Fouls" };
    private MatchAuto matchAuto;
    private MatchTeleop matchTeleop;
    private MatchPost matchPost;
    private MatchFouls matchFouls;
    private MatchEndgame matchEndgame;


    private Map<Integer,WeakReference<ScoutFragment>> fragments = new HashMap<>();

    private Map<String,ScoutValue> valueMap = null;

    public MatchScoutFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        matchAuto = new MatchAuto();
        matchTeleop = new MatchTeleop();
        matchPost = new MatchPost();
        matchEndgame = new MatchEndgame();
        matchFouls = new MatchFouls();
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
                fragment = matchAuto;
                break;
            case 1:
                fragment = matchTeleop;
                break;
            case 2:
                fragment = matchPost;
                break;
            case 3:
                fragment = matchEndgame;
                break;
            case 4:
                fragment = matchFouls;
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
        fragmentList.add(matchAuto);
        fragmentList.add(matchTeleop);
        fragmentList.add(matchPost);
        fragmentList.add(matchEndgame);
        fragmentList.add(matchFouls);
        return fragmentList;
    }

}
