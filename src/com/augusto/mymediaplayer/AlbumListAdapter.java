package com.augusto.mymediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.augusto.mymediaplayer.model.Album;

public class AlbumListAdapter extends BaseAdapter {
    
    private final Album[] albums;
    private final LayoutInflater layoutInflater;

    public AlbumListAdapter(Album[] albums, LayoutInflater layoutInflater) {
        this.albums = albums;
        this.layoutInflater = layoutInflater;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TwoLineListItem rowLayout;
        
        if( convertView == null) {
            rowLayout = (TwoLineListItem)layoutInflater.inflate(R.layout.track_row, parent, false);
        } else {
            rowLayout = (TwoLineListItem)convertView;
        }
        
        TextView lineOne = rowLayout.getText1();
        TextView lineTwo = rowLayout.getText2();
        
        Album album = albums[position];
        lineOne.setText(album.getTitle());
        String secondLine = String.format("%s - %d %s", 
                                album.getArtist(),
                                album.getTrackCount(),
                                (album.getTrackCount() > 1)? "tracks" : "track");
        lineTwo.setText(secondLine);
        
        return rowLayout;
    }

    public long getItemId(int position) {
        return albums[position].getId();
    }

    public Object getItem(int position) {
        return albums[position];
    }

    public int getCount() {
        return albums.length;
    }
}
