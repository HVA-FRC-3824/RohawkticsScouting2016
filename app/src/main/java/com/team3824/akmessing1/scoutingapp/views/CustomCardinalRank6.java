package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.CardinalRankListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public class CustomCardinalRank6 extends CustomScoutView {
    private String TAG = "CustomCardinalRank6";
    private String key;
    private DragSortListView listView;
    private Context context;
    private CardinalRankListAdapter adapter;
    public CustomCardinalRank6(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_cardinal_rank_6, this);

        TextView label = (TextView) this.findViewById(R.id.label);

        // Set label and get key
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        label.setText(typedArray.getString(R.styleable.CustomScoutView_label));
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        typedArray.recycle();

        listView = (DragSortListView)this.findViewById(R.id.list_view);
    }

    public void setArray(final ArrayList<Integer> array)
    {
        adapter = new CardinalRankListAdapter(context,R.layout.list_item_cardinal_rank,array);
        listView.setAdapter(adapter);
        listView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                int teamNumber = adapter.get(from);
                adapter.remove(teamNumber);
                adapter.add(to,teamNumber);
            }
        });

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
    public void writeToMap(Map<String, ScoutValue> map)
    {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < adapter.size(); i++) {
            jsonArray.put(adapter.get(i));
        }
        String value = jsonArray.toString();
        map.put(key, new ScoutValue(value));
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
                e.printStackTrace();
            }
        }
    }
}
