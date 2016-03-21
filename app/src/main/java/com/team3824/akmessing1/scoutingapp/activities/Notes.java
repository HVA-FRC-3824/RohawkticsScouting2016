package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.NotesFilterAdapter;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.list_items.MatchTeamNote;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Andrew Messing
 */
public class Notes extends Activity implements OnCheckedChangeListener, ImageButton.OnClickListener {

    private final String TAG = "Notes";

    ListView listView;

    NotesFilterAdapter adapter;
    ArrayList<MatchTeamNote> notes;

    EditText search;
    ImageButton searchButton;
    ImageButton clearButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        CustomHeader customHeader = (CustomHeader) findViewById(R.id.header);
        customHeader.removeHome();

        listView = (ListView) findViewById(R.id.listview);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        String eventId = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        MatchScoutDB matchScoutDB = new MatchScoutDB(this, eventId);
        Cursor matchCursor = matchScoutDB.getAllInfo();

        notes = new ArrayList<>();

        for (matchCursor.moveToFirst(); !matchCursor.isAfterLast(); matchCursor.moveToNext()) {
            int matchNumber = matchCursor.getInt(matchCursor.getColumnIndex(MatchScoutDB.KEY_MATCH_NUMBER));
            int teamNumber = matchCursor.getInt(matchCursor.getColumnIndex(MatchScoutDB.KEY_TEAM_NUMBER));
            String note = matchCursor.getString(matchCursor.getColumnIndex(Constants.Post_Match_Inputs.POST_NOTES));
            if (note == null || note.equals("")) {
                continue;
            }
            MatchTeamNote matchTeamNote = new MatchTeamNote(matchNumber, teamNumber, note);
            notes.add(matchTeamNote);
        }

        Collections.sort(notes, new Comparator<MatchTeamNote>() {
            @Override
            public int compare(MatchTeamNote lhs, MatchTeamNote rhs) {
                if (lhs.getTeamNumber() == rhs.getTeamNumber()) {
                    return lhs.getMatchNumber() - rhs.getMatchNumber();
                }
                return lhs.getTeamNumber() - rhs.getTeamNumber();
            }
        });

        adapter = new NotesFilterAdapter(this, R.layout.list_item_match_team_note, notes);

        listView.setAdapter(adapter);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(this);

        search = (EditText) findViewById(R.id.search);
        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
        clearButton = (ImageButton) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(this);

        Utilities.setupUI(this, findViewById(android.R.id.content));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_match_numbers:
                adapter.sortByMatch();
                break;
            case R.id.radio_team_numbers:
                adapter.sortByTeam();
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                String query = String.valueOf(search.getText());
                adapter.filter(query);
                break;
            case R.id.clear_button:
                search.setText("");
                adapter.filter("");
                break;
        }
    }
}
