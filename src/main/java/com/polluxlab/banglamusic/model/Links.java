package com.polluxlab.banglamusic.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samiron on 1/17/2015.
 */
public class Links {
    private String self;
    private String songs;
    private String artists;
    private String albums;
    private String tags;

    public String getSelf() {
        return self;
    }

    public String getSongs() {
        return songs;
    }

    public String getArtists() {
        return artists;
    }

    public String getAlbums() {
        return albums;
    }

    public String getTags() {
        return tags;
    }
}
