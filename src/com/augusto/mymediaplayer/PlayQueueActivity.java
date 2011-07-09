package com.augusto.mymediaplayer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.augusto.mymediaplayer.model.Track;
import com.augusto.mymediaplayer.services.AudioPlayer;

public class PlayQueueActivity extends Activity implements OnClickListener {
    private static String TAG="PlayQueueActivity";
    static final int UPDATE_INTERVAL = 250;
    private LayoutInflater layoutInflater;
    private Timer waitForAudioPlayertimer = new Timer();
    private Handler handler = new Handler();
    private ListView queue;
    private TextView message;
    private TextView elapsed;
    private Button stop;
    private Button close;
    private Button playPause;
    private View nonEmptyQueueView;
    private UpdateCurrentTrackTask updateCurrentTrackTask;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.play_queue);
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        message = (TextView)findViewById(R.id.message);
        elapsed = (TextView)findViewById(R.id.elapsed);
        queue = (ListView)findViewById(R.id.play_queue);
        nonEmptyQueueView = (View)findViewById(R.id.playqueue_not_empty);
        stop = (Button)findViewById(R.id.stop);
        close = (Button)findViewById(R.id.close);
        playPause = (Button)findViewById(R.id.playPause);
        
        stop.setOnClickListener(this);
        close.setOnClickListener(this);
        playPause.setOnClickListener(this);

    }

    private void refreshScreen() {
        if( audioPlayer() == null) {
            updateScreenAsync();
        } else {
            updatePlayQueue();
        }
    }

    @Override
    protected void onPause() {
        updateCurrentTrackTask.stop();
        updateCurrentTrackTask = null;
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        refreshScreen();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play_queue_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        case R.id.exit:
            Log.d(TAG, "Exiting application");
            Intent audioPlayerIntent = new Intent(getApplicationContext(), AudioPlayer.class);
            stopService(audioPlayerIntent);
            finish();
            break;
        case R.id.clear_all:
            Toast.makeText(this, "to implement", Toast.LENGTH_LONG).show();
            break;
        default:
            Log.e(TAG, "Menu item not recognized. FeatureId=" + item.getItemId());
        }
        
        return true;
    }
    
    private void updateScreenAsync() {
        waitForAudioPlayertimer.scheduleAtFixedRate( new TimerTask() {
            
            public void run() {
                Log.d(TAG,"updateScreenAsync running timmer");
                if( audioPlayer() != null) {
                    waitForAudioPlayertimer.cancel();
                    handler.post( new Runnable() {
                        public void run() {
                            updatePlayQueue();
                        }
                    });
                }
            }
            }, 10, UPDATE_INTERVAL);
    }

    public void updatePlayQueue() {
        Track[] queuedTracks = audioPlayer().getQueuedTracks();
        Log.d(TAG,"Queuedtracks (number): " + queuedTracks.length);
        
        if( queuedTracks.length == 0) {
            message.setText("No tracks selected");
            message.setVisibility(View.VISIBLE);
            nonEmptyQueueView.setVisibility(View.INVISIBLE);
        } else {
            message.setVisibility(View.GONE);
            message.setText("Tracks found: " + queuedTracks.length);
            queue.setAdapter(new TracksListAdapter(queuedTracks, layoutInflater));
            nonEmptyQueueView.setVisibility(View.VISIBLE);
        }
        
        updatePlayPauseButtonState();
        
        if( updateCurrentTrackTask == null) {
            updateCurrentTrackTask = new UpdateCurrentTrackTask();
            updateCurrentTrackTask.execute();
        } else {
            Log.e(TAG, "updateCurrentTrackTask is not null" );
        }
    }
    
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.playPause:
            onClickPlayPause();
            break;
        case R.id.stop:
            audioPlayer().stop();
            updatePlayPauseButtonState();
            break;
        case R.id.close:
            finish();
            break;
        }
    }

    private void onClickPlayPause() {
        if( audioPlayer().isPlaying() ) {
            audioPlayer().pause();
        } else {
            audioPlayer().play();
        }

        updatePlayPauseButtonState();
    }

    private void updatePlayPauseButtonState() {
        if( audioPlayer().isPlaying() ) {
            playPause.setText(R.string.pause);
        } else {
            playPause.setText(R.string.play);
        }
    }

    private AudioPlayer audioPlayer() {
        return MyMediaPlayer.getAudioPlayer();
    }
    
    private class UpdateCurrentTrackTask extends AsyncTask<Void, Track, Void> {

        public boolean stopped = false;
        
        @Override
        protected Void doInBackground(Void... params) {
            while( ! stopped ) {
                Track currentTrack = audioPlayer().getCurrentTrack();
                if( currentTrack != null ) {
                    publishProgress(currentTrack);
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) { }
            }
            
            Log.d(TAG,"AsyncTask stopped");
            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Track... track) {
            String message = track[0].getTitle() + " - " + getDurationAsMinsSecs(audioPlayer().elapsed()); 
            PlayQueueActivity.this.elapsed.setText(message);
        }

        public void stop() {
            stopped = true;
        }
        
        NumberFormat numberFormat = new DecimalFormat("00");
        public String getDurationAsMinsSecs(int duration) {
            int minutes = duration/60000;
            int seconds = (duration%60000)/1000;
            
            return numberFormat.format(minutes) + ":" + numberFormat.format(seconds);
        }
    }
}
