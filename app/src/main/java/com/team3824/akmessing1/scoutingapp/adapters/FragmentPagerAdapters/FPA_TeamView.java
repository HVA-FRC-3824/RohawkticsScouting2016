package com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.TeamView.TeamMatchData;
import com.team3824.akmessing1.scoutingapp.fragments.TeamView.TeamNotes;
import com.team3824.akmessing1.scoutingapp.fragments.TeamView.TeamPitData;
import com.team3824.akmessing1.scoutingapp.fragments.TeamView.TeamSchedule;
import com.team3824.akmessing1.scoutingapp.fragments.TeamView.TeamVisuals;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;

/**
 * Adapter that contains the fragments for the team view
 *
 * @author Andrew Messing
 * @version %I%
 */
public class FPA_TeamView extends FragmentPagerAdapter {

    private String tabTitles[] = new String[]{"Visuals", "Pit Data", "Match Data", "Notes", "Schedule"};

    private int teamNumber;

    /**
     * @param fm
     * @param teamNumber
     */
    public FPA_TeamView(FragmentManager fm, int teamNumber) {

        super(fm);
        this.teamNumber = teamNumber;
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
        Fragment fragment = null;
        switch (position) {
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
            case 4:
                fragment = new TeamSchedule();
                break;
            default:
                assert false;
        }
        Bundle args = new Bundle();
        args.putInt(Constants.Intent_Extras.TEAM_NUMBER, teamNumber);
        fragment.setArguments(args);
        return fragment;
    }
}
