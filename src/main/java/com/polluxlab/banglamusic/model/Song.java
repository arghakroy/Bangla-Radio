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
    private String track;
    private String time;
    private String year;
    private String bitrate;
    private String mode;

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getTrack() {
        return track;
    }

    public String getTime() {
        return time;
    }

    public String getYear() {
        return year;
    }

    public String getBitrate() {
        return bitrate;
    }

    public String getMode() {
        return mode;
    }

    public String getStreamLink(){
        return get(this.links.getMedia());
    }

    public void setTitile(String title){
        this.title=title;
    }
}
