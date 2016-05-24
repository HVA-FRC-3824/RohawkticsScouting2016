package com.team3824.akmessing1.scoutingapp.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ScoutMap {

    private HashMap<String, ScoutValue> map;

    public ScoutMap() {
        map = new HashMap<>();
    }

    public void put(String key, String value) {
        map.put(key, new ScoutValue(value));
    }

    public void put(String key, float value) {
        map.put(key, new ScoutValue(value));
    }

    public void put(String key, int value) {
        map.put(key, new ScoutValue(value));
    }

    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public ScoutValue get(String key) {
        return map.get(key);
    }

    public int getInt(String key) {
        return map.get(key).getInt();
    }

    public float getFloat(String key) {
        return map.get(key).getFloat();
    }

    public String getString(String key) {
        return map.get(key).getString();
    }

    public void remove(String key) {
        map.remove(key);
    }

    public Set<Map.Entry<String, ScoutValue>> entrySet() {
        return map.entrySet();
    }

    public void clear() {
        map.clear();
    }

    public void putAll(ScoutMap sm) {
        map.putAll(sm.getMap());
    }

    public HashMap<String, ScoutValue> getMap() {
        return map;
    }
}
