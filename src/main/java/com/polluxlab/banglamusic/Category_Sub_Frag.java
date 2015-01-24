package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.polluxlab.banglamusic.model.Artist;
import com.polluxlab.banglamusic.model.Category;
import com.polluxlab.banglamusic.model.Endpoint;
import com.polluxlab.banglamusic.model.Tag;
import com.polluxlab.banglamusic.util.JSONParser;
import com.polluxlab.banglamusic.util.Util;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ARGHA K ROY on 12/26/2014.
 */
public class Category_Sub_Frag extends RootFragment {


    GridView categoryItemList;
    List<Artist> categoryItem;
    Context con;

    public int position=0;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_layout_frag, container, false);
        con = getActivity();

        getActivity().getActionBar().setHomeButtonEnabled(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        position = getArguments().getInt("position");
        categoryItemList = (GridView) rootView.findViewById(R.id.categoryList);

        if (position == 1){
            new GetArtists().execute();
        }else{
            Category_List_Frag listFragment = new Category_List_Frag();
            listFragment.setArguments(getArguments());
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.root_container, listFragment).commit();
        }
        categoryItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Category_List_Frag.artist=categoryItem.get(i);
                Category_List_Frag listFragment = new Category_List_Frag();
                listFragment.setArguments(getArguments());
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.replace(R.id.root_container, listFragment).commit();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView=view;
            if(rowView==null){
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.single_subcategory_item, null);
            }
            TextView categoryName= (TextView) rowView.findViewById(R.id.singleSubCategoryName);
            categoryName.setText(categoryItem.get(i).getName());
            return rowView;
        }
    }

    class GetArtists extends AsyncTask<String,String,String> {

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
                Endpoint.init();
                List<Tag> tag=Endpoint.instance().getTags();
                for (int i=0;i<tag.size();i++){
                    if(tag.get(i).getName().equals("BanglaSong")){
                        categoryItem=tag.get(i).getArtists();
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
                    Toast.makeText(con, "Server is down, Please try again later", Toast.LENGTH_SHORT).show();
                }else
                    Util.showNoInternetDialog(con);
                return;
            }
            categoryItemList.setAdapter(new MyListAdapter());
        }
    }
}
