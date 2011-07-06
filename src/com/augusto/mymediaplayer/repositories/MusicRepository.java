package com.augusto.mymediaplayer.repositories;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import com.augusto.mymediaplayer.model.Track;

public class MusicRepository {

    private static final Track[] NO_TRACKS_FOUND = new Track[0];
    private final static String[] TRACK_COLUMNS = {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATA
    };

    
    public Track[] getAllTracks(Activity activity) {
        
        Cursor managedQuery = activity.managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TRACK_COLUMNS, null, null, null);
        
        if( managedQuery == null) {
            return NO_TRACKS_FOUND;
        }
        
        int idColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media._ID);
        int artistColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int trackColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.TRACK);
        int durationColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int displayNameColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int dataColumn = managedQuery.getColumnIndex(MediaStore.Audio.Media.DATA);

        Track[] tracks = new Track[managedQuery.getCount()];
        int index = 0;
        while( managedQuery.moveToNext()) {
            Track track = new Track(managedQuery.getInt(idColumn));
            track.setTitle(managedQuery.getString(titleColumn));
            track.setArtist(managedQuery.getString(artistColumn));
            track.setDuration(managedQuery.getInt(durationColumn));
            track.setTrackNumber(managedQuery.getInt(trackColumn));
            track.setPath(managedQuery.getString(dataColumn));
            track.setDisplay(managedQuery.getString(displayNameColumn));
            
            tracks[index++] = track;
        }
        
        return tracks;

    }
    
}
