package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.BreacherPick;
import com.team3824.akmessing1.scoutingapp.fragments.DefensivePick;
import com.team3824.akmessing1.scoutingapp.fragments.OffensivePick;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.fragments.ShooterPick;

/**
 * Contains the page fragments for the different pick lists
 */
public class FPA_PickList extends FragmentPagerAdapter {

    private String tabTitles[] = new String[]{"Offensive Pick", "Shooter Pick", "Breacher Pick", "Defensive Pick"};

    /**
     * @param fm
     */
    public FPA_PickList(FragmentManager fm) {
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

    /**
     * @param position the position of the tab
     * @return the fragment that corresponds to the tab
     */
    @Override
    public Fragment getItem(int position) {
        ScoutFragment fragment = null;
        switch (position) {
            case 0:
                fragment = new OffensivePick();
                break;
            case 1:
                fragment = new ShooterPick();
                break;
            case 2:
                fragment = new BreacherPick();
                break;
            case 3:
                fragment = new DefensivePick();
                break;
            default:
                assert false;
        }

        return fragment;
    }
}
