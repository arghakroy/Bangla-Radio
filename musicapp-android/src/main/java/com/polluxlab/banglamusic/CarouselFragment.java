package com.polluxlab.banglamusic;



import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.polluxlab.banglamusic.helper.OnBackPressListener;
import com.polluxlab.banglamusic.helper.ViewPagerAdapter;
import com.polluxlab.banglamusic.model.Song;
import com.polluxlab.banglamusic.util.AppConstant;
import com.polluxlab.banglamusic.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class CarouselFragment extends Fragment implements View.OnClickListener{

    /**
     * TabPagerIndicator
     *
     * Please refer to ViewPagerIndicator library
     */



    protected ViewPager pager;

    private ViewPagerAdapter adapter;
    Button freeCatBtn,prermCatBtn,settingCatBtn,closeBtn;
    ImageButton pauseBtn,prevBtn,nextBtn;
    FrameLayout playerLay;
    TextView songName,artistName;
    ImageView songImage;

    public MusicManager mManager=new MusicManager();
    public static MediaPlayer mPlayer;
    public static List<Song> currentSongs;

    static int currentPos;
    static int currentState;

    public CarouselFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(getClass().getName(), "onCreateView called");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_carousel, container, false);

        playerLay= (FrameLayout) rootView.findViewById(R.id.playerLayout);
        pauseBtn= (ImageButton) rootView.findViewById(R.id.pauseBtn);
        prevBtn= (ImageButton) rootView.findViewById(R.id.prevBtn);
        nextBtn= (ImageButton) rootView.findViewById(R.id.nextBtn);
        closeBtn= (Button) rootView.findViewById(R.id.playerUiClose);
        songImage= (ImageView) rootView.findViewById(R.id.playerUiImage);

        closeBtn.bringToFront();

        freeCatBtn= (Button) rootView.findViewById(R.id.mainFreeBtn);
        prermCatBtn= (Button) rootView.findViewById(R.id.mainPremBtn);
        settingCatBtn= (Button) rootView.findViewById(R.id.mainAccBtn);
        songName= (TextView) rootView.findViewById(R.id.playerUiName);
        artistName= (TextView) rootView.findViewById(R.id.playerUiArtist);

        pager = (ViewPager) rootView.findViewById(R.id.vp_pages);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                String title=getResources().getString(R.string.free_category_title);
                if(i==0){
                    title=getResources().getString(R.string.free_category_title);
                }else if(i==1){
                    title=getResources().getString(R.string.category_title);
                }else
                    title=getResources().getString(R.string.string_set);

                getActivity().getActionBar().setTitle(title);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        freeCatBtn.setOnClickListener(this);
        prermCatBtn.setOnClickListener(this);
        settingCatBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);

        Typeface tf=Typeface.createFromAsset(getActivity().getAssets(), "fonts/solaiman_bold.ttf");
        freeCatBtn.setTypeface(tf);
        prermCatBtn.setTypeface(tf);
        settingCatBtn.setTypeface(tf);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());

        pager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mainFreeBtn:
                pager.setCurrentItem(0);
                break;
            case R.id.mainPremBtn:
                returnHome();
                pager.setCurrentItem(1);
                break;
            case R.id.mainAccBtn:
                pager.setCurrentItem(2);
                break;
            case R.id.pauseBtn:
                if(currentState==2)player(1,currentPos,currentSongs);
                else if(currentState==1)player(2,currentPos,currentSongs);
                break;
            case R.id.prevBtn:
                if(currentPos==0)currentPos=currentSongs.size()-1;
                else currentPos--;
                player(1,currentPos,currentSongs);
                break;
            case R.id.nextBtn:
                if(currentPos==currentSongs.size()-1)currentPos=0;
                else currentPos++;
                player(1,currentPos,currentSongs);
                break;
            case R.id.playerUiClose:
                player(0,currentPos,currentSongs);
                break;
        }
    }
    /**
     * Retrieve the currently visible Tab Fragment and propagate the onBackPressed callback
     *
     * @return true = if this fragment and/or one of its associates Fragment can handle the backPress
     */
    public boolean onBackPressed() {
        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(pager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            return currentFragment.onBackPressed();
        }

        // this Fragment couldn't handle the onBackPressed call
        return false;
    }

    public void returnHome() {
        OnBackPressListener currentFragment = (OnBackPressListener) adapter.getRegisteredFragment(pager.getCurrentItem());

        if (currentFragment != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            currentFragment.onBackPressed();
            currentFragment.onBackPressed();
        }
    }

    public void player(int command, int pos,List<Song> songs) {
        Log.d(AppConstant.DEBUG,"Command: "+command+", Pos: "+pos);
        if(songs!=null){
            this.currentSongs=songs;
            currentPos=pos;
        }
        currentState=command;
        if(command==1){
            Util.showToast(getActivity(),"Loading. Please wait ...");
            playerLay.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) pager.getLayoutParams();
            params.bottomMargin = playerLay.getHeight()-closeBtn.getHeight();
            pager.setLayoutParams(params);
            songName.setText(currentSongs.get(currentPos).getTitle());
            artistName.setText(currentSongs.get(currentPos).getAlbum());
            Picasso.with(getActivity()).load(currentSongs.get(currentPos).getPreview()).error(R.drawable.music_icon).into(songImage);
            pauseBtn.setImageResource(R.drawable.ic_pause);
            mManager.init();
/*            PlayAudio.songs=songs;
            Intent objIntent = new Intent(getActivity(), PlayAudio.class);
            if(isMyServiceRunning(PlayAudio.class))
                getActivity().stopService(objIntent);

            objIntent.putExtra("pos",currentPos);
            getActivity().startService(objIntent);*/
        }else if(command==0){
            mManager.stop();
            pauseBtn.setImageResource(R.drawable.ic_play);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) pager.getLayoutParams();
            params.bottomMargin =0;
            pager.setLayoutParams(params);
            playerLay.setVisibility(View.GONE);
/*            if(isMyServiceRunning(PlayAudio.class)) {
                Intent objIntent = new Intent(getActivity(), PlayAudio.class);
                getActivity().stopService(objIntent);
            }*/

        }else if(command==2){
            mManager.stop();
            pauseBtn.setImageResource(R.drawable.ic_play);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    class MusicManager {
        final String LOGCAT = AppConstant.DEBUG;
        String link="";

        public void init(){
            new GetStreamLink().execute();
        }

        public void play(){
            if(mPlayer!=null){
                if(mPlayer.isPlaying())
                    mPlayer.stop();
            }
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(link);
            }catch (Exception e){
                Log.d(LOGCAT,"Error in onpost playaudio");
            }
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(AppConstant.DEBUG,"Music finished, current pos: "+mp.getCurrentPosition());
                    if(mp.getCurrentPosition()!=0){
                        mp.stop();
                        nextBtn.performClick();
                    }
                }
            });
            Log.d(LOGCAT, "Media Player started!");
            mPlayer.prepareAsync();
        }

        public void stop(){
            if(mPlayer!=null && mPlayer.isPlaying()){
                mPlayer.stop();
            }
        }

        class GetStreamLink extends AsyncTask<String,String,String> {

            @Override
            protected String doInBackground(String... params) {
                link=currentSongs.get(currentPos).getStreamLink();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                play();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mPlayer!=null){
            mPlayer.stop();
        }

    }
}
