package com.augusto.mymediaplayer;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.augusto.mymediaplayer.model.Album;
import com.augusto.mymediaplayer.repositories.MusicRepository;

public class BrowseAlbums extends ListActivity {

    private MusicRepository musicRepository;
    private Album albums[] = new Album[0];
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicRepository = new MusicRepository();
        
        albums = musicRepository.getAllAlbums(this);
        layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListAdapter adapter = new AlbumListAdapter(albums,layoutInflater);
        setListAdapter(adapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Toast.makeText(this, "selected: " + albums[position].getTitle(), Toast.LENGTH_SHORT).show();
    }
    
}
