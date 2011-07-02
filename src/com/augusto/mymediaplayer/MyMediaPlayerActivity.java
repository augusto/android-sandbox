package com.augusto.mymediaplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MyMediaPlayerActivity extends Activity 
implements OnCompletionListener {
    private static String TAG="MyMediaPlayer";
    private MediaPlayer mediaPlayer = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView textView = (TextView) findViewById(R.id.list);
        
        StringBuilder sb = new StringBuilder();
        List<String> files = getFiles();
        for( String mp3 : files) {
            sb.append(mp3).append("\n");
        }
    
        
        textView.setText(sb.toString());
        Uri uri = Uri.parse("file://" + files.get(0));
        mediaPlayer = MediaPlayer.create(this, uri );
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
    }
    

    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "Reached end of media");
        
    }
    
    @Override
    protected void onPause() {

        if( mediaPlayer != null) {
            mediaPlayer.release();
        }

        super.onPause();
    }
    
    
    
    public List<String> getFiles() {
        File sdcard = new File("/sdcard");
        List<String> files = getFiles(sdcard);        
        
        return files;
    }

    private List<String> getFiles(File directory) {
        List<String> files = new ArrayList<String>();
        File[] listFiles = directory.listFiles();
        if( listFiles == null ){
            return Collections.EMPTY_LIST;
        }
        for( File item : listFiles) {
            Log.i(TAG, "found "+ item.getAbsolutePath());
            
            if(item.isFile() && item.getName().toLowerCase().endsWith(".mp3")) {
                files.add(item.getAbsolutePath());
            }else if( item.isDirectory()) {
                files.addAll(getFiles(item));
            }
        }

        return files;
    }
}