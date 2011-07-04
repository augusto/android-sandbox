package com.augusto.mymediaplayer.model;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.net.Uri;

public class Track {

    private final int id;
    private String title;
    private int trackNumber;
    private int duration;
    private String path;
    private String display;

    public Track(int id) {
        this.id = id;
    }
    
    public int getId() {
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

    NumberFormat numberFormat = new DecimalFormat("00");
    public String getDurationAsMinsSecs() {
        int minutes = duration/60000;
        int seconds = (duration%60000)/1000;
        
        return numberFormat.format(minutes) + ":" + numberFormat.format(seconds);
    }

    public Uri asUri() {
        return Uri.fromFile(new File(path));
    }
}
