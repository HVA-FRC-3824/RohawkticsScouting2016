package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_PositionalShot;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_Auto;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_Defenses;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_Endgame;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_Foul;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_IndividualDefense;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_Points;
import com.team3824.akmessing1.scoutingapp.adapters.EventListAdapters.ELA_Shot;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_PositionalShot;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Auto;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Defenses;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Endgame;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Fouls;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_IndividualDefense;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Points;
import com.team3824.akmessing1.scoutingapp.list_items.event_list_items.ELI_Shots;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * This activity shows a table comparing all the teams at the current event. There is a dropdown menu
 * to select the criteria upon which to compare teams.
 */
public class EventView extends Activity implements AdapterView.OnItemSelectedListener {

    private static String TAG = "EventView";
    private ListView listView;
    private StatsDB statsDB;
    private Spinner mainSpinner;
    private Spinner subSpinner;

    /**
     * Sets up dropdown menu, the list view, and the stats database helper.
     *
     * @param savedInstanceState The Bundle containing anything saved from the last time this activity
     *                           was used.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        // Add the custom header and have the back button take the user to the home screen
        CustomHeader customHeader = (CustomHeader) findViewById(R.id.event_view_header);
        customHeader.removeHome();

        // Set up the dropdown menu for all the comparison categories
        mainSpinner = (Spinner) findViewById(R.id.event_view_spinner);
        ArrayList<String> mainList = new ArrayList<>(Arrays.asList(Constants.EVENT_VIEW.EVENT_VIEW_DROPDOWN));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mainList);
        mainSpinner.setAdapter(adapter);
        mainSpinner.setOnItemSelectedListener(this);

        subSpinner = (Spinner) findViewById(R.id.event_view_spinner_sub);
        subSpinner.setVisibility(View.GONE);
        subSpinner.setOnItemSelectedListener(this);

        listView = (ListView) findViewById(R.id.event_view_list);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        statsDB = new StatsDB(this, eventID);

    }

    /**
     * When a category is selected from the dropdown menu calculate all the values needed for the
     * columns and display.
     *
     * @param parent The parent adapter view of the view that is selected
     * @param view   The view that is selected
     * @param pos    The position of the selected item in the menu
     * @param id     The id of the selected item
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Cursor cursor = statsDB.getStats();
        switch (parent.getId()) {
            case R.id.event_view_spinner: {
                findViewById(R.id.key1).setVisibility(View.GONE);
                findViewById(R.id.key2).setVisibility(View.GONE);
                findViewById(R.id.key3).setVisibility(View.GONE);
                ArrayList<String> subList;
                ArrayAdapter<String> subAdapter;
                switch (pos) {
                    case Constants.EVENT_VIEW.POINTS:
                        points(cursor);
                        subSpinner.setVisibility(View.GONE);
                        break;
                    case Constants.EVENT_VIEW.DEFENSES:
                        defenses(cursor);
                        findViewById(R.id.key1).setVisibility(View.VISIBLE);

                        subList = new ArrayList<>(Arrays.asList(Constants.Defense_Arrays.DEFENSES_LABEL));
                        subList.add(0, "All");
                        subAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, subList);
                        subSpinner.setAdapter(subAdapter);
                        subSpinner.setVisibility(View.VISIBLE);
                        break;
                    case Constants.EVENT_VIEW.HIGH_GOAL:
                        goal(cursor, "high");

                        subList = new ArrayList<>(Arrays.asList(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_LABEL));
                        subList.add(0, "All");
                        subAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, subList);
                        subSpinner.setAdapter(subAdapter);
                        subSpinner.setVisibility(View.VISIBLE);
                        break;
                    case Constants.EVENT_VIEW.LOW_GOAL:
                        goal(cursor, "low");

                        subList = new ArrayList<>(Arrays.asList(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_LABEL));
                        subList.add(0, "All");
                        subAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, subList);
                        subSpinner.setAdapter(subAdapter);
                        subSpinner.setVisibility(View.VISIBLE);
                        break;
                    case Constants.EVENT_VIEW.AUTO:
                        auto(cursor);
                        findViewById(R.id.key2).setVisibility(View.VISIBLE);
                        findViewById(R.id.key3).setVisibility(View.VISIBLE);
                        subSpinner.setVisibility(View.GONE);
                        break;
                    case Constants.EVENT_VIEW.ENDGAME:
                        endgame(cursor);
                        subSpinner.setVisibility(View.GONE);
                        break;
                    case Constants.EVENT_VIEW.FOULS:
                        fouls(cursor);
                        subSpinner.setVisibility(View.GONE);
                        break;
                    default:
                        assert false;
                        break;
                }
            }
            break;
            case R.id.event_view_spinner_sub: {
                switch (mainSpinner.getSelectedItemPosition()) {
                    case Constants.EVENT_VIEW.DEFENSES:
                        switch (pos - 1) {
                            case -1: // All
                                defenses(cursor);
                                break;
                            default:
                                individual_defense(cursor, pos - 1);
                                break;
                        }
                        break;
                    case Constants.EVENT_VIEW.HIGH_GOAL:
                        switch (pos - 1) {
                            case -1: // All
                                goal(cursor, "high");
                                break;
                            default:
                                positional_shooting(cursor, "high", pos - 1);
                                break;
                        }
                        break;
                    case Constants.EVENT_VIEW.LOW_GOAL:
                        switch (pos - 1) {
                            case -1: // All
                                goal(cursor, "low");
                                break;
                            default:
                                positional_shooting(cursor, "low", pos - 1);
                                break;
                        }
                        break;
                }
            }
            break;
        }
    }

    /**
     * Not possible for nothing to be selected...
     *
     * @param parent
     */
    public void onNothingSelected(AdapterView<?> parent) {
        assert false;
    }

