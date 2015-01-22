package com.polluxlab.banglamusic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.polluxlab.banglamusic.helper.RootFragment;

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

        TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        final String number=tm.getLine1Number();

        numberSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://162.248.162.2/musicapp/server/web/app_dev.php/webservice/auth/login/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url+number));
                startActivity(i);
            }
        });
        return rootView;
    }
}
