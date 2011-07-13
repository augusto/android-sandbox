package com.augusto.mymediaplayer.model;

public class Album {

    private final int id;
    private String artist;
    private String title;
    private int trackCount;
    

    public Album(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setTrackCount(int trackCount) {
        this.trackCount = trackCount;
    }

    public int getTrackCount() {
        return trackCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

