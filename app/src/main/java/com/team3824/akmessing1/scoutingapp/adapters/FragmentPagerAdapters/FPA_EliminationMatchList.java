package com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.AllianceSelection;
import com.team3824.akmessing1.scoutingapp.fragments.BracketResults;

import java.util.ArrayList;

public class FPA_EliminationMatchList extends FragmentPagerAdapter{

    private String tabTitles[] = new String[]{"Alliance Selection", "Bracket Results"};

    AllianceSelection allianceSelection;
    BracketResults bracketResults;

    public FPA_EliminationMatchList(FragmentManager fm)
    {
        super(fm);
        allianceSelection = new AllianceSelection();
        bracketResults = new BracketResults();
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
        switch (position)
        {
            case 0:
                return allianceSelection;
            case 1:
                return bracketResults;
            default:
                assert false;
        }
        return null;
    }

    public void moveAlliances()
    {
        if(allianceSelection.getSaved()) {
            bracketResults.setAlliances();
            allianceSelection.resetSaved();
        }
    }

}
