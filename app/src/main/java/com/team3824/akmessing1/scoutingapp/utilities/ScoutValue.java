package com.team3824.akmessing1.scoutingapp.utilities;

// Object that supports multiple types
public class ScoutValue {
    public enum ValueType{STRING_TYPE, INT_TYPE, FLOAT_TYPE}

    private ValueType type;
    private int value1;
    private String value2;
    private float value3;

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
        switch (type) {
            case INT_TYPE:
                return String.valueOf(value1);
            case STRING_TYPE:
                return value2;
            case FLOAT_TYPE:
                return String.valueOf(value3);
            default:
                assert false;
                return "";
        }
    }

    public int getInt()
    {
        switch (type)
        {
            case INT_TYPE:
                return value1;
            case STRING_TYPE:
                return Integer.parseInt(value2);
            case FLOAT_TYPE:
                return (int)value3;
            default:
                assert false;
                return 0;
        }

    }

    public float getFloat()
    {
        switch (type)
        {
            case INT_TYPE:
                return (float)value1;
            case STRING_TYPE:
                return Float.parseFloat(value2);
            case FLOAT_TYPE:
                return value3;
            default:
                assert false;
                return 0.0f;
        }
    }

}
