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
public class InternalStorageSimple {
    private final static String LOG_KEY = "STORAGE";
    private InternalStorageSimple() {}

    public static void store(String key, String data) {
        try {
            Context context = GlobalContext.get();
            key = URLEncoder.encode(key, "UTF-8");
            FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
        } catch ( IOException e){
            Log.d(LOG_KEY, "Failed to stored");
            e.printStackTrace();
        }
    }

    public static String fetch(String key) {
        String data = new String();
        if (key.isEmpty()) return data;
        Context context = GlobalContext.get();

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = context.openFileInput(key);
            ois = new ObjectInputStream(fis);
            data = (String) ois.readObject();
        } catch (FileNotFoundException e) {
            Log.d(LOG_KEY, "File is not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(LOG_KEY, "File is not found");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.d(LOG_KEY, "File is not found");
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return data;
    }
}
