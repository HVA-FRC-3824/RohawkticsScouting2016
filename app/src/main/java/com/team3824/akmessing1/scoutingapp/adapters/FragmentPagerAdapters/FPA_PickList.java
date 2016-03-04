package com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.team3824.akmessing1.scoutingapp.fragments.PickLists.BreacherPick;
import com.team3824.akmessing1.scoutingapp.fragments.PickLists.DefensivePick;
import com.team3824.akmessing1.scoutingapp.fragments.PickLists.DNP;
import com.team3824.akmessing1.scoutingapp.fragments.PickLists.OffensivePick;
import com.team3824.akmessing1.scoutingapp.fragments.PickLists.ShooterPick;

import java.util.ArrayList;

/**
 * Contains the page fragments for the different pick lists
 *
 * @author Andrew Messing
 * @version
 */
public class FPA_PickList extends FragmentPagerAdapter {

    private final String TAG = "FPA_PickList";

    private String tabTitles[] = new String[]{"Offensive Pick", "Shooter Pick", "Breacher Pick", "Defensive Pick", "DNP List", "Decline List"};

    private OffensivePick offensivePick;
    private ShooterPick shooterPick;
    private BreacherPick breacherPick;
    private DefensivePick defensivePick;
    private DNP doNotPick;
    private DNP declinePick;

    /**
     * @param fm
     */
    public FPA_PickList(FragmentManager fm) {
        super(fm);
        offensivePick = new OffensivePick();
        shooterPick = new ShooterPick();
        breacherPick = new BreacherPick();
        defensivePick = new DefensivePick();
        doNotPick = new DNP();
        doNotPick.setDNP();
        declinePick = new DNP();
        declinePick.setDecline();
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
                fragment = offensivePick;
                break;
            case 1:
                fragment = shooterPick;
                break;
            case 2:
                fragment = breacherPick;
                break;
            case 3:
                fragment = defensivePick;
                break;
            case 4:
                fragment = doNotPick;
                break;
            case 5:
                fragment = declinePick;
                break;
            default:
                assert false;
        }

        return fragment;
    }

    /**
     * Gets a list of the teams that have been picked from a specific fragment
     *
     * @param position Which fragment to get the teams that have been picked from
     * @return A list of the teams that have been picked from a specific fragment
     */
    public ArrayList<Integer> getPicked(int position)
    {
        switch (position) {
            case 0:
                return offensivePick.getPicked();
            case 1:
                return shooterPick.getPicked();
            case 2:
                return breacherPick.getPicked();
            case 3:
                return defensivePick.getPicked();
            default:
                assert false;
        }
        return new ArrayList<>();
    }

    /**
     * Updates which teams are picked or unpicked for a given fragment
     *
     * @param position Which fragment to update the teams on whether they are picked or unpicked
     * @param picked A list of the teams that need to be switched to picked
     * @param unpicked A list of the teams that need to be switched to unpicked
     */
    public void setPickedUnpicked(int position, ArrayList<Integer> picked, ArrayList<Integer> unpicked)
    {
        switch (position)
        {
            case 0:
                offensivePick.setPickedUnpicked(picked,unpicked);
                break;
            case 1:
                shooterPick.setPickedUnpicked(picked,unpicked);
                break;
            case 2:
                breacherPick.setPickedUnpicked(picked, unpicked);
                break;
            case 3:
                defensivePick.setPickedUnpicked(picked, unpicked);
                break;
            default:
                assert false;
        }
    }
}
