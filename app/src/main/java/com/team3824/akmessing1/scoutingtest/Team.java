package com.team3824.akmessing1.scoutingtest;


import java.util.HashMap;

public class Team {
    private int teamNumber;
    private String nickname;
    private HashMap<String, ScoutValue> values;

    public Team(int teamNumber, String nickname)
    {
        this.teamNumber = teamNumber;
        this.nickname = nickname;
    }



    public int getTeamNumber()
    {
        return teamNumber;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setValueMap(HashMap<String, ScoutValue> map)
    {
        values = map;
    }

    public HashMap<String, ScoutValue> getValueMap()
    {
        return values;
    }

    public void setMapElement(String key, ScoutValue value)
    {
        if(values.containsKey(key))
        {
            values.remove(key);
        }
        values.put(key,value);
    }

    public ScoutValue getMapElement(String key)
    {
        if(values.containsKey(key))
        {
            return values.get(key);
        }
        return null;
    }

}
