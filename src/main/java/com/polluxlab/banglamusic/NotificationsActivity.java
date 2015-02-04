package com.polluxlab.banglamusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by ARGHA K ROY on 2/3/2015.
 */
public class NotificationsActivity extends Activity{

    ListView notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);
        centerActionBarTitle();
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        notificationList= (ListView) findViewById(R.id.notificationListView);
        notificationList.setAdapter(new MyListAdapter());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }
    class MyListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView=convertView;
            if(rowView==null){
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.single_notification_item, null);
            }
            return rowView;
        }
    }

    private void centerActionBarTitle()
    {

        int titleId = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        }
        else
        {
            // This is the id is from your app's generated R class when ActionBarActivity is used
            // for SupportActionBar
            titleId = R.id.action_bar_title;
        }

        // Final check for non-zero invalid id
        if (titleId > 0)
        {
            TextView titleTextView = (TextView) findViewById(titleId);
            titleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/solaiman_bold.ttf"));

            DisplayMetrics metrics = getResources().getDisplayMetrics();

            // Fetch layout parameters of titleTextView (LinearLayout.LayoutParams : Info from HierarchyViewer)
            LinearLayout.LayoutParams txvPars = (LinearLayout.LayoutParams) titleTextView.getLayoutParams();
            txvPars.gravity = Gravity.CENTER_HORIZONTAL;
            txvPars.width = metrics.widthPixels;
            titleTextView.setLayoutParams(txvPars);

            titleTextView.setGravity(Gravity.CENTER);
        }
    }
}
