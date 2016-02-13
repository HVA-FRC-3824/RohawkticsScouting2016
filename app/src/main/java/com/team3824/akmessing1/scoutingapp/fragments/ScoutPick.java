package com.team3824.akmessing1.scoutingapp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobeta.android.dslv.DragSortListView;
import com.team3824.akmessing1.scoutingapp.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.adapters.PickListAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.list_items.Team;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ScoutPick extends ScoutFragment implements DragSortListView.DropListener, View.OnClickListener{

    private String TAG = "ScoutPick";

    DragSortListView list;
    PickListAdapter adapter;
    StatsDB statsDB;
    SharedPreferences sharedPref;

    String pickType = "";
    ArrayList<Team> teams;

    Button save;

    File saveFile;
    FileInputStream saveFIS;
    FileOutputStream saveFOS;
    JSONArray rankingJSON;

    public ScoutPick() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick, container, false);
        list = (DragSortListView) view.findViewById(R.id.pick_list);

        teams = new ArrayList<>();
        sharedPref = getActivity().getSharedPreferences("appData", Context.MODE_PRIVATE);
        statsDB = new StatsDB(getContext(),sharedPref.getString(Constants.EVENT_ID, ""));
        Cursor statsCursor = statsDB.getStats();
        PitScoutDB pitScoutDB = new PitScoutDB(getContext(),sharedPref.getString(Constants.EVENT_ID,""));

        saveFile = new File(getContext().getFilesDir(), pickType + "ranking.txt");
        if(saveFile.exists())
        {
            try {
                saveFIS = new FileInputStream(saveFile);
                String jsonText = "";
                char current;
                while (saveFIS.available() > 0) {
                    current = (char) saveFIS.read();
                    jsonText += String.valueOf(current);
                }
                Log.d(TAG,jsonText);
                rankingJSON = new JSONArray(jsonText);
                saveFIS.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
            catch (IOException e) {
                Log.d(TAG,e.getMessage());
            } catch (JSONException e) {
                Log.d(TAG,e.getMessage());
            }
        }

        for(statsCursor.moveToFirst(); !statsCursor.isAfterLast(); statsCursor.moveToNext()) {
            int teamNumber = statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            Cursor pitCursor = pitScoutDB.getTeamInfo(teamNumber);

            Team team = new Team(teamNumber,
                    pitCursor.getString(pitCursor.getColumnIndex(PitScoutDB.KEY_NICKNAME)));

            if (pitCursor.getColumnIndex("robotPicture") != -1) {
                team.setMapElement("robotPicture", new ScoutValue(pitCursor.getString(pitCursor.getColumnIndex("robotPicture"))));
            }

            team.setMapElement(StatsDB.KEY_PICKED, new ScoutValue(statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_PICKED))));
            if(statsCursor.getColumnIndex(Constants.TOTAL_YELLOW_CARDS) > -1) {
                team.setMapElement(Constants.TOTAL_YELLOW_CARDS, new ScoutValue(statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_YELLOW_CARDS))));
                team.setMapElement(Constants.TOTAL_RED_CARDS, new ScoutValue(statsCursor.getInt(statsCursor.getColumnIndex(Constants.TOTAL_RED_CARDS))));
            }

            team = setupTeam(team,statsCursor);

            teams.add(team);
        }

        if(rankingJSON != null)
        {
            Map<Integer, Integer> rankingMap = new HashMap<>();
            try {
                for (int i = 0; i < rankingJSON.length(); i++) {
                    int teamNumber = rankingJSON.getInt(i);
                    rankingMap.put(teamNumber,i+1);
                }
                for(int i = 0; i < teams.size(); i++)
                {
                    Team team = teams.get(i);
                    team.setMapElement(pickType + "_pick_rank",new ScoutValue(rankingMap.get(team.getTeamNumber())));
                }
            } catch (JSONException e) {
                Log.d(TAG,e.getMessage());
            }
        }

        Comparator<Team> compare = new Comparator<Team>(){
            public int compare(Team a, Team b)
            {

                if(a.getMapElement(StatsDB.KEY_PICKED).getInt() > 0 && b.getMapElement(StatsDB.KEY_PICKED).getInt() > 0)
                {
                    if(b.containsMapElement(pickType+"_pick_rank"))
                    {
                        return a.getMapElement(pickType+"_pick_rank").getInt() - b.getMapElement(pickType+"_pick_rank").getInt();

                    }
                    else
                    {
                        return b.getMapElement(pickType+"_pickability").getInt() - a.getMapElement(pickType+"_pickability").getInt();
                    }
                }
                else if( a.getMapElement(StatsDB.KEY_PICKED).getInt() > 0 )
                {
                    return 1;
                }
                else if(b.getMapElement(StatsDB.KEY_PICKED).getInt() > 0)
                {
                    return -1;
                }
                else {
                    if(b.containsMapElement(pickType+"_pick_rank"))
                    {
                        return a.getMapElement(pickType+"_pick_rank").getInt() - b.getMapElement(pickType+"_pick_rank").getInt();
                    }
                    else
                    {
                        return b.getMapElement(pickType+"_pickability").getInt() - a.getMapElement(pickType+"_pickability").getInt();
                    }
                }
            }
        };

        Collections.sort(teams,compare);

        if(rankingJSON == null) {
            rankingJSON = new JSONArray();
            for (int i = 0; i < teams.size(); i++) {
                rankingJSON.put(teams.get(i).getTeamNumber());
            }
        }

        adapter = new PickListAdapter(getContext(), R.layout.list_item_pick, teams, pickType, statsDB, compare);

        list.setAdapter(adapter);

        list.setDropListener(this);

        Button reset = (Button)view.findViewById(R.id.reset);
        reset.setOnClickListener(this);

        save = (Button)view.findViewById(R.id.save);
        save.setOnClickListener(this);

        return view;
    }

    public void setPickType(String pt)
    {
        pickType = pt;
    }

    protected Team setupTeam(Team team, Cursor statsCursor)
    {
        return team;
    }

    @Override
    public void drop(int from, int to) {

        save.setVisibility(View.VISIBLE);

        HashMap<String, ScoutValue> map = new HashMap<String, ScoutValue>();
        Team team = adapter.getItem(from);
        team.setMapElement(pickType + "_pick_rank", new ScoutValue(to + 1));
        adapter.remove(team);
        adapter.add(to, team);

        if (from < to) {
            for (int i = from; i < to; i++) {
                team = adapter.getItem(i);
                team.setMapElement(pickType + "_pick_rank", new ScoutValue(i + 1));
            }
        } else if (from > to) {
            for (int i = to + 1; i <= from; i++) {
                team = adapter.getItem(i);
                team.setMapElement(pickType + "_pick_rank", new ScoutValue(i + 1));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.reset:
                if(saveFile.exists())
                {
                    saveFile.delete();
                }
                for(int i = 0; i < adapter.getCount(); i++)
                {
                    Team team = adapter.getItem(i);
                    team.removeMapElement(pickType + "_pick_rank");
                }
                adapter.sort();
                adapter.notifyDataSetChanged();
                break;
            case R.id.save:
                save.setVisibility(View.GONE);
                rankingJSON = new JSONArray();
                for(int i = 0; i < adapter.getCount(); i++)
                {
                    rankingJSON.put(adapter.getItem(i).getTeamNumber());
                }
                String rankingText = rankingJSON.toString();
                Log.d(TAG,rankingText);
                if(saveFile.exists())
                {
                    saveFile.delete();
                    try {
                        saveFile.createNewFile();
                    } catch (IOException e) {
                        Log.d(TAG,e.getMessage());
                    }
                }
                try
                {
                    saveFOS = new FileOutputStream(saveFile);
                    saveFOS.write(rankingText.getBytes());
                    saveFOS.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG,e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG,e.getMessage());
                }
                break;
        }
    }
}
