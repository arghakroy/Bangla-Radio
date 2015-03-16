package com.polluxlab.banglamusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.GlobalContext;
import com.polluxlab.banglamusic.util.Util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by ARGHA K ROY on 11/21/2014.
 */
public class AccountFragment extends RootFragment implements View.OnClickListener{

    TextView remainDays,lastDate,phoneNumber;
    Button buyBtn,helpBtn,exitBtn;
    LinearLayout accountStatusContainer;
    LinearLayout accountBuyContainer,logOutLayout;

    SharedPreferences sh;
    BroadcastReceiver broadCastReceive;
    public static int currentStatus=0;
    private String endDate="";
    private String phoneNum="";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GlobalContext.set(getActivity());

        View rootView = inflater.inflate(R.layout.setting_layout, container,  false);
        buyBtn= (Button) rootView.findViewById(R.id.account_buy_btn);
        accountStatusContainer = (LinearLayout) rootView.findViewById(R.id.account_status_container);
        accountBuyContainer = (LinearLayout) rootView.findViewById(R.id.account_buy_container);
        logOutLayout= (LinearLayout) rootView.findViewById(R.id.logOutLayout);
        remainDays= (TextView) rootView.findViewById(R.id.account_remainning_days);
        lastDate= (TextView) rootView.findViewById(R.id.account_last_date);
        phoneNumber= (TextView) rootView.findViewById(R.id.accountPhoneNumber);
        helpBtn= (Button) rootView.findViewById(R.id.helpLineBtn);
        exitBtn= (Button) rootView.findViewById(R.id.accountExitButton);

        exitBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        sh=getActivity().getSharedPreferences(AppConstant.PREF_NAME,Context.MODE_PRIVATE);
        buyBtn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), AppConstant.FONT));
        setupBroadCast();
        if(Util.isConnectingToInternet(getActivity())) {
            updateUI(currentStatus);
        }
        return rootView;

    }

    public void showSubscribeUI(){
        accountStatusContainer.setVisibility(View.VISIBLE);
        accountBuyContainer.setVisibility(View.GONE);
        logOutLayout.setVisibility(View.VISIBLE);
        SharedPreferences sh=getActivity().getSharedPreferences(AppConstant.PREF_NAME,Context.MODE_PRIVATE);
        if(!endDate.isEmpty()){
            SharedPreferences.Editor edit=sh.edit();
            edit.putString("enddate",endDate);
            edit.commit();
        }else endDate=sh.getString("enddate","");
        Log.d(AppConstant.DEBUG,"End date "+endDate);

        if(!phoneNum.isEmpty()){
            SharedPreferences.Editor edit=sh.edit();
            edit.putString("phoneNum",phoneNum);
            edit.commit();
        }else phoneNum=sh.getString("phoneNum","");

        phoneNumber.setText(phoneNum);

        if(!endDate.isEmpty()){
            SimpleDateFormat from = new SimpleDateFormat("MMMM dd',' yyyy hh':'mm");
            SimpleDateFormat to = new SimpleDateFormat("MMM dd',' yyyy");
            Date d2= null;
            try {
                d2 = from.parse(endDate);
                String date=to.format(d2);
                String month=date.split(" ")[0];
                lastDate.setText(Util.toBanglaMonth(month)+Util.toBanglaNumber(
                        date.substring(month.length(), date.length())
                ));
            } catch (ParseException e) {
                Log.d(AppConstant.DEBUG,"Error in setting");
                e.printStackTrace();
            }
        }

    }

    public void setBuyBtn(final String URL){
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(URL, false);
            }
        });
    }

    public void updateUI(int status){
        Log.d(AppConstant.DEBUG,status+"");
        if(status== AppConstant.SUBSCRIBED){
            showSubscribeUI();
        }
        else if(status==AppConstant.LOGGED_IN){
            logOutLayout.setVisibility(View.VISIBLE);
            setBuyBtn(Endpoint.instance().getPurchase());
            phoneNum=sh.getString("phoneNum","");
        }else{
            logOutLayout.setVisibility(View.GONE);
            setBuyBtn(Endpoint.instance().getAuthUrl());
        }
        phoneNumber.setText(phoneNum);
    }

    public void setupBroadCast(){
        broadCastReceive=new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int status=intent.getIntExtra("status",0);
                currentStatus=status;
                endDate=intent.getStringExtra("enddate");
                phoneNum=intent.getStringExtra("phone_number");
                updateUI(status);
            }
        };
        getActivity().registerReceiver(broadCastReceive, new IntentFilter("update-setting-ui"));
        Log.d(AppConstant.DEBUG,"registerred setting ui");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(AppConstant.DEBUG, "UNregisterring setting ui");
        getActivity().unregisterReceiver(broadCastReceive);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountExitButton:
                currentStatus=0;
                Util.storeSecret(getActivity(),"");
                getActivity().finish();
                break;
            case R.id.helpLineBtn:
                String uri = "tel:0162211800" ;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
                break;
        }
    }


    public void openBrowser(String url,boolean defaultBrowser){
        if(defaultBrowser){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if(url.contains("payment")) {
                String secret= Util.getSecretKey(getActivity());
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
            Intent i = new Intent(getActivity(), LogInWebViewActivity.class);
            i.putExtra("url", url);
            startActivity(i);
        }
    }
}

