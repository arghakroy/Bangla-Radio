package com.polluxlab.banglamusic.model;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *   "name": "New tag",
     "totalArtist": "1",
     "totalAlbum": "1",
     "totalSongs": "1",
     "links": {
         "self": "http://162.248.162.2/music/server/web/app_dev.php/webservice/tags/4",
         "songs": "http://162.248.162.2/music/server/web/app_dev.php/webservice/tags/4/songs",
         "albums": "http://162.248.162.2/music/server/web/app_dev.php/webservice/tags/4/albums",
         "artists" : "http://162.248.162.2/music/server/web/app_dev.php/webservice/tags/4/artists"
     }
 * Created by samiron on 1/18/2015.
 */

public class Tag extends ModelBase {

    private String name;
    private String totalArtist;
    private String totalAlbum;
    private String totalSongs;
    private Links links;

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

}
