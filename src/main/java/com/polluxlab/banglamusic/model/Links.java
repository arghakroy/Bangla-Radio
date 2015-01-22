package com.polluxlab.banglamusic.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by samiron on 1/17/2015.
 */
public class Links extends ModelBase {
    private String self;
    private String songs;
    private String artists;
    private String albums;
    private String tags;
    private String preview;
    private String media;
    private String purchase;
    private String subscriptions;

    public String getPreview() {
        return preview;
    }

    public String getMedia() {
        return media;
    }

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

    public Subscription getSubscription(){
        String response = get(this.subscriptions);
        return gson.fromJson(response, Subscription.class);
    }

    public String getPurchase(){
        return this.purchase;
    }
}
