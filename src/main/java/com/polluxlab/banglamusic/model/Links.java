package com.polluxlab.banglamusic.model;

import android.content.Context;
import android.net.Uri;

import com.google.gson.annotations.SerializedName;
import com.polluxlab.banglamusic.util.Util;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.List;

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
    private String login;

    public String getLogin() {
        return login;
    }

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

    public Subscription getSubscription(Context con){
        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader(HTTP_HEADER.HTTP_X_SECRET.name(), Util.getSecretKey(con)));
        String response = get(this.subscriptions, headers);

        return gson.fromJson(response, Subscription.class);
    }

    public String getPurchase(){
        return this.purchase;
    }
}

