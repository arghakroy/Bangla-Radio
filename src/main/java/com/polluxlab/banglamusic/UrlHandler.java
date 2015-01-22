package com.polluxlab.banglamusic;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.polluxlab.banglamusic.R;

public class UrlHandler extends Activity {

    PlaySoundHelper helper;
    Uri URIdata;
    String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //helper= (PlaySoundHelper) this;
        getActionBar().hide();

        URIdata = getIntent().getData();
         host = URIdata.getHost();  //=> success
        String secret = String.valueOf(URIdata.getQueryParameters("secret")); // => should return the secret
        Toast.makeText(this,host+" "+secret+" "+URIdata.toString(),Toast.LENGTH_SHORT).show();

        if(URIdata != null) {
            Log.d(getClass().getName(), "Received Url: " + URIdata.toString());
            if(host.equals("success"))
                setContentView(R.layout.buy_success_layout);
            else
                setContentView(R.layout.buy_fail_layout);
            return;
        }
    }

    public void btnClicked(View v){
        finish();
    }
}
