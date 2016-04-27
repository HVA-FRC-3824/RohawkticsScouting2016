package com.team3824.akmessing1.scoutingapp.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.DirectoryAdapter;
import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.utilities.Utilities;
import com.team3824.akmessing1.scoutingapp.utilities.file_manager.FileManager;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Andrew Messing
 */
public class PictureTransfer extends Activity implements View.OnClickListener{

    private final String TAG = "PictureTransfer";

    private FileManager fileManager;
    private DirectoryAdapter directoryAdapter;
    ArrayList<String> filenames;

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

        findViewById(android.R.id.content).setKeepScreenOn(true);

        fileManager = new FileManager();
        fileManager.setHomeDir(getFilesDir().getAbsolutePath());

        filenames = new ArrayList<String>(fileManager.setHomeDir(getFilesDir().getPath()));

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);
        eventID = sharedPreferences.getString(Constants.Settings.EVENT_ID, "");

        for (int i = 0; i < filenames.size(); i++) {
            String filename = filenames.get(i);
            if (!filename.contains(eventID)) {
                filenames.remove(i);
                i--;
            } else {
                String sub_ext = filename.substring(filename.lastIndexOf(".") + 1);
                if (!sub_ext.equalsIgnoreCase("jpg")) {
                    filenames.remove(i);
                    i--;
                }
            }
        }

        int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
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

        directoryAdapter = new DirectoryAdapter(this, filenames, fileManager);
        directoryAdapter.setNfcAdapter(nfcAdapter);
        directoryAdapter.setActivity(this);

        ListView listView = (ListView) findViewById(R.id.directory);
        listView.setAdapter(directoryAdapter);

        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        handleIntent(getIntent());

        Button sendAll = (Button)findViewById(R.id.send_all);
        sendAll.setVisibility(View.VISIBLE);
        sendAll.setOnClickListener(this);

        //nfcAdapter.setBeamPushUris(null, this);

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
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
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

            String dirPath = "";
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                dirPath = handleFileUri(beamUri);
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                dirPath = handleContentUri(beamUri);
            }
            File beamDir = new File(dirPath);
            for (File file : beamDir.listFiles()) {
                if (file.getName().contains("-")) {
                    file.delete();
                    continue;
                }

                if (file.getName().contains(eventID)) {
                    File newFile = new File(getFilesDir(), file.getName());
                    if (newFile.exists() && file.lastModified() > newFile.lastModified()) {
                        newFile.delete();
                        try {
                            newFile.createNewFile();
                            Utilities.copyFile(new FileInputStream(file), new FileOutputStream(newFile));
                        } catch (IOException e) {
                        }
                    } else if (!newFile.exists()) {
                        try {
                            newFile.createNewFile();
                            Utilities.copyFile(new FileInputStream(file), new FileOutputStream(newFile));
                        } catch (IOException e) {
                        }
                    }
                }
            }
            filenames = new ArrayList<String>(fileManager.setHomeDir(getFilesDir().getPath()));
            for (int i = 0; i < filenames.size(); i++) {
                String filename = filenames.get(i);
                if (!filename.contains(eventID)) {
                    filenames.remove(i);
                    i--;
                } else {
                    String sub_ext = filename.substring(filename.lastIndexOf(".") + 1);
                    if (!sub_ext.equalsIgnoreCase("jpg")) {
                        filenames.remove(i);
                        i--;
                    }
                }
            }
            directoryAdapter.updateDirectory(filenames);
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
            String[] projection = {MediaStore.MediaColumns.DATA};
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

    @Override
    public void onClick(View v) {
        if(filenames.size() > 0) {
            nfcAdapter.setBeamPushUris(null, this);
            Uri[] uris = new Uri[filenames.size()];
            File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            for (int i = 0; i < filenames.size(); i++) {
                File file = new File(fileManager.getCurrentDir() + "/" + filenames.get(i));
                File transferFile = new File(fileDirectory, filenames.get(i));
                transferFile.setReadable(true, false);
                try {
                    Utilities.copyFile(new FileInputStream(file), new FileOutputStream(transferFile));
                    uris[i] = Uri.fromFile(transferFile);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
            Toast.makeText(this,"Sending All Pictures via NFC beam",Toast.LENGTH_SHORT).show();
            nfcAdapter.setBeamPushUris(uris, this);
        }
    }
}
