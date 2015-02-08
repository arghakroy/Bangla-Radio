package com.polluxlab.banglamusic.model;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.polluxlab.banglamusic.util.AppConstant;
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
            Log.d(AppConstant.DEBUG, "Fetched from cache: \n" + response);
            s = gson.fromJson(response, Subscription.class);
        } else {
            Log.d(AppConstant.DEBUG, "Couldn't find from local storage");
        }
        if( s == null || (s != null && !s.valid()) ){
            response = get(url);
            Log.d(AppConstant.DEBUG,"Response "+response+" "+s);
            Log.d(AppConstant.DEBUG, "Fetched from webservice " + response);
            if( !response.isEmpty() ) {
                s = gson.fromJson(response, Subscription.class);
                InternalStorageSimple.store(key, response);
            }
        } else {
            if(s == null) {
                Log.d(AppConstant.DEBUG, "Subscription null !!" + response);
            } else {
                Date d = s.getEndDate();
                if(d == null){
                    Log.d(AppConstant.DEBUG, "Subscription end date is null");
                } else {
                    Log.d(AppConstant.DEBUG, "subscription end date is: " + s.getEndDate().toString());
                }
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

