package com.augusto.mymediaplayer.repositories;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.augusto.mymediaplayer.model.Album;
import com.augusto.mymediaplayer.model.Track;

public class MusicRepository {
    private static final Track[] NO_TRACKS_FOUND = new Track[0];
    private static final Album[] NO_ALBUMS_FOUND = new Album[0];
    
    private final static String[] TRACK_COLUMNS = {
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.DATA };
    
    private final static String[] ALBUM_COLUMNS = { 
        MediaStore.Audio.Albums._ID, 
        MediaStore.Audio.Albums.ARTIST,
        MediaStore.Audio.Albums.ALBUM, 
        MediaStore.Audio.Albums.NUMBER_OF_SONGS};


    public Track findTracksId(Context context, long trackId) {
        Track[] tracks = findTracks(context, trackId, null);
        
        if( tracks.length == 0) {
            return null;
        }
        
        return tracks[0];
    }
    
    public Track[] findTracksByAlbumId(Context context, Long albumId) {
        return findTracks(context, null, albumId);
    }
    
	private Track[] findTracks(Context context, Long trackId, Long albumId) {
		
		String where = null;
		String[] selectionArgs = null;
		String orderBy = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
		if( albumId != null) {
			where = MediaStore.Audio.Media.ALBUM_ID + " = ?";
			selectionArgs = new String[]{ albumId.toString() };
			orderBy = MediaStore.Audio.Media.TRACK;
		} else if ( trackId != null ){
            where = MediaStore.Audio.Media._ID + " = ?";
            selectionArgs = new String[]{ trackId.toString() };
            orderBy = MediaStore.Audio.Media.TRACK;
		}

    	ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, TRACK_COLUMNS, where, selectionArgs, orderBy);
        
        if( cursor == null) {
            return NO_TRACKS_FOUND;
        }
        
        int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
        int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int trackColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
        int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int displayNameColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
        int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

        Track[] tracks = new Track[cursor.getCount()];
        int index = 0;
        while( cursor.moveToNext()) {
            Track track = new Track(cursor.getLong(idColumn));
            track.setTitle(cursor.getString(titleColumn));
            track.setArtist(cursor.getString(artistColumn));
            track.setDuration(cursor.getInt(durationColumn));
            track.setTrackNumber(cursor.getInt(trackColumn));
            track.setPath(cursor.getString(dataColumn));
            track.setDisplay(cursor.getString(displayNameColumn));
            
            tracks[index++] = track;
        }
        
        cursor.close();
        return tracks;
	}

	public Album findAlbumById(Activity activity, Long albumId) {

		Album[] albums = findAlbumsFilteredBy(activity, albumId);
	
		if( albums.length > 0) {
			return null;
		}
		
		return albums[0];
	}

	private Album[] findAlbumsFilteredBy(Context context, Long albumId) {
		String where = null;
		String[] selectionArgs = null;
		if( albumId != null ) {
			where = MediaStore.Audio.Albums._ID + " = ?";
			selectionArgs = new String[]{ albumId.toString()};
		}
		
		ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, ALBUM_COLUMNS, where, selectionArgs, MediaStore.Audio.Albums.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return NO_ALBUMS_FOUND;
        }

        int idColumn = cursor.getColumnIndex(MediaStore.Audio.Albums._ID);
        int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
        int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
        int trackCountColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);
        
        Album[] albums = new Album[cursor.getCount()];
        int index = 0;
        while (cursor.moveToNext()) {
            Album album = new Album( cursor.getLong(idColumn));
            album.setArtist( cursor.getString(artistColumn));
            album.setTitle( cursor.getString(albumColumn));
            album.setTrackCount( cursor.getInt(trackCountColumn));
            
            albums[index++] = album;
        }
        
        cursor.close();
        return albums;
	}
	

    public Album[] findAllAlbums(Activity activity) {
    	return findAlbumsFilteredBy(activity, null);
    }
}
