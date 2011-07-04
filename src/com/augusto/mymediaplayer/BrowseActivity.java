package com.augusto.mymediaplayer;

import android.app.ListActivity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.augusto.mymediaplayer.model.Track;
import com.augusto.mymediaplayer.repositories.MusicRepository;

public class BrowseActivity extends ListActivity {
    private Track[] tracks;
    private MediaPlayer mediaPlayer;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        MusicRepository musicRepository = new MusicRepository();
        
        final LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tracks = musicRepository.getAllTracks(this);
        
        
        ListAdapter adapter = new BaseAdapter() {
            
            
            public View getView(int position, View convertView, ViewGroup parent) {
                TwoLineListItem rowLayout = (TwoLineListItem)layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
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
        super.onDestroy();
        stopMedia();
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("BA","clicked pos:" + position + " - " + tracks[position].getTitle());
        super.onListItemClick(l, v, position, id);
        
        Uri trackFile = tracks[position].asUri();
        play(trackFile);
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
