package com.team3824.akmessing1.scoutingapp.fragments;

import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.views.CustomScoutView;


/**
 * abstract base class for each of the match scouting fragments
 *
 * @author Andrew Messing
 * @version
 */
public abstract class ScoutFragment extends Fragment {

    private final String TAG = "ScoutFragment";
    protected ScoutMap valueMap;

    public ScoutFragment() {
        valueMap = new ScoutMap();
    }

    /**
     * @param map
     */
    public void setValuesMap(ScoutMap map) {
        valueMap = map;
    }

    /**
     * Recursive functions to get all the values and store them in a map
     *
     * @param map
     * @return
     */
    public String writeContentsToMap(ScoutMap map) {
        // Get the ViewGroup holding all of the widgets
        ViewGroup vg = (ViewGroup) getView();
        if (vg == null) {
            Log.d(TAG, "Null view");
            // If the view has been destroyed, state should already be saved
            // to parent activity
            map.putAll(valueMap);
            return "";
        }
        String error = "";
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof CustomScoutView) {
                error += ((CustomScoutView) view).writeToMap(map);
            } else if (view instanceof ViewGroup) {
                error += writeContentsToMap(map, (ViewGroup) view);
            }
        }

        return error;
    }

    /**
     * @param map
     * @param viewGroup
     * @return
     */
    protected String writeContentsToMap(ScoutMap map, ViewGroup viewGroup) {
        String error = "";
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof CustomScoutView) {
                error += ((CustomScoutView) view).writeToMap(map);
            } else if (view instanceof ViewGroup) {
                error += writeContentsToMap(map, (ViewGroup) view);
            }
        }
        return error;
    }

    /**
     * Recursive function to get all the values from a map and populate the fields
     *
     * @param map
     */
    public void restoreContentsFromMap(ScoutMap map) {
        // Get the ViewGroup holding all of the widgets
        ViewGroup vg = (ViewGroup) getView();
        if (vg == null) {
            Log.d(TAG, "Null view");
            // If the view has been destroyed, state should already be saved
            // to parent activity
            return;
        }
        int childCount = vg.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = vg.getChildAt(i);
            if (view instanceof CustomScoutView) {
                ((CustomScoutView) view).restoreFromMap(map);
            } else if (view instanceof ViewGroup) {
                restoreContentsFromMap(map, (ViewGroup) view);
            }
        }
    }

    /**
     * @param map
     * @param viewGroup
     */
    protected void restoreContentsFromMap(ScoutMap map, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof CustomScoutView) {
                ((CustomScoutView) view).restoreFromMap(map);
            } else if (view instanceof ViewGroup) {
                restoreContentsFromMap(map, (ViewGroup) view);
            }
        }
    }

    /**
     *
     */
    @Override
    public void onDestroyView() {
        valueMap.clear();
        writeContentsToMap(valueMap);
        super.onDestroyView();
    }
}
