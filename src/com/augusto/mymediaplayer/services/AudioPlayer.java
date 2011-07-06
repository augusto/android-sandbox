package com.augusto.mymediaplayer.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.augusto.mymediaplayer.model.Track;

public class AudioPlayer extends Service implements OnCompletionListener {
    private final String TAG = "AudioPlayer";

    private List<Track> tracks = new ArrayList<Track>();
    private MediaPlayer mediaPlayer;

    public class AudioPlayerBinder extends Binder {
        AudioPlayer getService() {
            return AudioPlayer.this;
        }
    }

    private final IBinder audioPlayerBinder = new AudioPlayerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return audioPlayerBinder;
    }

    
    public void addTrack(Track track) {
        tracks.add(track);
    }
    
    public void jumpToTrackPosition(int position) {
        if( position == 0) {
            return;
        }
        
        for( int i=0 ; i<position ; i++) {
            tracks.remove(0);
        }
        
        play();
    }
    
    private void play() {
        if( tracks.size() == 0) {
            return;
        }
        
        Track track = tracks.get(0);

        if( mediaPlayer.isPlaying() ) {
            mediaPlayer.stop();
        }
        
        try {
            mediaPlayer.setDataSource(this, track.asUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException ioe) {
            Log.e(TAG,"error trying to play " + track , ioe);
        }
    }


    @Override
    public void onCreate() {
        Log.v("PLAYERSERVICE", "onCreate");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "Service onStart called");
    }
    
    public void onDestroy() {
        Log.i(TAG, "Service onDestroy called");
        
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        Log.v("SIMPLESERVICE", "onDestroy");
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        nextTrack();
    }


    private void nextTrack() {
        tracks.remove(0);
        play();
        
    }
}
