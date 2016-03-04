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
import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.OrdinalRankListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CustomOrdinalRank extends CustomScoutView implements DragSortListView.DropListener{
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "CustomOrdinalRank";
    private String key;
    private DragSortListView listView;
    private Context context;
    private OrdinalRankListAdapter adapter;

    public static final String RANK = "rank";
    public static final String TIE = "tie";

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
        ArrayList<Team> copy = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            Team team = new Team(array.get(i),"");
            team.setMapElement(RANK,new ScoutValue(i+1));
            team.setMapElement(TIE,new ScoutValue(0));
            copy.add(team);
        }
        adapter = new OrdinalRankListAdapter(context, copy);
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

    private void setArray(ArrayList<Team> array, boolean extra)
    {
        adapter = new OrdinalRankListAdapter(context, array);
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
    public String writeToMap(ScoutMap map)
    {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < adapter.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            Team team = adapter.getItem(i);
            try {
                jsonObject.put(Constants.Intent_Extras.TEAM_NUMBER,team.getTeamNumber());
                jsonObject.put(RANK, team.getMapElement(RANK));
            } catch (JSONException e) {
            }
            jsonArray.put(jsonObject);
        }
        String value = jsonArray.toString();
        map.put(key, value);
        return "";
    }

    @Override
    public void restoreFromMap(ScoutMap map){
        ScoutValue sv = map.get(key);
        if(sv != null) {
            String value = sv.getString();
            try {
                JSONArray jsonArray = new JSONArray(value);
                ArrayList<Team> array = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Team team = new Team(jsonObject.getInt(Constants.Intent_Extras.TEAM_NUMBER),"");
                    team.setMapElement(RANK,new ScoutValue(jsonObject.getInt(RANK)));
                    if(i < jsonArray.length()-1) {
                        if(jsonArray.getJSONObject(i).getInt(RANK) == team.getMapElement(RANK).getInt())
                        {
                            team.setMapElement(TIE,new ScoutValue(1));
                        }
                        else
                        {
                            team.setMapElement(TIE,new ScoutValue(0));
                        }
                    }
                    else
                    {
                        team.setMapElement(TIE, new ScoutValue(0));
                    }
                    array.add(team);
                }

                Collections.sort(array, new Comparator<Team>() {
                    @Override
                    public int compare(Team lhs, Team rhs) {
                        if(lhs.getMapElement(RANK).getInt() < rhs.getMapElement(RANK).getInt())
                        {
                            return -1;
                        }
                        else if(rhs.getMapElement(RANK).getInt() < lhs.getMapElement(RANK).getInt())
                        {
                            return 1;
                        }
                        else
                        {
                            if(lhs.getMapElement(TIE).getInt() > 0 && rhs.getMapElement(TIE).getInt() == 0)
                            {
                                return -1;
                            }
                            else if(rhs.getMapElement(TIE).getInt() > 0 && lhs.getMapElement(TIE).getInt() == 0)
                            {
                                return 1;
                            }
                            else
                            {
                                return 0;
                            }
                        }
                    }
                });

                setArray(array, false);
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void drop(int from, int to) {
        Team team = adapter.getItem(from);
        int teamNumber = team.getTeamNumber();
        adapter.remove(teamNumber);
        adapter.add(to,teamNumber);
    }
}
