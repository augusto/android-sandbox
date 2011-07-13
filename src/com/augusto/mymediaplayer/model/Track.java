package com.augusto.mymediaplayer.model;

import java.io.File;
import android.net.Uri;

import com.augusto.mymediaplayer.common.Formatter;

public class Track {

    private final long id;
    private String artist;
    private String title;
    private int trackNumber;
    private int duration;
    private String path;
    private String display;

    public Track(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getDurationAsMinsSecs() {
        return Formatter.formatTimeFromMillis(duration);
    }

    public Uri asUri() {
        return Uri.fromFile(new File(path));
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }
    
    @Override
    public String toString() {
        return path;
    }
}
