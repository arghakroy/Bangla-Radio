package com.polluxlab.banglamusic.model;

import android.content.*;
import com.google.gson.reflect.TypeToken;
import com.polluxlab.banglamusic.util.Util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samiron on 1/17/2015.
 */
public class Endpoint extends ModelBase {

    private Links links;

    public static Endpoint instance(){
        if(self == null)
            init();
        return self;
    }

    @Deprecated
    public List<Song> getSongs(){
        String response = get(this.links.getSongs());
        if(response.isEmpty())
            return new ArrayList<Song>();
        else {
            Type songCollectionType = new TypeToken<List<Song>>(){}.getType();
            return gson.fromJson(response, songCollectionType);
        }
    }

    public List<Tag> getTags(){
        String response = get(this.links.getTags());
        if(response.isEmpty())
            return new ArrayList<Tag>();
        else {
            Type songCollectionType = new TypeToken<List<Tag>>(){}.getType();
            return gson.fromJson(response, songCollectionType);
        }
    }

    public String getBaseUrl(){
        return this.links.getSelf();
    }

    public String getAuthUrl(){
        return "https://128.199.142.142/musicapp/server/web/app_dev.php/webservice/auth/login";
    }

    public Subscription getSubscription(String secret){
        return this.links.getSubscription(secret);
    }

    public String getPurchase(Context con){
       // String currentProductURL=links.getCurrentProduct();
        //String productData=get(currentProductURL);
        String purchaseUrl = this.links.getPurchase();
        String secret = Util.getSecretKey(con);
        if( purchaseUrl != null ){
            return String.format("%s?%s=%s", purchaseUrl, "sharedSecret", secret);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public static boolean init(){
        try {
            if (self == null){
                String response = get( ENDPOINT_URL );
                self = gson.fromJson( response, Endpoint.class );
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private transient static Endpoint self = null;
    public transient static final String ENDPOINT_URL = "http://128.199.142.142/musicapp/server/web/app_dev.php/webservice/";
    private Endpoint(){
        super();
    }
}

