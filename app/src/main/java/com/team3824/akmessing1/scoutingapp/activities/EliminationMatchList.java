package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters.FPA_EliminationMatchList;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;

/**
 * Activity which contains Alliance Selection Fragments and Bracket Results fragments in order to set
 * up Elimination Match Views.
 *
 * @author Andrew Messing
 * @version
 */
public class EliminationMatchList extends Activity implements ViewPager.OnPageChangeListener{

    FPA_EliminationMatchList adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elimination_match_list);

        CustomHeader customHeader = (CustomHeader)findViewById(R.id.header);
        customHeader.removeHome();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new FPA_EliminationMatchList(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    //TODO: Set up new alliance selections to move over to bracket results
    @Override
    public void onPageSelected(int position) {

        // Move from 0 to 1
        if(position == 1)
        {
            adapter.moveAlliances();
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
