package com.pencil.pencil.businessbook.Business.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.pencil.pencil.businessbook.Business.Adapter.PagerAdapter;
import com.pencil.pencil.businessbook.R;


public class BusinessProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    //ui
   // private TabLayout tabLayout;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private  Context mContext;



    public BusinessProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_business_profile, container, false);
        bindVariable(view);
        mContext=getActivity();
        addTab();
        return view;
    }
    private void bindVariable( View view){
        mViewPager=view.findViewById(R.id.viewPager);
        mTabLayout=view.findViewById(R.id.tabLayout);
    }
    private void addTab(){
        mTabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.lightgrey));

       // mTabLayout.setTabTextColors(getResources().getColor(R.color.white),getResources().getColor(R.color.white));
        // Create a new Tab named "Front"
        TabLayout.Tab frontTab = mTabLayout.newTab();
        TextView createBusinessTextview =new TextView(mContext);
                //(TextView) LayoutInflater.from(mContext).inflate(R.layout.tab_custom_text, null);
        createBusinessTextview .setText(getResources().getText(R.string.business_profile)); // set the Text for the Front tyre Tab
        frontTab.setCustomView(createBusinessTextview);
        mTabLayout.addTab(frontTab); // add Front tyre at in the TabLayout

        // Create a new Tab named "Rear"
        TabLayout.Tab rearTab = mTabLayout.newTab();
        TextView createOfferTv =  new TextView(mContext);
        //LayoutInflater.from(mContext).inflate(R.layout.tab_custom_text, null);
        createOfferTv.setText(getResources().getText(R.string.Create_Offer)); // set the Text for the Rear tyre Tab
        rearTab.setCustomView(createOfferTv);
        mTabLayout.addTab(rearTab);
        PagerAdapter adapter = new PagerAdapter(getFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(adapter);

        // addOnPageChangeListener event change the tab on slide
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        // setOnTabSelectedListeners event change the tab on slide
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


}
