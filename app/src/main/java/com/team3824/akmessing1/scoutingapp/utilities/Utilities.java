package com.team3824.akmessing1.scoutingapp.utilities;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.mobeta.android.dslv.DragSortListView;
import com.team3824.akmessing1.scoutingapp.database_helpers.DriveTeamFeedbackDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.MatchScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.PitScoutDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.StatsDB;
import com.team3824.akmessing1.scoutingapp.database_helpers.SuperScoutDB;
import com.team3824.akmessing1.scoutingapp.views.CustomCardinalRank6;
import com.team3824.akmessing1.scoutingapp.views.CustomEdittext;
import com.team3824.akmessing1.scoutingapp.views.CustomNumeric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Utilities {

    private static String TAG = "Utilities";

    public static void setupUI(final Activity activity, View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof CustomEdittext) && !(view instanceof CustomNumeric) && !(view instanceof CustomCardinalRank6) && !(view instanceof DragSortListView)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(activity,innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if(activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    // class extension that fixes the utf8 response being handled correctly
    // needed for the blue alliance requests
    public static class JsonUTF8Request extends JsonRequest<JSONArray> {
        public JsonUTF8Request(int method, String url, JSONArray jsonRequest,
                               Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
            super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                    errorListener);
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data, "UTF-8");
                return Response.success(new JSONArray(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
    }

    public static String CursorToJsonString(Cursor cursor){

        JSONArray jsonArray = new JSONArray();
        while(!cursor.isAfterLast())
        {
            JSONObject jsonObject = new JSONObject();
            for(int i = 0; i < cursor.getColumnCount(); i++)
            {
                try {
                    switch (cursor.getType(i)) {
                        case Cursor.FIELD_TYPE_FLOAT:
                            jsonObject.put(cursor.getColumnName(i), cursor.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_STRING:
                            jsonObject.put(cursor.getColumnName(i), cursor.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            jsonObject.put(cursor.getColumnName(i), cursor.getInt(i));
                            break;
                    }
                }
                catch (JSONException ex)
                {
                    Log.e(TAG, ex.getMessage());
                }
            }
            jsonArray.put(jsonObject);
            cursor.moveToNext();
        }
        return jsonArray.toString();
    }

    public static byte[] intToByteArray(int a) {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    public static int byteArrayToInt(byte[] b) {
        return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + ((b[0] & 0xFF) << 24);
    }

    public static boolean digestMatch(byte[] imageData, byte[] digestData) {
        return Arrays.equals(getDigest(imageData), digestData);
    }

    public static byte[] getDigest(byte[] imageData) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(imageData);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            throw new UnsupportedOperationException("MD5 algorithm not available on this device.");
        }
    }

    public static void JsonToMatchDB(MatchScoutDB matchScoutDB, String message)
    {
        try {
            JSONArray jsonArray = new JSONArray(message.substring(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, ScoutValue> map = new HashMap<>();
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject.get(key);
                        if (value instanceof Integer) {
                            map.put(key, new ScoutValue((int) value));
                        } else if (value instanceof Float) {
                            map.put(key, new ScoutValue((float) value));
                        } else if (value instanceof String) {
                            map.put(key, new ScoutValue((String) value));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG,e.getMessage());
                    }
                }
                matchScoutDB.updateMatch(map);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void JsonToSuperDB(SuperScoutDB superScoutDB, String message)
    {
        try {
            JSONArray jsonArray = new JSONArray(message.substring(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, ScoutValue> map = new HashMap<>();
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject.get(key);
                        if (value instanceof Integer) {
                            map.put(key, new ScoutValue((int) value));
                        } else if (value instanceof Float) {
                            map.put(key, new ScoutValue((float) value));
                        } else if (value instanceof String) {
                            map.put(key, new ScoutValue((String) value));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                superScoutDB.updateMatch(map);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void JsonToPitDB(PitScoutDB pitScoutDB, String message)
    {
        try {
            JSONArray jsonArray = new JSONArray(message.substring(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, ScoutValue> map = new HashMap<>();
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject.get(key);
                        if (value instanceof Integer) {
                            map.put(key, new ScoutValue((int) value));
                        } else if (value instanceof Float) {
                            map.put(key, new ScoutValue((float) value));
                        } else if (value instanceof String) {
                            map.put(key, new ScoutValue((String) value));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                if(map.get(PitScoutDB.KEY_COMPLETE).getInt() == 1) {
                    pitScoutDB.updatePit(map);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void JsonToStatsDB(StatsDB statsDB, String message)
    {
        try {
            JSONArray jsonArray = new JSONArray(message.substring(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, ScoutValue> map = new HashMap<>();
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject.get(key);
                        if (value instanceof Integer) {
                            map.put(key, new ScoutValue((int) value));
                        } else if (value instanceof Float) {
                            map.put(key, new ScoutValue((float) value));
                        } else if (value instanceof String) {
                            map.put(key, new ScoutValue((String) value));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                statsDB.updateStats(map);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void JsonToDriveTeamDB(DriveTeamFeedbackDB driveTeamFeedbackDB, String message)
    {
        try {
            JSONArray jsonArray = new JSONArray(message.substring(1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                HashMap<String, ScoutValue> map = new HashMap<>();
                Iterator<String> iter = jsonObject.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject.get(key);
                        if (value instanceof Integer) {
                            map.put(key, new ScoutValue((int) value));
                        } else if (value instanceof Float) {
                            map.put(key, new ScoutValue((float) value));
                        } else if (value instanceof String) {
                            map.put(key, new ScoutValue((String) value));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                driveTeamFeedbackDB.updateComments(map.get(DriveTeamFeedbackDB.KEY_TEAM_NUMBER).getInt(), map.get(DriveTeamFeedbackDB.KEY_COMMENTS).getString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

}
