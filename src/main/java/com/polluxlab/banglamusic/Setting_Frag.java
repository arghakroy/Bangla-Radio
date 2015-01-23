package com.polluxlab.banglamusic;

import android.content.Context;
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

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;

/**
 * Created by ARGHA K ROY on 11/21/2014.
 */
public class Setting_Frag extends RootFragment {

    EditText numberEt;
    Button numberSubmitBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.setting_layout, container,  false);
        numberSubmitBtn= (Button) rootView.findViewById(R.id.buyBtn);
        numberEt= (EditText) rootView.findViewById(R.id.settingPhnnuberEt);
        //TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        Endpoint.init();
        final String loginUrl = Endpoint.instance().getAuthUrl();
        Log.d(getClass().getName(), "Url: " + loginUrl);

        numberSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(),LogInWebViewActivity.class);
                i.putExtra("url", loginUrl);
                i.setData(Uri.parse(loginUrl));
                startActivity(i);
            }
        });
        return rootView;
    }
}

