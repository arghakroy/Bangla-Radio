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
import android.widget.TextView;

import com.polluxlab.banglamusic.model.Category;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ARGHA K ROY on 11/20/2014.
 */
public class Category_Frag extends Fragment{

    GridView categoryList;
    int pos=0;

    static ArrayList<HashMap<String,String>> freeCategories;
    static ArrayList<HashMap<String,String>> premCategories;

    public Category_Frag(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.category_layout_frag, container,  false);
        categoryList= (GridView) rootView.findViewById(R.id.categoryList);

        Bundle b=getArguments();
        pos=b.getInt("pos");

        categoryList.setAdapter(new MyListAdapter());

        return rootView;
    }




    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(pos==0){
                return freeCategories.size();
            }else return premCategories.size();
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
                LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = infalInflater.inflate(R.layout.single_category_item, null);
            }
            TextView categoryName= (TextView) rowView.findViewById(R.id.singleCategoryName);
            if(pos==0){
                categoryName.setText(freeCategories.get(i).get(Category.name));
            }else{
                categoryName.setText(premCategories.get(i).get(Category.name));
            }
            return rowView;
        }
    }
}
