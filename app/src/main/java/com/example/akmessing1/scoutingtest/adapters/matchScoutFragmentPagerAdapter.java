package com.example.akmessing1.scoutingtest.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

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
        switch (position)
        {
            case 0:
                return new MatchPre();
            case 1:
                return new MatchAuto();
            case 2:
                return new MatchTeleop();
            case 3:
                return new MatchPost();
            default:
                return null;
        }
    }

}
