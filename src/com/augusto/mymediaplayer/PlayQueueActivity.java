package com.augusto.mymediaplayer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.augusto.mymediaplayer.common.Formatter;
import com.augusto.mymediaplayer.common.ThreadUtil;
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
    private SeekBar timeLine;
    private View nonEmptyQueueView;
    private UpdateCurrentTrackTask updateCurrentTrackTask;
    
    private BroadcastReceiver audioPlayerBroadcastReceiver = new AudioPlayerBroadCastReceiver();
    private ServiceConnection serviceConnection = new AudioPlayerServiceConnection();
    private AudioPlayer audioPlayer;
    private Intent audioPlayerIntent;
    
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
        timeLine = (SeekBar) findViewById(R.id.time_line);
        timeLine.setOnSeekBarChangeListener(new TimeLineChangeListener());
        
        stop.setOnClickListener(this);
        close.setOnClickListener(this);
        playPause.setOnClickListener(this);
        
        //bind to service
        audioPlayerIntent = new Intent(this, AudioPlayer.class);
        bindService(audioPlayerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	audioPlayerBroadcastReceiver = new AudioPlayerBroadCastReceiver();
        IntentFilter filter = new IntentFilter(AudioPlayer.UPDATE_PLAYLIST);
        registerReceiver(audioPlayerBroadcastReceiver, filter );
        
        refreshScreen();
    }
    
    @Override
    protected void onPause() {
        unregisterReceiver(audioPlayerBroadcastReceiver);
        audioPlayerBroadcastReceiver = null;
        
        updateCurrentTrackTask.stop();
        updateCurrentTrackTask = null;
        super.onPause();
    }
    
    
    @Override
    protected void onDestroy() {
    	unbindService(serviceConnection);
    	super.onDestroy();
    }
    
    private void refreshScreen() {
        if( audioPlayer == null) {
            updateScreenAsync();
        } else {
            updatePlayQueue();
        }
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
                if( audioPlayer != null) {
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
        Track[] queuedTracks = audioPlayer.getQueuedTracks();
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
            audioPlayer.stop();
            updatePlayPauseButtonState();
            break;
        case R.id.close:
            finish();
            break;
        }
    }

    private void onClickPlayPause() {
        if( audioPlayer.isPlaying() ) {
            audioPlayer.pause();
        } else {
            audioPlayer.play();
        }

        updatePlayPauseButtonState();
    }

    
    
    private void updatePlayPauseButtonState() {
        if( audioPlayer.isPlaying() ) {
            playPause.setText(R.string.pause);
        } else {
            playPause.setText(R.string.play);
        }
    }


    private void updatePlayPanel(final Track track) {
        runOnUiThread(new Runnable() {
            
            public void run() {
                int elapsedMillis = audioPlayer.elapsed();
                String message = track.getTitle() + " - " + Formatter.formatTimeFromMillis(elapsedMillis);
                timeLine.setMax(track.getDuration());
                timeLine.setProgress(elapsedMillis);
                PlayQueueActivity.this.elapsed.setText(message);
            }
        });
    }
    
    private class UpdateCurrentTrackTask extends AsyncTask<Void, Track, Void> {

        public boolean stopped = false;
        public boolean paused = false;
        
        @Override
        protected Void doInBackground(Void... params) {
            while( ! stopped ) {
                if( ! paused) {
                    Track currentTrack = audioPlayer.getCurrentTrack();
                    if( currentTrack != null ) {
                        publishProgress(currentTrack);
                    }
                }
                ThreadUtil.sleep(250);
            }
            
            Log.d(TAG,"AsyncTask stopped");
            
            return null;
        }
        
        @Override
        protected void onProgressUpdate(Track... track) {
            if( stopped || paused ) {
                return; //to avoid glitches
            }
            
            updatePlayPanel(track[0]);
        }

        public void stop() {
            stopped = true;
        }
        
        public void pause() {
            this.paused = true;
        }

        public void unPause() {
            this.paused = false;
        }
    }
    
    private class AudioPlayerBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"AudioPlayerBroadCastReceiver.onReceive action=" + intent.getAction());
            if( AudioPlayer.UPDATE_PLAYLIST.equals( intent.getAction())) {
                updatePlayQueue();
            }
        }
    }
    
    private class TimeLineChangeListener implements SeekBar.OnSeekBarChangeListener {
        private Timer delayedSeekTimer;
        
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if( fromUser ) {
                Log.d(TAG,"TimeLineChangeListener progress received from user: "+progress);
                
                scheduleSeek(progress);
                
                return;
            }
        }

        
        private void scheduleSeek(final int  progress) {
            if( delayedSeekTimer != null) {
                delayedSeekTimer.cancel();
            }
            delayedSeekTimer = new Timer();
            delayedSeekTimer.schedule(new TimerTask() {
                
                @Override
                public void run() {
                    Log.d(TAG,"Delayed Seek Timer run");
                    audioPlayer.seek(progress);
                    updatePlayPanel(audioPlayer.getCurrentTrack());
                }
            }, 170);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            Log.d(TAG,"TimeLineChangeListener started tracking touch");
            updateCurrentTrackTask.pause();
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.d(TAG,"TimeLineChangeListener stopped tracking touch");
            updateCurrentTrackTask.unPause();
        }
        
    }
    
    private final class AudioPlayerServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder baBinder) {
            Log.d(TAG,"AudioPlayerServiceConnection: Service connected");
            audioPlayer = ((AudioPlayer.AudioPlayerBinder) baBinder).getService();
            startService(audioPlayerIntent);
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG,"AudioPlayerServiceConnection: Service disconnected");
            audioPlayer = null;
        }
    }
}

