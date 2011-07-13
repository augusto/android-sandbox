package com.augusto.mymediaplayer.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.augusto.mymediaplayer.R;
import com.augusto.mymediaplayer.model.Track;
import com.augusto.mymediaplayer.repositories.MusicRepository;

public class AudioPlayer extends Service implements OnCompletionListener {
	public static final String INTENT_BASE_NAME = "com.augusto.mymediaplayer.AudioPlayer";
    public static final String UPDATE_PLAYLIST = INTENT_BASE_NAME + ".PLAYLIST_UPDATED";
	public static final String QUEUE_ALBUM = INTENT_BASE_NAME + ".QUEUE_ALBUM";
	public static final String PLAY_ALBUM = INTENT_BASE_NAME + ".PLAY_ALBUM";

    private final String TAG = "AudioPlayer";

    private List<Track> tracks = new ArrayList<Track>(); // this collection should be encapsulated in another class.
    private MusicRepository musicRepository = new MusicRepository();
    private MediaPlayer mediaPlayer;
    private boolean paused = false;

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
        Log.i(TAG, "AudioPlayer: onStart() called, instance=" + this.hashCode());
        
        String action = intent.getAction();
        if( PLAY_ALBUM.equals(action)) {
        	long albumId = intent.getLongExtra("id", -1);
        	Log.d(TAG, "Received intent to play album: " + albumId);
        	playAlbum(albumId);
        } else if( QUEUE_ALBUM.equals(action)) {
        	long albumId = intent.getLongExtra("id", -1);
        	Log.d(TAG, "Received intent to queue album: " + albumId);
        	queueAlbum(albumId);
        } else {
        	Log.d(TAG, "Action not recognized: " + action);
        }
    }

    private void playAlbum(long albumId) {
    	Track[] tracks = musicRepository.findTracksFilteredBy(this, albumId);
    	this.tracks.clear(); // I DON'T LIKE THIS!!!
    	stop();
    	for( Track track : tracks) {
    		this.tracks.add(track);
    	}
    	play();
	}

	private void queueAlbum(long albumId) {
    	Track[] tracks = musicRepository.findTracksFilteredBy(this, albumId);
    	for( Track track : tracks) {
    		addTrack(track);
    	}
	}

	@Override
    public void onDestroy() {
        Log.i(TAG, "AudioPlayer: onDestroy() called");
     
        release();
    }
    
    @Override
    public void onLowMemory() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String message = getString(R.string.closing_low_memory);
        Notification notification = new Notification(android.R.drawable.ic_dialog_alert, message, System.currentTimeMillis());
        notificationManager.notify(1, notification);
        stopSelf();
    }

    public void onCompletion(MediaPlayer _mediaPlayer) {
        release();
        nextTrack();
    }
    
    private void release() {
        if( mediaPlayer == null) {
            return;
        }
        
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
    
    public void play(Track track) {
        stop();
        tracks.clear();
        tracks.add(track);
        play();
    }
    
    public void play() {
        playListUpdated();
        if( tracks.size() == 0) {
            return;
        }
        
        Track track = tracks.get(0);

        if( mediaPlayer != null && paused) {
            mediaPlayer.start();
            paused = false;
            return;
        } else if( mediaPlayer != null ) {
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

    private void playListUpdated() {
        Intent updatePlaylistIntent = new Intent(UPDATE_PLAYLIST);
        this.sendBroadcast(updatePlaylistIntent);
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

    public boolean isPlaying() {
        if(tracks.isEmpty() || mediaPlayer == null) {
            return false;
        }
        return mediaPlayer.isPlaying();
    }

    public void pause() {
        if( mediaPlayer != null) {
            mediaPlayer.pause();
            paused = true;
        }
    }

    public Track getCurrentTrack() {
        if (tracks.isEmpty()) {
            return null;
        }
        
        return tracks.get(0);
    }

    public int elapsed() {
        if (mediaPlayer == null) {
            return 0;
        }
        return mediaPlayer.getCurrentPosition();
    }

    public void seek(int timeInMillis) {
        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(timeInMillis);
        }
    }
}
