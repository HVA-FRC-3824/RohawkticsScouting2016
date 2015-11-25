package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Based off of Wildrank's version by Liam

public class CustomStacksDisplay extends View {

    private String TAG = "CustomStacksDisplay";

    JSONArray matches;
    int numStacks;
    Paint textPaint, existingTotesPaint, newTotesPaint, canPaint, noodlePaint, outlinePaint, droppedPaint,backgroundPaint;

    int screenWidth, screenHeight;


    public CustomStacksDisplay(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        existingTotesPaint = new Paint();
        existingTotesPaint.setColor(Color.LTGRAY);
        newTotesPaint = new Paint();
        newTotesPaint.setColor(Color.DKGRAY);
        canPaint = new Paint();
        canPaint.setColor(Color.argb(255, 85, 107, 47)); // Dark Green
        noodlePaint = new Paint();
        noodlePaint.setColor(Color.YELLOW);
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.BLACK);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(3.0f);
        droppedPaint = new Paint();
        droppedPaint.setColor(Color.argb(200, 255, 0, 0)); // Translucent red
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y - 400;
        Log.d(TAG,"width: "+screenWidth+" height: "+screenHeight);
        //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(screenWidth,screenHeight);
        //setLayoutParams(layoutParams);
    }

    public void setMatches(Cursor matchCursor) {
        if(matchCursor == null || matchCursor.getCount() == 0)
            return;
        Log.d(TAG,"New data");
        matches = new JSONArray();
        numStacks = 0;

        do{
            String matchText = matchCursor.getString(matchCursor.getColumnIndex("teleop_stacks"));
            try {
                JSONArray match = new JSONArray(matchText);
                numStacks += match.length();
                matches.put(match);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            matchCursor.moveToNext();
        }while(!matchCursor.isAfterLast());
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        resolveSizeAndState(screenWidth,widthMeasureSpec,1);
        resolveSizeAndState(screenHeight,heightMeasureSpec,1);
        setMeasuredDimension(screenWidth,screenHeight);
    }


    @Override
    public void onDraw(Canvas c)
    {
        Log.d(TAG,"New drawing");
        if(matches.length() == 0)
        {
            c.drawText("No data exists for this team.", 100, 100, textPaint);
        }



        // First, compute the dimensions of our drawn items so that they're scaled properly
        float toteWidthToHeightRatio = 0.7f;
        // Default tote width is calculated so that the stacks will completely fill the screen horizontally
        float toteWidth = (float) screenWidth / (float) numStacks;
        // Default height is calculated based on the width
        float toteHeight = toteWidth * toteWidthToHeightRatio;
        // If this would result in a complete stack (7 elements) overflowing the screen, we'll calculate the stack height
        // so that a complete stack would completely fill the screen vertically
        if (toteHeight * 7 > screenHeight) {
            toteHeight = (float) screenHeight / 7f; // gives equal vertical space to 6 totes and 1 bin
            toteWidth = toteHeight / toteWidthToHeightRatio;
        }

        c.drawRect(0,screenHeight-toteHeight*7,screenWidth,screenHeight,backgroundPaint);
        c.drawRect(0,screenHeight - toteHeight * 7, screenWidth,screenHeight,outlinePaint);

        Log.d(TAG, "stack count: " + numStacks);
        Log.d(TAG, "match count: " + matches.length());

        int stackCount = 0;
        try
        {
            for (int matchIndex = 0; matchIndex < matches.length(); matchIndex++)
            {
                JSONArray match = matches.getJSONArray(matchIndex);

                for (int stackIndex = 0; stackIndex < match.length(); stackIndex++)
                {
                    JSONObject stack = match.getJSONObject(stackIndex);
                    if(!stack.getBoolean("isStackDropped")) {
                        int totalStackHeight = 0;
                        int preexistingTotes = stack.getInt("preexistingToteCount");
                        float x = toteWidth * stackCount;

                        for (int j = 0; j < preexistingTotes; j++) {
                            float left = x;
                            float right = x + toteWidth;
                            float bottom = screenHeight - (toteHeight * j);
                            float top = bottom - toteHeight;
                            c.drawRect(left, top, right, bottom, existingTotesPaint);
                            c.drawRect(left, top, right, bottom, outlinePaint);
                            totalStackHeight++;
                        }

                        int newTotes = stack.getInt("toteCount");
                        for (int j = preexistingTotes; j < preexistingTotes + newTotes; j++) {
                            float left = x;
                            float right = x + toteWidth;
                            float bottom = screenHeight - (toteHeight * j);
                            float top = bottom - toteHeight;
                            c.drawRect(left, top, right, bottom, newTotesPaint);
                            c.drawRect(left, top, right, bottom, outlinePaint);
                            totalStackHeight++;
                        }

                        if (stack.getBoolean("isCanned")) {
                            float radius;
                            if (toteHeight > toteWidth) {
                                radius = (toteWidth / 2f);
                            } else {
                                radius = (toteHeight / 2f);
                            }
                            float cx = x + (toteWidth / 2);
                            float cy = screenHeight - (toteHeight * (preexistingTotes + newTotes) + radius);
                            if (stack.getBoolean("isCanDropped")) {
                                c.drawCircle(cx, cy, radius, droppedPaint);
                                if (stack.getBoolean("isNoodled")) {
                                    float noodleRadius = radius / 3;
                                    c.drawCircle(cx, cy, noodleRadius, droppedPaint);
                                    c.drawCircle(cx, cy, noodleRadius, outlinePaint);

                                }
                            } else {
                                c.drawCircle(cx, cy, radius, canPaint);
                                if (stack.getBoolean("isNoodled")) {
                                    float noodleRadius = radius / 3;
                                    c.drawCircle(cx, cy, noodleRadius, noodlePaint);
                                    c.drawCircle(cx, cy, noodleRadius, outlinePaint);
                                }
                            }
                            c.drawCircle(cx, cy, radius, outlinePaint);
                            totalStackHeight++;

                        }
                    }
                    else
                    {
                        int totalStackHeight = 0;
                        float x = toteWidth * stackCount;
                        int newTotes = stack.getInt("toteCount");

                        for (int j = 0; j < newTotes; j++) {
                            float left = x;
                            float right = x + toteWidth;
                            float bottom = screenHeight - (toteHeight * j);
                            float top = bottom - toteHeight;
                            c.drawRect(left, top, right, bottom, droppedPaint);
                            c.drawRect(left, top, right, bottom, outlinePaint);
                            totalStackHeight++;
                        }
                        if (stack.getBoolean("isCanned") && stack.getBoolean("isCanDropped")) {
                            float radius;
                            if (toteHeight > toteWidth) {
                                radius = (toteWidth / 2f);
                            } else {
                                radius = (toteHeight / 2f);
                            }
                            float cx = x + (toteWidth / 2);
                            float cy = screenHeight - (toteHeight * newTotes + radius);
                            c.drawCircle(cx, cy, radius, droppedPaint);
                            c.drawCircle(cx, cy, radius, outlinePaint);

                            if (stack.getBoolean("isNoodled")) {
                                float noodleRadius = radius / 3;
                                c.drawCircle(cx, cy, noodleRadius, droppedPaint);
                                c.drawCircle(cx, cy, noodleRadius, outlinePaint);

                            }
                        }

                    }
                    stackCount++;
                }
                // Draw line to separate matches
                c.drawLine(stackCount * toteWidth, 0, stackCount * toteWidth, screenHeight, outlinePaint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
