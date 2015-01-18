package com.polluxlab.banglamusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ARGHA K ROY on 1/17/2015.
 */
public class Song extends ModelBase  {

    /**
     *
     */
    private SongLinks links;
    /**
     *
     */
    private String title;
    private String album;
    private int track;
    private int time;
    private int year;
    private long bitrate;
    private String mode;

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public int getTrack() {
        return track;
    }

    public int getTime() {
        return time;
    }

    public int getYear() {
        return year;
    }

    public long getBitrate() {
        return bitrate;
    }

    public String getMode() {
        return mode;
    }

    public String getStreamLink(){
        return get(this.links.getMedia());
    }

}
