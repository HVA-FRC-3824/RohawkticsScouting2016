package com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.fragments.SuperScouting.SuperDefenses;
import com.team3824.akmessing1.scoutingapp.fragments.SuperScouting.SuperNotes;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that contains the fragments for super scouting
 */
public class FPA_SuperScout extends FragmentPagerAdapter {

    private String tabTitles[] = new String[]{"Defenses"/*, "Qualitative"*/, "Notes"};

    private Map<Integer, WeakReference<ScoutFragment>> fragments = new HashMap<>();

    private ScoutMap valueMap = null;
    private ArrayList<Integer> teams;

    /**
     * @param fm
     */
    public FPA_SuperScout(FragmentManager fm, ArrayList<Integer> teams) {
        super(fm);
        this.teams = teams;
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
                fragment = new SuperDefenses();
                break;
            /*
            case 1:
                fragment = new SuperQualitative();
                ((SuperQualitative) fragment).setTeams(teams);
                break;
            */
            case 1:
                fragment = new SuperNotes();
                break;
            default:
                assert false;
        }

        // sets the value map for restoring values
        if (valueMap != null) {
            fragment.setValuesMap(valueMap);
        }
        fragments.put(position, new WeakReference<>(fragment));
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
        for (Map.Entry<Integer, WeakReference<ScoutFragment>> entry : fragments.entrySet()) {
            fragmentList.add(entry.getValue().get());
        }
        return fragmentList;
    }

}
