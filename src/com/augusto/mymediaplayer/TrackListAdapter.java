package com.augusto.mymediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.augusto.mymediaplayer.model.Track;

public class TrackListAdapter extends BaseAdapter {
    
    private final Track[] tracks;
    private final LayoutInflater layoutInflater;

    public TrackListAdapter(Track[] tracks, LayoutInflater layoutInflater) {
        this.tracks = tracks;
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
}
