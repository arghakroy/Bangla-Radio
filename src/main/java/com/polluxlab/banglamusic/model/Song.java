package com.polluxlab.banglamusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ARGHA K ROY on 1/17/2015.
 */
public class Song extends ModelBase  {

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

    /**
     *
     */
    private MusicLinks links;

    static class MusicLinks extends Links {
        public Preview preview;
        public String media;
    }

}
