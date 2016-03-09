package com.team3824.akmessing1.scoutingapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.DirectoryAdapter;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.utilities.file_manager.FileManager;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Andrew Messing
 */
public class PictureTransfer extends Activity {

    private final String TAG = "PictureTransfer";

    private FileManager fileManager;
    private DirectoryAdapter directoryAdapter;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    NfcAdapter nfcAdapter;
    PendingIntent mPendingIntent;

    String eventID;

    /**
     * Sets up the file manager and the list view
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        CustomHeader customHeader = (CustomHeader) findViewById(R.id.header);
        customHeader.removeHome();

        fileManager = new FileManager();
        fileManager.setHomeDir(getFilesDir().getAbsolutePath());

        ArrayList<String> directory = new ArrayList<String>(fileManager.setHomeDir(getFilesDir().getPath()));

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        for (int i = 0; i < directory.size(); i++) {
            String filename = directory.get(i);
            if (!filename.contains(eventID)) {
                directory.remove(i);
                i--;
            } else {
                String sub_ext = filename.substring(filename.lastIndexOf(".") + 1);
                if (!sub_ext.equalsIgnoreCase("jpg")) {
                    directory.remove(i);
                    i--;
                }
            }
        }

        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (!nfcAdapter.isEnabled()) {
            Log.d(TAG, "NFC is not enabled");
            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
        } else if (!nfcAdapter.isNdefPushEnabled()) {
            Log.d(TAG, "NFC Push is not enabled");
            startActivity(new Intent(android.provider.Settings.ACTION_NFCSHARING_SETTINGS));
        }

        directoryAdapter = new DirectoryAdapter(this, directory, fileManager);
        directoryAdapter.setNfcAdapter(nfcAdapter);
        directoryAdapter.setActivity(this);

        ListView listView = (ListView) findViewById(R.id.directory);
        listView.setAdapter(directoryAdapter);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        handleIntent(getIntent());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // Permission Denied
                    Toast.makeText(this, "WRITE EXTERNAL STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (NfcAdapter.getDefaultAdapter(this) != null)
            nfcAdapter.disableForegroundDispatch(this);
    }


    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = intent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */

            String dirPath="";
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                dirPath = handleFileUri(beamUri);
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                dirPath = handleContentUri(beamUri);
            }
            File beamDir = new File(dirPath);
            for(File file: beamDir.listFiles())
            {
                if(file.getName().contains("-"))
                {
                    file.delete();
                    continue;
                }

                if(file.getName().contains(eventID))
                {
                    File newFile = new File(getFilesDir(),file.getName());
                    if(newFile.exists() && file.lastModified() > newFile.lastModified())
                    {
                        newFile.delete();
                        try {
                            newFile.createNewFile();
                            Utilities.copyFile(new FileInputStream(file),new FileOutputStream(newFile));
                        } catch (IOException e) {
                        }
                    }
                    else if(!newFile.exists())
                    {
                        try {
                            newFile.createNewFile();
                            Utilities.copyFile(new FileInputStream(file),new FileOutputStream(newFile));
                        } catch (IOException e) {
                        }
                    }
                }
            }
            ArrayList<String> directory = new ArrayList<String>(fileManager.setHomeDir(getFilesDir().getPath()));
            for (int i = 0; i < directory.size(); i++) {
                String filename = directory.get(i);
                if (!filename.contains(eventID)) {
                    directory.remove(i);
                    i--;
                } else {
                    String sub_ext = filename.substring(filename.lastIndexOf(".") + 1);
                    if (!sub_ext.equalsIgnoreCase("jpg")) {
                        directory.remove(i);
                        i--;
                    }
                }
            }
            directoryAdapter.updateDirectory(directory);
        }
    }

    public String handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getParent();
    }

    public String handleContentUri(Uri beamUri) {
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                // Return the parent directory of the file
                return copiedFile.getParent();
            } else {
                // The query didn't work; return null
                return null;
            }
        }
        return null;
    }
}
