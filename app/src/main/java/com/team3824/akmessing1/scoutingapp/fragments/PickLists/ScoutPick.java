package com.team3824.akmessing1.scoutingapp.fragments.PickLists;

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
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.PickListAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.list_items.Team;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;

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

/**
 * Parent class of all the Pick Lists.
 *
 * @author Andrew Messing
 * @version 1
 */
public class ScoutPick extends ScoutFragment implements DragSortListView.DropListener, View.OnClickListener {

    private final String TAG = "ScoutPick";

    private PickListAdapter adapter;

    private String pickType = "";
    private ArrayList<Team> teams;
    private Map<Integer, Team> teamsMap;

    private Button save;

    private File saveFile;
    private JSONArray rankingJSON;

    public ScoutPick() {
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick, container, false);
        DragSortListView list = (DragSortListView) view.findViewById(R.id.pick_list);

        teams = new ArrayList<>();
        teamsMap = new HashMap<>();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPref.getString(Constants.Settings.EVENT_ID, "");
        StatsDB statsDB = new StatsDB(getContext(), eventId);
        Cursor statsCursor = statsDB.getStats();
        PitScoutDB pitScoutDB = new PitScoutDB(getContext(), sharedPref.getString(Constants.Settings.EVENT_ID, ""));

        saveFile = new File(getContext().getFilesDir(), String.format("%s_%s_ranking.txt", eventId, pickType));
        if (saveFile.exists()) {
            try {
                FileInputStream saveFIS = new FileInputStream(saveFile);
                String jsonText = "";
                char current;
                while (saveFIS.available() > 0) {
                    current = (char) saveFIS.read();
                    jsonText += String.valueOf(current);
                }
                Log.d(TAG, jsonText);
                rankingJSON = new JSONArray(jsonText);
                saveFIS.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        for (statsCursor.moveToFirst(); !statsCursor.isAfterLast(); statsCursor.moveToNext()) {
            int teamNumber = statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            Cursor pitCursor = pitScoutDB.getTeamInfo(teamNumber);

            Team team = new Team(teamNumber,
                    pitCursor.getString(pitCursor.getColumnIndex(PitScoutDB.KEY_NICKNAME)));

            if (pitCursor.getColumnIndex(Constants.Pit_Inputs.PIT_ROBOT_PICTURE) != -1) {
                team.setMapElement(Constants.Pit_Inputs.PIT_ROBOT_PICTURE, new ScoutValue(pitCursor.getString(pitCursor.getColumnIndex(Constants.Pit_Inputs.PIT_ROBOT_PICTURE))));
            }

            team.setMapElement(StatsDB.KEY_PICKED, new ScoutValue(statsCursor.getInt(statsCursor.getColumnIndex(StatsDB.KEY_PICKED))));
            if (statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_YELLOW_CARDS) > -1) {
                team.setMapElement(Constants.Calculated_Totals.TOTAL_YELLOW_CARDS, new ScoutValue(statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_YELLOW_CARDS))));
                team.setMapElement(Constants.Calculated_Totals.TOTAL_RED_CARDS, new ScoutValue(statsCursor.getInt(statsCursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_RED_CARDS))));
            }

            team = setupTeam(team, statsCursor);

            teams.add(team);
            teamsMap.put(team.getTeamNumber(), team);
        }

        if (rankingJSON != null) {
            Map<Integer, Integer> rankingMap = new HashMap<>();
            try {
                for (int i = 0; i < rankingJSON.length(); i++) {
                    int teamNumber = rankingJSON.getInt(i);
                    rankingMap.put(teamNumber, i + 1);
                }
                for (int i = 0; i < teams.size(); i++) {
                    Team team = teams.get(i);
                    team.setMapElement(pickType + Constants.Pick_List.PICK_RANK, new ScoutValue(rankingMap.get(team.getTeamNumber())));
                }
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }

        Comparator<Team> compare = new Comparator<Team>() {
            public int compare(Team a, Team b) {

                if (a.getMapElement(StatsDB.KEY_PICKED).getInt() > 0 && b.getMapElement(StatsDB.KEY_PICKED).getInt() > 0) {
                    if (b.containsMapElement(pickType + Constants.Pick_List.PICK_RANK)) {
                        return a.getMapElement(pickType + Constants.Pick_List.PICK_RANK).getInt() - b.getMapElement(pickType + Constants.Pick_List.PICK_RANK).getInt();

                    } else {
                        return b.getMapElement(pickType + Constants.Pick_List.PICKABILITY).getInt() - a.getMapElement(pickType + Constants.Pick_List.PICKABILITY).getInt();
                    }
                } else if (a.getMapElement(StatsDB.KEY_PICKED).getInt() > 0) {
                    return 1;
                } else if (b.getMapElement(StatsDB.KEY_PICKED).getInt() > 0) {
                    return -1;
                } else {
                    if (b.containsMapElement(pickType + Constants.Pick_List.PICK_RANK)) {
                        return a.getMapElement(pickType + Constants.Pick_List.PICK_RANK).getInt() - b.getMapElement(pickType + Constants.Pick_List.PICK_RANK).getInt();
                    } else {
                        return b.getMapElement(pickType + Constants.Pick_List.PICKABILITY).getInt() - a.getMapElement(pickType + Constants.Pick_List.PICKABILITY).getInt();
                    }
                }
            }
        };

        Collections.sort(teams, compare);

        if (rankingJSON == null) {
            rankingJSON = new JSONArray();
            for (int i = 0; i < teams.size(); i++) {
                rankingJSON.put(teams.get(i).getTeamNumber());
            }
        }

        adapter = new PickListAdapter(getContext(), teams, statsDB, compare);

        list.setAdapter(adapter);

        list.setDropListener(this);

        Button reset = (Button) view.findViewById(R.id.reset);
        reset.setOnClickListener(this);

        save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);

        return view;
    }

    void setPickType(String pt) {
        pickType = pt;
    }


    /**
     * Fills an individual team with all the data needed to display and sort it for this particular
     * pick type. Is overrided in each subclass.
     *
     * @param team        The team object to fill with data
     * @param statsCursor The response from the database with the data to use for the team
     * @return The filled team
     */
    Team setupTeam(Team team, Cursor statsCursor) {
        return team;
    }

    @Override
    public void drop(int from, int to) {

        save.setVisibility(View.VISIBLE);

        HashMap<String, ScoutValue> map = new HashMap<String, ScoutValue>();
        Team team = adapter.getItem(from);
        team.setMapElement(pickType + Constants.Pick_List.PICK_RANK, new ScoutValue(to + 1));
        adapter.remove(team);
        adapter.add(to, team);

        if (from < to) {
            for (int i = from; i < to; i++) {
                team = adapter.getItem(i);
                team.setMapElement(pickType + Constants.Pick_List.PICK_RANK, new ScoutValue(i + 1));
            }
        } else if (from > to) {
            for (int i = to + 1; i <= from; i++) {
                team = adapter.getItem(i);
                team.setMapElement(pickType + Constants.Pick_List.PICK_RANK, new ScoutValue(i + 1));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                if (saveFile.exists()) {
                    saveFile.delete();
                }
                for (int i = 0; i < adapter.getCount(); i++) {
                    Team team = adapter.getItem(i);
                    team.removeMapElement(pickType + Constants.Pick_List.PICK_RANK);
                }
                adapter.sort();
                adapter.notifyDataSetChanged();
                break;
            case R.id.save:
                save.setVisibility(View.GONE);
                rankingJSON = new JSONArray();
                for (int i = 0; i < adapter.getCount(); i++) {
                    rankingJSON.put(adapter.getItem(i).getTeamNumber());
                }
                String rankingText = rankingJSON.toString();
                Log.d(TAG, rankingText);
                if (saveFile.exists()) {
                    saveFile.delete();
                    try {
                        saveFile.createNewFile();
                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
                try {
                    FileOutputStream saveFOS = new FileOutputStream(saveFile);
                    saveFOS.write(rankingText.getBytes());
                    saveFOS.close();
                } catch (FileNotFoundException e) {
                    Log.d(TAG, e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
                break;
        }
    }

    /**
     * Gets a list of which teams have been picked
     *
     * @return A list of which teams have been picked
     */
    public ArrayList<Integer> getPicked() {
        ArrayList<Integer> picked = new ArrayList<>();
        for (Team team : teams) {
            if (team.getMapElement(StatsDB.KEY_PICKED).getInt() > 0) {
                picked.add(team.getTeamNumber());
            }
        }
        Collections.sort(picked);
        return picked;
    }

    /**
     * Updates the list of team with which ones are picked/unpicked
     *
     * @param picked   A list of the teams that need to updated to picked
     * @param unpicked A list of the teams that need to be updated to unpicked
     */
    public void setPickedUnpicked(ArrayList<Integer> picked, ArrayList<Integer> unpicked) {
        for (int i = 0; i < picked.size(); i++) {
            teamsMap.get(picked.get(i)).setMapElement(StatsDB.KEY_PICKED, new ScoutValue(1));
        }
        for (int i = 0; i < unpicked.size(); i++) {
            teamsMap.get(unpicked.get(i)).setMapElement(StatsDB.KEY_PICKED, new ScoutValue(0));
        }
        adapter.sort();
        adapter.notifyDataSetChanged();
    }
}
