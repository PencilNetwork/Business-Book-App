package com.pencil.pencil.businessbook.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by user on 2/25/2018.
 */

public class SliderPageAdapter extends PagerAdapter {
    private LayoutInflater layoutInflater;
    private Context mContext;
    private int[] mLayouts;
    public SliderPageAdapter(Context context,int[] layouts) {
        mContext=context;
        mLayouts=layouts;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate( mLayouts[position], container, false);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return  mLayouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}