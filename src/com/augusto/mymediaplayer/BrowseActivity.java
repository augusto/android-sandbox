package com.augusto.mymediaplayer;

import android.app.ListActivity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.augusto.mymediaplayer.model.Track;
import com.augusto.mymediaplayer.repositories.MusicRepository;

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
        
        list = (ListView)findViewById(android.R.id.list);
        registerForContextMenu(list);
    }
    
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG,"clicked pos:" + position + " - " + tracks[position].getTitle());

        addTrack(tracks[position]);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        Log.d(TAG, "onCreateContextMenu{ view id=" + v.getId() + ";play_queue=" + android.R.id.list );
        
        View view = this.findViewById(v.getId());
        Log.d(TAG, "view: " + view.getClass().getCanonicalName() );
        
        if (v == list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Track track = tracks[info.position];
            menu.setHeaderTitle(track.getTitle());
            menu.add(Menu.NONE, R.string.play, 0, R.string.play);
            menu.add(Menu.NONE, R.string.queue, 1, R.string.queue);
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemId = item.getItemId();
        
        Log.d(TAG, "onContextItemSelected{ position=" + info.position + ";menu_id=" + itemId );
        
        Track track = tracks[info.position];
        
        switch(itemId) {
        case R.string.play:
            playTrack(track);
            break;
        case R.string.queue:
            addTrack(track);
            break;
        default:
            Log.e(TAG, "Unknown menu item pressed");
        }
       
        return true;
    }
    
    private void playTrack(Track track) {
        MyMediaPlayer.getAudioPlayer().play(track);
        
        notify(track);
    }

    private void addTrack(Track track) {
        MyMediaPlayer.getAudioPlayer().addTrack(track);
        
        notify(track);
    }

    
    Toast toast=null;
    private ListView list;
    private void notify(Track track) {
        if( toast != null) {
            toast.cancel();
        }
        toast = new Toast(this);
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