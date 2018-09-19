package com.pencil.pencil.businessbook.Business.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pencil.pencil.businessbook.Business.Fragment.BProfileFragment;
import com.pencil.pencil.businessbook.Business.Fragment.CreateOfferFragment;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                BProfileFragment tab1 = new BProfileFragment();
                return tab1;
            case 1:
                CreateOfferFragment tab2 = new CreateOfferFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}