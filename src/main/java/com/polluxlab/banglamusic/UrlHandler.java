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

import java.util.List;

public class UrlHandler extends Activity {

    PlaySoundHelper helper;
    Uri URIdata;
    String host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();

        URIdata = getIntent().getData();
        if(URIdata == null){
            setContentView(R.layout.buy_fail_layout);
            return;
        }
        host = URIdata.getHost();
        String secret;

        if( host.equals("success")){
            List<String> keys = URIdata.getQueryParameters("sharedSecret");
            if(!keys.isEmpty())
                secret = keys.get(0);
            setContentView(R.layout.buy_success_layout);
        } else if( host.equals("cancelled")) {
            setContentView(R.layout.buy_fail_layout);
        } else if( host.equals("purchase")) {
            String purchaseStatus = null;
            List<String> keys = URIdata.getQueryParameters("status");
            if(!keys.isEmpty())
                purchaseStatus = keys.get(0);
            if(purchaseStatus.equals("success")){

            } else if(purchaseStatus.equals("cancelled")){

            }
        }
    }

    public void btnClicked(View v){
        finish();
    }
}
