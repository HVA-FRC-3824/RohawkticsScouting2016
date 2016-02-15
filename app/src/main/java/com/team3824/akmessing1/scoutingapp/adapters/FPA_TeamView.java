package com.team3824.akmessing1.scoutingapp.adapters;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.fragments.TeamMatchData;
import com.team3824.akmessing1.scoutingapp.fragments.TeamNotes;
import com.team3824.akmessing1.scoutingapp.fragments.TeamPitData;
import com.team3824.akmessing1.scoutingapp.fragments.TeamVisuals;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class FPA_TeamView extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Visuals","Pit Data", "Match Data", "Notes" };

    private Map<Integer,WeakReference<Fragment>> fragments = new HashMap<>();

    private int teamNumber;

    public FPA_TeamView(FragmentManager fm, int teamNumber) {

        super(fm);
        this.teamNumber = teamNumber;
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
        Fragment fragment;
        switch (position)
        {
            case 0:
                fragment = new TeamVisuals();
                break;
            case 1:
                fragment = new TeamPitData();
                break;
            case 2:
                fragment = new TeamMatchData();
                break;
            case 3:
                fragment = new TeamNotes();
                break;
            default:
                fragment = null; // There has been a problem!
                break;
        }
        Bundle args = new Bundle();
        args.putInt(Constants.TEAM_NUMBER, teamNumber);
        fragment.setArguments(args);
        fragments.put(position,new WeakReference<>(fragment));
        return fragment;
    }
}
