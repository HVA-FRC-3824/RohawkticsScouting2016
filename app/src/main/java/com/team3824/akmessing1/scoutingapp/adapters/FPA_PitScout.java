package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.PitDimensions;
import com.team3824.akmessing1.scoutingapp.fragments.PitMisc;
import com.team3824.akmessing1.scoutingapp.fragments.PitNotes;
import com.team3824.akmessing1.scoutingapp.fragments.PitRobotPicture;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that contains the fragments for pit scouting
 */
public class FPA_PitScout extends FragmentPagerAdapter {

    private String tabTitles[] = new String[]{"Robot Picture", "Dimensions", "Misc", "Notes"};

    private PitRobotPicture pitRobotPicture;
    private PitDimensions pitDimensions;
    private PitMisc pitMisc;
    private PitNotes pitNotes;

    private ScoutMap valueMap = null;

    /**
     * @param fm
     */
    public FPA_PitScout(FragmentManager fm) {
        super(fm);
        pitRobotPicture = new PitRobotPicture();
        pitDimensions = new PitDimensions();
        pitMisc = new PitMisc();
        pitNotes = new PitNotes();
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
                assert false;
        }

        // sets the value map for restoring values
        if (valueMap != null) {
            fragment.setValuesMap(valueMap);
        }
        return fragment;
    }

    /**
     * sets the value map for restoring previous values to the fields
     *
     * @param map The map containing the previous values
     */
    public void setValueMap(ScoutMap map) {
        valueMap = map;
    }

    /**
     * Used to get all the values for saving
     *
     * @return all the fragments
     */
    public List<ScoutFragment> getAllFragments() {
        List<ScoutFragment> fragmentList = new ArrayList<>();
        fragmentList.add(pitRobotPicture);
        fragmentList.add(pitDimensions);
        fragmentList.add(pitMisc);
        fragmentList.add(pitNotes);
        return fragmentList;
    }

}
