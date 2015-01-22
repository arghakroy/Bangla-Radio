package com.polluxlab.banglamusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.polluxlab.banglamusic.R;

public class UrlHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri URIdata = getIntent().getData();
        String host = URIdata.getHost();  //=> success
        String secret = String.valueOf(URIdata.getQueryParameters("secret")); // => should return the secret
        Toast.makeText(this,host+" "+secret+" "+URIdata.toString(),Toast.LENGTH_SHORT).show();

        if(URIdata != null) {
            Log.d(getClass().getName(), "Received Url: " + URIdata.toString());
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }
    }
}
