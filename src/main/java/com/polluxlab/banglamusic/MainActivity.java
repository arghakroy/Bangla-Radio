package com.polluxlab.banglamusic;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.polluxlab.banglamusic.model.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener{

    ViewPager pager;

    ArrayList<HashMap<String,String>> freeCategories;
    ArrayList<HashMap<String,String>> premCategories;

    Button freeCatBtn,prermCatBtn,settingCatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        freeCatBtn= (Button) findViewById(R.id.mainFreeBtn);
        prermCatBtn= (Button) findViewById(R.id.mainPremBtn);
        settingCatBtn= (Button) findViewById(R.id.mainAccBtn);

        Typeface tf=Typeface.createFromAsset(getAssets(), "fonts/solaiman-bold.ttf");
        freeCatBtn.setTypeface(tf);
        prermCatBtn.setTypeface(tf);
        settingCatBtn.setTypeface(tf);

        Parse.initialize(this, "2msjexhy2q7cYTDwUuGbqZLOOcJE9GEVLuyFjCQD", "gwL8F50eSzbvqoRBbGOA3nmxxDm5aRsvQJhsdMn7");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Categories");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (e == null) {
                    freeCategories=new ArrayList<HashMap<String, String>>();
                    premCategories=new ArrayList<HashMap<String, String>>();
                    for (int i=0;i<parseObjects.size();i++){
                        HashMap<String,String> map=new HashMap();
                        map.put(Category.name,parseObjects.get(i).get(Category.name).toString());
                        map.put(Category.accessLevel,parseObjects.get(i).get(Category.accessLevel).toString());
                        map.put(Category.image,parseObjects.get(i).get(Category.image).toString());
                        map.put(Category.externalId,parseObjects.get(i).get(Category.externalId).toString());
                        String access=parseObjects.get(i).get(Category.accessLevel).toString();
                        if(access.equals("free")){
                            freeCategories.add(map);
                        }else if(access.equals("premium")){
                            premCategories.add(map);
                        }
                    }

                    Category_Frag.freeCategories=freeCategories;
                    Category_Frag.premCategories=premCategories;

                    pager= (ViewPager) findViewById(R.id.pager);
                    pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

                    freeCatBtn.setOnClickListener(MainActivity.this);
                    prermCatBtn.setOnClickListener(MainActivity.this);
                    settingCatBtn.setOnClickListener(MainActivity.this);
                } else {
                    Toast.makeText(MainActivity.this,"Error "+e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        //tabs.setIndicatorHeight(10);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mainFreeBtn:
                pager.setCurrentItem(0);
                break;
            case R.id.mainPremBtn:
                pager.setCurrentItem(1);
                break;
            case R.id.mainAccBtn:
                pager.setCurrentItem(2);
                break;
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Bundle bun = new Bundle();
            Category_Frag catFrag=new Category_Frag();
            if(i==0){
                bun.putInt("pos",0);
                catFrag.setArguments(bun);
                return catFrag;
            }
            else if(i==1){
                bun.putInt("pos",1);
                catFrag.setArguments(bun);
                return catFrag;
            }
            else return new Setting_Frag();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Free";
                case 1: return "Premium";
                case 2: return "Account";
            }
            return "";
        }
    }
}
