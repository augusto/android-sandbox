package com.augusto.mymediaplayer;

import android.app.ListActivity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

public class BrowseActivity extends ListActivity {
    private static final String TAG = "BrowseActivity";
    private Track[] tracks;
    private MediaPlayer mediaPlayer;
    private LayoutInflater layoutInflater;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        MusicRepository musicRepository = new MusicRepository();
        
        tracks = musicRepository.getAllTracks(this);
        
        ListAdapter adapter = new BaseAdapter() {
            
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem rowLayout = (TwoLineListItem)layoutInflater.inflate(R.layout.track_row, parent, false);
                TextView lineOne = (TextView)rowLayout.getChildAt(0);
                TextView lineTwo = (TextView)rowLayout.getChildAt(1);
                
                lineOne.setText(tracks[position].getTitle());
                lineTwo.setText(tracks[position].getDurationAsMinsSecs());
                
                return rowLayout;
            }
            
            public long getItemId(int position) {
                return tracks[position].getId();
            }
            
            public Object getItem(int position) {
                return tracks[position];
            }
            
            public int getCount() {
                return tracks.length;
            }
        };

        setListAdapter(adapter);
    }
    
    @Override
    protected void onDestroy() {
        stopMedia();
        super.onDestroy();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG,"clicked pos:" + position + " - " + tracks[position].getTitle());
        addTrack(tracks[position]);
    }

    private void addTrack(Track track) {
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
        //toast.setGravity(Gravity.TOP |Gravity.CENTER_HORIZONTAL, 0, -10);
        toast.show();
    }

    private void play(Uri trackFile) {
        stopMedia();
        mediaPlayer = MediaPlayer.create(this, trackFile);
        mediaPlayer.start();
    }

    private void stopMedia() {
        if( mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
