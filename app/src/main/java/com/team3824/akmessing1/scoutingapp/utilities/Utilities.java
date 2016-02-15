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
import com.team3824.akmessing1.scoutingapp.views.CustomEdittext;
import com.team3824.akmessing1.scoutingapp.views.CustomNumeric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by akmessing1 on 1/2/16.
 */
public class Utilities {

    private static String TAG = "Utilities";

    public static void setupUI(final Activity activity, View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof CustomEdittext) && !(view instanceof CustomNumeric)) {

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
}
