package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.model.Subscription;
import com.polluxlab.banglamusic.model.Tag;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARGHA K ROY on 11/20/2014.
 */
public class PremiumCategoryFragment extends RootFragment {

    GridView categoryList;
    public static String currentTitle="";
    Context con;
    static List<Song> premCategories;
    static List<Tag> tags;
    PlaySoundHelper helper;
    boolean subscribed;
    View rootView;
    LinearLayout dialogUi;
    LayoutInflater inflater;
    BroadcastReceiver broadCastReceive;

    public PremiumCategoryFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(getClass().getName(), "Premium content onCcreateView");
        con = getActivity();
        currentTitle=getResources().getString(R.string.category_title);
        this.inflater=inflater;
        setupBroadCast();
        helper = (PlaySoundHelper) getActivity();
        rootView = inflater.inflate(R.layout.category_layout_frag, container, false);
        categoryList= (GridView) rootView.findViewById(R.id.categoryList);
        dialogUi= (LinearLayout) rootView.findViewById(R.id.premView);

        if(Util.isConnectingToInternet(getActivity())){
            checkAllowed();
        }
        return rootView;
    }

    class LoadSubscription extends AsyncTask<String,Subscription,Subscription>{
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog=new ProgressDialog(con);
            pDialog.setMessage("Loading. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Subscription doInBackground(String... params) {
            Subscription s = Endpoint.instance().getSubscription(
                    Util.getSecretKey(getActivity()));
            if( s != null) {
                subscribed = true;
                return s;
            }
            else  subscribed = false;
            return null;
        }

        @Override
        protected void onPostExecute(Subscription s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            //Util.showToast(getActivity(), "Suscribed : " + subscribed);
            Log.d(AppConstant.DEBUG,"Subscribed: "+subscribed);
            if(subscribed) {
                updateUi(subscribed, AppConstant.SUBSCRIBED);
                AccountFragment.currentStatus = AppConstant.SUBSCRIBED;
            }else if(!Util.getSecretKey(getActivity()).isEmpty()){
                Log.d(AppConstant.DEBUG,"Logged in");
                updateUi(true,AppConstant.LOGGED_IN);
                AccountFragment.currentStatus=AppConstant.LOGGED_IN;
            }
        }
    }

    public void updateUi(boolean allowed,int status) {
        if (allowed) {
            //Util.showToast(getActivity(),"inside ui");
            if(status==AppConstant.SUBSCRIBED) {
                dialogUi.setVisibility(View.GONE);
                categoryList.setVisibility(View.VISIBLE);
                generateData();
                categoryList.setAdapter(new MyListAdapter());
                categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        PremiumSubCategoryFragment.title=premCategories.get(i).getTitle();
                        getActivity().getActionBar().setTitle(premCategories.get(i).getTitle());
                        Bundle bun = new Bundle();
                        bun.putInt("position", i);
                        PremiumSubCategoryFragment subFragment = new PremiumSubCategoryFragment();
                        subFragment.setArguments(bun);
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        // Store the Fragment in stack
                        transaction.addToBackStack(null);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.replace(R.id.root_container, subFragment).commit();
                        //new GetStreamLink(i).execute();
                    }
                });
            }else if(status==AppConstant.LOGGED_IN){
                categoryList.setVisibility(View.GONE);
                dialogUi.removeAllViews();
                View v = getActivity().getLayoutInflater().inflate(R.layout.buy_now_layout, null);
                dialogUi.addView(v);
                Button buyBtn = (Button) rootView.findViewById(R.id.buyNowBtn);
                buyBtn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), AppConstant.FONT));
                buyBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        final String url = Endpoint.instance().getPurchase();
                        Log.d(AppConstant.DEBUG, "Url: " + url);
                        Intent i = new Intent(getActivity(), LogInWebViewActivity.class);
                        i.putExtra("url", url);
                        startActivity(i);
                    }
                });
            }
        } else {
            View v = getActivity().getLayoutInflater().inflate(R.layout.buy_now_layout, null);
            dialogUi.addView(v);
            categoryList.setVisibility(View.GONE);
            ImageView im = (ImageView) v.findViewById(R.id.buyNowImage);
            im.setImageResource(R.drawable.log_in_logo);
            Button loginSubmitBtn = (Button) rootView.findViewById(R.id.buyNowBtn);
            loginSubmitBtn.setText("OK");
            loginSubmitBtn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), AppConstant.FONT));
            loginSubmitBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    final String url = Endpoint.instance().getAuthUrl();
                    Log.d(AppConstant.DEBUG, "Url: " + url);
                    Intent i = new Intent(getActivity(), LogInWebViewActivity.class);
                    i.putExtra("url", url);
                    startActivity(i);
                }
            });
        }
    }

    private void checkAllowed() {
        String secret = Util.getSecretKey(getActivity());
        if( secret == null || secret.length()==0 ){
            updateUi(false,0);
            Log.d(AppConstant.DEBUG,"No secret key");
        } else {
            Log.d(AppConstant.DEBUG,"Secret key: "+secret);
            new LoadSubscription().execute();
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
        int images[]={R.drawable.pic0,R.drawable.pic_test_2,R.drawable.pic_test_1,R.drawable.pic5};

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
            categoryName.setTypeface(Typeface.createFromAsset(con.getAssets(), "fonts/solaiman_bold.ttf") );
            categoryName.setText(premCategories.get(i).getTitle());

            LinearLayout imageLay= (LinearLayout) rowView.findViewById(R.id.singleCategoryImageLayout);
            imageLay.setBackgroundResource(images[i]);
            return rowView;
        }
    }

    public void setupBroadCast(){
        broadCastReceive=new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Util.showToast(context,"Received broadccast");
                int status=intent.getIntExtra("status", 0);
                updateUi(true,status);
            }
        };
        Log.d(AppConstant.DEBUG,"registerred premium ui");
        getActivity().registerReceiver(broadCastReceive, new IntentFilter("update-prem-ui"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(AppConstant.DEBUG,"unregisterred premium ui");
        getActivity().unregisterReceiver( broadCastReceive);
    }
}
