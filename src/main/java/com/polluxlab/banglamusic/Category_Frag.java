package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Category;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.util.JSONParser;
import com.polluxlab.banglamusic.util.Util;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ARGHA K ROY on 11/20/2014.
 */
public class Category_Frag extends RootFragment {

    GridView categoryList;
    int pos=0;

    Context con;
    static List<Song> premCategories;
    static List<Song> freeCategories;
    PlaySoundHelper helper;

    public Category_Frag(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.category_layout_frag, container,  false);
        categoryList= (GridView) rootView.findViewById(R.id.categoryList);

        con=getActivity();
        helper= (PlaySoundHelper) getActivity();
        Bundle b=getArguments();
        pos=b.getInt("pos");

        if(freeCategories==null) {
            GetAlbums getAlbums = new GetAlbums();
            getAlbums.execute();
        }else{
            categoryList.setAdapter(new MyListAdapter());
        }
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
/*
                Category_Sub_Frag subFragment = new Category_Sub_Frag();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.addToBackStack(null);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.replace(R.id.root_container, subFragment).commit();*/
                new GetStreamLink(i).execute();
            }
        });
        return rootView;
    }

    class GetStreamLink extends AsyncTask<String,String,String>{

        ProgressDialog pDialog;
        int pos;
        String link="";
        GetStreamLink(int i){
            pos=i;
        }

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
        protected String doInBackground(String... params) {
            link=freeCategories.get(pos).getStreamLink();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            Util.showToast(con,"Please wait...");
            try {
                MediaPlayer player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setDataSource(link);
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                player.prepareAsync();
            } catch (Exception e) {
                Util.showToast(con,"Error in playing");
            }
        }
    }


    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(pos==0) return freeCategories.size();
            else
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

            if(pos==0)
                categoryName.setText(freeCategories.get(i).getTitle());
            else if(pos==1)
                categoryName.setText(premCategories.get(i).getTitle());
            return rowView;
        }
    }

    class GetAlbums extends AsyncTask<String,String,String>{

        ProgressDialog pDialog;
        String s="";
        int success=-1;
        int error=0;

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
        protected String doInBackground(String... st) {
            freeCategories=new ArrayList<>();
            premCategories=new ArrayList<>();
            try {
                Endpoint.init();
                freeCategories=Endpoint.instance().getSongs();
                premCategories=freeCategories;
            }catch(Exception e){
                error=1;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            if(error==1){
                if(Util.isConnectingToInternet(con)){
                    Toast.makeText(con, "Server is down, Please try again later", Toast.LENGTH_SHORT).show();
                }else
                    Util.showNoInternetDialog(con);
                return;
            }
            categoryList.setAdapter(new MyListAdapter());
        }
    }
}
