package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FPA_PickList;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;

/**
 * Activity that holds the fragments for the different pick lists
 */
public class PickList extends Activity implements ViewPager.OnPageChangeListener{
    final private String TAG = "PickList";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FPA_PickList adapter;

    int previousPage = 0;

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
        viewPager.setOnPageChangeListener(this);
        tabLayout = (TabLayout) findViewById(R.id.pick_list_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * As the page is changed the picked/unpicked updates are transferred to the new page
     * @param position The new page position.
     */
    @Override
    public void onPageSelected(int position) {
        if(previousPage != position)
        {
            ArrayList<Integer> teamsPicked1 = adapter.getPicked(previousPage);
            ArrayList<Integer> teamsPicked2 = adapter.getPicked(position);


            ArrayList<Integer> picked = new ArrayList<>();
            for(int i = 0; i < teamsPicked1.size(); i++) {
                picked.add(teamsPicked1.get(i));
            }
            picked.removeAll(teamsPicked2);

            ArrayList<Integer> unpicked = new ArrayList<>();
            for(int i = 0; i < teamsPicked2.size(); i++)
            {
                unpicked.add(teamsPicked2.get(i));
            }
            unpicked.removeAll(teamsPicked1);

            adapter.setPickedUnpicked(position,picked,unpicked);



            previousPage = position;
        }
    }

    /**
     *
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
