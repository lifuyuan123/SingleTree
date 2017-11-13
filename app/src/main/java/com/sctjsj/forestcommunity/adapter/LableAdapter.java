package com.sctjsj.forestcommunity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sctjsj.forestcommunity.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

/**
 * Created by haohaoliu on 2017/8/11.
 * explain:
 */

public class LableAdapter extends TagAdapter<String> {
    private List<String> data;
    private LayoutInflater inflater;
    private Context mContext;


    public LableAdapter(List<String> datas, Context mContext) {
        super(datas);
        this.mContext=mContext;
        this.inflater= (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=datas;
    }

    @Override
    public View getView(FlowLayout parent, int position, String s) {
        TextView mTextView= (TextView) inflater.inflate(R.layout.item_lable,parent,false);
        mTextView.setText(data.get(position));
        mTextView.setBackgroundResource(R.drawable.ic_from_lable_bg);
        return mTextView;
    }
}
