package com.polluxlab.banglamusic;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.Browser;
import android.util.*;
import android.view.*;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.DataLoader;
import com.polluxlab.banglamusic.util.GlobalContext;
import com.polluxlab.banglamusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class UrlHandler extends Activity {

    private Subscription subscription;
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
        GlobalContext.set(getApplicationContext());
        getActionBar().hide();
        final Uri URIdata = getIntent().getData();
        if(URIdata == null){
            setContentView(R.layout.buy_fail_layout);
            return;
        }
        final String host = URIdata.getHost();
        Log.d("MUSIC","URI "+URIdata);
        Log.d("MUSIC","HOST "+host);

        if( host.equals(LOGIN_SUCCESS) ){
            String key = URIdata.getQueryParameter(KEY_SECRET);
            if(key!=null){
                Util.storeSecret(getApplicationContext(), key);
                postLoginSuccessOperations();
            }
            Log.d(AppConstant.DEBUG,"Secret key: "+Util.getSecretKey(this));
            Log.d(AppConstant.DEBUG, "login successful");
        } else if( host.equals(LOGIN_CANCELLED)) {
            Log.d(AppConstant.DEBUG, "login cancelled");
        } else if( host.equals(PURCHASE)) {
            Log.d(AppConstant.DEBUG, "purchase successful");
            String purchaseStatus = URIdata.getQueryParameter(PURCHASE_STATUS);
            if(purchaseStatus.equals(PURCHASE_SUCCESS)){
                Log.d(AppConstant.DEBUG, "purchase successful");
                setContentView(R.layout.buy_success_layout);
                postLoginSuccessOperations();
            } else if(purchaseStatus.equals(PURCHASE_CANCELLED)){
                Log.d(AppConstant.DEBUG, "purchase cancelled");
                setContentView(R.layout.buy_fail_layout);
            }
        }
    }

    private void postLoginSuccessOperations() {
        new DataLoader<Subscription>(getApplicationContext(), new DataLoader.Worker<Subscription>(){
            @Override
            public Subscription work() {
                Log.d(getClass().getName(), "getting subscription");
                return Endpoint.instance().getSubscription(Util.getSecretKey(getApplicationContext()));
            }
            @Override
            public void done(Subscription s) {
                subscription=s;
                performBasedOnSubscription();
            }
        }).turnOfDialog()
                .execute();
    }

    private void performBasedOnSubscription() {
        if (subscription != null) {
            if("MY-RADIO-RADIOBANGLA-FULL-W".equalsIgnoreCase(subscription.getPackageName())) {
                Log.d("MUSIC", "User has trial subscription. Showing premium content for trial subscription");
                setContentView(R.layout.free_gift_layout);
            }
            else {
                Log.d("MUSIC", "User has subscription. Showing premium content");
                finish();
            }
            loadPremiumContent(AppConstant.SUBSCRIBED);
        } else {
            loadPremiumContent(AppConstant.LOGGED_IN);
            Log.d("MUSIC", "User DOESNT have subscription. We should show buy now screen");
            setContentView(R.layout.buy_now_layout);
        }
    }

    private void loadPremiumContent(int status) {
        Intent premIntent=new Intent("update-prem-ui");
        premIntent.putExtra("status",status);
        sendBroadcast(premIntent);

        Intent settingIntent=new Intent("update-setting-ui");
        settingIntent.putExtra("status",status);
        if(status==AppConstant.SUBSCRIBED){
            Log.d(AppConstant.DEBUG,"End "+subscription.getEndDate());
            if(subscription.getEndDate()!=null) {
                settingIntent.putExtra("enddate", subscription.getEndDate().toString());
            }
            settingIntent.putExtra("phone_number",subscription.getPhone_number());
        }
        sendBroadcast(settingIntent);
    }

    public void btnClicked(View v){
        String url="";
        switch (v.getId()){
            case R.id.buyNowBtn:
                url=Endpoint.instance().getPurchase();
                break;
            case R.id.freeTrialOk:
                Log.d("MUSIC", "User has trial subscription. Showing premium content for trial subscription");
                loadPremiumContent(AppConstant.SUBSCRIBED);
                break;
            case R.id.buyFailBtn:
                url = Endpoint.instance().getAuthUrl();
                break;
            case R.id.buySuccessBtn:
            default:
                break;
        }
        if(!url.isEmpty())
            openBrowser(url,false);
        finish();
    }

    public void openBrowser(String url,boolean defaultBrowser){
        if(defaultBrowser){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if(url.contains("payment")) {
                String secret= Util.getSecretKey(this);
                Map<String, String> map = new HashMap<>();
                String usernameRandomPassword = secret + ":" + secret;
                String authorization = null;
                try {
                    authorization = "Basic " + Base64.encodeToString(usernameRandomPassword.getBytes("UTF-8"), Base64.NO_WRAP);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                map.put("Authorization", authorization);
                Bundle bundle = new Bundle();
                if(map!=null){
                    for(String key: map.keySet()){
                        bundle.putString(key, map.get(key));
                    }
                }
                browserIntent.putExtra(Browser.EXTRA_HEADERS, bundle);
            }
            startActivity(browserIntent);
        }else{
            Intent i = new Intent(this, LogInWebViewActivity.class);
            i.putExtra("url", url);
            startActivity(i);
        }
    }
}
