package com.polluxlab.banglamusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.DataLoader;
import com.polluxlab.banglamusic.util.GlobalContext;
import com.polluxlab.banglamusic.util.Util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ARGHA K ROY on 11/21/2014.
 */
public class Setting_Frag extends RootFragment {

    EditText numberEt;
    TextView remainDays,lastDate;
    Button buyBtn;
    LinearLayout accountStatusContainer;
    LinearLayout accountBuyContainer;

    BroadcastReceiver broadCastReceive;
    public static int currentStatus=0;
    public String endDate="";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GlobalContext.set(getActivity());

        View rootView = inflater.inflate(R.layout.setting_layout, container,  false);
        buyBtn= (Button) rootView.findViewById(R.id.account_buy_btn);
        accountStatusContainer = (LinearLayout) rootView.findViewById(R.id.account_status_container);
        accountBuyContainer = (LinearLayout) rootView.findViewById(R.id.account_buy_container);
        remainDays= (TextView) rootView.findViewById(R.id.account_remainning_days);
        lastDate= (TextView) rootView.findViewById(R.id.account_last_date);
        numberEt= (EditText) rootView.findViewById(R.id.settingPhnnuberEt);

        buyBtn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), AppConstant.FONT));

        setupBroadCast();
        updateUI(currentStatus);
        return rootView;

    }

    public void showSubscribeUI(){
        accountStatusContainer.setVisibility(View.VISIBLE);
        accountBuyContainer.setVisibility(View.GONE);
        SharedPreferences sh=getActivity().getSharedPreferences(AppConstant.PREF_NAME,Context.MODE_PRIVATE);
        if(!endDate.isEmpty()){
            SharedPreferences.Editor edit=sh.edit();
            edit.putString("enddate",endDate);
            edit.commit();
        }else endDate=sh.getString("enddate","");

        if(!endDate.isEmpty()){
            Date d1=new Date();
            SimpleDateFormat from = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat to = new SimpleDateFormat("MMM dd',' yyyy");
            Date d2= null;
            try {
                d2 = from.parse(endDate);
                remainDays.setText(Util.toBangla((d2.getTime()-d1.getTime())/(1000*60*60*24)+1+" "));
                lastDate.setText("আপনার মেয়াদ শেষ হবে  "+Util.toBangla(to.format(d2)));
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
                Intent i = new Intent(getActivity(), LogInWebViewActivity.class);
                i.putExtra("url", URL);
                startActivity(i);
            }
        });
    }

    public void updateUI(int status){
        Log.d(AppConstant.DEBUG,status+"");
        if(status== AppConstant.SUBSCRIBED)
            showSubscribeUI();
        else if(status==AppConstant.LOGGED_IN){
            setBuyBtn(Endpoint.instance().getPurchase(getActivity()));
        }else{
            setBuyBtn(Endpoint.instance().getAuthUrl());
        }
    }

    public void setupBroadCast(){
        broadCastReceive=new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int status=intent.getIntExtra("status",0);
                currentStatus=status;
                endDate=intent.getStringExtra("enddate");
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
}

