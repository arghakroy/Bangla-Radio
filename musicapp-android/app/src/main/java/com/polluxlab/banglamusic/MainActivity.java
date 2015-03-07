package com.polluxlab.banglamusic;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.GlobalContext;
import com.polluxlab.banglamusic.util.Util;

import java.util.List;


public class MainActivity extends FragmentActivity implements PlaySoundHelper{

    private MainPagerFragment carouselFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalContext.set(getApplicationContext());
        setContentView(R.layout.activity_main);
        centerActionBarTitle();
       // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (savedInstanceState == null) {
            // withholding the previously created fragment from being created again
            // On orientation change, it will prevent fragment recreation
            // its necessary to reserving the fragment stack inside each tab
            initScreen();
        } else {
            // restoring the previously created fragment
            // and getting the reference
            carouselFragment = (MainPagerFragment) getSupportFragmentManager().getFragments().get(0);
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
            txvPars.gravity = Gravity.LEFT;
            txvPars.width = metrics.widthPixels;
           // txvPars.leftMargin= Util.getDipValue(this,1);
            titleTextView.setLayoutParams(txvPars);

            titleTextView.setGravity(Gravity.LEFT);
        }
    }

    private void initScreen() {
        // Creating the ViewPager container fragment once
        carouselFragment = new MainPagerFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, carouselFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) onBackPressed();
        switch (item.getItemId()){
            case R.id.notificationMenu:
                startActivity(new Intent(this,NotificationsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Only Activity has this special callback method
     * Fragment doesn't have any onBackPressed callback
     *
     * Logic:
     * Each time when the back button is pressed, this Activity will propagate the call to the
     * container Fragment and that Fragment will propagate the call to its each tab Fragment
     * those Fragments will propagate this method call to their child Fragments and
     * eventually all the propagated calls will get back to this initial method
     *
     * If the container Fragment or any of its Tab Fragments and/or Tab child Fragments couldn't
     * handle the onBackPressed propagated call then this Activity will handle the callback itself
     */
    @Override
    public void onBackPressed() {

        if (!carouselFragment.onBackPressed()) {
            // container Fragment or its associates couldn't handle the back pressed task
            // delegating the task to super class
            super.onBackPressed();

        } else {
            // carousel handled the back pressed task
            // do not call super
        }
    }

    @Override
    public void play(int command,int pos,List<Song> songs) {
        Log.d(AppConstant.DEBUG,"MainActivity play method");
        FragmentManager mgr=getSupportFragmentManager();
        MainPagerFragment carousel= (MainPagerFragment) mgr.findFragmentById(R.id.container);
        carousel.player(command,pos,songs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
