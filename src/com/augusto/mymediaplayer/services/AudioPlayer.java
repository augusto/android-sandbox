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
import android.widget.Toast;

import com.augusto.mymediaplayer.model.Track;

public class AudioPlayer extends Service implements OnCompletionListener {
    private final String TAG = "AudioPlayer";

    private List<Track> tracks = new ArrayList<Track>();
    private MediaPlayer mediaPlayer;

    public class AudioPlayerBinder extends Binder {
        public AudioPlayer getService() {
            Log.v(TAG, "AudioPlayerBinder: getService() called");
            return AudioPlayer.this;
        }
    }

    private final IBinder audioPlayerBinder = new AudioPlayerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "AudioPlayer: onBind() called");
        return audioPlayerBinder;
    }
    
    @Override
    public void onCreate() {
        Log.v(TAG, "AudioPlayer: onCreate() called");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "AudioPlayer: onStart() called");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "AudioPlayer: onDestroy() called");
     
        release();
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        release();
        nextTrack();
    }
    
    private void release() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public void addTrack(Track track) {
        Log.d(TAG, "addTrack " + track);
        tracks.add(track);
        if( tracks.size() == 1) {
            play();
        }
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


    public void play(Track track) {
        stop();
        tracks.clear();
        tracks.add(track);
        play();
    }
    
    private void play() {
        if( tracks.size() == 0) {
            return;
        }
        
        Track track = tracks.get(0);

        if( mediaPlayer != null ) {
            release();
        }
        
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, track.asUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        } catch (IOException ioe) {
            Log.e(TAG,"error trying to play " + track , ioe);
            String message = "error trying to play track: " + track + ".\nError: " + ioe.getMessage();
            Toast.makeText(this, message, Toast.LENGTH_LONG);
        }
    }

    private void nextTrack() {
        tracks.remove(0);
        play();
    }

    public Track[] getQueuedTracks() {
        Track[] tracksArray = new Track[tracks.size()];
        return tracks.toArray(tracksArray);
    }

    public void stop() {
        release();
    }
}
