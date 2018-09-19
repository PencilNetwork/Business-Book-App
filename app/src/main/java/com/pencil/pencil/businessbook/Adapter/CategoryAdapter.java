package com.pencil.pencil.businessbook.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pencil.pencil.businessbook.Business.Pojo.Category;
import com.pencil.pencil.businessbook.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<String> {

    ArrayList<Category> mData;

    Context mContext;

    public CategoryAdapter(@NonNull Context context, ArrayList<Category> titles) {
        super(context, R.layout.category_item);
        this.mData = titles;

        this.mContext = context;
    }

    public void add(ArrayList<Category> categories) {
        mData.addAll(categories);
        notifyDataSetChanged();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.category_item, parent, false);
            mViewHolder.mName = convertView.findViewById(R.id.categoryTv);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mName.setText(mData.get(position).getName());

        return convertView;
    }

    private static class ViewHolder {

        TextView mName;

    }
}