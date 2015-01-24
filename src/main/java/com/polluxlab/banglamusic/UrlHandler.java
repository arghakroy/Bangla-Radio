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
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.DataLoader;
import com.polluxlab.banglamusic.util.Util;

import java.util.List;

public class UrlHandler extends Activity {

    PlaySoundHelper helper;
    Uri URIdata;
    String host;
    boolean hasSubscription;
    private final static String LOGIN_SUCCESS="success";
    private final static String LOGIN_CANCELLED="cancelled";
    private final static String PURCHASE="purchase";
    private final static String PURCHASE_STATUS="status";
    private final static String PURCHASE_SUCCESS="success";
    private final static String PURCHASE_CANCELLED="cancelled";
    private final static String KEY_SECRET="sharedSecret";


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
        Log.d("MUSIC","URI "+URIdata);

        if( host.equals(LOGIN_SUCCESS) ){
            String key = URIdata.getQueryParameter(KEY_SECRET);
            if(key!=null){
                Util.storeSecret(getApplicationContext(), key);
                postLoginSuccessOperations();
            }
            Log.d(getClass().getName(), "login successful");
        } else if( host.equals(LOGIN_CANCELLED)) {
            Log.d(getClass().getName(), "login cancelled");
        } else if( host.equals(PURCHASE)) {
            String purchaseStatus = URIdata.getQueryParameter(PURCHASE_STATUS);
            if(purchaseStatus.equals(PURCHASE_SUCCESS)){
                Log.d(getClass().getName(), "purchase successful");
                setContentView(R.layout.buy_success_layout);
            } else if(purchaseStatus.equals(PURCHASE_CANCELLED)){
                Log.d(getClass().getName(), "purchase cancelled");
                setContentView(R.layout.buy_fail_layout);
            }
        }
    }

    private void postLoginSuccessOperations() {
        final Subscription subs;
        new DataLoader<Subscription>(getApplicationContext(), new DataLoader.Worker<Subscription>(){
            @Override
            public Subscription work() {
                Log.d(getClass().getName(), "getting subscription");
                return Endpoint.instance().getSubscription(Util.getSecretKey(getApplicationContext()));
            }
            @Override
            public void done(Subscription s) {
                hasSubscription = (s != null);
                performBasedOnSubscription();
            }
        }).turnOfDialog()
                .execute();
    }

    private void performBasedOnSubscription() {
        if (hasSubscription) {
            Log.d("MUSIC", "User has subscription. Showing premium conent");
            loadPremiumContent(AppConstant.SUBSCRIBED);
            finish();
        } else {
            loadPremiumContent(AppConstant.LOGGED_IN);
            Log.d("MUSIC", "User DOESNT have subscription. We should show buy now screen");
            setContentView(R.layout.buy_now_layout);
        }
    }

    //TODO: Implement it
    private void loadPremiumContent(int status) {
       if(status==AppConstant.SUBSCRIBED)
            sendBroadcast(new Intent("update-prem-ui"));

        Intent settingIntent=new Intent("update-setting-ui");
        settingIntent.putExtra("status",status);
        sendBroadcast(settingIntent);
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
//        Util.showToast(this,url);
        Intent i = new Intent(this,LogInWebViewActivity.class);
        i.putExtra("url", url);
        startActivity(i);
        finish();
    }
}
