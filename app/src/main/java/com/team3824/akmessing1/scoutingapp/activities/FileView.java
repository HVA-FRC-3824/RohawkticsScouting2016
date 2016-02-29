package com.team3824.akmessing1.scoutingapp.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public final class FileView extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    FileManager fileManager;
    DirectoryAdapter directoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);

        CustomHeader customHeader = (CustomHeader)findViewById(R.id.header);
        customHeader.removeHome();

        fileManager = new FileManager();
        fileManager.setHomeDir(getFilesDir().getAbsolutePath());

        directoryAdapter = new DirectoryAdapter(this,R.layout.list_item_file,new ArrayList<String>(fileManager.setHomeDir
                (getFilesDir().getPath())),fileManager);

        ListView listView = (ListView)findViewById(R.id.directory);
        listView.setAdapter(directoryAdapter);
        listView.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = directoryAdapter.get(position);
        File file = new File(fileManager.getCurrentDir() + "/" + item);
        String item_ext = null;

        try {
            item_ext = item.substring(item.lastIndexOf("."), item.length());

        } catch(IndexOutOfBoundsException e) {
            item_ext = "";
        }

        if (file.isDirectory()) {
            if(file.canRead()) {
                directoryAdapter.stopThumbnailThread();
                directoryAdapter.updateDirectory(fileManager.getNextDir(item, false));

            } else {
                Toast.makeText(this, "Can't read folder due to permissions", Toast.LENGTH_SHORT).show();
            }
        }

	    	/*music file selected--add more audio formats*/
        else if (item_ext.equalsIgnoreCase(".mp3") ||
                item_ext.equalsIgnoreCase(".m4a")||
                item_ext.equalsIgnoreCase(".mp4")) {

            Intent i = new Intent();
            i.setAction(android.content.Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(file), "audio/*");
            startActivity(i);

        }

	    	/*photo file selected*/
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

	    	/*video file selected--add more video formats*/
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
	    	/*pdf file selected*/
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
	    	/* HTML file */
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

	    	/* text file*/
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

	    	/* generic intent */
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
