package com.polluxlab.banglamusic.model;

import android.text.*;
import android.util.*;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.InternalStorageSimple;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    private String currentProduct;

    public String getLogin() {
        return login;
    }

    public String getPreview() {
        return Html.fromHtml(preview).toString();
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
        String key = "PREF_SUBSCRIPTION";
        try {
            key = URLEncoder.encode(this.subscriptions, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response = InternalStorageSimple.fetch(key);
        Subscription s = null;

        if( !response.isEmpty() ){
            Log.d(AppConstant.DEBUG, "Fetched from cache: \n" + response);
            s = gson.fromJson(response, Subscription.class);
        } else {
            Log.d(AppConstant.DEBUG, "Couldn't find from local storage");
        }
        if( s == null ){
            response = get(this.subscriptions);
            Log.d(AppConstant.DEBUG,"Response "+response+" "+s);
            Log.d(AppConstant.DEBUG, "Fetched from webservice " + response);
            if( !response.isEmpty() ) {
                s = gson.fromJson(response, Subscription.class);
                InternalStorageSimple.store(key, response);
            }
        }
        return s;
    }

    public String getPurchase(){
        return this.purchase;
    }

    public String getCurrentProduct() {
        return currentProduct;
    }
}

