package com.polluxlab.banglamusic;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.polluxlab.banglamusic.R;

public class UrlHandler extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri URIdata = getIntent().getData();
        if(URIdata != null) {
            Log.d(getClass().getName(), "Received Url: " + URIdata.toString());
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }
    }
}
