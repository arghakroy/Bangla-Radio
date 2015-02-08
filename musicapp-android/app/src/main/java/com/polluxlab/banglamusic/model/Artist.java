package com.polluxlab.banglamusic.model;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by samiron on 1/18/2015.
 */
public class Artist extends ModelBase  {
    private Links links;
    private String name;
    private String summary;
    private String yearformed;
    private String placeformed;

    /**
     *
     */
    public List<Song> getSongs(){
        String response = get(this.links.getSongs());
        if(response.isEmpty())
            return new ArrayList<Song>();
        else {
            Type songCollectionType = new TypeToken<List<Song>>(){}.getType();
            return gson.fromJson(response, songCollectionType);
        }
    }

    public String getName(){
        return name;
    }

    public String getSongsLink(){
        return links.getSongs();
    }

    public String getPreview(){
        return links.getPreview();
    }

}
