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

    /**
     *
     */
    public MusicLinks links;

    static class MusicLinks extends Links {
        public Preview preview;
    }

}
