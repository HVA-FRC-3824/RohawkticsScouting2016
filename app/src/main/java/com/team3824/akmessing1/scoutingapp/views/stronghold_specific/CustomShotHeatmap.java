package com.team3824.akmessing1.scoutingapp.views.stronghold_specific;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;

/**
 * @author Andrew Messing
 */
public class CustomShotHeatmap extends View {

    private final String TAG = "CustomShotHeatmap";

    private Bitmap courtyardBitmap;
    private Paint canvasPaint;
    private Paint textPaint;
    private Paint[][] paints;
    private int[][] hits, misses, totals, radiuses;
    private int[][] xs, ys;

    public static final int HIGH = 0;
    public static final int LOW = 1;
    public static final int BOTH = 2;

    private int mode;

    public CustomShotHeatmap(Context context, AttributeSet attrs) {
        super(context, attrs);
        canvasPaint = new Paint(Paint.DITHER_FLAG);

        textPaint = new Paint();
        textPaint.setColor(Color.MAGENTA);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        courtyardBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.courtyard_top_down);

        paints = new Paint[2][];
        paints[HIGH] = new Paint[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        for(int i = 0; i < paints[HIGH].length;i++)
        {
            paints[HIGH][i] = new Paint();
            paints[HIGH][i].setColor(Color.GREEN);
        }
        paints[LOW] = new Paint[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];
        for(int i = 0; i < paints[LOW].length;i++)
        {
            paints[LOW][i] = new Paint();
            paints[LOW][i].setColor(Color.GREEN);
        }

        hits = new int[2][];
        hits[HIGH] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        hits[LOW] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];

