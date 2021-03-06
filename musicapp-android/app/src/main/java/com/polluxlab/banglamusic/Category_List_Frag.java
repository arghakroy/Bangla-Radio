package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.polluxlab.banglamusic.helper.RootFragment;
import com.polluxlab.banglamusic.model.Artist;
import com.polluxlab.banglamusic.model.Category;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.model.Tag;
import com.polluxlab.banglamusic.util.JSONParser;
import com.polluxlab.banglamusic.util.Util;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ARGHA K ROY on 12/26/2014.
 */
public class Category_List_Frag extends RootFragment {


    ListView itemList;
    List<Song> categoryItem;
    PlaySoundHelper helper;
    Context con;
    public static Artist artist;
    int position;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_list_layout, container,  false);
        con=getActivity();
        helper = (PlaySoundHelper) getActivity();
        itemList=(ListView) rootView.findViewById(R.id.categoryItemList);

        //getActivity().getActionBar().setTitle("গানের লিস্ট");

        position=getArguments().getInt("position");

        new GetSongs().execute();
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                helper.play(1,position,categoryItem);
            }
        });
        return rootView;
    }


    class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return  categoryItem.size();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View rowView=view;
            if(rowView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.single_list_item, null);
            }

            TextView categoryName= (TextView) rowView.findViewById(R.id.singleListItemTitle);
            TextView categoryAlbum= (TextView) rowView.findViewById(R.id.singleListItemArtist);
            ImageView im= (ImageView) rowView.findViewById(R.id.singleListItemImage);
            categoryName.setText(categoryItem.get(i).getTitle());
            categoryAlbum.setText(categoryItem.get(i).getAlbum());
            Picasso.with(getActivity()).load(categoryItem.get(i).getPreview()).error(R.drawable.music_icon).into(im);
            return rowView;
        }
    }
    class GetSongs extends AsyncTask<String,String,String> {

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
            categoryItem=new ArrayList<>();
            try {
                if(position ==1 && artist!=null){
                    categoryItem=artist.getSongs();
                    return null;
                }
                Endpoint.init();
                List<Tag> tag=Endpoint.instance().getTags();
                if(position==0)
                    categoryItem=tag.get(7).getSongs();
                else if(position==2)
                    categoryItem=tag.get(1).getSongs();
                else if(position==3)
                    categoryItem=tag.get(2).getSongs();
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
            itemList.setAdapter(new MyListAdapter());
        }
    }
}
