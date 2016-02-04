package com.team3824.akmessing1.scoutingapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.team3824.akmessing1.scoutingapp.R;

public class DrawingView extends View {

    private String TAG = "DrawingView";

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int lastDrawColor;
    private int paintColor = 0xFF000000;
    private Canvas drawCanvas;
    private Bitmap backgroundBitmap, canvasBitmap;
    private Bitmap fieldBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase=false;
    private int screenWidth, screenHeight;

    public DrawingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        drawPath = new Path();
        drawPaint = new Paint();

        brushSize = getResources().getInteger(R.integer.extra_small_size);
        lastBrushSize = brushSize;

        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        fieldBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.field_top_down);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH)
    {
        super.onSizeChanged(w, h, oldH, oldW);
        screenWidth = w;
        screenHeight = h;
        backgroundBitmap = Bitmap.createScaledBitmap(fieldBitmap, w, h, false);
        canvasBitmap = Bitmap.createScaledBitmap(fieldBitmap, w, h, false);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(backgroundBitmap, 0, 0, canvasPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setColor(String newColor)
    {
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public void setBrushSize(float newSize)
    {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize = pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize = lastSize;
    }
    public float getLastBrushSize() {
        return lastBrushSize;
    }

    public void setErase(boolean isErase)
    {
        erase = isErase;
        if(erase)
        {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else
        {
            drawPaint.setXfermode(null);
        }
    }

    public void startNew(){
        canvasBitmap = Bitmap.createScaledBitmap(fieldBitmap, screenWidth, screenHeight, false);
        drawCanvas = new Canvas(canvasBitmap);
        invalidate();
    }
}