        misses = new int[2][];
        misses[HIGH] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        misses[LOW] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];

        totals = new int[2][];
        totals[HIGH] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        totals[LOW] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];

        xs = new int[2][];
        xs[HIGH] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        xs[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_OUTER_WORKS] = 550;
        xs[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_LEFT_BATTER] = 75;
        xs[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_CENTER_BATTER] = 170;
        xs[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_RIGHT_BATTER] = 75;
        xs[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_ALIGNMENT_LINE] = 400;
        xs[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_OPEN_SPACE] = 300;

        xs[LOW] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];
        xs[LOW][Constants.Calculated_Totals.SHOT_POSITION_LOW_LEFT_BATTER] = 75;
        xs[LOW][Constants.Calculated_Totals.SHOT_POSITION_LOW_RIGHT_BATTER] = 75;
        xs[LOW][Constants.Calculated_Totals.SHOT_POSITION_LOW_OPEN_SPACE] = 300;

        ys = new int[2][];
        ys[HIGH] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        ys[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_OUTER_WORKS] = 285;
        ys[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_LEFT_BATTER] = 400;
        ys[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_CENTER_BATTER] = 285;
        ys[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_RIGHT_BATTER] = 170;
        ys[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_ALIGNMENT_LINE] = 285;
        ys[HIGH][Constants.Calculated_Totals.SHOT_POSITION_HIGH_OPEN_SPACE] = 600;

        ys[LOW] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];
        ys[LOW][Constants.Calculated_Totals.SHOT_POSITION_LOW_LEFT_BATTER] = 400;
        ys[LOW][Constants.Calculated_Totals.SHOT_POSITION_LOW_RIGHT_BATTER] = 170;
        ys[LOW][Constants.Calculated_Totals.SHOT_POSITION_LOW_OPEN_SPACE] = 600;

        radiuses = new int[2][];
        radiuses[HIGH] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length];
        radiuses[LOW] = new int[Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length];

        mode = -1;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldH, oldW);
        courtyardBitmap = Bitmap.createScaledBitmap(courtyardBitmap, w, h, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(courtyardBitmap, 0, 0, canvasPaint);

        switch (mode) {
            case HIGH:
                for (int i = 0; i < hits[HIGH].length; i++) {
                    canvas.drawCircle(xs[HIGH][i], ys[HIGH][i], radiuses[HIGH][i], paints[HIGH][i]);

                    if (totals[HIGH][i] > 0) {
                        canvas.drawText(String.format("%d / %d", hits[HIGH][i], totals[HIGH][i]), xs[HIGH][i], ys[HIGH][i] - radiuses[HIGH][i] - 50, textPaint);
                        canvas.drawText(String.format("%.1f%%", (float) hits[HIGH][i] / (float) totals[HIGH][i] * 100.0f), xs[HIGH][i], ys[HIGH][i] - radiuses[HIGH][i] - 10, textPaint);
                    }
                }
                break;
            case LOW:
                for (int i = 0; i < hits[LOW].length; i++) {
                    canvas.drawCircle(xs[LOW][i], ys[LOW][i], radiuses[LOW][i],paints[LOW][i]);
                    if (totals[LOW][i] > 0) {
                        canvas.drawText(String.format("%d / %d", hits[LOW][i], totals[LOW][i]), xs[LOW][i], ys[LOW][i] + radiuses[LOW][i] + 40, textPaint);
                        canvas.drawText(String.format("%.1f%%", (float) hits[LOW][i] / (float) totals[LOW][i] * 100.0f), xs[LOW][i], ys[LOW][i] + radiuses[LOW][i] + 80, textPaint);
                    }
                }
                break;
            case BOTH:
            for (int i = 0; i < hits[HIGH].length; i++) {
                canvas.drawArc(xs[HIGH][i] - radiuses[HIGH][i], ys[HIGH][i] - radiuses[HIGH][i], xs[HIGH][i] + radiuses[HIGH][i], ys[HIGH][i] + radiuses[HIGH][i], 0, -180, false, paints[HIGH][i]);

                if (totals[HIGH][i] > 0) {
                    canvas.drawText(String.format("%d / %d", hits[HIGH][i], totals[HIGH][i]), xs[HIGH][i], ys[HIGH][i] - radiuses[HIGH][i] - 50, textPaint);
                    canvas.drawText(String.format("%.1f%%", (float) hits[HIGH][i] / (float) totals[HIGH][i] * 100.0f), xs[HIGH][i], ys[HIGH][i] - radiuses[HIGH][i] - 10, textPaint);
                }
            }
            for (int i = 0; i < hits[LOW].length; i++) {
                canvas.drawArc(xs[LOW][i] - radiuses[LOW][i], ys[LOW][i] - radiuses[LOW][i], xs[LOW][i] + radiuses[LOW][i], ys[LOW][i] + radiuses[LOW][i], 0, 180, false, paints[LOW][i]);
                if (totals[LOW][i] > 0) {
                    canvas.drawText(String.format("%d / %d", hits[LOW][i], totals[LOW][i]), xs[LOW][i], ys[LOW][i] + radiuses[LOW][i] + 40, textPaint);
                    canvas.drawText(String.format("%.1f%%", (float) hits[LOW][i] / (float) totals[LOW][i] * 100.0f), xs[LOW][i], ys[LOW][i] + radiuses[LOW][i] + 80, textPaint);
                }
            }
                break;
        }
    }

    public void setPaints(ScoutMap data) {
        if(!data.containsKey(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS[0]))
            return;
        for(int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS.length; i++)
        {
            hits[HIGH][i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_HIT[i]);
            misses[HIGH][i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS_MISS[i]);
            totals[HIGH][i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_HIGH_POSITIONS[i]);
            radiuses[HIGH][i] = totals[HIGH][i] * 5;
            if(radiuses[HIGH][i] > 60)
            {
                radiuses[HIGH][i] = 60;
            }
            paints[HIGH][i].setARGB(255, (int) (255 * (1.0f - ((float) hits[HIGH][i] / (float) totals[HIGH][i]))), (int)(255 * ((float)hits[HIGH][i] / (float)totals[HIGH][i])), 0);
        }

        for(int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS.length; i++)
        {
            hits[LOW][i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_HIT[i]);
            misses[LOW][i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS_MISS[i]);
            totals[LOW][i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_LOW_POSITIONS[i]);
            radiuses[LOW][i] = totals[LOW][i] * 5;
            if(radiuses[LOW][i] > 60)
            {
                radiuses[LOW][i] = 60;
            }
            paints[LOW][i].setARGB(255, (int) (255 * (1.0f - ((float) hits[LOW][i] / (float) totals[LOW][i]))), (int)(255 * ((float)hits[LOW][i] / (float)totals[LOW][i])), 0);

        }
    }

    public void setMode(int m)
    {
        if(mode != m) {
            mode = m;
            invalidate();
        }
    }
}
