package com.sctjsj.forestcommunity.adapter;


import android.graphics.Color;
import android.media.Image;
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
public class UltraPagerAdapter extends PagerAdapter {

    //是否多屏
    private boolean isMultiScr;

    private List<Integer> data;

    public UltraPagerAdapter(boolean isMultiScr) {
        this.isMultiScr = isMultiScr;
    }

    public UltraPagerAdapter(boolean isMultiScr,List<Integer> data) {
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
        View view = (View) LayoutInflater.from(container.getContext()).
                inflate(R.layout.layout_recommend_horizontal, null);

        //每一个 item img
        ImageView mIV = (ImageView) view.findViewById(R.id.img);
        Picasso.with(container.getContext()).load((int)(data.get(position))).into(mIV);


        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
