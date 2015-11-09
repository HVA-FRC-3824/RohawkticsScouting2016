package com.team3824.akmessing1.scoutingapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.FirstPick;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.fragments.SecondPick;
import com.team3824.akmessing1.scoutingapp.fragments.ThirdPick;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Setup up page fragments for pick list
public class PickListFragmentPagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "1st Pick", "2nd Pick" };

    private Map<Integer,WeakReference<ScoutFragment>> fragments = new HashMap<>();

    public PickListFragmentPagerAdapter(FragmentManager fm) {
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
                fragment = new FirstPick();
                break;
            case 1:
                fragment = new SecondPick();
                break;
            case 2:
                fragment = new ThirdPick();
                break;
            default:
                fragment = null;
                break;
        }

        fragments.put(position,new WeakReference<>(fragment));
        return fragment;
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
