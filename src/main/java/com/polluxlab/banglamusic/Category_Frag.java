package com.polluxlab.banglamusic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

/**
 * Created by ARGHA K ROY on 11/20/2014.
 */
public class Category_Frag extends Fragment{

    GridView channelList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.category_layout_frag, container,  false);
        channelList= (GridView) rootView.findViewById(R.id.categoryList);
        channelList.setAdapter(new MyListAdapter());
        return rootView;
    }


    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView=view;
            if(rowView==null){
                LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = infalInflater.inflate(R.layout.single_category_item, null);
            }
            return rowView;
        }
    }
}
