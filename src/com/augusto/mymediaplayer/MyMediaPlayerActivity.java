package com.augusto.mymediaplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MyMediaPlayerActivity extends Activity 
implements OnCompletionListener {
    private static String TAG="MyMediaPlayer";
    private MediaPlayer mediaPlayer = null;
    private TextView textView;
    private Handler handler = new Handler();
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        textView = (TextView) findViewById(R.id.list);
        
        getAlbums();
        
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }
    

    private void getAlbums() {
        String[] columns = {MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.NUMBER_OF_SONGS};

        Cursor managedQuery = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, columns, null, null, null);
        if( managedQuery == null) {
            textView.setText("no files found");
            return;
        }
        
        textView.append("\n--------------------------------\n");
        StringBuilder sb = new StringBuilder();
        int idColumn = managedQuery.getColumnIndex(MediaStore.Audio.Albums._ID);
        int artistColumn = managedQuery.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
        int albumColumn = managedQuery.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
        int numberOfSongsColumn = managedQuery.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
        while( managedQuery.moveToNext()) {
            int albumId = managedQuery.getInt(idColumn);
            sb.append("id: ='").append(albumId).append("';");
            sb.append("artist: ='").append(managedQuery.getString(artistColumn)).append("';");
            sb.append("album: ='").append(managedQuery.getString(albumColumn)).append("';");
            sb.append("songs: ='").append(managedQuery.getInt(numberOfSongsColumn)).append("'\n");
            addSongs(sb, albumId);
        }
        
        textView.append(sb.toString());
    }
    
    private void addSongs(StringBuilder sb, int albumId) {
        String[] columns = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.DATA
                };
        
        String where = MediaStore.Audio.Media.ALBUM_ID + " = ?";
        String[] whereArgs = {Integer.toString(albumId)}; 
        String orderBy = MediaStore.Audio.Media.TRACK;

        Cursor managedQuery = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereArgs, orderBy);
        
        int idColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media._ID);
        int titleColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int trackColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.TRACK);
        int durationColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.DURATION);
        
        int displayNameColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int dataColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.DATA);
        

        
        while( managedQuery.moveToNext()) {
            sb.append("id: ='").append(managedQuery.getInt(idColumn)).append("';");
            sb.append("title: ='").append(managedQuery.getString(titleColumn)).append("';");
            sb.append("track: ='").append(managedQuery.getInt(trackColumn)).append("';");
            sb.append("duration: ='").append(managedQuery.getInt(durationColumn)).append("';");
            sb.append("display: ='").append(managedQuery.getString(displayNameColumn)).append("';");
            sb.append("data: ='").append(managedQuery.getString(dataColumn)).append("'\n");
        }
       
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
    
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            Log.i(TAG, "level: " + level + "; scale: " + scale);
            int percent = (level*100)/scale;
            
            final String text = String.valueOf(percent) + "%";
            handler.post( new Runnable() {
                
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
            
        }
    };
}