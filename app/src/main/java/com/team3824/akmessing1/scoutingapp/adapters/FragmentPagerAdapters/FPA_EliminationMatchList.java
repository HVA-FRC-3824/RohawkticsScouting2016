package com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.AllianceSelection;
import com.team3824.akmessing1.scoutingapp.fragments.BracketResults;

public class FPA_EliminationMatchList extends FragmentPagerAdapter{

    private String tabTitles[] = new String[]{"Alliance Selection", "Bracket Results"};

    public FPA_EliminationMatchList(FragmentManager fm)
    {
        super(fm);
    }

    /**
     * @param position the position of the tab
     * @return The title of the tab
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    /**
     * @return The number of tabs
     */
    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new AllianceSelection();
                break;
            case 1:
                fragment = new BracketResults();
                break;
            default:
                assert false;
        }
        return fragment;
    }

}
