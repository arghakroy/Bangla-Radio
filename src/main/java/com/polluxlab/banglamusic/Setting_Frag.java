package com.polluxlab.banglamusic;

import android.content.Context;
import android.content.Entity;
import android.content.Intent;
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

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.util.DataLoader;
import com.polluxlab.banglamusic.util.Util;

import java.io.Serializable;

/**
 * Created by ARGHA K ROY on 11/21/2014.
 */
public class Setting_Frag extends RootFragment {

    EditText numberEt;
    Button numberSubmitBtn;
    LinearLayout accountStatusContainer;
    LinearLayout accountBuyContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.setting_layout, container,  false);
        numberSubmitBtn= (Button) rootView.findViewById(R.id.buyBtn);
        accountStatusContainer = (LinearLayout) rootView.findViewById(R.id.account_status_container);
        accountBuyContainer = (LinearLayout) rootView.findViewById(R.id.account_buy_container);

        numberEt= (EditText) rootView.findViewById(R.id.settingPhnnuberEt);
        showView();
        return rootView;

    }

    private void showView() {
        String secret = Util.getSecretKey(getActivity());

        if( secret.isEmpty() ){
            showNotAuthenticatedUI();
        } else {
            showAuthenticatedUI(secret);
        }
    }

    private void showAuthenticatedUI(final String secret) {
        new DataLoader<Subscription>(getActivity(), new DataLoader.Worker<Subscription>(){

            @Override
            public Subscription work() {
                Subscription s = Endpoint.instance().getSubscription(secret);
                return s;
            }

            @Override
            public void done(Subscription s) {
                String url = new String();
                if(s == null){
                    showButton(url);
                } else {
                    showSubscriptionStatus(s);
                }
            }
        }).turnOfDialog().execute();
    }

    private void showSubscriptionStatus(Subscription s) {
        accountStatusContainer.setVisibility(LinearLayout.VISIBLE);
        //TODO: calculate dates from Subscription and show in the layer
    }

    private void showNotAuthenticatedUI() {
        String url = Endpoint.instance().getAuthUrl();
        showButton(url);
    }

    private void showButton(final String url) {
        numberSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LogInWebViewActivity.class);
                i.putExtra("url", url);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }
}

