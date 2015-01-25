package com.polluxlab.banglamusic.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.polluxlab.banglamusic.util.GlobalContext;
import com.polluxlab.banglamusic.util.InternalStorage;
import com.polluxlab.banglamusic.util.InternalStorageSimple;
import com.polluxlab.banglamusic.util.StorageDataProvider;
import com.polluxlab.banglamusic.util.Util;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
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

    public Subscription getSubscription(final String secret){
        String url = this.subscriptions;
        String key = "PREF_SUBSCRIPTION";
        try {
            key = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = InternalStorageSimple.fetch(key);
        Subscription s = null;

        if( !response.isEmpty() ){
            Log.d(getClass().getName(), "Fetched from cache: \n" + response);
            s = gson.fromJson(response, Subscription.class);
        } else {
            Log.d(getClass().getName(), "Couldn't find from local storage");
        }
        if( s == null || (s != null && !s.valid()) ){
            response = get(url);
            Log.d(getClass().getName(), "Fetched from webservice " + response);
            if( !response.isEmpty() ) {
                s = gson.fromJson(response, Subscription.class);
                InternalStorageSimple.store(key, response);
            }
        } else {
            if(s == null) {
                Log.d(getClass().getName(), "Subscription null !!" + response);
            } else {
                Date d = s.getEndDate();
                if(d == null){
                    Log.d(getClass().getName(), "Subscription end date is null");
                } else {
                    Log.d(getClass().getName(), "subscription end date is: " + s.getEndDate().toString());
                }
            }
        }
        return s;
    }

    public String getPurchase(){
        return this.purchase;
    }
}

