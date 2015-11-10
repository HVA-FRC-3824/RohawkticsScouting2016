package com.team3824.akmessing1.scoutingapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class PitBasicInfo extends ScoutFragment{
    private String TAG = "PitBasicInfo";
    private String mCurrentPhotoPath = "";
    private ImageView mImageView;
    private Button mButton;
    View.OnClickListener buttonClick;

    public PitBasicInfo() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pit_basic_info, container, false);


        mImageView = (ImageView)view.findViewById(R.id.robotPicture);
        mButton = (Button)view.findViewById(R.id.take_picture);

        // restore all values from the database
        if(valueMap != null) {
            // Set up the image if one already exists
            if(valueMap.containsKey("robotPicture")) {
                mCurrentPhotoPath = valueMap.get("robotPicture").getString();
                valueMap.remove("robotPicture");
                if (!mCurrentPhotoPath.equals("")) {
                    setPic();
                    mButton.setText("Remove Picture");
                }
            }
            restoreContentsFromMap(valueMap, (ViewGroup) view);
        }

        // button either will take a picture if one does not exist or delete it if it does
        buttonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = String.valueOf(mButton.getText());
                if (text.equals("Take Picture")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

                        // Create the File where the photo should go
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "robotPicture_" + timeStamp+".jpg";
                        File photoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),imageFileName);
                        try {
                            photoFile.getParentFile().mkdirs();
                            photoFile.createNewFile();
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                mCurrentPhotoPath = imageFileName;
                                Log.d(TAG,mCurrentPhotoPath);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photoFile));
                                startActivityForResult(takePictureIntent, 1/*REQUEST_IMAGE_CAPTURE*/);
                            }
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            ex.printStackTrace();
                        }
                    }
                }
                // Removes the image from the file system
                else if(text.equals("Remove Picture")){
                    File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), mCurrentPhotoPath);
                    boolean deleted = file.delete();
                    Log.d(TAG,"deleted: " + deleted);
                    mButton.setText("Take Picture");
                    mImageView.setImageDrawable(null);
                }
            }
        };

        int permission = verifyStoragePermissions(getActivity());
        if(permission == PackageManager.PERMISSION_GRANTED)
        {
            mButton.setOnClickListener(buttonClick);
        }

        return view;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 400;//mImageView.getWidth();
        int targetH = 600;//mImageView.getHeight();

        String fullPath = getActivity().getFilesDir().getAbsolutePath() +"/"+ mCurrentPhotoPath;

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
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"Request: "+requestCode+" Result: "+resultCode);
        if (requestCode == 1/*REQUEST_IMAGE_CAPTURE*/ && resultCode == Activity.RESULT_OK) {
            setPic();
            mButton.setText("Remove Picture");
        }
    }

    @Override
    public void writeContentsToMap(Map<String, ScoutValue> map)
    {
        map.put("robotPicture",new ScoutValue(mCurrentPhotoPath));
        super.writeContentsToMap(map);
    }

    public int verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2 /*REQUEST_EXTERNAL_STORAGE*/
            );
        }
        return permission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == 2/*REQUEST_EXTERNAL_STORAGE*/)
        {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(),"Permission Granted",Toast.LENGTH_SHORT);
                mButton.setOnClickListener(buttonClick);

            } else {

                Log.d(TAG,"Permission denied");
            }
            return;
        }
    }
}
