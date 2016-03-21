package com.team3824.akmessing1.scoutingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.list_items.MatchTeamNote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Andrew Messing
 */
public class NotesFilterAdapter extends ArrayAdapter<MatchTeamNote> {

    private final String TAG = "NotesFilterAdapter";

    private ArrayList<MatchTeamNote> notes;
    private ArrayList<MatchTeamNote> filteredNotes;

    private boolean swap;
    private String filter;

    public NotesFilterAdapter(Context context, int resource, ArrayList<MatchTeamNote> objects) {
        super(context, resource, objects);
        notes = new ArrayList<>();
        filteredNotes = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            notes.add(objects.get(i));
            filteredNotes.add(objects.get(i));
        }
        swap = false;
        filter = "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentView) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_match_team_note, null);

        MatchTeamNote matchTeamNote = filteredNotes.get(position);

        TextView team_number, match_number, note;
        if (swap) {
            match_number = (TextView) convertView.findViewById(R.id.team_number);
            match_number.setText(String.format("M%d", matchTeamNote.getMatchNumber()));
            team_number = (TextView) convertView.findViewById(R.id.match_number);
            team_number.setText(String.valueOf(matchTeamNote.getTeamNumber()));
        } else {
            match_number = (TextView) convertView.findViewById(R.id.match_number);
            match_number.setText(String.format("M%d", matchTeamNote.getMatchNumber()));
            team_number = (TextView) convertView.findViewById(R.id.team_number);
            team_number.setText(String.valueOf(matchTeamNote.getTeamNumber()));
        }
        note = (TextView) convertView.findViewById(R.id.note);
        note.setText(matchTeamNote.getNote());

        return convertView;
    }

    Comparator teamSort = new Comparator<MatchTeamNote>() {
        @Override
        public int compare(MatchTeamNote lhs, MatchTeamNote rhs) {
            if (lhs.getTeamNumber() == rhs.getTeamNumber()) {
                return lhs.getMatchNumber() - rhs.getMatchNumber();
            }
            return lhs.getTeamNumber() - rhs.getTeamNumber();
        }
    };

    public void sortByTeam() {
        swap = false;
        Collections.sort(filteredNotes, teamSort);
        notifyDataSetChanged();
    }

    Comparator matchSort = new Comparator<MatchTeamNote>() {
        @Override
        public int compare(MatchTeamNote lhs, MatchTeamNote rhs) {
            if (lhs.getMatchNumber() == rhs.getMatchNumber()) {
                return lhs.getTeamNumber() - rhs.getTeamNumber();
            }
            return lhs.getMatchNumber() - rhs.getMatchNumber();
        }
    };

    public void sortByMatch() {
        swap = true;
        Collections.sort(filteredNotes, matchSort);
        notifyDataSetChanged();
    }

    public void filter(String f) {
        filter = f;
        filteredNotes = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            MatchTeamNote matchTeamNote = notes.get(i);
            if (filter.equals("") || matchTeamNote.getNote().contains(filter)) {
                filteredNotes.add(matchTeamNote);
            }
        }
        clear();
        addAll(filteredNotes);
        notifyDataSetChanged();
    }
}
