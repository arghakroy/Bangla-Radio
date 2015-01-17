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
import com.polluxlab.banglamusic.model.Category;
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
    ArrayList<HashMap<String,String>> categoryItem;
    Context con;
    public static String albumUrl="http://162.248.162.2/musicapp/server/web/app_dev.php/webservice/albums/";
    JSONParser jparser=new JSONParser();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_layout_frag, container,  false);
        con=getActivity();

        categoryItemList=(GridView) rootView.findViewById(R.id.categoryList);

        new GetAlbums().execute();

        categoryItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Category_List_Frag listFragment = new Category_List_Frag();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                // Store the Fragment in stack
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.replace(R.id.root_container, listFragment).commit();
            }
        });

        return rootView;
    }

    private void generate_data() {
        categoryItem=new ArrayList<HashMap<String, String>>();

        for (int i=0;i<10;i++){
            HashMap<String,String> map=new HashMap<String, String>();
            map.put(Category.name,"SubCategory "+i);
            map.put(Category.externalId,i+"");
            categoryItem.add(map);
        }
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
            categoryName.setText(categoryItem.get(i).get(Category.name));
            return rowView;
        }
    }

    class GetAlbums extends AsyncTask<String,String,String> {

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
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                String respond=jparser.makeHttpRequest(albumUrl, "GET", params);
                s=respond;
                JSONArray jArray=new JSONArray(respond);
                for (int i=0;i<jArray.length();i++){
                    HashMap<String,String> map=new HashMap<String, String>();
                    map.put(Category.name, jArray.getJSONObject(i).getString("name"));
                    categoryItem.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error=1;
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
