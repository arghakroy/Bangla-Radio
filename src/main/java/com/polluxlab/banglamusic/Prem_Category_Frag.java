package com.polluxlab.banglamusic;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.model.Tag;
import com.polluxlab.banglamusic.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARGHA K ROY on 11/20/2014.
 */
public class Prem_Category_Frag extends RootFragment {

    GridView categoryList;

    Context con;
    static List<Song> premCategories;
    static List<Tag> tags;
    PlaySoundHelper helper;

    public Prem_Category_Frag(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.category_layout_frag, container,  false);
        categoryList= (GridView) rootView.findViewById(R.id.categoryList);
        con = getActivity();
        helper = (PlaySoundHelper) getActivity();

        if( isAllowed() ) {
            generateData();
            categoryList.setAdapter(new MyListAdapter());
            categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle bun = new Bundle();
                    bun.putInt("position", i);
                    Category_Sub_Frag subFragment = new Category_Sub_Frag();
                    subFragment.setArguments(bun);
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    // Store the Fragment in stack
                    transaction.addToBackStack(null);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.root_container, subFragment).commit();
                    //new GetStreamLink(i).execute();
                }
            });
        }else{
            rootView=inflater.inflate(R.layout.dialog_layout, container,  false);
            ImageView im=(ImageView) rootView.findViewById(R.id.dialogImageView);
            im.setImageResource(R.drawable.log_in_logo);
            Button loginSubmitBtn=(Button) rootView.findViewById(R.id.dialogBtn);
            loginSubmitBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                    final String number=tm.getLine1Number();
                    String url = "https://162.248.162.2/musicapp/server/web/app_dev.php/webservice/auth/login/"+ Util.getUserId(con);
                    Intent i = new Intent(getActivity(),LogInWebViewActivity.class);
                    i.putExtra("url",url);
                    startActivity(i);
                }
            });
        }
        return rootView;
    }
/*
    private void showLoginDialog() {
        Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //regDialog.setTitle("Registration");
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        ImageView im=(ImageView) dialog.findViewById(R.id.dialogImageView);

        Button loginSubmitBtn=(Button) dialog.findViewById(R.id.dialogBtn);
        loginSubmitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
        dialog.show();
    }*/

    private boolean isAllowed() {
        String secret = Util.getSecretKey(getActivity());
        if( secret == null || secret.isEmpty() ){
            return false;
        } else {
            Endpoint.init();
            Subscription s = Endpoint.instance().getSubscription(getActivity());
            if( s != null) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void generateData() {
        premCategories=new ArrayList<>();

        Song s1=new Song();
        s1.setTitile("বাংলা সংবাদ");
        premCategories.add(s1);

        Song s2=new Song();
        s2.setTitile("বাংলা গান");
        premCategories.add(s2);

        Song s3=new Song();
        s3.setTitile("নাটক");
        premCategories.add(s3);

        Song s4=new Song();
        s4.setTitile("টক শো");
        premCategories.add(s4);
    }


    class MyListAdapter extends BaseAdapter{
        int images[]={R.drawable.pic0,R.drawable.pic_test_1,R.drawable.pic_test_2,R.drawable.pic5};

        @Override
        public int getCount() {
            return premCategories.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView=view;
            if(rowView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.single_category_item, null);
            }
            TextView categoryName= (TextView) rowView.findViewById(R.id.singleCategoryName);
            categoryName.setTypeface(Typeface.createFromAsset(con.getAssets(), "fonts/solaiman-bold.ttf") );
            categoryName.setText(premCategories.get(i).getTitle());

            LinearLayout imageLay= (LinearLayout) rowView.findViewById(R.id.singleCategoryImageLayout);
            imageLay.setBackgroundResource(images[i]);
            return rowView;
        }
    }
}
