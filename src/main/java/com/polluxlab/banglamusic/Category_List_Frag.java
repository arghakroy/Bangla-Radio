package com.polluxlab.banglamusic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
public class Category_List_Frag extends RootFragment {


    ListView itemList;
    ArrayList<HashMap<String,String>> categoryItem;
    PlaySoundHelper helper;


    Context con;
    public static String albumUrl="http://162.248.162.2/musicapp/server/web/app_dev.php/webservice/albums/8/songs";
    JSONParser jparser=new JSONParser();



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.category_list_layout, container,  false);
        con=getActivity();
        helper = (PlaySoundHelper) getActivity();
        itemList=(ListView) rootView.findViewById(R.id.categoryItemList);
        new GetSongs().execute();
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
                rowView = inflater.inflate(R.layout.single_list_item, null);
            }

            TextView categoryName= (TextView) rowView.findViewById(R.id.singleListItemTitle);
            categoryName.setText(categoryItem.get(i).get(Category.name));



            ImageButton plaBtn= (ImageButton) rowView.findViewById(R.id.playBtn);
            plaBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //helper.play(1);
                    //Intent objIntent = new Intent(getActivity(), PlayAudio.class);
                    //getActivity().startService(objIntent);
                }
            });
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
                List<NameValuePair> params=new ArrayList<NameValuePair>();
                String respond=jparser.makeHttpRequest(albumUrl, "GET", params);
                s=respond;
                JSONArray jArray=new JSONArray(respond);
                for (int i=0;i<jArray.length();i++){
                    HashMap<String,String> map=new HashMap<String, String>();
                    map.put(Category.name, jArray.getJSONObject(i).getString("title"));
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
            itemList.setAdapter(new MyListAdapter());
        }
    }
}
