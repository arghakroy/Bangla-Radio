package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.model.Tag;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARGHA K ROY on 11/20/2014.
 */
public class FreeCategoryFragment extends RootFragment {

    ListView freeSongListView;

    Context con;
    List<Song> freeCategories;
    PlaySoundHelper helper;

    public FreeCategoryFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.category_list_layout, container,  false);
        freeSongListView = (ListView) rootView.findViewById(R.id.categoryItemList);

        con=getActivity();
        helper= (PlaySoundHelper) getActivity();

        if(freeCategories==null) {
            GetFreeSongs getFreeSongs = new GetFreeSongs();
            getFreeSongs.execute();
        }else{
            freeSongListView.setAdapter(new MyListAdapter());
        }
        freeSongListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(AppConstant.DEBUG,"Free song list clicked "+i);
                helper.play(1,i,freeCategories);
            }
        });
        return rootView;
    }

    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return freeCategories.size();
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
                rowView = inflater.inflate(R.layout.single_list_item, null);
            }
            TextView songTitle= (TextView) rowView.findViewById(R.id.singleListItemTitle);
            TextView songAlbum= (TextView) rowView.findViewById(R.id.singleListItemArtist);
            ImageView im= (ImageView) rowView.findViewById(R.id.singleListItemImage);
            songTitle.setText(freeCategories.get(i).getTitle());

            Picasso.with(getActivity()).load(freeCategories.get(i).getPreview()).error(R.drawable.music_icon).into(im);
            songAlbum.setText(freeCategories.get(i).getAlbum());
            return rowView;
        }
    }

    class GetFreeSongs extends AsyncTask<String,String,String>{

        ProgressDialog pDialog;
        int error=0;
        String msg="";

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
            freeCategories = new ArrayList<>();
            try {
                List<Tag> tags = Endpoint.instance().getTags();
                for( Tag t : tags ){
                    if(t.getName().equalsIgnoreCase("free")){
                        freeCategories = t.getSongs();
                        break;
                    }
                }
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
                    Toast.makeText(con, "Server is down, Please try again later "+msg, Toast.LENGTH_SHORT).show();
                }else {
                    Util.showNoInternetDialog(con);
                }
                return;
            }
            Log.d("MuSIC", msg);
            freeSongListView.setAdapter(new MyListAdapter());
        }
    }
}
