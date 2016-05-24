package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.FragmentPagerAdapters.FPA_PickList;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;

/**
 * Activity that holds the fragments for the different pick lists
 *
 * @author Andrew Messing
 * @version
 */
public class PickList extends Activity implements ViewPager.OnPageChangeListener {
    private final String TAG = "PickList";

    private FPA_PickList adapter;

    private int previousPage = 0;
    private int previousPickListPage = 0;

    /**
     * Sets up the view pager, pager adapter, and tab layout.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list);

        CustomHeader header = (CustomHeader) findViewById(R.id.pick_list_header);
        header.removeHome();

        findViewById(android.R.id.content).setKeepScreenOn(true);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pick_list_view_pager);
        adapter = new FPA_PickList(getFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.pick_list_tab_layout);
        tabLayout.setTabTextColors(Color.WHITE, Color.GREEN);
        tabLayout.setSelectedTabIndicatorColor(Color.GREEN);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * As the page is changed the picked/unpicked updates are transferred to the new page
     *
     * @param position The new page position.
     */
    @Override
    public void onPageSelected(int position) {

        if (position < 4) {
            ArrayList<Integer> teamsPicked1 = adapter.getPicked(previousPickListPage);
            ArrayList<Integer> teamsPicked2 = adapter.getPicked(position);

            ArrayList<Integer> picked = new ArrayList<>();
            for (int i = 0; i < teamsPicked1.size(); i++) {
                picked.add(teamsPicked1.get(i));
            }
            picked.removeAll(teamsPicked2);

            ArrayList<Integer> unpicked = new ArrayList<>();
            for (int i = 0; i < teamsPicked2.size(); i++) {
                unpicked.add(teamsPicked2.get(i));
            }
            unpicked.removeAll(teamsPicked1);

            adapter.setPickedUnpicked(position, picked, unpicked);

            ArrayList<Integer> teamsDNP = adapter.getDNP(4);
            ArrayList<Integer> teamsDNP2 = adapter.getDNP(position);

            ArrayList<Integer> dnp = new ArrayList<>();
            for( int i = 0; i < teamsDNP.size(); i++)
            {
                dnp.add(teamsDNP.get(i));
            }
            dnp.removeAll(teamsDNP2);

            ArrayList<Integer> undnp = new ArrayList<>();
            for (int i = 0; i < teamsDNP2.size(); i++) {
                undnp.add(teamsDNP2.get(i));
            }
            undnp.removeAll(teamsDNP);
            adapter.setDnpUndnp(position, dnp, undnp);


            previousPickListPage = position;
        }

    }

    /**
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
