package com.augusto.mymediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.augusto.mymediaplayer.model.Track;

public class TracksListAdapter extends BaseAdapter {
    
    private final Track[] tracks;
    private final LayoutInflater layoutInflater;

    public TracksListAdapter(Track[] tracks, LayoutInflater layoutInflater) {
        this.tracks = tracks;
        this.layoutInflater = layoutInflater;
    }

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
}
