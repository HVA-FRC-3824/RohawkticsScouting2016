package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.views.DrawingView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *  Activity where the user can draw on a canvas where the background is the field. Includes features
 *  for multiple colors, multiple size brushes, erasers, and save/recovery of a drawing.
 *
 *  @author Andrew Messing
 *  @version
 */
public class MatchPlanning extends Activity implements View.OnClickListener {

    private String TAG = "MatchPlanning";

    private DrawingView drawView;
    private ImageButton currPaint;
    private float extraSmallBrush, smallBrush, mediumBrush, largeBrush;

    /**
     * Sets up the draw, erase, new, save, and load buttons
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_planning);

        drawView = (DrawingView) findViewById(R.id.drawing);

        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);
        currPaint = (ImageButton) paintLayout.getChildAt(3); //black
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed, null));

        extraSmallBrush = 5;
        smallBrush = 10;
        mediumBrush = 20;
        largeBrush = 30;

        ImageButton drawBtn = (ImageButton) findViewById(R.id.draw_btn);
        drawBtn.setOnClickListener(this);

        ImageButton eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(this);

        ImageButton newBtn = (ImageButton) findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        ImageButton saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        ImageButton openBtn = (ImageButton) findViewById(R.id.open_btn);
        openBtn.setOnClickListener(this);

        Button backBtn = (Button) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        drawView.setBrushSize(extraSmallBrush);
    }

    /**
     *  Changes the color for the Draw View and sets the color button to pressed
     *
     * @param view
     */
    public void paintClicked(View view) {
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if (view != currPaint) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed, null));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint, null));
            currPaint = (ImageButton) view;
        }
    }

    /**
     *  Implements the actions for the various buttons
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.draw_btn:
                drawView.setErase(false);
                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.dialog_brush_chooser);

                // extra small button sets the size of the brush to extra small
                ImageButton extraSmallBtn = (ImageButton) brushDialog.findViewById(R.id.extra_small_brush);
                extraSmallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(extraSmallBrush);
                        drawView.setLastBrushSize(extraSmallBrush);
                        brushDialog.dismiss();
                    }
                });

                // small button sets the size of the brush to small
                ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });

                // medium button sets the size of the brush to medium
                ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });

                // large button sets the size of the brush to large
                ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
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

                // extra small button sets the size of the eraser brush to extra small
                ImageButton eraser_extraSmallBtn = (ImageButton) eraser_brushDialog.findViewById(R.id.extra_small_brush);
                eraser_extraSmallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(extraSmallBrush);
                        eraser_brushDialog.dismiss();
                    }
                });

                // small button sets the size of the eraser brush to small
                ImageButton eraser_smallBtn = (ImageButton) eraser_brushDialog.findViewById(R.id.small_brush);
                eraser_smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        eraser_brushDialog.dismiss();
                    }
                });

                // medium button sets the size of the eraser brush to medium
                ImageButton eraser_mediumBtn = (ImageButton) eraser_brushDialog.findViewById(R.id.medium_brush);
                eraser_mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        eraser_brushDialog.dismiss();
                    }
                });

                // large button sets the size of the eraser brush to large
                ImageButton eraser_largeBtn = (ImageButton) eraser_brushDialog.findViewById(R.id.large_brush);
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

                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });

                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                newDialog.show();
                break;

            // Saves drawn image to file
            case R.id.save_btn:
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
                saveDialog.setTitle("Save strategy");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawView.setDrawingCacheEnabled(true);
                        Bitmap saveImage = drawView.getDrawingCache();
                        String imageName = "strategy.png";
                        FileOutputStream fos = null;
                        try {
                            fos = openFileOutput(imageName, Context.MODE_WORLD_WRITEABLE);
                            saveImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, e.getMessage());
                        }
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                Log.d(TAG, e.getMessage());
                            }
                        }
                        drawView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                saveDialog.show();
                break;

            // Recovers the last saved drawn image
            case R.id.open_btn:
                AlertDialog.Builder openDialog = new AlertDialog.Builder(this);
                openDialog.setTitle("Load last strategy");
                openDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String imageName = "strategy.png";
                        try {
                            Bitmap loadImage = BitmapFactory.decodeStream(openFileInput(imageName));
                            drawView.load(loadImage.copy(Bitmap.Config.ARGB_8888, true));
                        } catch (FileNotFoundException ex) {
                            Log.d(TAG, ex.getMessage());
                        }
                    }
                });
                openDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                openDialog.show();
                break;

            case R.id.back_btn:
                this.finish();
                break;

            default:
                assert false;
                break;
        }

    }

}
