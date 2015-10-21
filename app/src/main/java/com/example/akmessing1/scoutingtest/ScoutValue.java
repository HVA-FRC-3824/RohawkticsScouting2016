package com.example.akmessing1.scoutingtest;

// Object that supports multiple types
public class ScoutValue {
    public enum ValueType{STRING_TYPE, INT_TYPE, FLOAT_TYPE};
    ValueType type;
    int value1;
    String value2;
    float value3;

    public ScoutValue(int value)
    {
        type = ValueType.INT_TYPE;
        value1 = value;
    }

    public ScoutValue(String value)
    {
        type = ValueType.STRING_TYPE;
        value2 = value;
    }

    public ScoutValue(float value)
    {
        type = ValueType.FLOAT_TYPE;
        value3 = value;
    }

    public ValueType getType()
    {
        return type;
    }

    public String getString()
    {
        return value2;
    }

    public int getInt()
    {
        return value1;
    }

    public float getFloat()
    {
        return value3;
    }

}
