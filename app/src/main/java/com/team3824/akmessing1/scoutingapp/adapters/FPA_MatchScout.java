package com.team3824.akmessing1.scoutingapp.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.MatchAuto;
import com.team3824.akmessing1.scoutingapp.fragments.MatchEndgame;
import com.team3824.akmessing1.scoutingapp.fragments.MatchFouls;
import com.team3824.akmessing1.scoutingapp.fragments.MatchPost;
import com.team3824.akmessing1.scoutingapp.fragments.MatchTeleop;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that contains the fragments for match scouting
 */
public class FPA_MatchScout extends FragmentPagerAdapter {

    private String tabTitles[] = new String[]{"Autonomous", "Teleop", "Endgame", "Post-Match", "Fouls"};
    private MatchAuto matchAuto;
    private MatchTeleop matchTeleop;
    private MatchPost matchPost;
    private MatchFouls matchFouls;
    private MatchEndgame matchEndgame;

    private Map<Integer, WeakReference<ScoutFragment>> fragments = new HashMap<>();

    private ScoutMap valueMap = null;

    /**
     * @param fm
     */
    public FPA_MatchScout(FragmentManager fm) {
        super(fm);
        matchAuto = new MatchAuto();
        matchTeleop = new MatchTeleop();
        matchPost = new MatchPost();
        matchEndgame = new MatchEndgame();
        matchFouls = new MatchFouls();
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
                fragment = matchAuto;
                break;
            case 1:
                fragment = matchTeleop;
                break;
            case 2:
                fragment = matchEndgame;
                break;
            case 3:
                fragment = matchPost;
                break;
            case 4:
                fragment = matchFouls;
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
        fragmentList.add(matchAuto);
        fragmentList.add(matchTeleop);
        fragmentList.add(matchPost);
        fragmentList.add(matchEndgame);
        fragmentList.add(matchFouls);
        return fragmentList;
    }

}
