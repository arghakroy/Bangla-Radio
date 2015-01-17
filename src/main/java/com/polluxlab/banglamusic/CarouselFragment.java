package com.polluxlab.banglamusic;



import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.polluxlab.banglamusic.helper.OnBackPressListener;
import com.polluxlab.banglamusic.helper.ViewPagerAdapter;


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
    Button freeCatBtn,prermCatBtn,settingCatBtn;
    ImageButton pauseBtn;
    LinearLayout playerLay;

    int pos=0;

    public CarouselFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_carousel, container, false);

        playerLay= (LinearLayout) rootView.findViewById(R.id.playerLayout);
        pauseBtn= (ImageButton) rootView.findViewById(R.id.pauseBtn);
        freeCatBtn= (Button) rootView.findViewById(R.id.mainFreeBtn);
        prermCatBtn= (Button) rootView.findViewById(R.id.mainPremBtn);
        settingCatBtn= (Button) rootView.findViewById(R.id.mainAccBtn);
        pager = (ViewPager) rootView.findViewById(R.id.vp_pages);


        freeCatBtn.setOnClickListener(this);
        prermCatBtn.setOnClickListener(this);
        settingCatBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);

        Typeface tf=Typeface.createFromAsset(getActivity().getAssets(), "fonts/solaiman-bold.ttf");
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
                pager.setCurrentItem(1);
                break;
            case R.id.mainAccBtn:
                pager.setCurrentItem(2);
                break;
            case R.id.pauseBtn:
                if(pos==0)player(1);
                else if(pos==1)player(0);
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


    public void player(int pos) {
        playerLay.setVisibility(View.VISIBLE);
        this.pos=pos;
       if(pos==1){
            Intent objIntent = new Intent(getActivity(), PlayAudio.class);
            getActivity().startService(objIntent);
            pauseBtn.setImageResource(R.drawable.ic_pause);
        }else if(pos==0){
            Intent objIntent = new Intent(getActivity(), PlayAudio.class);
            getActivity().stopService(objIntent);
            pauseBtn.setImageResource(R.drawable.ic_play);
        }
    }
}
