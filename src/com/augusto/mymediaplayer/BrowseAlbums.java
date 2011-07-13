package com.augusto.mymediaplayer;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.augusto.mymediaplayer.model.Album;
import com.augusto.mymediaplayer.repositories.MusicRepository;
import com.augusto.mymediaplayer.services.AudioPlayer;

public class BrowseAlbums extends ListActivity {
	private static final String TAG = "BrowseAlbums"; 

    private MusicRepository musicRepository;
    private Album albums[] = new Album[0];
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicRepository = new MusicRepository();
        
        albums = musicRepository.findAllAlbums(this);
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListAdapter adapter = new AlbumListAdapter(albums,layoutInflater);
        setListAdapter(adapter);
        View list = findViewById(android.R.id.list);
        registerForContextMenu(list);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Toast.makeText(this, "selected: " + albums[position].getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, BrowseTracks.class);
        intent.putExtra("album_id", id);
        startActivity(intent);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        Log.d(TAG, "onCreateContextMenu{ view id=" + v.getId() + ";play_queue=" + android.R.id.list );
        
        View view = this.findViewById(v.getId());
        Log.d(TAG, "view: " + view.getClass().getCanonicalName() );
        
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Album album = albums[info.position];
            menu.setHeaderTitle(album.getTitle());
            menu.add(Menu.NONE, R.string.play, 0, R.string.play);
            menu.add(Menu.NONE, R.string.queue, 1, R.string.queue);
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int itemId = item.getItemId();
        
        Log.d(TAG, "onContextItemSelected{ position=" + info.position + ";menu_id=" + itemId );
        
        Album album = albums[info.position];
        
        switch(itemId) {
        case R.string.play:
            playAlbum(album);
            break;
        case R.string.queue:
            queueAlbum(album);
            break;
        default:
            Log.e(TAG, "Unknown menu item pressed");
        }
       
        return true;
    }

	private void queueAlbum(Album album) {
		Intent intent = new Intent(AudioPlayer.QUEUE_ALBUM);
		intent.putExtra("id", album.getId());
		//this.startService(intent);
		this.sendBroadcast(intent);
	}

	private void playAlbum(Album album) {
		Intent intent = new Intent(AudioPlayer.PLAY_ALBUM);
		intent.putExtra("id", album.getId());
		//this.startService(intent);
		this.sendBroadcast(intent);
	}
}
