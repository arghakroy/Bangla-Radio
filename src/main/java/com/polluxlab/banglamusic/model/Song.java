package com.polluxlab.banglamusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ARGHA K ROY on 1/17/2015.
 */
public class Song extends ModelBase  {

    /**
     *
     */
    public String title;
    public String album;
    public int track;
    public int time;
    public int year;
    public long bitrate;
    public String mode;

    /**
     *
     */
    public MusicLinks links;

    static class MusicLinks extends Links {
        public Preview preview;
        public String media;
    }

}
