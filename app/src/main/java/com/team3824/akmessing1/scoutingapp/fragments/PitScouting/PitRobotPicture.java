package com.team3824.akmessing1.scoutingapp.fragments.PitScouting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.fragments.ScoutFragment;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Fragment for pit scouting that handles taking pictures of a team's robot
 *
 * @author Andrew Messing
 * @version 1
 */
public class PitRobotPicture extends ScoutFragment implements View.OnClickListener {
    private Context context;
    private final String TAG = "PitRobotPicture";
    private String mCurrentPhotoPath = "";
    private ImageView mImageView;
    private Button mButton;

    public PitRobotPicture() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pit_robot_picture, container, false);

        context = getContext();
        mImageView = (ImageView) view.findViewById(R.id.robotPicture);
        mButton = (Button) view.findViewById(R.id.take_picture);

        // restore all values from the database
        if (valueMap != null) {
            // Set up the image if one already exists
            if (valueMap.containsKey(Constants.Pit_Inputs.PIT_ROBOT_PICTURE)) {
                mCurrentPhotoPath = valueMap.getString(Constants.Pit_Inputs.PIT_ROBOT_PICTURE);
                valueMap.remove(Constants.Pit_Inputs.PIT_ROBOT_PICTURE);
                if (!mCurrentPhotoPath.equals("")) {
                    if (setPic()) {
                        mButton.setText("Remove Picture");
                    }
                }
            }
            //restoreContentsFromMap(valueMap, (ViewGroup) view);
        }

        mButton.setOnClickListener(this);

        Utilities.setupUI(getActivity(), view);

        return view;
    }

    /**
     * Sets the Image view to display the image
     *
     * @return
     */
    private boolean setPic() {
        // Get the dimensions of the View
        int targetW = 400;
        int targetH = 600;

        String fullPath = getContext().getFilesDir().getAbsolutePath() + "/" + mCurrentPhotoPath;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fullPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(fullPath, bmOptions);
        try {
            FileOutputStream fos = context.openFileOutput(mCurrentPhotoPath, Context.MODE_WORLD_WRITEABLE);
            if (fos != null && bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                mImageView.setImageBitmap(bitmap);
                return true;
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return false;
    }

    /**
     * Handles when the camera app returns with the image
     *
     * @param requestCode The code for what type of activity was requested
     * @param resultCode  Whether or not the result was ok
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Request: " + requestCode + " Result: " + resultCode);
        if (requestCode == 1/*REQUEST_IMAGE_CAPTURE*/ && resultCode == Activity.RESULT_OK) {
            setPic();
            mButton.setText("Remove Picture");
        }
    }

    /**
     * Special write that records the path of the picture
     *
     * @param map
     * @return
     */
    @Override
    public String writeContentsToMap(ScoutMap map) {
        if(!mCurrentPhotoPath.equals("")) {
            map.put(Constants.Pit_Inputs.PIT_ROBOT_PICTURE, mCurrentPhotoPath);
        }
        return super.writeContentsToMap(map);
    }

    @Override
    public void onClick(View v) {
        String text = String.valueOf(mButton.getText());
        if (text.equals("Take Picture")) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                // Create the File where the photo should go
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "robotPicture_" + timeStamp + ".jpg";
                mCurrentPhotoPath = imageFileName;
                //Remove if exists, the file MUST be created using the lines below
                File f = new File(context.getFilesDir(), imageFileName);
                f.delete();
                //Create new file
                FileOutputStream fos = null;
                try {
                    fos = context.openFileOutput(imageFileName, Context.MODE_WORLD_WRITEABLE);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Get reference to the file
                f = new File(context.getFilesDir(), imageFileName);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(f));
                startActivityForResult(takePictureIntent, 1/*REQUEST_IMAGE_CAPTURE*/);
            }
        }
        // Removes the image from the file system
        else if (text.equals("Remove Picture")) {
            File file = new File(getContext().getFilesDir(), mCurrentPhotoPath);
            boolean deleted = file.delete();
            Log.d(TAG, "deleted: " + deleted);
            mButton.setText("Take Picture");
            mImageView.setImageDrawable(null);
        }
    }
}
