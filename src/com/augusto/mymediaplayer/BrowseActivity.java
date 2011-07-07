package com.augusto.mymediaplayer;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.augusto.mymediaplayer.model.Track;
import com.augusto.mymediaplayer.repositories.MusicRepository;
import com.augusto.mymediaplayer.services.AudioPlayer;

public class BrowseActivity extends ListActivity {
    private static final String TAG = "BrowseActivity";
    private Track[] tracks;
    private LayoutInflater layoutInflater;
        
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.d(TAG, "this: " + this);
        Log.d(TAG, "base: " + this.getBaseContext());
                
        MusicRepository musicRepository = new MusicRepository();
        
        tracks = musicRepository.getAllTracks(this);
        
        ListAdapter adapter = new TracksListAdapter(tracks,layoutInflater);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG,"clicked pos:" + position + " - " + tracks[position].getTitle());
        addTrack(tracks[position]);
    }

    private void addTrack(Track track) {
        MyMediaPlayer.getAudioPlayer().addTrack(track);
        
        Toast toast = new Toast(this);
        LinearLayout trackAddedToastView = (LinearLayout)layoutInflater.inflate(R.layout.track_added_toast, null);
        TextView artistView = (TextView)trackAddedToastView.findViewById(R.id.artist);
        TextView titleView = (TextView)trackAddedToastView.findViewById(R.id.title);
        TextView durationView = (TextView)trackAddedToastView.findViewById(R.id.duration);
        
        Log.i(TAG,"title: " + track.getTitle());
        
        artistView.setText(track.getArtist());
        titleView.setText(track.getTitle());
        durationView.setText(track.getDurationAsMinsSecs());
        
        toast.setView(trackAddedToastView);
        toast.setDuration(Toast.LENGTH_SHORT);
        //The toast should open as far as possible from the place where the user "clicked"
        // Another option is to render it with vertical text, from bottom to top.
        //toast.setGravity(Gravity.TOP |Gravity.CENTER_HORIZONTAL, 0, -10);
        toast.show();
    }
}