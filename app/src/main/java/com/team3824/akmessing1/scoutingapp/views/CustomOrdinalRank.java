package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleFloatViewManager;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.OrdinalRankListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public class CustomOrdinalRank extends CustomScoutView implements DragSortListView.DropListener{
    private String TAG = "CustomOrdinalRank";
    private String key;
    private DragSortListView listView;
    private Context context;
    private OrdinalRankListAdapter adapter;
    public CustomOrdinalRank(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_ordinal_rank, this);

        TextView label = (TextView) this.findViewById(R.id.label);

        // Set label and get key
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();

        listView = (DragSortListView)this.findViewById(R.id.list_view);
        listView.setFloatViewManager(new SimpleFloatViewManager(listView));
    }

    public void setArray(ArrayList<Integer> array)
    {
        ArrayList<Integer> copy = new ArrayList<>();
        for(int i = 0; i < array.size(); i++)
            copy.add(array.get(i).intValue());
        adapter = new OrdinalRankListAdapter(context,R.layout.list_item_ordinal_rank,copy);
        listView.setAdapter(adapter);
        listView.setDropListener(this);


        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public String writeToMap(Map<String, ScoutValue> map)
    {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < adapter.size(); i++) {
            jsonArray.put(adapter.get(i));
        }
        String value = jsonArray.toString();
        map.put(key, new ScoutValue(value));
        return "";
    }

    @Override
    public void restoreFromMap(Map<String, ScoutValue> map){
        ScoutValue sv = map.get(key);
        if(sv != null) {
            String value = sv.getString();
            try {
                JSONArray jsonArray = new JSONArray(value);
                ArrayList<Integer> array = new ArrayList<Integer>();
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    array.add(jsonArray.getInt(i));
                }
                setArray(array);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void drop(int from, int to) {
        int teamNumber = adapter.get(from);
        adapter.remove(teamNumber);
        adapter.add(to,teamNumber);
    }
}
