package com.team3824.akmessing1.scoutingapp.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.views.DrawingView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MatchPlanning extends AppCompatActivity implements View.OnClickListener{

    private String TAG = "MatchPlanning";

    private DrawingView drawView;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, openBtn;
    private float extraSmallBrush, smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_planning);

        drawView = (DrawingView)findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(5); //black
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed, null));

        extraSmallBrush = 5;
        smallBrush = 10;
        mediumBrush = 20;
        largeBrush = 30;

        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);
/*
        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        openBtn = (ImageButton)findViewById(R.id.open_btn);
        openBtn.setOnClickListener(this);
*/
        Button backBtn = (Button)findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        drawView.setBrushSize(extraSmallBrush);
    }

    public void paintClicked(View view)
    {
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if(view!=currPaint){
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed,null));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint,null));
            currPaint=(ImageButton)view;
        }
    }


    @Override
    public void onClick(View view)
    {
        switch(view.getId())
        {
            case R.id.draw_btn:
                drawView.setErase(false);
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.dialog_brush_chooser);
                ImageButton extraSmallBtn = (ImageButton)brushDialog.findViewById(R.id.extra_small_brush);
                extraSmallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(extraSmallBrush);
                        drawView.setLastBrushSize(extraSmallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });

                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
                break;
            case R.id.erase_btn:
                final Dialog eraser_brushDialog = new Dialog(this);
                eraser_brushDialog.setTitle("Eraser size:");
                eraser_brushDialog.setContentView(R.layout.dialog_brush_chooser);
                ImageButton eraser_extraSmallBtn = (ImageButton)eraser_brushDialog.findViewById(R.id.extra_small_brush);
                eraser_extraSmallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(extraSmallBrush);
                        eraser_brushDialog.dismiss();
                    }
                });
                ImageButton eraser_smallBtn = (ImageButton)eraser_brushDialog.findViewById(R.id.small_brush);
                eraser_smallBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        eraser_brushDialog.dismiss();
                    }
                });
                ImageButton eraser_mediumBtn = (ImageButton)eraser_brushDialog.findViewById(R.id.medium_brush);
                eraser_mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        eraser_brushDialog.dismiss();
                    }
                });
                ImageButton eraser_largeBtn = (ImageButton)eraser_brushDialog.findViewById(R.id.large_brush);
                eraser_largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        eraser_brushDialog.dismiss();
                    }
                });
                eraser_brushDialog.show();
                break;
            case R.id.new_btn:
                AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new strategy (you will lose the current strategy)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                newDialog.show();
                break;
/*
            case R.id.save_btn:
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save strategy");
                saveDialog.setMessage("Save strategy to device Gallery?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        drawView.setDrawingCacheEnabled(true);
                        Bitmap saveImage = drawView.getDrawingCache();
                        String imageName = "strategy_"+UUID.randomUUID().toString()+".png";
                        FileOutputStream fos = null;
                        try {
                            fos = openFileOutput(imageName, Context.MODE_WORLD_WRITEABLE);
                            saveImage.compress(Bitmap.CompressFormat.PNG,100,fos);
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        if(fos != null)
                        {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                Log.d(TAG,e.getMessage());
                            }
                        }
                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;
            case R.id.open_btn:
                // TODO: allow image that is saved to be opened
                break;
*/
            case R.id.back_btn:
                this.finish();
                break;
        }

    }

}