    /**
     * Calculate all the different types of points for each team and then sort the teams based on
     * average points
     *
     * @param cursor The response from the stats database table
     */
    private void points(Cursor cursor) {
        ArrayList<ELI_Points> teams = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Points team = new ELI_Points(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);

            if (totalMatches > -1) {
                totalMatches = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES));

                team.mHighPoints = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT)) * 10;

                team.mLowPoints = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_HIT)) * 2 +
                        cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_LOW_HIT)) * 5;

                team.mEndgamePoints = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_CHALLENGE)) * 5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_SCALE)) * 15;

                for (int i = 0; i < 9; i++) {
                    team.mDefensePoints += cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[i])) * 2;
                    team.mDefensePoints += cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i])) * 10;
                    team.mDefensePoints += cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i])) * 5;
                }

                team.mTeleopPoints = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_HIT)) * 5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_HIT)) * 2;
                for (int i = 0; i < 9; i++) {
                    team.mTeleopPoints += cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED_POINTS[i])) * 5;
                }

                team.mAutoPoints = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT)) * 10 +
                        cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_LOW_HIT)) * 5;
                for (int i = 0; i < 9; i++) {
                    team.mAutoPoints += cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[i])) * 2;
                    team.mAutoPoints += cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i])) * 10;
                }


                team.mFoulPoints = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_FOULS)) * -5 +
                        cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TECH_FOULS)) * -5;

                team.mTotalPoints = team.mEndgamePoints + team.mTeleopPoints + team.mAutoPoints + team.mFoulPoints;

                team.mAvgPoints = (totalMatches == 0.0f) ? 0.0f : (float) team.mTotalPoints / totalMatches;
            }
            teams.add(team);
        }

        // Sorting is based on average points
        Collections.sort(teams, new Comparator<ELI_Points>() {
            @Override
            public int compare(ELI_Points lhs, ELI_Points rhs) {
                return Float.compare(rhs.mAvgPoints, lhs.mAvgPoints);
            }
        });

        // Header row hack
        teams.add(0, new ELI_Points(-1));
        ELA_Points eventPointsListAdapter = new ELA_Points(this, teams);
        listView.setAdapter(eventPointsListAdapter);
    }

    /**
     * Calculate all the values for comparing team's performance the defenses
     *
     * @param cursor The response from the stats database table
     */
    private void defenses(Cursor cursor) {
        ArrayList<ELI_Defenses> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Defenses team = new ELI_Defenses(teamNumber);

            int totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);

            if (totalMatches > 0) {
                for (int i = 0; i < 9; i++) {
                    team.crosses[i] = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED[i])) +
                            cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i]));
                    team.seens[i] = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[i]));
                    team.notCrosses[i] = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[i]));
                    if (team.seens[i] == 0) {
                        team.time[i] = -1;
                    } else if (team.seens[i] == team.notCrosses[i]) {
                        team.time[i] = 0;
                    } else {
                        team.time[i] = ((float) cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_TIME[i]))) / ((float) (team.seens[i] - team.notCrosses[i]));
                    }
                    team.totalCrosses += team.crosses[i];
                }
            }
            teams.add(team);
        }

        // Compares based on average total number of crosses
        Collections.sort(teams, new Comparator<ELI_Defenses>() {
            @Override
            public int compare(ELI_Defenses lhs, ELI_Defenses rhs) {
                // tertiary statement make the values 0 if the team has been in no matches
                float leftValue = (lhs.seens[Constants.Defense_Arrays.LOW_BAR_INDEX] == 0) ? 0 : (lhs.totalCrosses / (float) lhs.seens[Constants.Defense_Arrays.LOW_BAR_INDEX]);
                float rightValue = (rhs.seens[Constants.Defense_Arrays.LOW_BAR_INDEX] == 0) ? 0 : (rhs.totalCrosses / (float) rhs.seens[Constants.Defense_Arrays.LOW_BAR_INDEX]);
                return Float.compare(rightValue, leftValue);
            }
        });

        // Header row hack
        teams.add(0, new ELI_Defenses(-1));
        ELA_Defenses ela_Defenses = new ELA_Defenses(this, teams);
        listView.setAdapter(ela_Defenses);
    }

    /**
     * Calculates all the values for comparing teams by their performance with an individual defense
     *
     * @param cursor        The response from the stats database table
     * @param defense_index The index of the individual defense for which the values are
     *                      being calculated
     */
    private void individual_defense(Cursor cursor, int defense_index) {

        assert defense_index >= 0 && defense_index < 9;

        ArrayList<ELI_IndividualDefense> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_IndividualDefense team = new ELI_IndividualDefense(teamNumber);

            int totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mTeleopCross = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_CROSSED[defense_index]));
                team.mAutoCross = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[defense_index]));
                team.mAutoReach = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_REACHED[defense_index]));
                team.mSeen = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[defense_index]));
                team.mNotCross = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_NOT_CROSSED[defense_index]));
                if (team.mSeen == 0) {
                    team.mTime = -1;
                } else if (team.mSeen == team.mNotCross) {
                    team.mTime = 0;
                } else {
                    team.mTime = ((float) cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_TELEOP_TIME[defense_index]))) / ((float) (team.mSeen - team.mNotCross));
                }
            }
            teams.add(team);
        }

        // Sorts based on the speed of crossing if a team has crossed this defense
        Collections.sort(teams, new Comparator<ELI_IndividualDefense>() {
            @Override
            public int compare(ELI_IndividualDefense lhs, ELI_IndividualDefense rhs) {
                // tertiary statement make the values 0 if the team has been in no matches
                if (lhs.mAutoCross == 0 && lhs.mTeleopCross == 0 && rhs.mAutoCross == 0 && rhs.mTeleopCross == 0) {
                    return 0;
                } else if (lhs.mAutoCross == 0 && lhs.mTeleopCross == 0) {
                    return 1;
                } else if (rhs.mAutoCross == 0 && rhs.mTeleopCross == 0) {
                    return -1;
                } else {
                    if (lhs.mTime < rhs.mTime) {
                        return -1;
                    } else if (lhs.mTime > rhs.mTime) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        // Header row hack
        teams.add(0, new ELI_IndividualDefense(-1));
        ELA_IndividualDefense eventIndividualDefenseListAdapter = new ELA_IndividualDefense(this, teams);
        listView.setAdapter(eventIndividualDefenseListAdapter);
    }

    /**
     * Calculates all the values for comparing teams by their shoot performance
     *
     * @param cursor The response from the stats database table
     * @param goal   Which goal to calculate values for (high or low)
     */
    private void goal(Cursor cursor, String goal) {

        assert goal.equals("high") || goal.equals("low");

        ArrayList<ELI_Shots> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Shots team = new ELI_Shots(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mAutoMade = cursor.getInt(cursor.getColumnIndex("total_auto_" + goal + "_hit"));
                team.mAutoTaken = cursor.getInt(cursor.getColumnIndex("total_auto_" + goal + "_miss")) + team.mAutoMade;
                team.mAutoPercentage = (team.mAutoTaken == 0) ? 0 : (float) team.mAutoMade / (float) team.mAutoTaken * 100.0f;

                team.mTeleopMade = cursor.getInt(cursor.getColumnIndex("total_teleop_" + goal + "_hit"));
                team.mTeleopTaken = cursor.getInt(cursor.getColumnIndex("total_teleop_" + goal + "_miss")) + team.mTeleopMade;
                team.mTeleopPercentage = (team.mTeleopTaken == 0) ? 0 : (float) team.mTeleopMade / (float) team.mTeleopTaken * 100.0f;

                team.mTime = cursor.getInt(cursor.getColumnIndex("total_teleop_" + goal + "_aim_time"));
                team.mTime = (team.mTeleopTaken == 0) ? 0 : team.mTime / (float) team.mTeleopTaken;
            }
            teams.add(team);
        }

        // Sorted based on percentage if more than 10 shots taken or
        // number of shots made if less than 10
        Collections.sort(teams, new Comparator<ELI_Shots>() {
            @Override
            public int compare(ELI_Shots lhs, ELI_Shots rhs) {

                if (lhs.mTeleopMade > 5 && rhs.mTeleopMade > 5) {
                    if (lhs.mTeleopPercentage > rhs.mTeleopPercentage) {
                        return -1;
                    } else if (rhs.mTeleopPercentage > lhs.mTeleopPercentage) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else if (lhs.mTeleopMade > 5) {
                    return -1;
                } else if (rhs.mTeleopMade > 5) {
                    return 1;
                } else {
                    return rhs.mTeleopMade - lhs.mTeleopMade;
                }
            }
        });

        // Header row hack
        teams.add(0, new ELI_Shots(-1));
        ELA_Shot eventShotListAdapter = new ELA_Shot(this, teams);
        listView.setAdapter(eventShotListAdapter);
    }

    /**
     * @param cursor
     * @param goal
     * @param position
     */
    private void positional_shooting(Cursor cursor, String goal, int position) {
        assert goal.equals("high") || goal.equals("low");
        assert position > -1 && position < Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length;

        ArrayList<ELI_PositionalShot> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));

            ELI_PositionalShot team = new ELI_PositionalShot(teamNumber);
            int totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);
            if (totalMatches > 0) {
                switch (goal) {
                    case "high":
                        team.mMade = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_HIT[position]));
                        team.mTotal = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS[position]));
                        team.mPercent = (team.mTotal == 0) ? 0.0f : (float) team.mMade / (float) team.mTotal * 100.0f;
                        break;
                    case "low":
                        team.mMade = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_HIT[position]));
                        team.mTotal = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS[position]));
                        team.mPercent = (team.mTotal == 0) ? 0.0f : (float) team.mMade / (float) team.mTotal * 100.0f;
                        break;
                }
            }

            teams.add(team);
        }

        Collections.sort(teams, new Comparator<ELI_PositionalShot>() {
            @Override
            public int compare(ELI_PositionalShot lhs, ELI_PositionalShot rhs) {
                if (lhs.mMade > 5 && rhs.mMade > 5) {
                    if (lhs.mPercent > rhs.mPercent) {
                        return -1;
                    } else if (rhs.mPercent > lhs.mPercent) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else if (lhs.mMade > 5) {
                    return -1;
                } else if (rhs.mMade > 5) {
                    return 1;
                } else {
                    return rhs.mMade - lhs.mMade;
                }
            }
        });

        teams.add(0, new ELI_PositionalShot(-1));
        ELA_PositionalShot eventShotListAdapter = new ELA_PositionalShot(this, teams);
        listView.setAdapter(eventShotListAdapter);
    }

    /**
     * Calculates values for comparing teams by their autonomous performance
     *
     * @param cursor The response from the stats database table
     */
    private void auto(Cursor cursor) {
        ArrayList<ELI_Auto> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Auto team = new ELI_Auto(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);


            if (totalMatches > -1) {
                for (int i = 0; i < 9; i++) {
                    team.mDefenses.crosses[i] = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_AUTO_CROSSED[i]));
                    team.mDefenses.seens[i] = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_DEFENSES_SEEN[i]));
                    team.mDefenses.totalCrosses += team.mDefenses.crosses[i];
                }

                team.mHigh.mAutoMade = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT));
                team.mHigh.mAutoTaken = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_MISS)) + team.mHigh.mAutoMade;
                team.mHigh.mAutoPercentage = (team.mHigh.mAutoTaken == 0) ? 0 : (float) team.mHigh.mAutoMade / (float) team.mHigh.mAutoTaken * 100.0f;

                team.mLow.mAutoMade = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_HIGH_HIT));
                team.mLow.mAutoTaken = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_AUTO_LOW_MISS)) + team.mLow.mAutoMade;
                team.mLow.mAutoPercentage = (team.mLow.mAutoTaken == 0) ? 0 : (float) team.mLow.mAutoMade / (float) team.mLow.mAutoTaken * 100.0f;

            }
            teams.add(team);
        }

        // Compares based on average auto points
        Collections.sort(teams, new Comparator<ELI_Auto>() {
            @Override
            public int compare(ELI_Auto lhs, ELI_Auto rhs) {
                if (lhs.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX] == 0 && rhs.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX] == 0) {
                    return 0;
                } else if (lhs.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX] == 0) {
                    if (rhs.mDefenses.totalCrosses > 0 || rhs.mLow.mAutoMade > 0 || rhs.mHigh.mAutoMade > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else if (rhs.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX] == 0) {
                    if (lhs.mDefenses.totalCrosses > 0 || lhs.mLow.mAutoMade > 0 || lhs.mHigh.mAutoMade > 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                } else {
                    float leftValue = (lhs.mDefenses.totalCrosses * 10.0f + lhs.mHigh.mAutoMade * 10.0f + lhs.mLow.mAutoMade * 5.0f) / (float) lhs.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX];
                    float rightValue = (rhs.mDefenses.totalCrosses * 10.0f + rhs.mHigh.mAutoMade * 10.0f + rhs.mLow.mAutoMade * 5.0f) / (float) rhs.mDefenses.seens[Constants.Defense_Arrays.LOW_BAR_INDEX];
                    return Float.compare(rightValue, leftValue);
                }
            }
        });

        // Header row hack
        teams.add(0, new ELI_Auto(-1));
        ELA_Auto ela_Auto = new ELA_Auto(this, teams);
        listView.setAdapter(ela_Auto);
    }

    /**
     * Calculates values for comparing teams by their endgame performance
     *
     * @param cursor The response from the stats database table
     */
    private void endgame(Cursor cursor) {
        ArrayList<ELI_Endgame> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Endgame team = new ELI_Endgame(teamNumber);

            int totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);

            if (totalMatches > -1) {
                team.mTotalMatches = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES));
                team.mFailedChallenge = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_FAILED_CHALLENGE));
                team.mChallenge = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_CHALLENGE));
                team.mFailedScale = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_FAILED_SCALE));
                team.mScale = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_SCALE));
            }
            teams.add(team);
        }

        // Sorts based on average endgame points
        Collections.sort(teams, new Comparator<ELI_Endgame>() {
            @Override
            public int compare(ELI_Endgame lhs, ELI_Endgame rhs) {
                if (lhs.mTotalMatches == 0 && rhs.mTotalMatches == 0) {
                    return 0;
                } else if (lhs.mTotalMatches == 0) {
                    if (rhs.mChallenge > 0 || rhs.mScale > 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                } else if (rhs.mTotalMatches == 0) {
                    if (lhs.mChallenge > 0 || lhs.mScale > 0) {
                        return -1;
                    } else {
                        return 0;
                    }
                }

                float delta = ((lhs.mScale * 15 + lhs.mChallenge * 5) / (float) lhs.mTotalMatches) - ((rhs.mScale * 15 + rhs.mChallenge * 5) / (float) rhs.mTotalMatches);
                if (delta < 0)
                    return 1;
                else if (delta > 0)
                    return -1;
                else
                    return 0;
            }
        });

        // Header row hack
        teams.add(0, new ELI_Endgame(-1));
        ELA_Endgame ela_Endgame = new ELA_Endgame(this, teams);
        listView.setAdapter(ela_Endgame);
    }

    /**
     * Calculates values for comparing teams by the fouls they cause
     *
     * @param cursor The response from the stats database table
     */
    private void fouls(Cursor cursor) {
        ArrayList<ELI_Fouls> teams = new ArrayList<>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int teamNumber = cursor.getInt(cursor.getColumnIndex(StatsDB.KEY_TEAM_NUMBER));
            ELI_Fouls team = new ELI_Fouls(teamNumber);

            float totalMatches = cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_MATCHES);

            if (totalMatches > 0) {
                team.mFouls = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_FOULS));
                team.mTechFouls = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_TECH_FOULS));
                team.mYellowCards = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_YELLOW_CARDS));
                team.mRedCards = cursor.getInt(cursor.getColumnIndex(Constants.Calculated_Totals.TOTAL_RED_CARDS));
            }
            teams.add(team);
        }


        Collections.sort(teams, new Comparator<ELI_Fouls>() {
            @Override
            public int compare(ELI_Fouls lhs, ELI_Fouls rhs) {
                return (lhs.mRedCards * 4 + lhs.mYellowCards * 3 + lhs.mTechFouls * 2 + lhs.mFouls) - (rhs.mRedCards * 4 + rhs.mYellowCards * 3 + rhs.mTechFouls * 2 + rhs.mFouls);
            }
        });

        // Header row hack
        teams.add(0, new ELI_Fouls(-1));
        ELA_Foul eventFoulListAdapter = new ELA_Foul(this, teams);
        listView.setAdapter(eventFoulListAdapter);
    }
}
