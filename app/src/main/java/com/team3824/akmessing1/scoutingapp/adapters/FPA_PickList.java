package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

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
public class FPA_PickList extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "1st Pick"/*, "2nd Pick"*/ };


    public FPA_PickList(FragmentManager fm) {
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

        return fragment;
    }
}
