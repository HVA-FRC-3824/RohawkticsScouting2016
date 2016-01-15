package com.team3824.akmessing1.scoutingapp;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SchulzeMethod {

    private String TAG = "SchulzeMethod";

    private String key;
    private int[][] matrix;
    private int[][] strongestPathMatrix;
    private List<Integer> teamNumbers;
    private Set<Integer> rankedTeamNumbers;
    String[] ranking;

    public SchulzeMethod(String key, List<Integer> teamNumbers)
    {
        this.key = key;
        this.teamNumbers = teamNumbers;
        rankedTeamNumbers = new HashSet<>();
        matrix = new int[teamNumbers.size()][teamNumbers.size()];
        for (int[] line : matrix) {
            Arrays.fill(line, 0);
        }

        strongestPathMatrix = new int[teamNumbers.size()][teamNumbers.size()];
        for (int[] line : strongestPathMatrix) {
            Arrays.fill(line, 0);
        }
        ranking = new String[teamNumbers.size()];
        Arrays.fill(ranking,"T1");
    }

    public String[] calcRanking(Cursor newData)
    {
        if(newData.getCount() > 0)
        {
            JSONArray jsonArray = null;
            ArrayList<Integer> before = new ArrayList<>();
            do{

                String line = newData.getString(newData.getColumnIndex(key));
                try {
                    jsonArray = new JSONArray(line);
                    before.clear();
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        int index2 = teamNumbers.indexOf(jsonArray.getInt(i));
                        for(int j = 0; j < before.size(); j++)
                        {
                            int index1 = teamNumbers.indexOf(before.get(j));
                            matrix[index1][index2]++;
                        }
                        before.add(jsonArray.getInt(i));
                        rankedTeamNumbers.add(jsonArray.getInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
                newData.moveToNext();
            }while(!newData.isAfterLast());

            for(int i = 0; i < teamNumbers.size(); i++)
            {
                for(int j = 0; j < teamNumbers.size(); j++)
                {
                    if(matrix[i][j] > matrix[j][i])
                    {
                        strongestPathMatrix[i][j] = matrix[i][j];
                    }
                    else
                    {
                        strongestPathMatrix[i][j] = 0;
                    }
                }
            }

            for(int i = 0; i < teamNumbers.size(); i++)
            {
                for(int j = 0; j < teamNumbers.size(); j++)
                {
                    if(i == j) {
                        continue;
                    }
                    for(int k = 0; k < teamNumbers.size(); k++)
                    {
                        if(i != k && j != k)
                        {
                            strongestPathMatrix[j][k] = Math.max(strongestPathMatrix[j][k], Math.min(strongestPathMatrix[j][i], strongestPathMatrix[i][k]));

                        }
                    }
                }
            }

            List<Integer> sortedRanking = new ArrayList<>();
            sortedRanking.addAll(rankedTeamNumbers);
            // sort the teams based on their paths to another team
            Collections.sort(sortedRanking, new Comparator<Integer>() {
                public int compare(Integer a, Integer b) {
                    int indexA = teamNumbers.indexOf(a);
                    int indexB = teamNumbers.indexOf(b);
                    if (strongestPathMatrix[indexA][indexB] > strongestPathMatrix[indexB][indexA]) {
                        return -1;
                    } else if (strongestPathMatrix[indexA][indexB] == strongestPathMatrix[indexB][indexA]) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });

            int rank = 1;
            for(int i = 0; i < sortedRanking.size(); i++)
            {
                boolean tied = false;
                int currentTeamNumber = sortedRanking.get(i);
                int currentTeamNumberIndex = teamNumbers.indexOf(currentTeamNumber);
                if(i > 0)
                {
                    int previousTeamNumber = sortedRanking.get(i-1);
                    int previousTeamNumberIndex = teamNumbers.indexOf(previousTeamNumber);

                    if(strongestPathMatrix[currentTeamNumberIndex][previousTeamNumberIndex] == strongestPathMatrix[previousTeamNumberIndex][currentTeamNumberIndex])
                    {
                        tied = true;
                    }
                }
                if(!tied && i < sortedRanking.size()-1)
                {
                    int nextTeamNumber = sortedRanking.get(i+1);
                    int nextTeamNumberIndex = teamNumbers.indexOf(nextTeamNumber);

                    if(strongestPathMatrix[currentTeamNumberIndex][nextTeamNumberIndex] == strongestPathMatrix[nextTeamNumberIndex][currentTeamNumberIndex])
                    {
                        tied = true;
                    }
                }
                if(tied)
                {
                    ranking[currentTeamNumberIndex] = "T"+String.valueOf(rank);
                }
                else
                {
                    rank = i+1;
                    ranking[currentTeamNumberIndex] = String.valueOf(rank);
                }
                Log.i(TAG,teamNumbers.get(currentTeamNumberIndex)+": "+ranking[currentTeamNumberIndex]);
            }
            rank = sortedRanking.size()+1;
            for(int i = 0; i < teamNumbers.size(); i++)
            {
                if(sortedRanking.indexOf(teamNumbers.get(i)) == -1)
                {
                    ranking[i] = "T"+ String.valueOf(rank);
                    Log.i(TAG,teamNumbers.get(i)+": "+ranking[i]);
                }
            }
        }
        return ranking;
    }


}
