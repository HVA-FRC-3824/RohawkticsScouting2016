package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_PickList;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

/**
 * Activity that holds the fragments for the different pick lists
 */
public class PickList extends Activity {
    final private String TAG = "PickList";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_PickList adapter;

    /**
     * Sets up the view pager, pager adapter, and tab layout.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list);

        CustomHeader header = (CustomHeader) findViewById(R.id.pick_list_header);
        header.removeHome();

        findViewById(android.R.id.content).setKeepScreenOn(true);
        viewPager = (ViewPager) findViewById(R.id.pick_list_view_pager);
        adapter = new FPA_PickList(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.pick_list_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);
    }
}
