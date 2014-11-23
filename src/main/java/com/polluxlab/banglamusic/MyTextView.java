package com.polluxlab.banglamusic;

/**
 * Created by ARGHA K ROY on 11/23/2014.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setType(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setType(context);
    }

    public MyTextView(Context context) {
        super(context);
        setType(context);
    }

    private void setType(Context context){
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/solaiman-bold.ttf"));
        this.setShadowLayer(1.5f, 5, 5, android.R.color.black);
    }
}