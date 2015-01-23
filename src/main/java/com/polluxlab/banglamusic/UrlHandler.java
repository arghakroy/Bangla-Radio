package com.polluxlab.banglamusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.polluxlab.banglamusic.R;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.util.Util;

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
            if(!keys.isEmpty()){
                secret = keys.get(0);
                SharedPreferences sh=getSharedPreferences("MUSIC_PREF",MODE_PRIVATE);
                SharedPreferences.Editor edit=sh.edit();
                edit.putString("sharedSecret",secret);
                edit.commit();
            }
            setContentView(R.layout.buy_now_layout);
        } else if( host.equals("cancelled")) {
            setContentView(R.layout.buy_fail_layout);
        } else if( host.equals("purchase")) {
            String purchaseStatus = null;
            List<String> keys = URIdata.getQueryParameters("status");
            if(!keys.isEmpty())
                purchaseStatus = keys.get(0);
            if(purchaseStatus.equals("success")){
                setContentView(R.layout.buy_success_layout);
            } else if(purchaseStatus.equals("cancelled")){
                setContentView(R.layout.buy_fail_layout);
            }
        }
    }

    public void btnClicked(View v){
        String url="";
        switch (v.getId()){
            case R.id.buyNowBtn:
                url=Endpoint.instance().getPurchase(this);
                break;
            case R.id.buyFailBtn:
                 url = Endpoint.instance().getAuthUrl();
                break;
            case R.id.buySuccessBtn:
            default:
                finish();
                return;
        }
        Util.showToast(this,url);
        Intent i = new Intent(this,LogInWebViewActivity.class);
        i.putExtra("url", url);
        startActivity(i);
        finish();
    }
}
