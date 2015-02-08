package com.polluxlab.banglamusic.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;

/**
 * Created by samiron on 1/25/2015.
 */
public class InternalStorageSimple {
    private final static String INTERNAL_STG = "INTERNAL_STORAGE";
    private InternalStorageSimple() {}

    public static void store(String key, String data) {
        Log.d(INTERNAL_STG, String.format("storing key and data. key: %s, data: %s", key, data));
        if( data.isEmpty())
            return;
        Context context = GlobalContext.get();
        SharedPreferences sh=context.getSharedPreferences(INTERNAL_STG, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sh.edit();
        edit.putString(key, data);
        edit.commit();
    }

    public static String fetch(String key) {
        if( key.isEmpty() ){
            return "";
        }

        Context context = GlobalContext.get();
        SharedPreferences sh=context.getSharedPreferences(INTERNAL_STG, Context.MODE_PRIVATE);
        String s = sh.getString(key, "");
        Log.d(Util.class.getName(), "fetched: " + s);
        return s;
    }
}
