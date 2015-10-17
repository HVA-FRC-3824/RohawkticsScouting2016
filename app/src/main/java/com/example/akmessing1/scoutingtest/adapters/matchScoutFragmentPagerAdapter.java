package com.example.akmessing1.scoutingtest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.akmessing1.scoutingtest.fragments.MatchAuto;
import com.example.akmessing1.scoutingtest.fragments.MatchPost;
import com.example.akmessing1.scoutingtest.fragments.MatchTeleop;


public class MatchScoutFragmentPagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Autonomous", "Teleop","Post-Match" };
    //private String tabTitles[] = new String[] { "Autonomous", "Teleop", "Endgame", "Post-Match" };

    public MatchScoutFragmentPagerAdapter(FragmentManager fm) {
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
        switch (position)
        {
            case 0: return new MatchAuto();
            case 1: return new MatchTeleop();
            case 2: return new MatchPost();
            //case 3: return new MatchEndgame();
            default: return null;
        }
    }

}
