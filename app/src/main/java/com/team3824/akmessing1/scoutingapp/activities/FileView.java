package com.team3824.akmessing1.scoutingapp.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.adapters.DirectoryAdapter;
import com.team3824.akmessing1.scoutingapp.utilities.file_manager.FileManager;
import com.team3824.akmessing1.scoutingapp.views.CustomHeader;

import java.io.File;
import java.util.ArrayList;

/**
 *  @author Andrew Messing
 *  @version 1
 *
 *  Activity to display the files in the application internal files directory. Used mainly for
 *  debugging. Can also use to remove files.
 */
public final class FileView extends Activity implements AdapterView.OnItemSelectedListener{

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "FileView";

    private FileManager fileManager;
    private DirectoryAdapter directoryAdapter;

    /**
     * Sets up the file manager and the list view
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        CustomHeader customHeader = (CustomHeader)findViewById(R.id.header);
        customHeader.removeHome();

        fileManager = new FileManager();
        fileManager.setHomeDir(getFilesDir().getAbsolutePath());

        directoryAdapter = new DirectoryAdapter(this, new ArrayList<String>(fileManager.setHomeDir
                (getFilesDir().getPath())),fileManager);

        ListView listView = (ListView)findViewById(R.id.directory);
        listView.setAdapter(directoryAdapter);
        listView.setOnItemSelectedListener(this);

    }

    /**
     *  Tries to open the file that is selected or moves to the directory if a folder is selected
     *
     * @param parent The parent adapter view of the view that is selected. In this case is the list
     *               view containing all the files.
     * @param view The view that is selected
     * @param position The position of the selected row item in the menu
     * @param id The id of the selected row item
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = directoryAdapter.get(position);
        File file = new File(fileManager.getCurrentDir() + "/" + item);
        String item_ext = null;

        Log.d(TAG, "Item selected");

        try {
            item_ext = item.substring(item.lastIndexOf("."), item.length());

        } catch(IndexOutOfBoundsException e) {
            item_ext = "";
        }

        // Directory selected
        if (file.isDirectory()) {
            if(file.canRead()) {
                directoryAdapter.stopThumbnailThread();
                directoryAdapter.updateDirectory(fileManager.getNextDir(item, false));

            } else {
                Toast.makeText(this, "Can't read folder due to permissions", Toast.LENGTH_SHORT).show();
            }
        }

        // music file selected--add more audio formats
        else if (item_ext.equalsIgnoreCase(".mp3") ||
                item_ext.equalsIgnoreCase(".m4a")||
                item_ext.equalsIgnoreCase(".mp4")) {

            Intent i = new Intent();
            i.setAction(android.content.Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(i);

        }

	    // photo file selected
        else if(item_ext.equalsIgnoreCase(".jpeg") ||
                item_ext.equalsIgnoreCase(".jpg")  ||
                item_ext.equalsIgnoreCase(".png")  ||
                item_ext.equalsIgnoreCase(".gif")  ||
                item_ext.equalsIgnoreCase(".tiff")) {

            if (file.exists()) {

                Intent picIntent = new Intent();
                picIntent.setAction(android.content.Intent.ACTION_VIEW);
                picIntent.setDataAndType(Uri.fromFile(file), "image/*");
                startActivity(picIntent);

            }
        }

        // video file selected
        else if(item_ext.equalsIgnoreCase(".m4v") ||
                item_ext.equalsIgnoreCase(".3gp") ||
                item_ext.equalsIgnoreCase(".wmv") ||
                item_ext.equalsIgnoreCase(".mp4") ||
                item_ext.equalsIgnoreCase(".ogg") ||
                item_ext.equalsIgnoreCase(".wav")) {

            if (file.exists()) {

                Intent movieIntent = new Intent();
                movieIntent.setAction(android.content.Intent.ACTION_VIEW);
                movieIntent.setDataAndType(Uri.fromFile(file), "video/*");
                startActivity(movieIntent);

            }
        }
	    // pdf file selected
        else if(item_ext.equalsIgnoreCase(".pdf")) {

            if(file.exists()) {

                Intent pdfIntent = new Intent();
                pdfIntent.setAction(android.content.Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(Uri.fromFile(file),
                        "application/pdf");

                try {
                    startActivity(pdfIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Sorry, couldn't find a pdf viewer",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

        // HTML file
        else if(item_ext.equalsIgnoreCase(".html")) {

            if(file.exists()) {

                Intent htmlIntent = new Intent();
                htmlIntent.setAction(android.content.Intent.ACTION_VIEW);
                htmlIntent.setDataAndType(Uri.fromFile(file), "text/html");

                try {
                    startActivity(htmlIntent);
                } catch(ActivityNotFoundException e) {
                    Toast.makeText(this, "Sorry, couldn't find a HTML viewer",
                            Toast.LENGTH_SHORT).show();
                }

            }
        }

        // text file
        else if(item_ext.equalsIgnoreCase(".txt")) {

            if(file.exists()) {

                Intent txtIntent = new Intent();
                txtIntent.setAction(android.content.Intent.ACTION_VIEW);
                txtIntent.setDataAndType(Uri.fromFile(file), "text/plain");

                try {
                    startActivity(txtIntent);
                } catch(ActivityNotFoundException e) {
                    txtIntent.setType("text/*");
                    startActivity(txtIntent);
                }

            }
        }

        // generic intent
        else {
            if(file.exists()) {
                Intent generic = new Intent();
                generic.setAction(android.content.Intent.ACTION_VIEW);
                generic.setDataAndType(Uri.fromFile(file), "text/plain");

                try {
                    startActivity(generic);
                } catch(ActivityNotFoundException e) {
                    Toast.makeText(this, "Sorry, couldn't find anything " +
                                    "to open " + file.getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
