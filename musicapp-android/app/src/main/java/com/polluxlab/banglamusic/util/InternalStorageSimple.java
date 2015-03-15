package com.polluxlab.banglamusic.util;

import android.content.*;
import android.util.*;

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
        return sh.getString(key, "");
    }
}
