package com.polluxlab.banglamusic.util;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by samiron on 1/25/2015.
 */
public class InternalStorage {
    private final static String LOG_KEY = "STORAGE";
    private InternalStorage() {}

    public static Object store(Context context, String key, Object data) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(data);
        oos.close();
        fos.close();
        return data;
    }
    public static Object store(String key, Object data) throws IOException {
        return store(GlobalContext.get(), key, data);
    }

    public static Object fetch(String key, StorageDataProvider d) {
        try {
            key = URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(LOG_KEY, "Failed to encode key: " + key);
            e.printStackTrace();
            return d.getData();
        }
        Log.d(LOG_KEY, "Fetching object for key: " + key);

        Context context = GlobalContext.get();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(key);
            ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            Log.d(LOG_KEY, "Cache hit");
            if(d.validate(object)){
                Log.d(LOG_KEY, "Object validated. Returning");
                return object;
            } else {
                Log.d(LOG_KEY, "Object is not validated. Getting data.");
                fis.close();
                ois.close();
                return reloadDataAndSave(context, key, d);
            }
        } catch (FileNotFoundException | ClassNotFoundException e) {
            Log.d(LOG_KEY, "Cache miss. Exception: " + e.toString());
            e.printStackTrace();
            try {
                fis.close();
                ois.close();
            } catch (IOException | NullPointerException e1 ) {
                Log.d(LOG_KEY, "Exception to close streams: " + e.toString());
                e1.printStackTrace();
            }
            return reloadDataAndSave(context, key, d);
        } catch( IOException e) {
            Log.d(LOG_KEY, "Something bad happened: Cant save object");
            e.printStackTrace();
            return d.getData();
        } finally {
            try {
                fis.close();
                ois.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private static Object reloadDataAndSave(Context c, String key, StorageDataProvider d){
        Object data = d.getData();
        if( data != null) {
            Log.d(LOG_KEY, "Retrieved data");
            try {
                return store(c, key, data);
            } catch (IOException e) {
                Log.d(LOG_KEY, "Failed to store data. Returning null");
                e.printStackTrace();
            }
        }
        return data;
    }
}
