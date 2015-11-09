package com.team3824.akmessing1.scoutingtest.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.dslv.DragSortListView;
import com.team3824.akmessing1.scoutingtest.PitScoutDB;
import com.team3824.akmessing1.scoutingtest.R;
import com.team3824.akmessing1.scoutingtest.ScoutValue;
import com.team3824.akmessing1.scoutingtest.StatsDB;
import com.team3824.akmessing1.scoutingtest.Team;
import com.team3824.akmessing1.scoutingtest.activities.PickList;
import com.team3824.akmessing1.scoutingtest.adapters.PickListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SecondPick extends ScoutFragment{

    DragSortListView list;
    PickListAdapter adapter;

    public SecondPick() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second_pick, container, false);
        list = (DragSortListView)view.findViewById(R.id.second_pick_list);

        final Comparator<Team> compare = new Comparator<Team>(){
            public int compare(Team a, Team b)
            {
                int rankA = a.getMapElement(StatsDB.KEY_SECOND_PICK_RANK).getInt();
                int rankB = b.getMapElement(StatsDB.KEY_SECOND_PICK_RANK).getInt();
                return rankA - rankB;
            }
        };

        ArrayList<Team> teams = new ArrayList<>();
        SharedPreferences sharedPref = getActivity().getSharedPreferences("appData", Context.MODE_PRIVATE);
        final StatsDB statsDB = new StatsDB(getContext(),sharedPref.getString("event_id",""));
        Cursor statsCursor = statsDB.getStats();
        PitScoutDB pitScoutDB = new PitScoutDB(getContext(),sharedPref.getString("event_id",""));
        do{
            int teamNumber = statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            Cursor pitCursor = pitScoutDB.getTeamInfo(teamNumber);

            Team team = new Team(teamNumber,
                    pitCursor.getString(pitCursor.getColumnIndex(PitScoutDB.KEY_NICKNAME)));

            HashMap<String, ScoutValue> map = new HashMap<>();

            if(pitCursor.getColumnIndex("robotPicture") != -1) {
                map.put("robotPicture", new ScoutValue(pitCursor.getString(pitCursor.getColumnIndex("robotPicture"))));
            }

            int secondRank = statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_SECOND_PICK_RANK));
            int computedSecondPickRank = statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK));
            map.put(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(secondRank));
            map.put(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK,new ScoutValue(computedSecondPickRank));

            String bottomText = "Pick Rank: "+String.valueOf(map.get(StatsDB.KEY_SECOND_PICK_RANK).getInt())+" Computed Pick Rank: "+String.valueOf(map.get(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK).getInt());
            map.put("second_pick_bottom_text", new ScoutValue(bottomText));

            team.setValueMap(map);
            teams.add(team);

            statsCursor.moveToNext();
        }while(!statsCursor.isAfterLast());
        Collections.sort(teams, compare);

        adapter = new PickListAdapter(getContext(),R.id.second_pick_list,teams,2);
        list.setAdapter(adapter);

        list.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from < to) {
                    HashMap<String, ScoutValue> map = new HashMap<String, ScoutValue>();
                    Team team = adapter.getItem(from);
                    team.setMapElement(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(to + 1));
                    int teamNumber = team.getTeamNumber();
                    int secondPick = to + 1;
                    int computedSecondPick = team.getMapElement(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK).getInt();
                    String bottomText = "Pick Rank: " + String.valueOf(secondPick) + " Computed Pick Rank: " + String.valueOf(computedSecondPick);
                    team.setMapElement("second_pick_bottom_text", new ScoutValue(bottomText));
                    adapter.remove(team);
                    adapter.add(to, team);
                    map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                    map.put(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(secondPick));
                    map.put(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK, new ScoutValue(computedSecondPick));
                    statsDB.updateStats(map);
                    for (int i = from; i < to; i++) {
                        team = adapter.getItem(i);
                        team.setMapElement(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(i + 1));
                        teamNumber = team.getTeamNumber();
                        secondPick = i+1;
                        computedSecondPick = team.getMapElement(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK).getInt();
                        bottomText = "Pick Rank: " + String.valueOf(secondPick) + " Computed Pick Rank: " + String.valueOf(computedSecondPick);
                        team.setMapElement("second_pick_bottom_text", new ScoutValue(bottomText));
                        map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                        map.put(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(secondPick));
                        map.put(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK, new ScoutValue(computedSecondPick));
                        statsDB.updateStats(map);
                    }
                } else if (from > to) {
                    HashMap<String, ScoutValue> map = new HashMap<String, ScoutValue>();
                    Team team = adapter.getItem(from);
                    int teamNumber = team.getTeamNumber();
                    int secondPick = to+1;
                    int computedSecondPick = team.getMapElement(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK).getInt();
                    team.setMapElement(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(to + 1));
                    String bottomText = "Pick Rank: " + String.valueOf(secondPick) + " Computed Pick Rank: " + String.valueOf(computedSecondPick);
                    team.setMapElement("second_pick_bottom_text", new ScoutValue(bottomText));
                    adapter.remove(team);
                    adapter.add(to, team);
                    map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                    map.put(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(secondPick));
                    map.put(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK, new ScoutValue(computedSecondPick));
                    statsDB.updateStats(map);
                    for (int i = to+1; i <= from; i++) {
                        team = adapter.getItem(i);
                        team.setMapElement(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(i + 1));
                        teamNumber = team.getTeamNumber();
                        secondPick = i+1;
                        computedSecondPick = team.getMapElement(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK).getInt();
                        bottomText = "Pick Rank: " + String.valueOf(secondPick) + " Computed Pick Rank: " + String.valueOf(computedSecondPick);                        team.setMapElement("second_pick_bottom_text", new ScoutValue(bottomText));
                        team.setMapElement("second_pick_bottom_text", new ScoutValue(bottomText));
                        map.put(StatsDB.KEY_TEAM_NUMBER, new ScoutValue(teamNumber));
                        map.put(StatsDB.KEY_SECOND_PICK_RANK, new ScoutValue(secondPick));
                        map.put(StatsDB.KEY_COMPUTED_SECOND_PICK_RANK, new ScoutValue(computedSecondPick));
                        statsDB.updateStats(map);
                    }
                }

                adapter.notifyDataSetChanged();

            }
        });
        
        
        return view;
    }


}
