package com.sctjsj.forestcommunity.adapter;


import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by chrisjason
 */
public class VerticalUltraPagerAdapter extends PagerAdapter {

    //是否多屏
    private boolean isMultiScr;

    private List<String> data;

    public VerticalUltraPagerAdapter(boolean isMultiScr) {
        this.isMultiScr = isMultiScr;
    }

    public VerticalUltraPagerAdapter(boolean isMultiScr, List<String> data) {
        this.isMultiScr = isMultiScr;
        this.data=data;
    }


    @Override
    public int getCount() {
        return data==null?5:data.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View root = (View) LayoutInflater.from(container.getContext()).
                inflate(R.layout.layout_marqueeview_vertical, null);

        //每一个 item img
        TextView mTV = (TextView) root.findViewById(R.id.text);

        mTV.setText(data.get(position));




        container.addView(root);
        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
