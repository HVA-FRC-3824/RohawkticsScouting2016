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
public class CustomIntakeHeatmap extends View {

    private final String TAG = "CustomIntakeHeatmap";

    private Bitmap fieldBitmap;
    private Paint[] paints;
    private Paint textPaint;
    private Paint labelPaint;
    private Paint canvasPaint;
    private int[] totals;
    private int[] alphas;

    public CustomIntakeHeatmap(Context context, AttributeSet attrs) {
        super(context, attrs);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(80);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        fieldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.intake_field_top_down);

        paints = new Paint[Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS.length];
        for (int i = 0; i < paints.length; i++) {
            paints[i] = new Paint();
            paints[i].setColor(Color.GREEN);
        }

        totals = new int[Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS.length];
        alphas = new int[Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS.length];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldH, oldW);
        fieldBitmap = Bitmap.createScaledBitmap(fieldBitmap, w, h, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(fieldBitmap, 0, 0, canvasPaint);

        for (int i = 0; i < totals.length; i++) {
            drawZone(canvas, i);
        }

        canvas.drawText("Alliance", 225, 750, labelPaint);
        canvas.drawText("Opponent", 1700, 100, labelPaint);
    }

    public void setPaints(ScoutMap data) {
        if (!data.containsKey(Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS[0])) {
            for (int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS.length; i++) {
                paints[i].setAlpha(0);
            }
            return;
        }
        int sum = 0;
        for (int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS.length; i++) {
            totals[i] = data.getInt(Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS[i]);
            sum += totals[i];
        }
        for (int i = 0; i < Constants.Calculated_Totals.TOTAL_TELEOP_INTAKE_POSITIONS.length; i++) {
            paints[i].setAlpha((sum == 0) ? 0 : 200 * totals[i] / sum);
        }
    }

    private void drawZone(Canvas canvas, int zone) {
        Paint paint = paints[zone];
        int total = totals[zone];
        switch (zone) {
            case Constants.Calculated_Totals.INTAKE_POSITION_OPPONENT_COURTYARD:
                canvas.drawRect(1450, 0, 2100, 650, paint);
                if(total > 0) {
                    canvas.drawText(String.format("%d", total), 1650, 400, textPaint);
                }
                break;
            case Constants.Calculated_Totals.INTAKE_POSITION_OPPONENT_SECRET_PASSAGE:
                canvas.drawRect(1150, 675, 2100, 800, paint);
                if(total > 0) {
                    canvas.drawText(String.format("%d", total), 1650, 775, textPaint);
                }
                break;
            case Constants.Calculated_Totals.INTAKE_POSITION_TEAM_SECRET_PASSAGE:
                canvas.drawRect(0, 0, 875, 125, paint);
                if(total > 0) {
                    canvas.drawText(String.format("%d", total), 400, 125, textPaint);
                }
                break;
            case Constants.Calculated_Totals.INTAKE_POSITION_TEAM_COURTYARD:
                canvas.drawRect(0, 150, 575, 800, paint);
                if(total > 0) {
                    canvas.drawText(String.format("%d", total), 400, 400, textPaint);
                }
                break;
            case Constants.Calculated_Totals.INTAKE_POSITION_NEUTRAL_ZONE:
                canvas.drawRect(915, 0, 1275, 650, paint);
                canvas.drawRect(915, 650, 1115, 800, paint);
                canvas.drawRect(750, 150, 915, 800, paint);
                if(total > 0) {
                    canvas.drawText(String.format("%d", total), 1000, 400, textPaint);
                }
                break;
        }
    }
}
