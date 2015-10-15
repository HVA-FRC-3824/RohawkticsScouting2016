package com.example.akmessing1.scoutingtest.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.akmessing1.scoutingtest.fragments.MatchAuto;
import com.example.akmessing1.scoutingtest.fragments.MatchPost;
import com.example.akmessing1.scoutingtest.fragments.MatchPre;
import com.example.akmessing1.scoutingtest.fragments.MatchTeleop;


public class MatchScoutFragmentPagerAdapter extends FragmentPagerAdapter{

    private String tabTitles[] = new String[] { "Pre-Match", "Autonomous", "Teleop","Post-Match" };

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
        Fragment fragment;
        switch (position)
        {
            case 0:
                fragment = new MatchPre();
                break;
            case 1:
                fragment = new MatchAuto();
                break;
            case 2:
                fragment = new MatchTeleop();
                break;
            case 3:
                fragment = new MatchPost();
                break;
            default:
                fragment = null;
                break;
        }
        return fragment;
    }


}
