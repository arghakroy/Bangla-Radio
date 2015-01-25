package com.polluxlab.banglamusic.util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

/**
 * Created by samiron on 1/25/2015.
 */
public class GlobalContext {

    private static Context context = null;

    public static void set(Context c) {
        if(context == null)
            context = c;
    }

    public static void set(Context c, boolean force){
        context = c;
    }

    public static Context get(){
        return context;
    }
}
