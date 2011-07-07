package com.augusto.mymediaplayer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.augusto.mymediaplayer.model.Track;

public class PlayQueueActivity extends Activity implements OnClickListener {
    private static String TAG="PlayQueueActivity";
    static final int UPDATE_INTERVAL = 300;
    private LayoutInflater layoutInflater;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private ListView queue;
    private TextView message;
    private Button stop;
    private Button close;
    private View nonEmptyQueueView;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.play_queue);
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        message = (TextView)findViewById(R.id.message);
        queue = (ListView)findViewById(R.id.play_queue);
        nonEmptyQueueView = (View)findViewById(R.id.playqueue_not_empty);
        stop = (Button)findViewById(R.id.stop);
        close = (Button)findViewById(R.id.close);
        
        stop.setOnClickListener(this);
        close.setOnClickListener(this);

        if( MyMediaPlayer.getAudioPlayer() == null) {
            updateScreenAsync();
        } else {
            updatePlayQueue();
        }
            
    }
    
    private void updateScreenAsync() {
        timer.scheduleAtFixedRate( new TimerTask() {
            
            public void run() {
                Log.d(TAG,"updateScreenAsync running timmer");
                if( MyMediaPlayer.getAudioPlayer() != null) {
                    timer.cancel();
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
        Track[] queuedTracks = MyMediaPlayer.getAudioPlayer().getQueuedTracks();
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
        
        Log.d(TAG,"please do refresh!!!");
    }
    
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.stop:
            MyMediaPlayer.getAudioPlayer().stop();
            break;
        case R.id.close:
            finish();
            break;
        }
    }
}
